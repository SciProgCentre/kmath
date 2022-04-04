plugins {
    kotlin("multiplatform")
    id("ru.mipt.npm.gradle.common")
//    id("ru.mipt.npm.gradle.native")
}

kotlin.sourceSets.commonMain {
    dependencies {
        api(project(":kmath-core"))
    }
}

readme {
    maturity = ru.mipt.npm.gradle.Maturity.PROTOTYPE
}

// Testing multi-receiver!
tasks.withType<org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile> {
    enabled = false
}
