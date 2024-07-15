package ru.landgrafhomyak.icpp.parser.tests.runtime.api

import ru.landgrafhomyak.icpp.parser.environment.BuilderState

interface FixedEntryPointTestsInitializerScope<S : BuilderState<*, *, Nothing?>> {
    fun test(vararg testers: ru.landgrafhomyak.icpp.parser.tests.runtime.api.Tester): ru.landgrafhomyak.icpp.parser.tests.runtime.api.TestCallsChainBuilder<S>
}
