package ru.landgrafhomyak.icpp.parser.predicates

import ru.landgrafhomyak.icpp.parser.environment.Predicate

object OperatorSymbolPredicate : Predicate {
    @Suppress("RemoveRedundantQualifierName")
    override fun check(c: Char): Boolean = when (c) {
        '<', '#' -> true
        else -> OperatorSymbolPredicate.checkFirstChar(c)
    }

    @JvmStatic
    fun checkFirstChar(c: Char) = when (c) {
        '!', '@', '$', '%', '^', '&', '*', '~', '|', '>', '-', '+', '=', '/', '?' -> true
        else -> false
    }
}