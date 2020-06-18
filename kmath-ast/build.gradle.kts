plugins {
    id("scientifik.mpp")
}

repositories {
    maven("https://dl.bintray.com/hotkeytlt/maven")
}

kotlin.sourceSets {
//    all {
//       languageSettings.apply{
//            enableLanguageFeature("NewInference")
//       }
//    }
    commonMain {
        dependencies {
            api(project(":kmath-core"))
            implementation("com.github.h0tk3y.betterParse:better-parse-multiplatform:0.4.0-alpha-3")
            implementation("com.github.h0tk3y.betterParse:better-parse-multiplatform-metadata:0.4.0-alpha-3")
        }
    }

    jvmMain {
        dependencies {
            implementation("com.github.h0tk3y.betterParse:better-parse-jvm:0.4.0-alpha-3")
            implementation("org.ow2.asm:asm:8.0.1")
            implementation("org.ow2.asm:asm-commons:8.0.1")
        }
    }

    jsMain {
        dependencies {
            implementation("com.github.h0tk3y.betterParse:better-parse-js:0.4.0-alpha-3")
        }
    }
}