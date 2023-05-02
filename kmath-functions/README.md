# Module kmath-functions

Functions and interpolations.

 - [piecewise](src/commonMain/kotlin/space/kscience/kmath/functions/Piecewise.kt) : Piecewise functions.
 - [polynomials](src/commonMain/kotlin/space/kscience/kmath/functions/Polynomial.kt) : Polynomial functions.
 - [linear interpolation](src/commonMain/kotlin/space/kscience/kmath/interpolation/LinearInterpolator.kt) : Linear XY interpolator.
 - [spline interpolation](src/commonMain/kotlin/space/kscience/kmath/interpolation/SplineInterpolator.kt) : Cubic spline XY interpolator.
 - [integration](#) : Univariate and multivariate quadratures


## Artifact:

The Maven coordinates of this project are `space.kscience:kmath-functions:0.4.0-dev-1`.

**Gradle Groovy:**
```groovy
repositories {
    maven { url 'https://repo.kotlin.link' }
    mavenCentral()
}

dependencies {
    implementation 'space.kscience:kmath-functions:0.4.0-dev-1'
}
```
**Gradle Kotlin DSL:**
```kotlin
repositories {
    maven("https://repo.kotlin.link")
    mavenCentral()
}

dependencies {
    implementation("space.kscience:kmath-functions:0.4.0-dev-1")
}
```
