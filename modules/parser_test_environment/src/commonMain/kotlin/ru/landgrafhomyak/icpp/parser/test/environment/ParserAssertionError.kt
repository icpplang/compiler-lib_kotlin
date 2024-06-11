package ru.landgrafhomyak.icpp.parser.test.environment

import kotlin.reflect.KClass

class ParserAssertionError(
    message: String,
    val source: String,
    val posInSource: IntRange,
    val frames: Array<out Frame>
) : AssertionError(message) {
    class Frame(
        val builder: KClass<*>
    )
}