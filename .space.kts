job("Build") {
    gradlew("openjdk:11", "build") {
        env["SPACE_USER"] = Secrets("space_user")
        env["SPACE_TOKEN"] = Secrets("space_token")
    }
}
