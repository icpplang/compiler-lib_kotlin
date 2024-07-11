plugins {
    kotlin("multiplatform")
}
apply(from=rootDir.resolve("../_build_utilities/scripts/init_build.gradle.kts"))

kotlin {
    jvm {}
}