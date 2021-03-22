# Module kmath-functions

Functions and interpolations.

 - [piecewise](Piecewise functions.) : src/commonMain/kotlin/space/kscience/kmath/functions/Piecewise.kt
 - [polynomials](Polynomial functions.) : src/commonMain/kotlin/space/kscience/kmath/functions/Polynomial.kt
 - [linear interpolation](Linear XY interpolator.) : src/commonMain/kotlin/space/kscience/kmath/interpolation/LinearInterpolator.kt
 - [spline interpolation](Cubic spline XY interpolator.) : src/commonMain/kotlin/space/kscience/kmath/interpolation/SplineInterpolator.kt


## Artifact:

The Maven coordinates of this project are `space.kscience:kmath-functions:0.3.0-dev-3`.

**Gradle:**
```gradle
repositories {
    maven { url 'https://repo.kotlin.link' }
    maven { url 'https://dl.bintray.com/hotkeytlt/maven' }
    maven { url "https://dl.bintray.com/kotlin/kotlin-eap" } // include for builds based on kotlin-eap
}

dependencies {
    implementation 'space.kscience:kmath-functions:0.3.0-dev-3'
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
    implementation("space.kscience:kmath-functions:0.3.0-dev-3")
}
```
