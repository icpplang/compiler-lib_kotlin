package io.github.landgrafhomyak.icpp.parser.predicates

import io.github.landgrafhomyak.icpp.parser.environment.Predicate

object NonSpaceSymbolsPredicate : Predicate {
    override fun check(c: Char): Boolean = !SpaceSymbolsMultilinePredicate.check(c)
}