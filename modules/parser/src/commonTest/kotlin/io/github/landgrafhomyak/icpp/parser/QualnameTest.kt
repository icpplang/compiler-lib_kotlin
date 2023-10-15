package io.github.landgrafhomyak.icpp.parser

import io.github.landgrafhomyak.icpp.ast.builders.QualnameBuilder
import io.github.landgrafhomyak.icpp.parser.test.environment.CollectedSubstringTestImpl
import io.github.landgrafhomyak.icpp.parser.test.environment.ParserTest
import io.github.landgrafhomyak.icpp.parser.test.environment.PosTestImpl
import io.github.landgrafhomyak.icpp.parser.test.environment.TestCaseBuilder
import kotlin.test.Test

class QualnameTest {
    private fun ParserTest.runQualnameTest(consumeSpaces: Boolean, consumeInvalidIdentifier: Boolean, testBuilder: TestCaseBuilder<QualnameBuilder<*, *>>.() -> Unit) =
        this
            .build(testBuilder)
            .run<QualnameBuilder<CollectedSubstringTestImpl, PosTestImpl>> { stream, builder ->
                IdentifierParsers.parseQualname(
                    stream,
                    builder,
                    consumeSpaces = consumeSpaces,
                    consumeInvalidIdentifier = consumeInvalidIdentifier
                )
            }

    @Test
    fun testValidOneLevel() = ParserTest
        .runQualnameTest(consumeSpaces = false, consumeInvalidIdentifier = false) {
            substr(QualnameBuilder<*, *>::addLevel, "abc")
        }

    @Test
    fun testValidTwoLevels() = ParserTest
        .runQualnameTest(consumeSpaces = false, consumeInvalidIdentifier = false) {
            substr(QualnameBuilder<*, *>::addLevel, "abc")
            range(QualnameBuilder<*, *>::separator, IdentifierParsers.SEPARATOR)
            substr(QualnameBuilder<*, *>::addLevel, "def")
        }

    @Test
    fun testValidTwoLevelsWithSpacesOff1() = ParserTest
        .runQualnameTest(consumeSpaces = false, consumeInvalidIdentifier = false) {
            substr(QualnameBuilder<*, *>::addLevel, "abc")
            +" "
            +IdentifierParsers.SEPARATOR
            +"def"
        }

    @Test
    fun testValidTwoLevelsWithSpacesOff2() = ParserTest
        .runQualnameTest(consumeSpaces = false, consumeInvalidIdentifier = false) {
            substr(QualnameBuilder<*, *>::addLevel, "abc")
            range(QualnameBuilder<*, *>::separator, IdentifierParsers.SEPARATOR)
            pos(QualnameBuilder<*, *>::nothingAfterSeparator)
            +" "
            +"def"
        }

    @Test
    fun testValidTwoLevelsWithSpacesOn1() = ParserTest
        .runQualnameTest(consumeSpaces = true, consumeInvalidIdentifier = false) {
            substr(QualnameBuilder<*, *>::addLevel, "abc")
            range(QualnameBuilder<*, *>::spacesBetweenSeparatorAndName, "   ")
            range(QualnameBuilder<*, *>::separator, IdentifierParsers.SEPARATOR)
            substr(QualnameBuilder<*, *>::addLevel, "def")
        }

    @Test
    fun testValidTwoLevelsWithSpacesOn2() = ParserTest
        .runQualnameTest(consumeSpaces = true, consumeInvalidIdentifier = false) {
            substr(QualnameBuilder<*, *>::addLevel, "abc")
            range(QualnameBuilder<*, *>::separator, IdentifierParsers.SEPARATOR)
            range(QualnameBuilder<*, *>::spacesBetweenSeparatorAndName, "   ")
            substr(QualnameBuilder<*, *>::addLevel, "def")
        }

    @Test
    fun testValidTwoLevelsWithSpacesOn3() = ParserTest
        .runQualnameTest(consumeSpaces = true, consumeInvalidIdentifier = false) {
            substr(QualnameBuilder<*, *>::addLevel, "abc")
            range(QualnameBuilder<*, *>::spacesBetweenSeparatorAndName, "   ")
            range(QualnameBuilder<*, *>::separator, IdentifierParsers.SEPARATOR)
            range(QualnameBuilder<*, *>::spacesBetweenSeparatorAndName, "   ")
            substr(QualnameBuilder<*, *>::addLevel, "def")
        }

