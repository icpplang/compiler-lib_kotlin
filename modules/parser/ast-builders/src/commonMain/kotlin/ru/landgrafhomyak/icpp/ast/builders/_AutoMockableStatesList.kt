package ru.landgrafhomyak.icpp.ast.builders

import kotlin.reflect.KClass
import ru.landgrafhomyak.icpp.parser.environment.BuilderState

@Suppress("ClassName")
/**
 * Reflection object with a list of states in this module for which mocks can be generated automatically.
 *
 * Generated at compile time, so actually doesn't use reflection API.
 */
object _AutoMockableStatesList : Collection<KClass<BuilderState<*, *, *>>> by _AutoMockableStatesList_data