package io.github.landgrafhomyak.icpp.parser.test.environment

import io.github.landgrafhomyak.icpp.parser.environment.Pos
import kotlin.jvm.JvmInline

@JvmInline
value class PosTestImpl internal constructor(internal val value: Int) : Pos