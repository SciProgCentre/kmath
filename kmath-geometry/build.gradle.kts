plugins { id("ru.mipt.npm.mpp") }

kotlin.sourceSets.commonMain {
    dependencies {
        api(project(":kmath-core"))
    }
}

readme{
    maturity = ru.mipt.npm.gradle.Maturity.PROTOTYPE
}
