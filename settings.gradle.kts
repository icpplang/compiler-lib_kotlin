rootDir.resolve("./kotlin-version.txt")
    .readText()
    .also { s ->
        if (Regex("^[^\\s:]+$").matchEntire(s) == null)
            throw IllegalArgumentException("Bad kotlin-version.txt")
    }

fun includeBuildModule(location:String, name: String, ) {
    includeBuild("$location$name/") {
        this@includeBuild.name = name

//        dependencySubstitution {
//            substitute(module("ru.landgrafhomyak.icpp.compiler-lib:$name"))
//                .using(project(":"))
//        }
    }
}

includeBuildModule("./modules/", "_build_utilities")
includeBuildModule("./modules/", "parser_environment")
includeBuildModule("./modules/", "ast_builders_abstract")
includeBuildModule("./modules/", "parser_tester")
includeBuildModule("./modules/", "parser")