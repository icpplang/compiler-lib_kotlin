rootDir.resolve("./kotlin-version.txt")
    .readText()
    .also { s ->
        if (Regex("^[^\\s:]+$").matchEntire(s) == null)
            throw IllegalArgumentException("Bad kotlin-version.txt")
    }

fun includeBuildModule(location:String, name: String, ) {
    includeBuild(location) {
        this@includeBuild.name = name

        dependencySubstitution {
            substitute(module("ru.landgrafhomyak.icpp.compiler-lib:$name"))
                .using(project(":"))
        }
    }
}

includeBuildModule("./modules/_build_utilities/shared/", "_build_utilities")
includeBuildModule("./modules/_build_utilities/mpp/", "_build_utilities/mpp")
includeBuildModule("./modules/_build_utilities/grdl/", "_build_utilities/gradle")
includeBuildModule("./modules/parser/environment/", "parser/environment")
includeBuildModule("./modules/parser/ast_builders/", "parser/ast_builders")
//includeBuildModule("./modules/parser_tester/api/", "parser_tester_api")
//includeBuildModule("./modules/parser_tester/gradle_plugin/", "parser_tester_gradle_plugin")
includeBuildModule("./modules/parser/impl/", "parser")