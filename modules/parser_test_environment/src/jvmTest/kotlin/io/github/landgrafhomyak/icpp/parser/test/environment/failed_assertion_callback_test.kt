package io.github.landgrafhomyak.icpp.parser.test.environment

import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import kotlin.reflect.jvm.javaMethod
import kotlin.test.Test
import kotlin.test.assertEquals
import java.lang.reflect.Proxy
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberFunctions

private val methodsToTest =
    FailedAssertionCallback::class
        .memberFunctions
        .filter { fn -> fn.hasAnnotation<FailedAssertionCallback_NeedTest>() }
        .toTypedArray()
        .asList()

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
        FailedAssertionOnlyFirstCallbackTest::class,
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
                KFunction::class -> Any::toString
                Int::class -> 0
                else -> throw IllegalArgumentException("Can't mock ${pt.type.classifier}")
            }
        }.toTypedArray()

        try {
            fn.call(*args)
        } catch (e: InvocationTargetException) {
            throw e.cause!!
        }
    }
}

@Suppress("TestFunctionName")
private fun FAC_createProxy(meta: InvocationHandler): FailedAssertionCallback = Proxy.newProxyInstance(
    FailedAssertionCallback::class.java.classLoader,
    arrayOf(FailedAssertionCallback::class.java),
    meta
) as FailedAssertionCallback


private class FailedAssertionCallbackProxy(val expectedMethod: Method) : InvocationHandler {
    var invocationsCount = 0
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


internal class FailedAssertionOnlyFirstCallbackTest {

    @Test
    fun testForward() {
        for (fn in methodsToTest) {
            val targetMeta = FailedAssertionCallbackProxy(fn.javaMethod!!)
            val target = FAC_createProxy(targetMeta)
            val wrapper = FailedAssertionOnlyFirstCallback(target)

            Mocks.callFunction(fn, wrapper)

            assert(targetMeta.invocationsCount == 1) { "Expected 1 call, got ${targetMeta.invocationsCount}" }
        }
    }
}

private val TOFAC_instance = ThrowOnFailedAssertionCallback("test_source".toCharArray())

internal class ThrowOnFailedAssertionCallbackTest {
    @Test
    fun testThrow() {

        for (fn in methodsToTest) {
            var isThrown = false
            try {
                Mocks.callFunction(fn, TOFAC_instance)
            } catch (a: AssertionError) {
                isThrown = true
            }

            assert(isThrown) { "Expected AssertionError from method ${fn.name}, got nothing" }
        }
    }
}

internal class ScheduledThrowOnFailedAssertionCallbackTest {


    @Test
    fun testForward() {
        for (fn in methodsToTest) {
            val targetMeta = FailedAssertionCallbackProxy(fn.javaMethod!!)
            val target = FAC_createProxy(targetMeta)
            val wrapper = ScheduledThrowOnFailedAssertionCallback(target)

            Mocks.callFunction(fn, wrapper)

            assert(targetMeta.invocationsCount == 1) { "Expected 1 call, got ${targetMeta.invocationsCount}" }
        }
    }


    private class FailedAssertionCallbackInterceptorProxy(private val wrapped: FailedAssertionCallback) : InvocationHandler {
        var exception: AssertionError? = null
            private set

        override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
            try {
                method.invoke(this.wrapped, *(args ?: arrayOf()))
            } catch (i: InvocationTargetException) {
                i.cause?.let { a -> a as? AssertionError }?.also { a -> this.exception = a }?.also { a -> throw a }
                throw i
            }
            return null
        }
    }

    @Test
    fun testNoErr() {
        val instance = ScheduledThrowOnFailedAssertionCallback(TOFAC_instance)
        try {
            instance.assert()
        } catch (a: AssertionError) {
            assert(false) { "ScheduledThrowOnFailedAssertionCallback thrown unexpected exception" }
        }
    }

    @Test
    fun testLaziness() {
        for (fn in methodsToTest) {
            val interceptor = FailedAssertionCallbackInterceptorProxy(TOFAC_instance)
            val instance = ScheduledThrowOnFailedAssertionCallback(FAC_createProxy(interceptor))

            try {
                Mocks.callFunction(fn, instance)
            } catch (a: AssertionError) {
                assert(false) { "ScheduledThrowOnFailedAssertionCallback didn't intercept exception" }
            }

            var isThrown = false
            try {
                instance.assert()
            } catch (a: AssertionError) {
                assert(interceptor.exception === a) { "ScheduledThrowOnFailedAssertionCallback rethrown another exception instance" }
                isThrown = true
            }
            assert(isThrown) { "ScheduledThrowOnFailedAssertionCallback didn't rethrow expected exception" }
        }
    }

    @Test
    fun testIntegrationWithOnlyFirst() {
        for (fn in methodsToTest) {
            val interceptor = FailedAssertionCallbackInterceptorProxy(TOFAC_instance)
            val lazy = ScheduledThrowOnFailedAssertionCallback(FAC_createProxy(interceptor))
            val instance = FailedAssertionOnlyFirstCallback(lazy)

            try {
                Mocks.callFunction(fn, instance)
            } catch (a: AssertionError) {
                assert(false) { "ScheduledThrowOnFailedAssertionCallback didn't intercept exception" }
            }

            for (rfn in methodsToTest.shuffled()) {
                Mocks.callFunction(rfn, instance)
            }

            var isThrown = false
            try {
                lazy.assert()
            } catch (a: AssertionError) {
                assert(interceptor.exception === a) { "Integration failed, another exception instance thrown" }
                isThrown = true
            }
            assert(isThrown) { "Integration failed, no exception throw" }
        }
    }
}
