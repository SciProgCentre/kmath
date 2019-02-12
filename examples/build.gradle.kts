plugins {
    kotlin("jvm")
}

description = "Examples for different kmath features"

dependencies {
    implementation(project(":kmath-core"))
    implementation(project(":kmath-coroutines"))
    implementation(project(":kmath-commons"))
    implementation(project(":kmath-koma"))
    implementation(group = "com.kyonifer", name = "koma-core-ejml", version = "0.12")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}
