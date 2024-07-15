plugins {
    kotlin("jvm") version embeddedKotlinVersion

}

repositories {
    mavenCentral()
}

kotlin {
    sourceSets {
        val main by getting {
            dependencies {
                compileOnly("dev.gradleplugins:gradle-api:${gradle.gradleVersion}")
            }
        }
    }
}