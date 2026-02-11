package br.com.b256.core.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Classe base abstrata para ViewModels que gerenciam um estado de UI seguindo a arquitetura MVI.
 *
 * Esta classe fornece uma estrutura robusta para ViewModels que seguem o padrão MVI
 * (Model-View-Intent), onde:
 * - O estado da UI é representado por um objeto imutável ([S])
 * - As ações do usuário são modeladas como intents ([I])
 * - Efeitos colaterais one-shot são representados por efeitos ([E])
 *
 * ## Características principais:
 * - Estado unidirecional e previsível
 * - Separação clara entre estado persistente e efeitos temporários
 * - Thread-safe através do uso de coroutines e flows
 *
 * ## Exemplo de uso:
 * ```kotlin
 * // Definir os contratos
 * sealed interface LoginIntent : UiIntent {
 *     data class EmailChanged(val email: String) : LoginIntent
 *     object Submit : LoginIntent
 * }
 *
 * data class LoginState(
 *     val email: String = "",
 *     val isLoading: Boolean = false
 * ) : UiState
 *
 * sealed interface LoginEffect : UiEffect {
 *     object NavigateToHome : LoginEffect
 *     data class ShowError(val message: String) : LoginEffect
 * }
 *
 * // Implementar o ViewModel
 * class LoginViewModel : BaseViewModel<LoginIntent, LoginState, LoginEffect>(
 *     initialState = LoginState()
 * ) {
 *     override suspend fun handleIntent(intent: LoginIntent) {
 *         when (intent) {
 *             is LoginIntent.EmailChanged -> reduce { copy(email = intent.email) }
 *             is LoginIntent.Submit -> performLogin()
 *         }
 *     }
 *
 *     private suspend fun performLogin() {
 *         reduce { copy(isLoading = true) }
 *         // lógica de login...
 *         sendEffect(LoginEffect.NavigateToHome)
 *     }
 * }
 *
 * // Na View/Fragment
 * viewModel.dispatch(LoginIntent.EmailChanged("user@example.com"))
 * ```
 *
 * @param I Intent - Representa as intenções/ações do usuário ou eventos da UI
 * @param S State - Representa o estado imutável da UI
 * @param E Effect - Representa eventos one-shot (navegação, snackbar, toasts, etc.)
 * @param initialState O estado inicial da UI
 *
 * @see UiIntent
 * @see UiState
 * @see UiEffect
 */
