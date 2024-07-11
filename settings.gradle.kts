rootDir.resolve("./kotlin-version.txt")
    .readText()
    .also { s ->
        if (Regex("^[^\\s:]+$").matchEntire(s) == null)
            throw IllegalArgumentException("Bad kotlin-version.txt")
    }

fun includeBuildModule(name: String) {
    includeBuild("./modules/$name") {
        this@includeBuild.name = name

        dependencySubstitution {
            substitute(module("ru.landgrafhomyak.icpp.compiler-lib:$name"))
                .using(project(":"))
        }
    }
}

includeBuildModule("_build_utilities")
includeBuildModule("parser_environment")
includeBuildModule("ast_builders_abstract")
includeBuildModule("parser")