package ru.landgrafhomyak.icpp.parser_tester.api

import kotlin.reflect.KFunction
import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction2
import kotlin.reflect.KFunction3
import ru.landgrafhomyak.icpp.parser.environment.BuilderState
import ru.landgrafhomyak.icpp.parser.environment.CollectedSubstring
import ru.landgrafhomyak.icpp.parser.environment.Pos

interface TestCallsChainBuilder<S : BuilderState<*, *, *>> {
    fun call(method: KFunction<Nothing?>, args: TestCallBuilder.() -> Unit)
    fun <R : BuilderState<*, *, *>> call(method: KFunction<R>, args: TestCallBuilder.() -> Unit): TestCallsChainBuilder<R>

    fun call(method: KFunction1<S, Nothing?>) =
        this.call(method as KFunction<Nothing?>) {}

    fun <R : BuilderState<*, *, *>> call(method: KFunction1<S, R>): TestCallsChainBuilder<R> =
        this.call(method as KFunction<R>) {}

    fun call(method: KFunction2<S, Pos, Nothing?>) =
        this.call(method as KFunction<Nothing?>) {
            pos()
        }

    fun <R : BuilderState<*, *, *>> call(method: KFunction2<S, Pos, R>): TestCallsChainBuilder<R> =
        this.call(method as KFunction<R>) {
            pos()
        }

    fun call(method: KFunction2<S, CollectedSubstring, Nothing?>, s: String) =
        this.call(method as KFunction<Nothing?>) {
            collectedSubstring(s)
        }

    fun <R : BuilderState<*, *, *>> call(method: KFunction2<S, CollectedSubstring, R>, s: String): TestCallsChainBuilder<R> =
        this.call(method as KFunction<R>) {
            collectedSubstring(s)
        }

    fun call(method: KFunction3<S, Pos, Pos, Nothing?>, s: String) =
        this.call(method as KFunction<Nothing?>) {
            pos()
            sourceText(s)
            pos()
        }

    fun <R : BuilderState<*, *, *>> call(method: KFunction3<S, Pos, Pos, R>, s: String): TestCallsChainBuilder<R> =
        this.call(method as KFunction<R>) {
            pos()
            sourceText(s)
            pos()
        }
}