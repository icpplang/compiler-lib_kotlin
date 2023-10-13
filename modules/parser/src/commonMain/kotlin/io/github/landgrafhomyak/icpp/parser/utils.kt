package io.github.landgrafhomyak.icpp.parser

internal fun badParser(message: String): Nothing = throw BadParserError(message)