# Module kmath-complex

Complex and hypercomplex number systems in KMath.

 - [complex](src/commonMain/kotlin/space/kscience/kmath/complex/Complex.kt) : Complex numbers operations
 - [quaternion](src/commonMain/kotlin/space/kscience/kmath/complex/Quaternion.kt) : Quaternions and their composition


## Artifact:

The Maven coordinates of this project are `space.kscience:kmath-complex:0.4.0-dev-1`.

**Gradle Groovy:**
```groovy
repositories {
    maven { url 'https://repo.kotlin.link' }
    mavenCentral()
}

dependencies {
    implementation 'space.kscience:kmath-complex:0.4.0-dev-1'
}
```
**Gradle Kotlin DSL:**
```kotlin
repositories {
    maven("https://repo.kotlin.link")
    mavenCentral()
}

dependencies {
    implementation("space.kscience:kmath-complex:0.4.0-dev-1")
}
```
