package ru.landgrafhomyak.icpp.parser.tests.runtime.api

abstract class TestGroup(
    vararg val requires: ru.landgrafhomyak.icpp.parser.tests.runtime.api.TestGroup
) {
    abstract fun ru.landgrafhomyak.icpp.parser.tests.runtime.api.TestsInitializerRootScope.configureTests()
}