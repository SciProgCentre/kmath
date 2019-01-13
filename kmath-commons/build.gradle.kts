plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":kmath-core"))
    api("org.apache.commons:commons-math3:3.6.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

//dependencies {
////    compile(project(":kmath-core"))
////    //compile project(":kmath-coroutines")
////}