package dev.timatifey.posanie.fakes

import dev.timatifey.posanie.usecases.SettingsUseCase
import org.mockito.kotlin.mock

object SettingsUseCaseMockFactory {

    fun create(): SettingsUseCase {
        return mock()
    }
}
