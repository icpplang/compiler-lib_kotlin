package ru.landgrafhomyak.icpp.parser.test.environment

import ru.landgrafhomyak.icpp.parser.environment.CollectedSubstring

class CollectedSubstringTestImpl(
    private val source: CharArray,
    val startPos: Int,
    val endPos: Int
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