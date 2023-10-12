package io.github.landgrafhomyak.icpp.parser

import io.github.landgrafhomyak.icpp.parser.test.environment.SourceStreamTestImpl
import io.github.landgrafhomyak.icpp.parser.test.environment.formatTestSourceRange
import io.github.landgrafhomyak.icpp.parser.test.environment.runParserTestCoro
import kotlin.test.Test

class IdentifierTest {
    private fun testIdentifierParser(identifier: String = "", noise: String = "", matchExpected: Boolean = true) {
        val source = (identifier + noise).toCharArray()
        val stream = SourceStreamTestImpl(source)
        val result = runParserTestCoro { parseIdentifier(stream) }
        if (result != null) {
            if (!matchExpected)
                throw AssertionError("Unexpected identifier match: ${formatTestSourceRange(source, result.startPos, result.endPos)}")

            if (result.startPos != 0)
                throw AssertionError("Expected identifier start at start of source, got ${result.startPos}: ${formatTestSourceRange(source, result.startPos, result.endPos)}")

            @Suppress("KotlinConstantConditions")
            if (result.endPos != identifier.length)
                throw AssertionError("Wrong identifier end: ${formatTestSourceRange(source, result.startPos, result.endPos)}")
        } else {
            if (matchExpected)
                throw AssertionError("Expected identifier match, got nothing")
        }
    }

    @Test
    fun testNothing() = this.testIdentifierParser(matchExpected = false)

    @Test
    fun testNoise() = this.testIdentifierParser(noise = " pf,pre,pfoe,rfpo,repfe", matchExpected = false)

    @Test
    fun testStartsWithDigit() = this.testIdentifierParser(noise = "3PO", matchExpected = false)

    @Test
    fun testNormal() = this.testIdentifierParser("C3PO")

    @Test
    fun testNormalWithNoise() = this.testIdentifierParser("C3PO", " qqwerqwe")

    @Test
    fun testNormalWithNoiseSpecialChars() {
        this.testIdentifierParser("C3PO", ".")
        this.testIdentifierParser("C3PO", "$")
        this.testIdentifierParser("C3PO", "[")
        this.testIdentifierParser("C3PO", "(")
        this.testIdentifierParser("C3PO", "<")
    }
}