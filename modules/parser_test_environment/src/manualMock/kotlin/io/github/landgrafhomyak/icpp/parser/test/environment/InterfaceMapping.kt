package io.github.landgrafhomyak.icpp.parser.test.environment

import io.github.landgrafhomyak.icpp.ast.builders.AttributesListBuilder
import io.github.landgrafhomyak.icpp.parser.test.environment.mockimpl.AttributesListTestBuilder
import kotlin.reflect.KClass

internal object InterfaceMapping {
    private operator fun get(i: KClass<*>): () -> Any =
        this.map[i] ?: throw IllegalArgumentException("Mock for interface ${i.qualifiedName} not set")


    @Suppress("UNCHECKED_CAST")
    fun <I : Any> constructor(i: KClass<I>): () -> I =
        this[i] as () -> I

    @Suppress("UNCHECKED_CAST")
    fun <I : Any> construct(i: KClass<I>): I =
        this[i]() as I

    private val map = Builder.build {
        add<AttributesListBuilder<*, *>>(::AttributesListTestBuilder)
    }


    private object Builder {
        abstract class Abc {
            inline fun <reified I : Any> add(noinline constructor: () -> I) = this.add(I::class, constructor)

            abstract fun <I : Any> add(i: KClass<I>, constructor: () -> I)
        }

        private class Impl(private val map: MutableMap<KClass<*>, () -> Any>) : Abc() {
            override fun <I : Any> add(i: KClass<I>, constructor: () -> I) {
                if (!i::class.java.isInterface)
                    throw IllegalArgumentException()

                if (i in this.map)
                    throw IllegalArgumentException()

                this.map[i] = constructor
            }
        }

        @JvmStatic
        inline fun build(init: Abc.() -> Unit): Map<KClass<*>, () -> Any> {
            val map = HashMap<KClass<*>, () -> Any>()
            val ctx = Impl(map)
            init(ctx)
            return map
        }
    }
}