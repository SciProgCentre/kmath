# Real number specialization module (`kmath-for-real`)

 - [RealVector](src/commonMain/kotlin/kscience/kmath/real/RealVector.kt) : Numpy-like operations for Buffers/Points
 - [RealMatrix](src/commonMain/kotlin/kscience/kmath/real/RealMatrix.kt) : Numpy-like operations for 2d real structures
 - [grids](src/commonMain/kotlin/kscience/kmath/structures/grids.kt) : Uniform grid generators


> #### Artifact:
>
> This module artifact: `kscience.kmath:kmath-for-real:0.2.0-dev-4`.
>
> Bintray release version:        [ ![Download](https://api.bintray.com/packages/mipt-npm/kscience/kmath-for-real/images/download.svg) ](https://bintray.com/mipt-npm/kscience/kmath-for-real/_latestVersion)
>
> Bintray development version:    [ ![Download](https://api.bintray.com/packages/mipt-npm/dev/kmath-for-real/images/download.svg) ](https://bintray.com/mipt-npm/dev/kmath-for-real/_latestVersion)
>
> **Gradle:**
>
> ```gradle
> repositories {
>     maven { url "https://dl.bintray.com/kotlin/kotlin-eap" }
>     maven { url 'https://dl.bintray.com/mipt-npm/kscience' }
>     maven { url 'https://dl.bintray.com/mipt-npm/dev' }
>     maven { url 'https://dl.bintray.com/hotkeytlt/maven' }

> }
> 
> dependencies {
>     implementation 'kscience.kmath:kmath-for-real:0.2.0-dev-4'
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
>     implementation("kscience.kmath:kmath-for-real:0.2.0-dev-4")
> }
> ```
