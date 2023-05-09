job("Build and run tests") {
    gradlew("amazoncorretto:17-alpine", "build")
}