abstract class BaseViewModel<I, S, E>(initialState: S) :
    ViewModel() {

    /**
     * Estado interno mutável da UI.
     *
     * Este é o único ponto onde o estado pode ser modificado internamente.
     * Representa a única fonte de verdade (single source of truth) do estado da UI.
     *
     * @see uiState para acesso somente leitura
     * @see reduce para modificação segura do estado
     */
    private val _uiState = MutableStateFlow(initialState)

    /**
     * Estado observável da UI (somente leitura).
     *
     * A View deve coletar este StateFlow para reagir às mudanças de estado.
     * Garante que a View não possa modificar o estado diretamente.
     *
     * ## Exemplo de coleta na View:
     * ```kotlin
     * lifecycleScope.launch {
     *     viewModel.uiState.collect { state ->
     *         updateUI(state)
     *     }
     * }
     * ```
     */
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    /**
     * Fluxo de intents enviados pela View.
     *
     * Utiliza [MutableSharedFlow] com buffer para garantir que nenhum intent seja perdido,
     * mesmo durante picos de eventos da UI. O buffer de 64 é suficiente para a maioria
     * dos casos de uso, incluindo cliques rápidos ou eventos em rajada.
     *
     * @see dispatch para enviar intents
     */
    private val intents = MutableSharedFlow<I>(extraBufferCapacity = 64)

    /**
     * Canal de efeitos one-shot.
     *
     * Efeitos são eventos que devem ser consumidos apenas uma vez, como:
     * - Navegação para outra tela
     * - Exibição de Snackbar/Toast
     * - Vibração do dispositivo
     * - Abertura de diálogos
     *
     * Utiliza [Channel.BUFFERED] para garantir que efeitos não sejam perdidos
     * se a View não estiver coletando no momento do envio.
     */
    private val _effect = Channel<E>(Channel.BUFFERED)

    /**
     * Fluxo público de efeitos one-shot.
     *
     * A View deve coletar este flow para reagir a efeitos colaterais.
     * Cada efeito é entregue apenas uma vez e removido após o consumo.
     *
     * ## Exemplo de coleta na View:
     * ```kotlin
     * lifecycleScope.launch {
     *     viewModel.effect.collect { effect ->
     *         when (effect) {
     *             is MyEffect.ShowSnackbar -> showSnackbar(effect.message)
     *             is MyEffect.Navigate -> navigate(effect.destination)
     *         }
     *     }
     * }
     * ```
     */
    val effect: Flow<E> = _effect.receiveAsFlow()

    init {
        subscribeEvents()
    }

    /**
     * Envia um intent para ser processado pelo ViewModel.
     *
     * Este é o único ponto de entrada para a View enviar ações ao ViewModel,
     * garantindo um fluxo de dados unidirecional (View → ViewModel → State → View).
     *
     * ## Características:
     * - Thread-safe: pode ser chamado de qualquer thread
     * - Não-bloqueante: retorna imediatamente
     * - Garantido: intents são enfileirados e processados em ordem
     *
     * ## Exemplo:
     * ```kotlin
     * // Em um click listener
     * button.setOnClickListener {
     *     viewModel.dispatch(MyIntent.ButtonClicked)
     * }
     * ```
     *
     * @param intent O intent a ser processado
     */
    fun dispatch(intent: I) {
        intents.tryEmit(intent)
    }

    /**
     * Inicia a coleta e processamento de intents.
     *
     * Este método é chamado automaticamente no [init] e garante que todos os intents
     * enviados via [dispatch] sejam processados sequencialmente através de [handleIntent].
     *
     * A coleta ocorre no [viewModelScope], sendo automaticamente cancelada quando
     * o ViewModel é destruído.
     */
    private fun subscribeEvents() {
        viewModelScope.launch {
            intents.collect { intent ->
                handleIntent(intent)
            }
        }
    }

    /**
     * Processa um intent recebido da UI.
     *
     * Implementações devem interpretar o intent e executar a lógica de negócio apropriada,
     * que pode incluir:
     * - Atualizar o estado via [reduce]
     * - Emitir efeitos via [sendEffect]
     * - Executar operações assíncronas (chamadas de API, acesso ao banco de dados, etc.)
     *
     * ## Boas práticas:
     * - Use `when` exhaustivo para garantir que todos os intents sejam tratados
     * - Delegue lógica complexa para métodos privados
     * - Sempre use [reduce] para mudanças de estado
     * - Use [sendEffect] para eventos one-shot
     *
     * ## Exemplo:
     * ```kotlin
     * override suspend fun handleIntent(intent: MyIntent) {
     *     when (intent) {
     *         is MyIntent.Load -> loadData()
     *         is MyIntent.Refresh -> {
     *             reduce { copy(isRefreshing = true) }
     *             refreshData()
     *         }
     *         is MyIntent.ItemClicked -> {
     *             sendEffect(MyEffect.NavigateToDetail(intent.itemId))
     *         }
     *     }
     * }
     * ```
     *
     * @param intent O intent a ser processado
     */
    protected abstract suspend fun handleIntent(intent: I)

    /**
     * Atualiza o estado da UI de forma segura e imutável.
     *
     * Esta é a **única forma permitida** de modificar o estado. Garante que:
     * - O estado seja atualizado atomicamente
     * - Todas as mudanças sejam thread-safe
     * - O princípio da imutabilidade seja respeitado
     *
     * ## Como funciona:
     * A função recebe um lambda que opera sobre o estado atual ([S]) e retorna
     * um novo estado. O estado anterior nunca é modificado diretamente.
     *
     * ## Exemplo:
     * ```kotlin
     * // Atualizar um único campo
     * reduce { copy(isLoading = true) }
     *
     * // Atualizar múltiplos campos
     * reduce {
     *     copy(
     *         userName = "João",
     *         isLoggedIn = true,
     *         loginAttempts = 0
     *     )
     * }
     *
     * // Com lógica condicional
     * reduce {
     *     if (errorCount > 3) {
     *         copy(isLocked = true, errorCount = 0)
     *     } else {
     *         copy(errorCount = errorCount + 1)
     *     }
     * }
     * ```
     *
     * @param reducer Função que recebe o estado atual e retorna o novo estado
     */
    protected fun reduce(reducer: S.() -> S) {
        _uiState.update { current ->
            reducer(current)
        }
    }

    /**
     * Emite um efeito one-shot para a View.
     *
     * Efeitos são eventos que devem ser consumidos apenas uma vez e não fazem parte
     * do estado persistente da UI. Exemplos comuns:
     * - Navegação (`NavigateTo`, `NavigateBack`)
     * - Feedback ao usuário (`ShowSnackbar`, `ShowToast`, `ShowDialog`)
     * - Interações do sistema (`Vibrate`, `PlaySound`, `RequestPermission`)
     *
     * ## Diferença entre State e Effect:
     * - **State**: Persiste e pode ser recriado (ex: loading, dados exibidos)
     * - **Effect**: Temporário e consumido uma vez (ex: toast, navegação)
     *
     * ## Exemplo:
     * ```kotlin
     * // Em handleIntent ou outro método
     * suspend fun deleteItem(itemId: String) {
     *     repository.delete(itemId)
     *         .onSuccess {
     *             reduce { copy(items = items.filter { it.id != itemId }) }
     *             sendEffect(MyEffect.ShowSuccess("Item excluído"))
     *         }
     *         .onFailure { error ->
     *             sendEffect(MyEffect.ShowError(error.message))
     *         }
     * }
     * ```
     *
     * @param effect O efeito a ser emitido
     */
    protected fun sendEffect(effect: E) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
