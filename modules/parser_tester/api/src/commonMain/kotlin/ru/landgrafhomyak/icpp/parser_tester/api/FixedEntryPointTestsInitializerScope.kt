package ru.landgrafhomyak.icpp.parser_tester.api

import ru.landgrafhomyak.icpp.parser.environment.BuilderState

interface FixedEntryPointTestsInitializerScope<S : BuilderState<*, *, Nothing?>> {
    fun test(vararg testers: Tester): TestCallsChainBuilder<S>
}
