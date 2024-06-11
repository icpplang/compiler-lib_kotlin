package ru.landgrafhomyak.icpp.parser.predicates

import ru.landgrafhomyak.icpp.parser.environment.Predicate

object SpaceSymbolsPredicate: Predicate {
    override fun check(c: Char): Boolean = c == ' ' || c == '\t'
}