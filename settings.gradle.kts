rootDir.resolve("./kotlin-version.txt")
    .readText()
    .also { s ->
        if (Regex("^[^\\s:]+$").matchEntire(s) == null)
            throw IllegalArgumentException("Bad kotlin-version.txt")
    }

fun includeBuildModule(location: String, name: String, config: ConfigurableIncludedBuild.() -> Unit = {}) {
    includeBuild(location) {
        this@includeBuild.name = name

        dependencySubstitution {
            substitute(module("ru.landgrafhomyak.icpp.compiler-lib:$name"))
                .using(project(":"))
        }

        this.config()
    }
}

includeBuildModule("./modules/-build-utilities/shared/", "-build-utilities")
includeBuildModule("./modules/-build-utilities/mpp/", "-build-utilities/mpp")
includeBuildModule("./modules/-build-utilities/grdl/", "-build-utilities/gradle")

includeBuildModule("./modules/parser/environment/", "parser/environment")
includeBuildModule("./modules/parser/ast-builders/", "parser/ast-builders")
includeBuildModule("./modules/parser/impl/", "parser")

includeBuildModule("./modules/parser/tests/runtime-api/", "parser/tests/runtime-api")
includeBuildModule("./modules/parser/tests/runtime-impl/", "parser/tests/runtime")
includeBuildModule("./modules/parser/tests/testers/", "parser/tests/testers")
includeBuildModule("./modules/parser/tests/impl", "parser/tests")
includeBuildModule("./modules/parser/tests/run/", "parser/tests/-run")
