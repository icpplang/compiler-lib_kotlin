@file:JvmName("DslManualMockKt")

package io.github.landgrafhomyak.icpp.parser.test.environment

import kotlin.reflect.KClass

internal actual fun <T : Any> mock(i: KClass<T>, meta: ScopeAsserter): T = InterfaceMapping.construct(i, meta)

