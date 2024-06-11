package ru.landgrafhomyak.icpp.parser.predicates

import ru.landgrafhomyak.icpp.parser.environment.Predicate

object NameSymbolsPredicate : Predicate {
    override fun check(c: Char): Boolean = c in 'a'..'z' || c in 'A'..'Z' || c in '0'..'9' || c == '_'

    @JvmStatic
    fun checkFirstChar(c: Char): Boolean = c in 'a'..'z' || c in 'A'..'Z' || c == '_'
}