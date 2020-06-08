plugins {
    id("scientifik.mpp")
}

kotlin.sourceSets {
    commonMain.get().dependencies { api(project(":kmath-coroutines")) }

    jvmMain.get().dependencies {
        api("org.apache.commons:commons-rng-sampling:1.3")
        api("org.apache.commons:commons-rng-simple:1.3")
    }
}