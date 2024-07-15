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

includeBuildModule("./modules/-build-utilities/shared/", "-build-utilities")
includeBuildModule("./modules/-build-utilities/mpp/", "-build-utilities/mpp")
includeBuildModule("./modules/-build-utilities/grdl/", "-build-utilities/gradle")
includeBuildModule("./modules/parser/environment/", "parser/environment")
includeBuildModule("./modules/parser/ast-builders/", "parser/ast-builders")
//includeBuildModule("./modules/parser_tester/api/", "parser_tester_api")
//includeBuildModule("./modules/parser_tester/gradle_plugin/", "parser_tester_gradle_plugin")
includeBuildModule("./modules/parser/impl/", "parser")