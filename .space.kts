import kotlin.io.path.readText

val projectName = "kmath"

job("Build") {
    //Perform only jvm tests
    gradlew("spc.registry.jetbrains.space/p/sci/containers/kotlin-ci:1.0.3", "test", "jvmTest")
}

job("Publish") {
    startOn {
        gitPush { enabled = false }
    }
    container("spc.registry.jetbrains.space/p/sci/containers/kotlin-ci:1.0.3") {
        env["SPACE_USER"] = "{{ project:space_user }}"
        env["SPACE_TOKEN"] = "{{ project:space_token }}"
        kotlinScript { api ->

            val spaceUser = System.getenv("SPACE_USER")
            val spaceToken = System.getenv("SPACE_TOKEN")

            // write the version to the build directory
            api.gradlew("version")

            //read the version from build file
            val version = java.nio.file.Path.of("build/project-version.txt").readText()

            val revisionSuffix = if (version.endsWith("SNAPSHOT")) {
                "-" + api.gitRevision().take(7)
            } else {
                ""
            }

            api.space().projects.automation.deployments.start(
                project = api.projectIdentifier(),
                targetIdentifier = TargetIdentifier.Key(projectName),
                version = version+revisionSuffix,
                // automatically update deployment status based on the status of a job
                syncWithAutomationJob = true
            )
            api.gradlew(
                "publishAllPublicationsToSpaceRepository",
                "-Ppublishing.space.user=\"$spaceUser\"",
                "-Ppublishing.space.token=\"$spaceToken\"",
            )
        }
    }
}