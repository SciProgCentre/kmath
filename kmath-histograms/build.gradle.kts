plugins { id("ru.mipt.npm.mpp") }

kotlin.sourceSets.commonMain {
    dependencies {
        api(project(":kmath-core"))
        api(project(":kmath-for-real"))
    }
}
