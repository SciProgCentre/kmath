plugins {
    `multiplatform-config`
}

// Just an example how we can collapse nested DSL for simple declarations
kotlin.sourceSets.commonMain {
    dependencies {
        api(project(":kmath-core"))
    }
}
