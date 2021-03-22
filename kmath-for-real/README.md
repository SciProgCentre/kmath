# Module kmath-for-real

Specialization of KMath APIs for Double numbers.

 - [DoubleVector](src/commonMain/kotlin/space/kscience/kmath/real/DoubleVector.kt) : Numpy-like operations for Buffers/Points
 - [DoubleMatrix](src/commonMain/kotlin/space/kscience/kmath/real/DoubleMatrix.kt) : Numpy-like operations for 2d real structures
 - [grids](src/commonMain/kotlin/space/kscience/kmath/structures/grids.kt) : Uniform grid generators


## Artifact:

The Maven coordinates of this project are `space.kscience:kmath-for-real:0.3.0-dev-3`.

**Gradle:**
```gradle
repositories {
    maven { url 'https://repo.kotlin.link' }
    maven { url 'https://dl.bintray.com/hotkeytlt/maven' }
    maven { url "https://dl.bintray.com/kotlin/kotlin-eap" } // include for builds based on kotlin-eap
}

dependencies {
    implementation 'space.kscience:kmath-for-real:0.3.0-dev-3'
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
    implementation("space.kscience:kmath-for-real:0.3.0-dev-3")
}
```
