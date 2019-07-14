plugins {
    `npm-multiplatform`
}

kotlin.sourceSets.commonMain {
    dependencies {
        api(project(":kmath-core"))
    }
}