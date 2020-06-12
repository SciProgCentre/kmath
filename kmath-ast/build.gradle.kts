plugins {
    id("scientifik.mpp")
}

repositories{
    maven("https://dl.bintray.com/hotkeytlt/maven")
}

kotlin.sourceSets {
    commonMain {
        dependencies {
            api(project(":kmath-core"))
            implementation("com.github.h0tk3y.betterParse:better-parse-multiplatform:0.4.0-alpha-3")
        }
    }
}