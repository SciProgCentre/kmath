import ru.mipt.npm.gradle.Maturity

plugins {
    id("ru.mipt.npm.gradle.mpp")
}

kotlin.sourceSets.commonMain {
    dependencies {
        api(project(":kmath-core"))
    }
}

readme {
    maturity = Maturity.PROTOTYPE
}
