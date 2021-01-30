# Functions (`kmath-functions`)

Functions and interpolations:

 - [piecewise](src/commonMain/kotlin/kscience/kmath/functions/Piecewise.kt) : Piecewise functions.
 - [polynomials](src/commonMain/kotlin/kscience/kmath/functions/Polynomial.kt) : Polynomial functions.
 - [linear interpolation](src/commonMain/kotlin/kscience/kmath/interpolation/LinearInterpolator.kt) : Linear XY interpolator.
 - [spline interpolation](src/commonMain/kotlin/kscience/kmath/interpolation/SplineInterpolator.kt) : Cubic spline XY interpolator.


> #### Artifact:
>
> This module artifact: `kscience.kmath:kmath-functions:0.2.0-dev-6`.
>
> Bintray release version:        [ ![Download](https://api.bintray.com/packages/mipt-npm/kscience/kmath-functions/images/download.svg) ](https://bintray.com/mipt-npm/kscience/kmath-functions/_latestVersion)
>
> Bintray development version:    [ ![Download](https://api.bintray.com/packages/mipt-npm/dev/kmath-functions/images/download.svg) ](https://bintray.com/mipt-npm/dev/kmath-functions/_latestVersion)
>
> **Gradle:**
>
> ```gradle
> repositories {
>     maven { url "https://dl.bintray.com/kotlin/kotlin-eap" }
>     maven { url 'https://dl.bintray.com/mipt-npm/kscience' }
>     maven { url 'https://dl.bintray.com/mipt-npm/dev' }
>     maven { url 'https://dl.bintray.com/hotkeytlt/maven' }
> 
> }
> 
> dependencies {
>     implementation 'kscience.kmath:kmath-functions:0.2.0-dev-6'
> }
> ```
> **Gradle Kotlin DSL:**
>
> ```kotlin
> repositories {
>     maven("https://dl.bintray.com/kotlin/kotlin-eap")
>     maven("https://dl.bintray.com/mipt-npm/kscience")
>     maven("https://dl.bintray.com/mipt-npm/dev")
>     maven("https://dl.bintray.com/hotkeytlt/maven")
> }
> 
> dependencies {
>     implementation("kscience.kmath:kmath-functions:0.2.0-dev-6")
> }
> ```
