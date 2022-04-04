plugins {
    id("ru.mipt.npm.gradle.mpp")
//    id("ru.mipt.npm.gradle.native")
}

kscience {
    useAtomic()
}

kotlin.sourceSets {
    commonMain {
        dependencies {
            api(project(":kmath-coroutines"))
        }
    }

    jvmMain {
        dependencies {
            api("org.apache.commons:commons-rng-sampling:1.3")
            api("org.apache.commons:commons-rng-simple:1.3")
        }
    }
}

readme {
    maturity = ru.mipt.npm.gradle.Maturity.EXPERIMENTAL
}

// Testing multi-receiver!
tasks.withType<org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile> {
    enabled = false
}
