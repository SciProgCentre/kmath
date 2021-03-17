# Real number specialization module (`kmath-for-real`)

 - [DoubleVector](src/commonMain/kotlin/space/kscience/kmath/real/DoubleVector.kt) : Numpy-like operations for Buffers/Points
 - [DoubleMatrix](src/commonMain/kotlin/space/kscience/kmath/real/DoubleMatrix.kt) : Numpy-like operations for 2d real structures
 - [grids](src/commonMain/kotlin/space/kscience/kmath/structures/grids.kt) : Uniform grid generators


> #### Artifact:
>
> This module artifact: `space.kscience:kmath-for-real:0.3.0-dev-3`.
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
> }
> 
> dependencies {
>     implementation 'space.kscience:kmath-for-real:0.3.0-dev-3'
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
>     implementation("space.kscience:kmath-for-real:0.3.0-dev-3")
> }
> ```
