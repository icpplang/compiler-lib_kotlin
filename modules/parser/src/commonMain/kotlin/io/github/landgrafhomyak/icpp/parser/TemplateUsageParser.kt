package io.github.landgrafhomyak.icpp.parser

import io.github.landgrafhomyak.icpp.ast.builders.TemplateUsageBuilder
import io.github.landgrafhomyak.icpp.parser.environment.CollectedSubstring
import io.github.landgrafhomyak.icpp.parser.environment.Pos
import io.github.landgrafhomyak.icpp.parser.environment.SourceStream

object TemplateUsageParser {
    @JvmStatic
    suspend fun <CS : CollectedSubstring, P : Pos> parseTemplateArgs(
        stream: SourceStream<CS, P>,
        builder: TemplateUsageBuilder<CS, P>
    ) {
        TODO()
    }
}