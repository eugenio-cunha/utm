package br.com.b256.core.ui.base

/**
 * Interface que define o contrato para estados da UI na arquitetura MVI.
 *
 * UiState representa o **estado completo e imutável** de uma tela ou componente da UI
 * em um determinado momento. É a única fonte de verdade (single source of truth) que
 * determina o que deve ser exibido na interface.
 *
 * ## Características:
 * - **Imutável**: Uma vez criado, não pode ser modificado (use `data class` com `copy()`)
 * - **Completo**: Contém todas as informações necessárias para renderizar a UI
 * - **Serializável**: Pode ser salvo e restaurado (útil para process death)
 * - **Testável**: Estado explícito facilita testes e verificação
 *
 * ## Princípios de design:
 * - Represente TODOS os estados possíveis da tela
 * - Use tipos específicos em vez de genéricos (String vs UserId)
 * - Modele estados de erro, vazio, sucesso explicitamente
 * - Evite estados impossíveis através do design
 * - Mantenha o estado flat quando possível
 *
 * ## Propriedade comum [isLoading]:
 * Todas as implementações devem fornecer a propriedade [isLoading] que indica
 * se a UI está em processo de carregamento. Esta propriedade é útil para:
 * - Exibir progress bars ou skeletons
 * - Desabilitar controles durante operações assíncronas
 * - Fornecer feedback visual ao usuário
 *
 * ## Exemplo básico de implementação:
 * ```kotlin
 * data class LoginState(
 *     val email: String = "",
 *     val password: String = "",
 *     val isPasswordVisible: Boolean = false,
 *     val emailError: String? = null,
 *     val passwordError: String? = null,
 *     override val isLoading: Boolean = false,
 *     val error: String? = null
 * ) : UiState
 * ```
 *
 * ## Modelando diferentes estados de carregamento:
 * ```kotlin
 * data class UserProfileState(
 *     val userId: String? = null,
 *     val userName: String = "",
 *     val bio: String = "",
 *     val avatarUrl: String? = null,
 *     val followers: Int = 0,
 *     val following: Int = 0,
 *
 *     // Estados de carregamento específicos
 *     override val isLoading: Boolean = false,        // Carregamento inicial
 *     val isUpdatingProfile: Boolean = false,         // Salvando alterações
 *     val isLoadingPosts: Boolean = false,            // Carregando posts
 *     val isFollowing: Boolean = false,               // Processando follow/unfollow
 *
 *     val posts: List<Post> = emptyList(),
 *     val error: String? = null
 * ) : UiState {
 *     /**
 *      * Verifica se alguma operação está em andamento.
 *      */
 *     val isAnyOperationInProgress: Boolean
 *         get() = isLoading || isUpdatingProfile || isLoadingPosts || isFollowing
 * }
 * ```
 *
 * ## Padrão de estados vazios e de erro:
 * ```kotlin
 * data class ArticleListState(
 *     val articles: List<Article> = emptyList(),
 *     override val isLoading: Boolean = false,
 *     val error: String? = null
 * ) : UiState {
 *     /**
 *      * Estado vazio: sem artigos, sem carregamento, sem erro.
 *      */
 *     val isEmpty: Boolean
 *         get() = articles.isEmpty() && !isLoading && error == null
 *
 *     /**
 *      * Estado de erro: tem erro e não está carregando.
 *      */
 *     val isError: Boolean
 *         get() = error != null && !isLoading
 *
 *     /**
 *      * Estado de sucesso: tem dados, não está carregando, sem erro.
 *      */
 *     val isSuccess: Boolean
 *         get() = articles.isNotEmpty() && !isLoading && error == null
 * }
 * ```
 *
 * ## Exemplo de atualização no ViewModel:
 * ```kotlin
 * class LoginViewModel : BaseViewModel<LoginIntent, LoginState, LoginEffect>(
 *     initialState = LoginState()
 * ) {
 *     override suspend fun handleIntent(intent: LoginIntent) {
 *         when (intent) {
 *             is LoginIntent.EmailChanged -> {
 *                 reduce { copy(email = intent.email, emailError = null) }
 *             }
 *
 *             is LoginIntent.LoginClicked -> {
 *                 // Ativar loading
 *                 reduce { copy(isLoading = true, error = null) }
 *
 *                 loginUseCase(uiState.value.email, uiState.value.password)
 *                     .onSuccess {
 *                         // Desativar loading
 *                         reduce { copy(isLoading = false) }
 *                         sendEffect(LoginEffect.NavigateToHome)
 *                     }
 *                     .onFailure { error ->
 *                         // Desativar loading e mostrar erro
 *                         reduce {
 *                             copy(
 *                                 isLoading = false,
 *                                 error = error.message
 *                             )
 *                         }
 *                     }
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * ## Salvando estado (Process Death):
 * ```kotlin
 * // No ViewModel com SavedStateHandle
 * class MyViewModel(
 *     private val savedStateHandle: SavedStateHandle
 * ) : BaseViewModel<MyIntent, MyState, MyEffect>(
 *     initialState = savedStateHandle.get<MyState>("state") ?: MyState()
 * ) {
 *     override suspend fun handleIntent(intent: MyIntent) {
 *         // ... processar intent
 *     }
 *
 *     override fun reduce(reducer: MyState.() -> MyState) {
 *         super.reduce(reducer)
 *         // Salvar estado após cada mudança
 *         savedStateHandle["state"] = uiState.value
 *     }
 * }
 * ```
 *
 * ## Boas práticas:
 * - Use `data class` para aproveitar `copy()`, `equals()`, `hashCode()` e `toString()`
 * - Forneça valores padrão sensatos para todas as propriedades
 * - Documente propriedades computadas e suas regras de negócio
 * - Use tipos específicos do domínio em vez de tipos primitivos
 * - Agrupe propriedades relacionadas em objetos nested quando apropriado
 * - Mantenha o estado normalizado (evite duplicação de dados)
 * - Use sealed classes para estados mutuamente exclusivos
 *
 * @property isLoading Indica se a interface do usuário está em estado de carregamento.
 *                     Útil para exibir indicadores de progresso, desabilitar controles
 *                     ou fornecer feedback visual durante operações assíncronas.
 *
 * @see UiIntent
 * @see UiEffect
 * @see BaseViewModel
 */
interface UiState {
    /**
     * Indica se a interface do usuário está atualmente em estado de carregamento.
     *
     * Esta propriedade é normalmente `true` quando:
     * - Dados estão sendo buscados de uma API ou banco de dados
     * - Uma operação assíncrona está em andamento
     * - O sistema está processando uma requisição do usuário
     *
     * Quando `true`, a UI normalmente deve:
     * - Exibir um indicador de progresso (ProgressBar, Skeleton, etc.)
     * - Desabilitar controles interativos para evitar múltiplas submissões
     * - Fornecer feedback visual claro ao usuário
     *
     * ## Exemplo de uso:
     * ```kotlin
     * // No State
     * data class MyState(
     *     override val isLoading: Boolean = false,
     *     val data: List<Item> = emptyList()
     * ) : UiState
     *
     * // No ViewModel
     * reduce { copy(isLoading = true) }
     * // ... operação assíncrona
     * reduce { copy(isLoading = false) }
     *
     * // Na View
     * progressBar.isVisible = state.isLoading
     * submitButton.isEnabled = !state.isLoading
     * ```
     *
     * @see UiState
     */
    val isLoading: Boolean
}
