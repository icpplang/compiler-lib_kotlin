package ru.landgrafhomyak.icpp.parser

import ru.landgrafhomyak.icpp.ast.builders.MultilineCommentBuilder
import ru.landgrafhomyak.icpp.parser.environment.CollectedSubstring
import ru.landgrafhomyak.icpp.parser.environment.Pos
import ru.landgrafhomyak.icpp.parser.environment.Predicate
import ru.landgrafhomyak.icpp.parser.environment.SourceStream

object MultilineCommentParser {
    private object NonCommentSymbolsPredicate : Predicate {
        override fun check(c: Char): Boolean {
            return c != '/' && c != '*'
        }
    }

    suspend fun <P : Pos, CS : CollectedSubstring> continueParsingMultilineComment(
        stream: SourceStream<P, CS>,
        builder: MultilineCommentBuilder<P, CS>
    ) {
        if (stream.isEnded) {
            builder.unclosedComment()
            builder.finalize()
            return
        }

        var levels = 0uL

        while (true) {
            stream.skip(NonCommentSymbolsPredicate)

            when (stream.current) {
                '/' -> {
                    if (stream.move()) {
                        builder.unclosedComment()
                        builder.finalize()
                        return
                    }
                    if (stream.current == '*') {
                        if (levels == ULong.MAX_VALUE)
                            throw RuntimeException("Multiline comments recursion depth reached, how about using version control system?")
                        levels++
                    }
                    continue
                }

                '*' -> {
                    if (stream.move()) {
                        builder.unclosedComment()
                        builder.finalize()
                        return
                    }
                    if (stream.current == '/') {
                        if (levels == 0uL) {
                            stream.move()
                            builder.closingSequence(stream.pos)
                            builder.finalize()
                            return
                        }
                        levels--
                    }
                    continue
                }

                else -> {
                    if (stream.move()) {
                        builder.unclosedComment()
                        builder.finalize()
                        return
                    }
                    continue
                }
            }
        }
    }
}