package ru.landgrafhomyak.icpp.parser.environment

interface Predicate {
    fun check(c: Char): Boolean
}