plugins {
    id("ru.mipt.npm.jvm")
}

description = "Commons math binding for kmath"

dependencies {
    api(project(":kmath-core"))
    api(project(":kmath-coroutines"))
    api(project(":kmath-prob"))
    api("org.apache.commons:commons-math3:3.6.1")
}
