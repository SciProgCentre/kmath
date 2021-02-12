# The Core Module (`kmath-core`)

The core features of KMath:

 - [algebras](src/commonMain/kotlin/kscience/kmath/operations/Algebra.kt) : Algebraic structures like rings, spaces and fields.
 - [nd](src/commonMain/kotlin/kscience/kmath/structures/NDStructure.kt) : Many-dimensional structures and operations on them.
 - [linear](src/commonMain/kotlin/kscience/kmath/operations/Algebra.kt) : Basic linear algebra operations (sums, products, etc.), backed by the `Space` API. Advanced linear algebra operations like matrix inversion and LU decomposition.
 - [buffers](src/commonMain/kotlin/kscience/kmath/structures/Buffers.kt) : One-dimensional structure
 - [expressions](src/commonMain/kotlin/kscience/kmath/expressions) : By writing a single mathematical expression once, users will be able to apply different types of 
objects to the expression by providing a context. Expressions can be used for a wide variety of purposes from high 
performance calculations to code generation.
 - [domains](src/commonMain/kotlin/kscience/kmath/domains) : Domains
 - [autodif](src/commonMain/kotlin/kscience/kmath/expressions/SimpleAutoDiff.kt) : Automatic differentiation


> #### Artifact:
>
> This module artifact: `kscience.kmath:kmath-core:0.2.0-dev-7`.
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
>     implementation 'kscience.kmath:kmath-core:0.2.0-dev-7'
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
>     implementation("kscience.kmath:kmath-core:0.2.0-dev-7")
> }
> ```
