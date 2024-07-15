package ru.landgrafhomyak.icpp.parser.predicates

import ru.landgrafhomyak.icpp.parser.environment.Predicate

object NonSpaceSymbolsPredicate : Predicate {
    override fun check(c: Char): Boolean = !SpaceSymbolsMultilinePredicate.check(c)
}