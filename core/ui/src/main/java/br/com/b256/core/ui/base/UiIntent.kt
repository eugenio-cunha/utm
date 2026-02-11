package br.com.b256.core.ui.base

/**
 * Interface marcadora para intenções (ações) do usuário na UI.
 *
 * Intents representam **todas as possíveis ações** que o usuário pode realizar na interface,
 * fornecendo um vocabulário explícito e type-safe para interações. São o único ponto de
 * entrada para comunicação View → ViewModel na arquitetura MVI.
 *
 * ## Características:
 * - **Imutáveis**: Representam ações que já aconteceram
 * - **Declarativos**: Descrevem "o que" aconteceu, não "como" processar
 * - **Type-safe**: Aproveitam o sistema de tipos do Kotlin
 * - **Testáveis**: Facilitam testes unitários ao isolar ações
 *
 * ## Princípios de design:
 * - Cada interação do usuário deve ter um Intent correspondente
 * - Intents devem ser auto-descritivos e legíveis
 * - Nomeie com verbos ou substantivos que descrevam a ação do usuário
 * - Carregue apenas dados necessários para processar a ação
 *
 * ## Casos de uso comuns:
 * - **Entrada de dados**: Alterações em campos de texto, seleções
 * - **Cliques**: Botões, itens de lista, ações do menu
 * - **Eventos do ciclo de vida**: Tela iniciada, retomada, pausada
 * - **Navegação**: Voltar, avançar, abrir detalhes
 * - **Gestos**: Swipe, long press, scroll
 *
 * ## Exemplo de implementação:
 * ```kotlin
 * sealed interface LoginIntent : UiIntent {
 *     /**
 *      * Disparado quando o usuário altera o campo de e-mail.
 *      *
 *      * @property email O novo valor do campo de e-mail
 *      */
 *     data class EmailChanged(val email: String) : LoginIntent
 *
 *     /**
 *      * Disparado quando o usuário altera o campo de senha.
 *      *
 *      * @property password O novo valor do campo de senha
 *      */
 *     data class PasswordChanged(val password: String) : LoginIntent
 *
 *     /**
 *      * Disparado quando o usuário alterna a visibilidade da senha.
 *      */
 *     object TogglePasswordVisibility : LoginIntent
 *
 *     /**
 *      * Disparado quando o usuário clica no botão de login.
 *      */
 *     object LoginClicked : LoginIntent
 *
 *     /**
 *      * Disparado quando o usuário clica em "Esqueci minha senha".
 *      */
 *     object ForgotPasswordClicked : LoginIntent
 *
 *     /**
 *      * Disparado quando o usuário clica em "Criar conta".
 *      */
 *     object SignUpClicked : LoginIntent
 *
 *     /**
 *      * Disparado quando a tela é iniciada (onCreate/onViewCreated).
 *      */
 *     object ScreenStarted : LoginIntent
 *
 *     /**
 *      * Disparado quando o usuário tenta novamente após um erro.
 *      */
 *     object RetryClicked : LoginIntent
 * }
 * ```
 *
 * ## Exemplo de processamento no ViewModel:
 * ```kotlin
 * class LoginViewModel(
 *     private val loginUseCase: LoginUseCase,
 *     private val validateEmailUseCase: ValidateEmailUseCase
 * ) : BaseViewModel<LoginIntent, LoginState, LoginEffect>(
 *     initialState = LoginState()
 * ) {
 *     override suspend fun handleIntent(intent: LoginIntent) {
 *         when (intent) {
 *             is LoginIntent.EmailChanged -> {
 *                 val isValid = validateEmailUseCase(intent.email)
 *                 reduce {
 *                     copy(
 *                         email = intent.email,
 *                         emailError = if (isValid) null else "E-mail inválido"
 *                     )
 *                 }
 *             }
 *
 *             is LoginIntent.PasswordChanged -> {
 *                 reduce { copy(password = intent.password) }
 *             }
 *
 *             is LoginIntent.TogglePasswordVisibility -> {
 *                 reduce { copy(isPasswordVisible = !isPasswordVisible) }
 *             }
 *
 *             is LoginIntent.LoginClicked -> {
 *                 performLogin()
 *             }
 *
 *             is LoginIntent.ForgotPasswordClicked -> {
 *                 sendEffect(LoginEffect.NavigateToForgotPassword)
 *             }
 *
 *             is LoginIntent.SignUpClicked -> {
 *                 sendEffect(LoginEffect.NavigateToSignUp)
 *             }
 *
 *             is LoginIntent.ScreenStarted -> {
 *                 // Carregar dados iniciais se necessário
 *                 loadSavedCredentials()
 *             }
 *
 *             is LoginIntent.RetryClicked -> {
 *                 reduce { copy(error = null) }
 *                 performLogin()
 *             }
 *         }
 *     }
 *
 *     private suspend fun performLogin() {
 *         val state = uiState.value
 *
 *         reduce { copy(isLoading = true, error = null) }
 *
 *         loginUseCase(state.email, state.password)
 *             .onSuccess {
 *                 reduce { copy(isLoading = false) }
 *                 sendEffect(LoginEffect.NavigateToHome)
 *             }
 *             .onFailure { error ->
 *                 reduce {
 *                     copy(
 *                         isLoading = false,
 *                         error = error.message
 *                     )
 *                 }
 *                 sendEffect(LoginEffect.ShowError(error.message))
 *             }
 *     }
 * }
 * ```
 *
 * ## Boas práticas:
 * - Use `sealed interface` ou `sealed class` para garantir exaustividade no `when`
 * - Use `object` para intents sem dados e `data class` para intents com dados
 * - Nomeie intents com base na ação do usuário, não na consequência
 * - Mantenha intents simples; evite lógica de negócio dentro deles
 * - Documente cada intent explicando quando ele é disparado
 * - Agrupe intents relacionados usando nested sealed classes quando apropriado
 *
 * @see UiState
 * @see UiEffect
 * @see BaseViewModel
 */
interface UiIntent
