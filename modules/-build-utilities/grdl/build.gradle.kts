import ru.landgrafhomyak.icpp.compiler_lib._build_utilities.Modules

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
                compileOnly("dev.gradleplugins:gradle-api:${gradle.gradleVersion}")
                implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${embeddedKotlinVersion}")
                implementation(Modules.named("-build-utilities"))
            }
        }
    }
}
