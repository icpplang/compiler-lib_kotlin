package ru.landgrafhomyak.icpp.parser.tests.runtime.api

import ru.landgrafhomyak.icpp.parser.environment.BuilderState
import ru.landgrafhomyak.icpp.parser.environment.CollectedSubstring
import ru.landgrafhomyak.icpp.parser.environment.Pos
import ru.landgrafhomyak.icpp.parser.environment.SourceStream

interface EntryPoint<S: BuilderState<*, *, Nothing?>> {
    fun <P : Pos, CS : CollectedSubstring> launch(stream: SourceStream<P, CS>, factory: ru.landgrafhomyak.icpp.parser.tests.runtime.api.Tester)
}