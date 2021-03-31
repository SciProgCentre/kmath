# Module kmath-ejml

EJML based linear algebra implementation.

 - [ejml-vector](src/main/kotlin/space/kscience/kmath/ejml/EjmlVector.kt) : The Point implementation using SimpleMatrix.
 - [ejml-matrix](src/main/kotlin/space/kscience/kmath/ejml/EjmlMatrix.kt) : The Matrix implementation using SimpleMatrix.
 - [ejml-linear-space](src/main/kotlin/space/kscience/kmath/ejml/EjmlLinearSpace.kt) : The LinearSpace implementation using SimpleMatrix.


## Artifact:

The Maven coordinates of this project are `space.kscience:kmath-ejml:0.3.0-dev-4`.

**Gradle:**
```gradle
repositories {
    maven { url 'https://repo.kotlin.link' }
    maven { url 'https://dl.bintray.com/hotkeytlt/maven' }
    maven { url "https://dl.bintray.com/kotlin/kotlin-eap" } // include for builds based on kotlin-eap
}

dependencies {
    implementation 'space.kscience:kmath-ejml:0.3.0-dev-4'
}
```
**Gradle Kotlin DSL:**
```kotlin
repositories {
    maven("https://repo.kotlin.link")
    maven("https://dl.bintray.com/kotlin/kotlin-eap") // include for builds based on kotlin-eap
    maven("https://dl.bintray.com/hotkeytlt/maven") // required for a
}

dependencies {
    implementation("space.kscience:kmath-ejml:0.3.0-dev-4")
}
```
