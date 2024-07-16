package ru.landgrafhomyak.icpp.ast.builders
import ru.landgrafhomyak.icpp.parser.environment.BuilderState

/**
 * Internal annotation to avoid generating mocks for utility and third-party interfaces inherited from [BuilderState].
 *
 * Can be referenced by any users for generating mocks.
 */
@Suppress("ClassName")
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class _IcppParserState_AutoGenerateMock
