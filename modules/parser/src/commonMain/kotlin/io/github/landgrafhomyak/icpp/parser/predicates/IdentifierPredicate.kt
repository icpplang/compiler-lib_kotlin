package io.github.landgrafhomyak.icpp.parser.predicates

import io.github.landgrafhomyak.icpp.parser.environment.Predicate

object IdentifierPredicate: Predicate {
    override fun check(c: Char): Boolean = c in 'a'..'z' || c in 'A'..'Z' || c in '0'..'9' || c == '_'

    fun checkFirstChar(c: Char): Boolean =  c in 'a'..'z' || c in 'A'..'Z' || c == '_'
}