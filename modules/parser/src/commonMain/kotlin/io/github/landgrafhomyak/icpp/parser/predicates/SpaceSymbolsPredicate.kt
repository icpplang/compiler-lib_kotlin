package io.github.landgrafhomyak.icpp.parser.predicates

import io.github.landgrafhomyak.icpp.parser.environment.Predicate

object SpaceSymbolsPredicate: Predicate {
    override fun check(c: Char): Boolean = c == ' ' || c == '\t' || c == '\u000B'
}