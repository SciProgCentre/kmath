plugins {
    id("scientifik.jvm")
}

dependencies {
    api(project(path = ":kmath-core"))
    implementation("org.ow2.asm:asm:8.0.1")
    implementation("org.ow2.asm:asm-commons:8.0.1")
}
