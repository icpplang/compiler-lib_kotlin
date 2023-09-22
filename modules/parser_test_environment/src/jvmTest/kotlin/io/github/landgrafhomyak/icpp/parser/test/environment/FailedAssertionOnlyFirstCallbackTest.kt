package io.github.landgrafhomyak.icpp.parser.test.environment

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.jvm.javaMethod
import kotlin.test.Test
import kotlin.test.assertEquals
import java.lang.reflect.Proxy
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

internal class FailedAssertionOnlyFirstCallbackTest {
    private object Mocks {
        private object FailedAssertionCallbackScopeSentinel : InvocationHandler {
            override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
                throw AssertionError("Callback passed to ScopeAsserter mustn't be called by wrapper callback")
            }
        }

        private object NoAccessEntityList : MutableEntityList {
            private fun raise(): Nothing = throw AssertionError("Entity list passed to scope asserter mustn't be accessed by wrapper")
            override fun add(key: KFunction<*>, pos1: Int) = this.raise()

            override fun add(key: KFunction<*>, pos1: Int, pos2: Int) = this.raise()

            override fun add(key: KClass<*>, subEntities: EntityList) = this.raise()

            override fun iterator(): Iterator<EntityList.Entity> = this.raise()

            override fun flatIterator(): Iterator<EntityList.Entity> = this.raise()
        }

        private val scopeAsserter = ScopeAsserter(
            Proxy.newProxyInstance(
                FailedAssertionCallback::class.java.classLoader,
                arrayOf(FailedAssertionCallback::class.java),
                FailedAssertionCallbackScopeSentinel
            ) as FailedAssertionCallback,
            EmptyEntityList,
            NoAccessEntityList
        )

        @JvmStatic
        fun callFunction(fn: KFunction<*>, self: FailedAssertionCallback) {
            val args = fn.parameters.map { pt ->
                when (pt.type.classifier) {
                    FailedAssertionCallback::class -> self
                    ScopeAsserter::class -> this.scopeAsserter
                    KClass::class -> FailedAssertionOnlyFirstCallbackTest::class
                    KFunction::class -> ::callFunction
                    Int::class -> 0
                    else -> throw IllegalArgumentException("Can't mock ${pt.type.classifier}")
                }
            }.toTypedArray()

            fn.call(*args)
        }


    }

    private class FailedAssertionCallbackProxy(val expectedMethod: Method) : InvocationHandler {
        public var invocationsCount = 0
            private set

        override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
            assertEquals(
                this.expectedMethod,
                method,
                "Expected forward to method `${this.expectedMethod.name}`, got `${method.name}`"
            )
            this.invocationsCount++
            return null
        }
    }

    @Test
    fun testForward() {
        for (fn in FailedAssertionCallback::class.declaredMemberFunctions) {
            val targetMeta = FailedAssertionCallbackProxy(fn.javaMethod!!)
            val target = Proxy.newProxyInstance(
                FailedAssertionCallback::class.java.classLoader,
                arrayOf(FailedAssertionCallback::class.java),
                targetMeta
            ) as FailedAssertionCallback
            val wrapper = FailedAssertionOnlyFirstCallback(target)

            Mocks.callFunction(fn, wrapper)

            assert(targetMeta.invocationsCount == 1) { "Expected 1 call, got ${targetMeta.invocationsCount}" }
        }
    }
}