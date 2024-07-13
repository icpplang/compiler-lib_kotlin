plugins {
    kotlin("jvm") version embeddedKotlinVersion

}

repositories {
    mavenCentral()
}

group = "ru.landgrafhomyak.icpp.compiler-lib"

kotlin {
    sourceSets {
        val main by getting {
            dependencies {
                compileOnly("dev.gradleplugins:gradle-api:${gradle.gradleVersion}")
            }
        }
    }
}