package ru.landgrafhomyak.icpp.parser.environment


interface SourceStream<out P : Pos, out CS : CollectedSubstring> {
    suspend fun collect(predicate: Predicate): CS

    suspend fun skip(predicate: Predicate) {
        this.collect(predicate)
    }

    val pos: P

    val isEnded: Boolean

    /**
     * true if ended
     */
    suspend fun move(): Boolean

    val current: Char
}


