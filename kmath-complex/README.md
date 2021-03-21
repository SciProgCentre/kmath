# The Core Module (`kmath-core`)

Complex and hypercomplex number systems in KMath:

 - [complex](src/commonMain/kotlin/space/kscience/kmath/complex/Complex.kt) : Complex Numbers
 - [quaternion](src/commonMain/kotlin/space/kscience/kmath/complex/Quaternion.kt) : Quaternions


> #### Artifact:
>
> This module artifact: `space.kscience:kmath-complex:0.3.0-dev-3`.
>
> Bintray release version:        [ ![Download](https://api.bintray.com/packages/mipt-npm/kscience/kmath-complex/images/download.svg) ](https://bintray.com/mipt-npm/kscience/kmath-complex/_latestVersion)
>
> Bintray development version:    [ ![Download](https://api.bintray.com/packages/mipt-npm/dev/kmath-complex/images/download.svg) ](https://bintray.com/mipt-npm/dev/kmath-complex/_latestVersion)
>
> **Gradle:**
>
> ```gradle
> repositories {
>     maven { url 'https://repo.kotlin.link' }
>     maven { url 'https://dl.bintray.com/hotkeytlt/maven' }
>     maven { url "https://dl.bintray.com/kotlin/kotlin-eap" } // include for builds based on kotlin-eap
> }
> 
> dependencies {
>     implementation 'space.kscience:kmath-complex:0.3.0-dev-3'
> }
> ```
> **Gradle Kotlin DSL:**
>
> ```kotlin
> repositories {
>     maven("https://repo.kotlin.link")
>     maven("https://dl.bintray.com/kotlin/kotlin-eap") // include for builds based on kotlin-eap
>     maven("https://dl.bintray.com/hotkeytlt/maven") // required for a
> }
> 
> dependencies {
>     implementation("space.kscience:kmath-complex:0.3.0-dev-3")
> }
> ```
