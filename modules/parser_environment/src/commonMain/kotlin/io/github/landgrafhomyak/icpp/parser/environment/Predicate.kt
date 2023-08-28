package io.github.landgrafhomyak.icpp.parser.environment

interface Predicate {
    fun check(c: Char): Boolean
}