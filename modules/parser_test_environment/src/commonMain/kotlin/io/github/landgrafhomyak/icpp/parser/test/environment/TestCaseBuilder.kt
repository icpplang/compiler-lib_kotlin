package io.github.landgrafhomyak.icpp.parser.test.environment

import kotlin.reflect.KClass
import kotlin.reflect.KFunction2
import kotlin.reflect.KFunction3

abstract class TestCaseBuilder<T : Any> {
    abstract operator fun String.unaryPlus()
    abstract fun range(expectedCallback: KFunction3<T, PosTestImpl, PosTestImpl, Unit>, inner: TestCaseBuilder<T>.() -> Unit)
    abstract fun range(expectedCallback: KFunction3<T, PosTestImpl, PosTestImpl, Unit>, string: String)
    abstract fun range(expectedCallback: KFunction2<T, CollectedSubstringTestImpl, Unit>, inner: TestCaseBuilder<T>.() -> Unit)
    abstract fun range(expectedCallback: KFunction2<T,CollectedSubstringTestImpl, Unit>, string: String)
    abstract fun symbol(vararg expectedCallbacks: KFunction2<T, PosTestImpl, Unit>, chr: Char)
    abstract fun pos(vararg expectedCallbacks: KFunction2<Any, PosTestImpl, Unit>)
    abstract fun <S : Any> subObject(kClass: KClass<S>, inner: TestCaseBuilder<S>.() -> Unit)
    inline fun <reified S : Any> subObject(noinline inner: TestCaseBuilder<S>.() -> Unit) =
        this.subObject(S::class, inner)
}


