# ejml-simple support (`kmath-ejml`)

This subproject implements the following features:

 - [ejml-vector](src/main/kotlin/space/kscience/kmath/ejml/EjmlVector.kt) : The Point implementation using SimpleMatrix.
 - [ejml-matrix](src/main/kotlin/space/kscience/kmath/ejml/EjmlMatrix.kt) : The Matrix implementation using SimpleMatrix.
 - [ejml-linear-space](src/main/kotlin/space/kscience/kmath/ejml/EjmlLinearSpace.kt) : The LinearSpace implementation using SimpleMatrix.


> #### Artifact:
>
> This module artifact: `space.kscience:kmath-ejml:0.3.0-dev-3`.
>
> Bintray release version:        [ ![Download](https://api.bintray.com/packages/mipt-npm/kscience/kmath-ejml/images/download.svg) ](https://bintray.com/mipt-npm/kscience/kmath-ejml/_latestVersion)
>
> Bintray development version:    [ ![Download](https://api.bintray.com/packages/mipt-npm/dev/kmath-ejml/images/download.svg) ](https://bintray.com/mipt-npm/dev/kmath-ejml/_latestVersion)
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
>     implementation 'space.kscience:kmath-ejml:0.3.0-dev-3'
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
>     implementation("space.kscience:kmath-ejml:0.3.0-dev-3")
> }
> ```
