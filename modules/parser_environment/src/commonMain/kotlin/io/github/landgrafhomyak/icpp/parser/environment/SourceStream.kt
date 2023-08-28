package io.github.landgrafhomyak.icpp.parser.environment


interface SourceStream<CS : CollectedSubstring, P : Pos> {
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


