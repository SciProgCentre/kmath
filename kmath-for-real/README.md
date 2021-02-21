# Real number specialization module (`kmath-for-real`)

 - [RealVector](src/commonMain/kotlin/kscience/kmath/real/RealVector.kt) : Numpy-like operations for Buffers/Points
 - [RealMatrix](src/commonMain/kotlin/kscience/kmath/real/RealMatrix.kt) : Numpy-like operations for 2d real structures
 - [grids](src/commonMain/kotlin/kscience/kmath/structures/grids.kt) : Uniform grid generators


> #### Artifact:
>
> This module artifact: `space.kscience:kmath-for-real:0.2.0`.
>
> Bintray release version:        [ ![Download](https://api.bintray.com/packages/mipt-npm/kscience/kmath-for-real/images/download.svg) ](https://bintray.com/mipt-npm/kscience/kmath-for-real/_latestVersion)
>
> Bintray development version:    [ ![Download](https://api.bintray.com/packages/mipt-npm/dev/kmath-for-real/images/download.svg) ](https://bintray.com/mipt-npm/dev/kmath-for-real/_latestVersion)
>
> **Gradle:**
>
> ```gradle
> repositories {
>     maven { url 'https://repo.kotlin.link' }
>     maven { url 'https://dl.bintray.com/hotkeytlt/maven' }
>     maven { url "https://dl.bintray.com/kotlin/kotlin-eap" } // include for builds based on kotlin-eap
>//     Uncomment if repo.kotlin.link is unavailable 
>//     maven { url 'https://dl.bintray.com/mipt-npm/kscience' }
>//     maven { url 'https://dl.bintray.com/mipt-npm/dev' }
> }
> 
> dependencies {
>     implementation 'space.kscience:kmath-for-real:0.2.0'
> }
> ```
> **Gradle Kotlin DSL:**
>
> ```kotlin
> repositories {
>     maven("https://repo.kotlin.link")
>     maven("https://dl.bintray.com/kotlin/kotlin-eap") // include for builds based on kotlin-eap
>     maven("https://dl.bintray.com/hotkeytlt/maven") // required for a
>//     Uncomment if repo.kotlin.link is unavailable 
>//     maven("https://dl.bintray.com/mipt-npm/kscience")
>//     maven("https://dl.bintray.com/mipt-npm/dev")
> }
> 
> dependencies {
>     implementation("space.kscience:kmath-for-real:0.2.0")
> }
> ```
