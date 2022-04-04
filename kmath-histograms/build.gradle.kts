plugins {
    kotlin("multiplatform")
    id("ru.mipt.npm.gradle.common")
//    id("ru.mipt.npm.gradle.native")
}

kscience {
    useAtomic()
}

kotlin.sourceSets {
    filter { it.name.contains("test", true) }
        .map(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::languageSettings)
        .forEach {
            it.optIn("space.kscience.kmath.misc.UnstableKMathAPI")
        }

    commonMain {
        dependencies {
            api(project(":kmath-core"))
        }
    }
    commonTest {
        dependencies {
            implementation(project(":kmath-for-real"))
            implementation(projects.kmath.kmathStat)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0")
        }
    }
}

readme {
    maturity = ru.mipt.npm.gradle.Maturity.PROTOTYPE
}

// Testing multi-receiver!
tasks.withType<org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile> {
    enabled = false
}
