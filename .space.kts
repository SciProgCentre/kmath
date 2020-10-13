job("Publish") {
    gradlew("openjdk:11", "publish") {
        env["SPACE_USER"] = Secrets("space_user")
        env["SPACE_TOKEN"] = Secrets("space_token")
    }
}
