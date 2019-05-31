plugins {
    kotlin("jvm")
    `maven-publish`
}

description = "Commons math binding for kmath"

dependencies {
    api(project(":kmath-core"))
    api(project(":kmath-coroutines"))
    api("org.apache.commons:commons-math3:3.6.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}


val sourcesJar by tasks.registering(Jar::class) {
    classifier = "sources"
    from(sourceSets.main.get().allSource)
}

publishing {
    publications {
        register("jvm", MavenPublication::class) {
            from(components["java"])
            artifact(sourcesJar.get())
        }
    }
}