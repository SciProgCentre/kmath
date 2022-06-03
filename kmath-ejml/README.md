# Module kmath-ejml

EJML based linear algebra implementation.

 - [ejml-vector](src/main/kotlin/space/kscience/kmath/ejml/EjmlVector.kt) : Point implementations.
 - [ejml-matrix](src/main/kotlin/space/kscience/kmath/ejml/EjmlMatrix.kt) : Matrix implementation.
 - [ejml-linear-space](src/main/kotlin/space/kscience/kmath/ejml/EjmlLinearSpace.kt) : LinearSpace implementations.


## Artifact:

The Maven coordinates of this project are `space.kscience:kmath-ejml:0.3.0`.

**Gradle Groovy:**
```groovy
repositories {
    maven { url 'https://repo.kotlin.link' }
    mavenCentral()
}

dependencies {
    implementation 'space.kscience:kmath-ejml:0.3.0'
}
```
**Gradle Kotlin DSL:**
```kotlin
repositories {
    maven("https://repo.kotlin.link")
    mavenCentral()
}

dependencies {
    implementation("space.kscience:kmath-ejml:0.3.0")
}
```
