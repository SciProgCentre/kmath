# Functions (`kmath-functions`)

Functions and interpolations:

 - [piecewise](Piecewise functions.) : src/commonMain/kotlin/kscience/kmath/functions/Piecewise.kt
 - [polynomials](Polynomial functions.) : src/commonMain/kotlin/kscience/kmath/functions/Polynomial.kt
 - [linear interpolation](Linear XY interpolator.) : src/commonMain/kotlin/kscience/kmath/interpolation/LinearInterpolator.kt
 - [spline interpolation](Cubic spline XY interpolator.) : src/commonMain/kotlin/kscience/kmath/interpolation/SplineInterpolator.kt


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
