# The Core Module (`kmath-core`)

Complex and hypercomplex number systems in KMath:

 - [complex](src/commonMain/kotlin/kscience/kmath/complex/Complex.kt) : Complex Numbers
 - [quaternion](src/commonMain/kotlin/kscience/kmath/complex/Quaternion.kt) : Quaternions


> #### Artifact:
>
> This module artifact: `kscience.kmath:kmath-complex:0.2.0-dev-6`.
>
> Bintray release version:        [ ![Download](https://api.bintray.com/packages/mipt-npm/kscience/kmath-complex/images/download.svg) ](https://bintray.com/mipt-npm/kscience/kmath-complex/_latestVersion)
>
> Bintray development version:    [ ![Download](https://api.bintray.com/packages/mipt-npm/dev/kmath-complex/images/download.svg) ](https://bintray.com/mipt-npm/dev/kmath-complex/_latestVersion)
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
>     implementation 'kscience.kmath:kmath-complex:0.2.0-dev-6'
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
>     implementation("kscience.kmath:kmath-complex:0.2.0-dev-6")
> }
> ```
