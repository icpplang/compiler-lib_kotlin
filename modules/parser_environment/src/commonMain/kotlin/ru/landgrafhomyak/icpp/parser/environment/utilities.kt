package ru.landgrafhomyak.icpp.parser.environment

operator fun Predicate.contains(c: Char) = this.check(c)

@Suppress("NOTHING_TO_INLINE")
inline fun <P : Pos, CS : CollectedSubstring> SourceStream<P, CS>.currentIs(vararg chars: Char) =
    this.current in chars
