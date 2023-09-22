package io.github.landgrafhomyak.icpp.parser.test.environment

import io.github.landgrafhomyak.icpp.parser.environment.Pos
import kotlin.jvm.JvmInline

@JvmInline
value class PosTestImpl internal constructor(internal val value: Int) : Pos

internal operator fun Int.compareTo(wrapper: PosTestImpl) = this.compareTo(wrapper.value)

internal operator fun PosTestImpl.compareTo(raw: Int) = this.value.compareTo(raw)

internal operator fun PosTestImpl.compareTo(wrapper: PosTestImpl) = this.value.compareTo(wrapper.value)
