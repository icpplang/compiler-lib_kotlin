package ru.landgrafhomyak.icpp.parser

internal fun badParser(message: String): Nothing = throw ru.landgrafhomyak.icpp.parser.BadParserError(message)