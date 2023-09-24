package io.github.landgrafhomyak.icpp.parser.test.environment

import io.github.landgrafhomyak.icpp.parser.environment.CollectedSubstring
import io.github.landgrafhomyak.icpp.parser.environment.Pos
import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.reflect
import kotlin.test.Test

internal class ManualMockTest {
    private class ScopeAsserterProxy(private val expectedKey: Any) : InvocationHandler {
        override fun invoke(proxy: Any, method: Method, args: Array<Any>?): Any? {
            when (method) {
                ScopeAsserter::addPos.javaMethod -> {}
                ScopeAsserter::addRange.javaMethod -> {}
                ScopeAsserter::getChildAsserter.javaMethod -> {}
                else -> return null
            }

            assert(this.expectedKey == args!![0])
            return null
        }

    }

    private fun callFunction(fn: KFunction<*>, self: Any) {
        val args = fn.parameters.mapIndexed { i, pt ->
            if (i == 0) return@mapIndexed self
            when (val t = pt.type.classifier) {
                is KTypeParameter -> when {
                    Pos::class in t.upperBounds.map(KType::classifier) -> PosTestImpl(0)
                    CollectedSubstring::class in t.upperBounds.map(KType::classifier) -> CollectedSubstringTestImpl(charArrayOf(), 0, 0)
                    else -> throw IllegalArgumentException("Can't mock ${pt.type.classifier}")
                }
                Pos::class -> PosTestImpl(0)
                CollectedSubstring::class -> CollectedSubstringTestImpl(charArrayOf(), 0, 0)
                Int::class -> 0
                Char::class -> '\u0000'
                else -> throw IllegalArgumentException("Can't mock ${pt.type.classifier}")
            }
        }.toTypedArray()

        try {
            fn.call(*args)
        } catch (e: InvocationTargetException) {
            throw e.cause!!
        }
    }

    @Test
    fun testKeys() {
        for ((i, c) in InterfaceMapping.mocksIterator()) {
            for (fn in i.declaredMemberFunctions) {
                val proxyMeta = ScopeAsserterProxy(fn)
                val proxy =
                    Proxy.newProxyInstance(
                        ScopeAsserter::class.java.classLoader,
                        arrayOf(ScopeAsserter::class.java),
                        proxyMeta
                    ) as ScopeAsserter
                val instance = c(proxy)
                this.callFunction(fn, instance)
            }
        }

    }
}