package io.github.landgrafhomyak.icpp.parser.test.environment

import kotlin.reflect.KClass
import kotlin.reflect.KFunction2
import kotlin.reflect.KFunction3

internal class TestCaseBuilderImpl<T : Any> internal constructor(
    private val source: StringBuilder,
    private val scope: Scope
) : TestCaseBuilder<T>() {
    override fun String.unaryPlus() {
        this@TestCaseBuilderImpl.source.append(this@unaryPlus)
    }

    override fun <S : Any> subObject(kClass: KClass<S>, inner: TestCaseBuilder<S>.() -> Unit) {
        val child = Scope(kClass)
        this.scope.children.add(child)
        inner(TestCaseBuilderImpl(this.source, child))
    }

    override fun pos(vararg expectedCallbacks: KFunction2<Any, PosTestImpl, Unit>) {
        for (cb in expectedCallbacks)
            this.scope.entities.add(cb, this.source.length)
    }

    override fun symbol(vararg expectedCallbacks: KFunction2<T, PosTestImpl, Unit>, chr: Char) {
        for (cb in expectedCallbacks)
            this.scope.entities.add(cb, this.source.length)
        this.source.append(chr)
    }

    override fun range(expectedCallback: KFunction3<T, PosTestImpl, PosTestImpl, Unit>, string: String) {
        val start = this.source.length
        this.source.append(string)
        this.scope.entities.add(expectedCallback, start, this.source.length)
    }

    override fun range(expectedCallback: KFunction3<T, PosTestImpl, PosTestImpl, Unit>, inner: TestCaseBuilder<T>.() -> Unit) {
        val start = this.source.length
        inner(this)
        this.scope.entities.add(expectedCallback, start, this.source.length)
    }

    override fun range(expectedCallback: KFunction2<T, CollectedSubstringTestImpl, Unit>, inner: TestCaseBuilder<T>.() -> Unit) {
        val start = this.source.length
        inner(this)
        this.scope.entities.add(expectedCallback, start, this.source.length)
    }

    override fun range(expectedCallback: KFunction2<T, CollectedSubstringTestImpl, Unit>, string: String) {
        val start = this.source.length
        this.source.append(string)
        this.scope.entities.add(expectedCallback, start, this.source.length)
    }
}