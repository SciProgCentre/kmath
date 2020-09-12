plugins { id("scientifik.mpp") }

kotlin.sourceSets {
    commonMain {
        dependencies {
            api("com.ionspin.kotlin:bignum:0.2.0")
            api(project(":kmath-core"))
        }
    }
}
