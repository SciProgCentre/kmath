plugins {
    id("ru.mipt.npm.jvm")
}

dependencies {
    api("com.github.breandan:kotlingrad:0.3.2")
    api(project(":kmath-ast"))
}
