import ru.landgrafhomyak.icpp.compiler_lib._build_utilities.InitBuild
import ru.landgrafhomyak.icpp.compiler_lib._build_utilities.kotlin

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("ru.landgrafhomyak.icpp.compiler-lib:_build_utilities")
    }
}

repositories {
    mavenCentral()
}

apply<InitBuild>()

kotlin {
    jvm {}
}