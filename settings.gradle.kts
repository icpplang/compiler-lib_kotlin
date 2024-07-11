//rootProject.name = "icpp"
//
//pluginManagement {
//    repositories {
//        mavenCentral()
//        maven(url = rootDir.resolve("./m2-repo/"))
//    }
//
//    buildscript {
//        dependencies {
//            classpath("ru.landgrafhomyak.icpp.compiler-lib:_build_utilities:-")
//        }
//    }
//}
//
//buildscript {
//    repositories {
//        mavenCentral()
//        maven(url = rootDir.resolve("./m2-repo/"))
//    }
//
//    dependencies {
//        classpath("ru.landgrafhomyak.icpp.compiler-lib:_build_utilities:-")
//    }
//}


fun includeBuildModule(name: String) {
    includeBuild("./modules/$name") {
        this@includeBuild.name = name

        dependencySubstitution {
//            substitute(module("ru.landgrafhomyak.icpp.compiler-lib:$name"))
//                .using(project(":"))
        }
    }
}

includeBuildModule("_build_utilities")
includeBuildModule("parser_environment")
includeBuildModule("ast_builders_abstract")
includeBuildModule("parser")