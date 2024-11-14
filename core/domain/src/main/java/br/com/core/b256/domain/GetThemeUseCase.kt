package br.com.core.b256.domain

import br.com.b256.core.data.repository.SettingsRepository
import br.com.b256.core.model.Theme
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetThemeUseCase @Inject constructor(
    private val repository: SettingsRepository,
) {
    operator fun invoke(): Flow<Theme> {
        return repository.getTheme()
    }
}
