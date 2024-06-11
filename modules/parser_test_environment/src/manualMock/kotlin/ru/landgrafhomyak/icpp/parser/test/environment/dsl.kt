@file:JvmName("DslManualMockKt")

package ru.landgrafhomyak.icpp.parser.test.environment

import kotlin.reflect.KClass

internal actual fun <T : Any> mock(i: KClass<T>, meta: ScopeAsserter): T = InterfaceMapping.construct(i, meta)

