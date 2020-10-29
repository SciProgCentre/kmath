plugins {
    id("ru.mipt.npm.jvm")
}

dependencies {
    implementation("com.github.breandan:kaliningraph:0.1.2")
    implementation("com.github.breandan:kotlingrad:0.3.7")
    api(project(":kmath-ast"))
}
