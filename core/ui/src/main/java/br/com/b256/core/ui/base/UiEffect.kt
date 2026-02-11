package br.com.b256.core.ui.base

/**
 * Interface marcadora para efeitos colaterais one-shot da UI.
 *
 * Efeitos representam eventos que devem ser consumidos **apenas uma vez** pela View
 * e não fazem parte do estado persistente da aplicação. São ideais para ações que
 * não devem ser repetidas em recriações de configuração (rotação de tela, etc.).
 *
 * ## Características:
 * - **One-shot**: Consumidos uma única vez e descartados
 * - **Não persistentes**: Não sobrevivem a mudanças de configuração
 * - **Orientados a ação**: Representam comandos ou eventos temporários
 *
 * ## Casos de uso comuns:
 * - **Navegação**: Navegar para outra tela, voltar, abrir deep links
 * - **Feedback visual**: Exibir Snackbar, Toast, Dialog
 * - **Interações do sistema**: Vibrar, reproduzir som, solicitar permissão
 * - **Ações externas**: Abrir navegador, compartilhar conteúdo, fazer ligação
 *
 * ## Quando usar Effect vs State:
 * - **Use Effect quando**: A ação deve acontecer uma vez (navegação, toast)
 * - **Use State quando**: A informação deve persistir e pode ser recriada (loading, dados)
 *
 * ## Exemplo de implementação:
 * ```kotlin
 * sealed interface LoginEffect : UiEffect {
 *     /**
 *      * Navega para a tela principal após login bem-sucedido.
 *      */
 *     object NavigateToHome : LoginEffect
 *
 *     /**
 *      * Navega para a tela de cadastro.
 *      */
 *     object NavigateToSignUp : LoginEffect
 *
 *     /**
 *      * Exibe uma mensagem de erro ao usuário.
 *      *
 *      * @property message A mensagem a ser exibida
 *      */
 *     data class ShowError(val message: String) : LoginEffect
 *
 *     /**
 *      * Exibe uma mensagem de sucesso.
 *      *
 *      * @property message A mensagem de sucesso
 *      */
 *     data class ShowSnackbar(val message: String) : LoginEffect
 *
 *     /**
 *      * Vibra o dispositivo para feedback tátil.
 *      *
 *      * @property duration Duração da vibração em milissegundos
 *      */
 *     data class Vibrate(val duration: Long = 100) : LoginEffect
 * }
 * ```
 *
 * ## Exemplo de uso no ViewModel:
 * ```kotlin
 * class LoginViewModel : BaseViewModel<LoginIntent, LoginState, LoginEffect>(
 *     initialState = LoginState()
 * ) {
 *     override suspend fun handleIntent(intent: LoginIntent) {
 *         when (intent) {
 *             is LoginIntent.Submit -> {
 *                 reduce { copy(isLoading = true) }
 *
 *                 loginUseCase(uiState.value.email, uiState.value.password)
 *                     .onSuccess {
 *                         sendEffect(LoginEffect.NavigateToHome)
 *                     }
 *                     .onFailure { error ->
 *                         reduce { copy(isLoading = false) }
 *                         sendEffect(LoginEffect.ShowError(error.message))
 *                     }
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * ## Boas práticas:
 * - Use `sealed interface` ou `sealed class` para definir uma hierarquia fechada de efeitos
 * - Nomeie efeitos com verbos que indiquem ação (Navigate, Show, Play, Request)
 * - Mantenha efeitos simples e focados em uma única responsabilidade
 * - Documente cada efeito explicando quando e por que ele é emitido
 * - Evite lógica complexa dentro dos efeitos; eles devem apenas carregar dados
 *
 *
 * @see UiIntent
 * @see UiState
 * @see BaseViewModel
 */
interface UiEffect
