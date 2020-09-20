plugins {
    id("scientifik.jvm")
}

dependencies {
    api(project(":kmath-core"))
    api("org.nd4j:nd4j-api:1.0.0-beta7")
    testImplementation("org.deeplearning4j:deeplearning4j-core:1.0.0-beta7")
    testImplementation("org.nd4j:nd4j-native-platform:1.0.0-beta7")
    testImplementation("org.slf4j:slf4j-simple:1.7.30")
}
