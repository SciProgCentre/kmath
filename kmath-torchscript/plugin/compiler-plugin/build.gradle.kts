plugins {
    kotlin("jvm")
    id("ru.mipt.npm.gradle.common")
}

dependencies {
    compileOnly(kotlin("compiler-embeddable"))
}
