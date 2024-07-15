package ru.landgrafhomyak.icpp.parser.tests.runtime.api

import ru.landgrafhomyak.icpp.parser.environment.BuilderState

interface TestsInitializerRootScope {
    fun <S : BuilderState<*, *, Nothing?>> entry(
        ep: ru.landgrafhomyak.icpp.parser.tests.runtime.api.EntryPoint<S>,
        b: ru.landgrafhomyak.icpp.parser.tests.runtime.api.FixedEntryPointTestsInitializerScope<S>.() -> Unit
    )

}