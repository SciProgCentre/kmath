## Artifact:

The Maven coordinates of this project are `${group}:${name}:${version}`.

**Gradle:**
```groovy
repositories {
    maven { url 'https://repo.kotlin.link' }
    mavenCentral()
    // development and snapshot versions
    maven { url 'https://maven.pkg.jetbrains.space/spc/p/sci/dev' }
}

dependencies {
    implementation '${group}:${name}:${version}'
}
```
**Gradle Kotlin DSL:**
```kotlin
repositories {
    maven("https://repo.kotlin.link")
    mavenCentral()
    // development and snapshot versions
    maven("https://maven.pkg.jetbrains.space/spc/p/sci/dev")
}

dependencies {
    implementation("${group}:${name}:${version}")
}
```