package ru.landgrafhomyak.icpp.parser

internal fun badParser(message: String): Nothing = throw BadParserError(message)
internal fun badParserExpectedCharBadStreamEnded(expectedCharMessage: String): Nothing = throw BadParserError("This function must be called after checking that current char is ${expectedCharMessage}, but stream is ended")