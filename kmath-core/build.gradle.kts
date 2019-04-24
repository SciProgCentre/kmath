plugins {
    `multiplatform-config`
}

kotlin.sourceSets {
    commonMain {
        dependencies {
            api(project(":kmath-memory"))
        }
    }
    //mingwMain {}
    //mingwTest {}
}