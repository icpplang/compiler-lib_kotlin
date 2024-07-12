package ru.landgrafhomyak.icpp.parser_tester.api

import ru.landgrafhomyak.icpp.parser.environment.BuilderState

interface TestsInitializerRootScope {
    fun <S : BuilderState<*, *, Nothing?>> entry(
        ep: EntryPoint<S>,
        b: FixedEntryPointTestsInitializerScope<S>.() -> Unit
    )

}