package br.com.core.b256.domain

import br.com.b256.core.data.repository.SettingsRepository
import br.com.b256.core.model.Theme
import javax.inject.Inject

class SetThemeUseCase @Inject constructor(
    private val repository: SettingsRepository,
) {
    suspend operator fun invoke(value: Theme) {
        return repository.setTheme(value = value)
    }
}
