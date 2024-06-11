package ru.landgrafhomyak.icpp.parser.test.environment

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KFunction2
import kotlin.reflect.KFunction3

abstract class TestCaseBuilder<T : Any> {
    abstract operator fun String.unaryPlus()
    abstract fun range(expectedCallback: KFunction3<T, *, *, Any?>, inner: TestCaseBuilder<T>.() -> Unit)
    abstract fun range(expectedCallback: KFunction3<T, *, *, Any?>, string: String)
    abstract fun substr(expectedCallback: KFunction2<T, *, Any?>, inner: TestCaseBuilder<T>.() -> Unit)
    abstract fun substr(expectedCallback: KFunction2<T, *, Any?>, string: String)
    abstract fun symbol(vararg expectedCallbacks: KFunction2<T, *, Any?>, chr: Char)

    /**
     * Position of next symbol
     */
    abstract fun pos(vararg expectedCallbacks: KFunction2<T, *, Any?>)

    abstract fun <S : Any> subObject(kClass: KClass<S>, inner: TestCaseBuilder<S>.() -> Unit)
    inline fun <reified S : Any> subObject(noinline inner: TestCaseBuilder<S>.() -> Unit) =
        this.subObject(S::class, inner)
}


internal class TestCaseBuilderImpl<T : Any>(
    private val source: StringBuilder,
    private val entities: MutableEntityList
) : TestCaseBuilder<T>() {
    private var isChild = false

    private fun assertNoChild() {
        if (this.isChild)
            throw IllegalStateException("This scope is not deepest, close all child scopes before")
    }

    private inline fun childScope(action: () -> Unit) {
        this.isChild = true
        action()
        this.isChild = false
    }

    override fun String.unaryPlus() {
        this@TestCaseBuilderImpl.assertNoChild()
        this@TestCaseBuilderImpl.source.append(this@unaryPlus)
    }

    override fun pos(vararg expectedCallbacks: KFunction2<T, *, Any?>) {
        this.assertNoChild()
        val pos = this.source.length
        for (k in expectedCallbacks)
            this.entities.add(k, pos)
    }

    override fun <S : Any> subObject(kClass: KClass<S>, inner: TestCaseBuilder<S>.() -> Unit) {
        this.assertNoChild()
        this.childScope {
            val subEntities = EntityArrayList()
            this.entities.add(kClass, subEntities)
            val child = TestCaseBuilderImpl<S>(this.source, subEntities)
            inner(child)
        }
    }

    override fun symbol(vararg expectedCallbacks: KFunction2<T, *, Any?>, chr: Char) {
        this.assertNoChild()
        val pos = this.source.length
        for (k in expectedCallbacks)
            this.entities.add(k, pos)
        this.source.append(chr)
    }

    private fun addRange(expectedCallback: KFunction<*>, string: String) {
        val pos1 = this.source.length
        this.source.append(string)
        this.entities.add(expectedCallback, pos1, this.source.length)
    }

    override fun substr(expectedCallback: KFunction2<T, *, Any?>, string: String) {
        this.assertNoChild()
        this.addRange(expectedCallback, string)
    }

    override fun range(expectedCallback: KFunction3<T, *, *, Any?>, string: String) {
        this.assertNoChild()
        this.addRange(expectedCallback, string)
    }

    private fun addRange(expectedCallback: KFunction<*>, inner: TestCaseBuilder<T>.() -> Unit) {
        val pos1 = this.source.length
        val child = TestCaseBuilderImpl<T>(this.source, this.entities)
        inner(child)
        this.entities.add(expectedCallback, pos1, this.source.length)
    }

    override fun substr(expectedCallback: KFunction2<T, *, Any?>, inner: TestCaseBuilder<T>.() -> Unit) {
        this.assertNoChild()
        this.childScope {
            this.addRange(expectedCallback, inner)
        }
    }


    override fun range(expectedCallback: KFunction3<T, *, *, Any?>, inner: TestCaseBuilder<T>.() -> Unit) {
        this.assertNoChild()
        this.childScope {
            this.addRange(expectedCallback, inner)
        }
    }
}
