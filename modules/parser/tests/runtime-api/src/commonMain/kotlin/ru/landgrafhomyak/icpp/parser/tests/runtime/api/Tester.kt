package ru.landgrafhomyak.icpp.parser.tests.runtime.api

import kotlin.reflect.KClass
import ru.landgrafhomyak.icpp.parser.environment.BuilderState
import ru.landgrafhomyak.icpp.parser.environment.CollectedSubstring
import ru.landgrafhomyak.icpp.parser.environment.Pos

interface Tester {
    fun <P : Pos, CS : CollectedSubstring, S : BuilderState<P, CS, Nothing?>> getScope(key: KClass<S>): S
}

inline fun <P : Pos, CS : CollectedSubstring, reified S : BuilderState<P, CS, Nothing?>> ru.landgrafhomyak.icpp.parser.tests.runtime.api.Tester.getScope() =
    this.getScope(S::class)