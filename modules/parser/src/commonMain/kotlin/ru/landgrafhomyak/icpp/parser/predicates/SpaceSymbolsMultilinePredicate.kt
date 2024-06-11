package ru.landgrafhomyak.icpp.parser.predicates

import ru.landgrafhomyak.icpp.parser.environment.Predicate

object SpaceSymbolsMultilinePredicate : Predicate {
    override fun check(c: Char): Boolean = c == ' ' || c == '\t' || c == '\n'
}