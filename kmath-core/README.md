# The Core Module (`kmath-core`)

The core features of KMath:

 - [algebras](src/commonMain/kotlin/kscience/kmath/operations/Algebra.kt) : Algebraic structures: contexts and elements
 - [nd](src/commonMain/kotlin/kscience/kmath/structures/NDStructure.kt) : Many-dimensional structures
 - [buffers](src/commonMain/kotlin/kscience/kmath/structures/Buffers.kt) : One-dimensional structure
 - [expressions](src/commonMain/kotlin/kscience/kmath/expressions) : Functional Expressions
 - [domains](src/commonMain/kotlin/kscience/kmath/domains) : Domains
 - [autodif](src/commonMain/kotlin/kscience/kmath/expressions/SimpleAutoDiff.kt) : Automatic differentiation


> #### Artifact:
>
> This module artifact: `kscience.kmath:kmath-core:0.2.0-dev-4`.
>
> Bintray release version:        [ ![Download](https://api.bintray.com/packages/mipt-npm/kscience/kmath-core/images/download.svg) ](https://bintray.com/mipt-npm/kscience/kmath-core/_latestVersion)
>
> Bintray development version:    [ ![Download](https://api.bintray.com/packages/mipt-npm/dev/kmath-core/images/download.svg) ](https://bintray.com/mipt-npm/dev/kmath-core/_latestVersion)
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
>     implementation 'kscience.kmath:kmath-core:0.2.0-dev-4'
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
>     implementation("kscience.kmath:kmath-core:0.2.0-dev-4")
> }
> ```
