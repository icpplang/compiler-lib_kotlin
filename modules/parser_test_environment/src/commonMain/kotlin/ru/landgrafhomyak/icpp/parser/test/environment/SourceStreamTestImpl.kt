package ru.landgrafhomyak.icpp.parser.test.environment

import ru.landgrafhomyak.icpp.parser.environment.Predicate
import ru.landgrafhomyak.icpp.parser.environment.SourceStream

class SourceStreamTestImpl(private val source: CharArray) : SourceStream<CollectedSubstringTestImpl, PosTestImpl> {
    override suspend fun collect(predicate: Predicate): CollectedSubstringTestImpl {
        val start = this._pos
        while (this._pos < this.source.size && predicate.check(this.source[this._pos]))
            this._pos++

        return CollectedSubstringTestImpl(this.source, start, this._pos)
    }

    override suspend fun skip(predicate: Predicate) {
        while (this._pos < this.source.size && predicate.check(this.source[this._pos]))
            this._pos++
    }

    private var _pos = 0
    override val pos: PosTestImpl
        get() = PosTestImpl(this._pos)
    override val isEnded: Boolean
        get() = this._pos >= this.source.size

    override suspend fun move(): Boolean {
        if (this._pos >= this.source.size)
            return true
        this._pos++
        return false
    }

    override val current: Char
        get() {
            if (this._pos >= this.source.size)
                throw IllegalStateException("Stream is ended")
            return this.source[this._pos]
        }
}