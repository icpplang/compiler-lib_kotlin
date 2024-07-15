package ru.landgrafhomyak.icpp.parser.tests.runtime.api

interface TestCallBuilder {
    fun sourceText(s: String)

    fun sourceText(builder: StringBuilder.() -> Unit) =
        this.sourceText(buildString(builder))

    fun pos()

    fun collectedSubstring(s: String)

    fun collectedSubstring(builder: StringBuilder.() -> Unit) =
        this.collectedSubstring(buildString(builder))

    fun freeArg(o: Any?)
}