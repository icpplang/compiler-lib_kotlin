import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    kotlin("multiplatform") version "1.7.10" apply false
}

allprojects {
    repositories {
        mavenCentral()
    }

    tasks.withType<KotlinJvmCompile> {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
}