    @Test
    fun testInvalidSecondOff() = ParserTest
        .runQualnameTest(consumeSpaces = false, consumeInvalidIdentifier = false) {
            substr(QualnameBuilder<*, *>::addLevel, "abc")
            range(QualnameBuilder<*, *>::separator, IdentifierParsers.SEPARATOR)
            pos(QualnameBuilder<*, *>::nothingAfterSeparator)
            +"0def"
        }

    @Test
    fun testInvalidSecondOn() = ParserTest
        .runQualnameTest(consumeSpaces = false, consumeInvalidIdentifier = true) {
            substr(QualnameBuilder<*, *>::addLevel, "abc")
            range(QualnameBuilder<*, *>::separator, IdentifierParsers.SEPARATOR)
            range(QualnameBuilder<*, *>::addBadLevel, "0def")
        }

    @Test
    fun testInvalidSecondOnWithSpacesOff1() = ParserTest
        .runQualnameTest(consumeSpaces = false, consumeInvalidIdentifier = true) {
            substr(QualnameBuilder<*, *>::addLevel, "abc")
            +" "
            +IdentifierParsers.SEPARATOR
            +"0def"
        }

    @Test
    fun testInvalidSecondOnWithSpacesOff2() = ParserTest
        .runQualnameTest(consumeSpaces = false, consumeInvalidIdentifier = true) {
            substr(QualnameBuilder<*, *>::addLevel, "abc")
            range(QualnameBuilder<*, *>::separator, IdentifierParsers.SEPARATOR)
            pos(QualnameBuilder<*, *>::nothingAfterSeparator)
            +" "
            +"0def"
        }

    @Test
    fun testInvalidSecondOnWithSpacesOff3() = ParserTest
        .runQualnameTest(consumeSpaces = false, consumeInvalidIdentifier = true) {
            substr(QualnameBuilder<*, *>::addLevel, "abc")
            +" "
            +IdentifierParsers.SEPARATOR
            +" "
            +"0def"
        }

    @Test
    fun testInvalidSecondOnWithSpacesOn1() = ParserTest
        .runQualnameTest(consumeSpaces = true, consumeInvalidIdentifier = true) {
            substr(QualnameBuilder<*, *>::addLevel, "abc")
            range(QualnameBuilder<*, *>::spacesBetweenSeparatorAndName, "   ")
            range(QualnameBuilder<*, *>::separator, IdentifierParsers.SEPARATOR)
            range(QualnameBuilder<*, *>::addBadLevel, "0def")
        }

    @Test
    fun testInvalidSecondOnWithSpacesOn2() = ParserTest
        .runQualnameTest(consumeSpaces = true, consumeInvalidIdentifier = true) {
            substr(QualnameBuilder<*, *>::addLevel, "abc")
            range(QualnameBuilder<*, *>::separator, IdentifierParsers.SEPARATOR)
            range(QualnameBuilder<*, *>::spacesBetweenSeparatorAndName, "   ")
            range(QualnameBuilder<*, *>::addBadLevel, "0def")
        }

    @Test
    fun testInvalidSecondOnWithSpacesOn3() = ParserTest
        .runQualnameTest(consumeSpaces = true, consumeInvalidIdentifier = true) {
            substr(QualnameBuilder<*, *>::addLevel, "abc")
            range(QualnameBuilder<*, *>::spacesBetweenSeparatorAndName, "   ")
            range(QualnameBuilder<*, *>::separator, IdentifierParsers.SEPARATOR)
            range(QualnameBuilder<*, *>::spacesBetweenSeparatorAndName, "   ")
            range(QualnameBuilder<*, *>::addBadLevel, "0def")
        }
    @Test
    fun testOneInvalid() = ParserTest
        .runQualnameTest(consumeSpaces = false, consumeInvalidIdentifier = true) {
            range(QualnameBuilder<*, *>::addBadLevel, "0abc")
        }
}
