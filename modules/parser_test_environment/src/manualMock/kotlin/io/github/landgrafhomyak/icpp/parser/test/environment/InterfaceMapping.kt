package io.github.landgrafhomyak.icpp.parser.test.environment

import io.github.landgrafhomyak.icpp.ast.builders.QualnameUsageBuilder
import io.github.landgrafhomyak.icpp.parser.test.environment.mockimpl.QualnameAsserter
import kotlin.reflect.KClass
import kotlin.reflect.KFunction1

@Suppress("REDUNDANT_PROJECTION")
private typealias ConstructorSignature<I> = KFunction1<ScopeAsserter, out I>

internal object InterfaceMapping {
    private operator fun get(i: KClass<*>): ConstructorSignature<Any> =
        this.map[i] ?: throw IllegalArgumentException("Mock for interface ${i.qualifiedName} not set")


    @Suppress("UNCHECKED_CAST")
    fun <I : Any> constructor(i: KClass<I>): ConstructorSignature<I> =
        this[i] as ConstructorSignature<I>

    @Suppress("UNCHECKED_CAST")
    fun <I : Any> construct(i: KClass<I>, scopeAsserter: ScopeAsserter): I =
        this[i](scopeAsserter) as I

    private val map = Builder.build {
        add<QualnameUsageBuilder<*, *>>(::QualnameAsserter)
    }


    private object Builder {
        abstract class Abc {
            inline fun <reified I : Any> add(constructor: ConstructorSignature<I>) = this.add(I::class, constructor)

            abstract fun <I : Any> add(i: KClass<I>, constructor: ConstructorSignature<I>)
        }

        private class Impl(private val map: MutableMap<KClass<*>, ConstructorSignature<Any>>) : Abc() {
            override fun <I : Any> add(i: KClass<I>, constructor: ConstructorSignature<I>) {
                if (!i.java.isInterface)
                    throw IllegalArgumentException()

                if (i in this.map)
                    throw IllegalArgumentException()

                this.map[i] = constructor
            }
        }

        @JvmStatic
        inline fun build(init: Abc.() -> Unit): Map<KClass<*>, ConstructorSignature<Any>> {
            val map = HashMap<KClass<*>, ConstructorSignature<Any>>()
            val ctx = Impl(map)
            init(ctx)
            return map
        }
    }

    fun mocksIterator(): Iterator<Pair<KClass<*>, ConstructorSignature<Any>>> = this.map
        .entries
        .map { (i, c) -> i to c }
        .iterator()
}