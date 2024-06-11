package ru.landgrafhomyak.icpp.parser.test.environment

import ru.landgrafhomyak.icpp.parser.environment.Pos
import kotlin.jvm.JvmInline

@JvmInline
value class PosTestImpl internal constructor(internal val value: Int) : Pos
