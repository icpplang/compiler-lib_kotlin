package ru.landgrafhomyak.icpp.parser_tester.api

abstract class TestGroup(
    vararg val requires: TestGroup
) {
    abstract fun TestsInitializerRootScope.configureTests()

    init {
        if (this::class.objectInstance !== this)
            TODO()
    }
}