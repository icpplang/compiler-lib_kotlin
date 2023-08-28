package io.github.landgrafhomyak.icpp.parser.test.environment

import io.github.landgrafhomyak.icpp.parser.environment.CollectedSubstring

class CollectedSubstringTestImpl(
    private val source: CharArray,
    private val startPos: Int,
    private val endPos: Int
) : CollectedSubstring {
    override fun compareKeyword(kw: String): Boolean {
        var sPos = this.startPos
        var kwPos = 0
        while (sPos < this.endPos) {
            if (this.source[sPos] != kw[kwPos])
                return false
            sPos++
            kwPos++
        }
        return true
    }
}