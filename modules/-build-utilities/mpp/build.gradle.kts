import ru.landgrafhomyak.icpp.compiler_lib._build_utilities.Modules
import ru.landgrafhomyak.icpp.compiler_lib._build_utilities.Versions

plugins {
    kotlin("jvm") version embeddedKotlinVersion
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("ru.landgrafhomyak.icpp.compiler-lib:-build-utilities")
    }
}

repositories {
    mavenCentral()
}

kotlin {
    sourceSets {
        val main by getting {
            dependencies {
                implementation(Modules.named("-build-utilities"))
                compileOnly("dev.gradleplugins:gradle-api:${gradle.gradleVersion}")
                implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${rootDir.resolve("./../../../kotlin-version.txt").readText()}")
                implementation("com.google.devtools.ksp:symbol-processing-gradle-plugin:${Versions.KSP}")
            }
        }
    }
}
