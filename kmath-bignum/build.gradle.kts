plugins { id("scientifik.mpp") }

kotlin.sourceSets {
    commonMain {
        dependencies {
            api("com.ionspin.kotlin:bignum:0.1.5")
            api(project(":kmath-core"))
        }
    }
}
