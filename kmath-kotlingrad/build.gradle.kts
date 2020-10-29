plugins {
    id("ru.mipt.npm.jvm")
}

dependencies {
    api("com.github.breandan:kotlingrad:0.3.7")
    api(project(":kmath-ast"))
}
