import ru.landgrafhomyak.icpp.compiler_lib._build_utilities.Modules

plugins {
    kotlin("jvm") version embeddedKotlinVersion
}

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

kotlin {
    sourceSets {
        val main by getting {
            dependencies {
                compileOnly("dev.gradleplugins:gradle-api:${gradle.gradleVersion}")
                implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${rootDir.resolve("./../../../kotlin-version.txt").readText()}")
                implementation(Modules.named("_build_utilities"))
            }
        }
    }
}
