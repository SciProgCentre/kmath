# GNU Scientific Library Integration (`kmath-core`)

This subproject implements the following features:

 - [matrix-contexts](src/nativeMain/kotlin/kscience/kmath/gsl/GslMatrixContext.kt) : Matrix Contexts over Double, Float and Complex implemented with GSL


> #### Artifact:
>
> This module artifact: `kscience.kmath:kmath-gsl:0.2.0-dev-6`.
>
> Bintray release version:        [ ![Download](https://api.bintray.com/packages/mipt-npm/kscience/kmath-gsl/images/download.svg) ](https://bintray.com/mipt-npm/kscience/kmath-gsl/_latestVersion)
>
> Bintray development version:    [ ![Download](https://api.bintray.com/packages/mipt-npm/dev/kmath-gsl/images/download.svg) ](https://bintray.com/mipt-npm/dev/kmath-gsl/_latestVersion)
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
>     implementation 'kscience.kmath:kmath-gsl:0.2.0-dev-6'
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
>     implementation("kscience.kmath:kmath-gsl:0.2.0-dev-6")
> }
> ```
