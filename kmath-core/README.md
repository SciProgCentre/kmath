# The Core Module (`kmath-core`)

The core features of KMath:
 - [algebras](src/commonMain/kotlin/kscience/kmath/operations/Algebra.kt) : Algebraic structures: contexts and elements
 - [nd](src/commonMain/kotlin/kscience/kmath/structures/NDStructure.kt) : Many-dimensional structures
 - [buffers](src/commonMain/kotlin/kscience/kmath/structures/Buffers.kt) : One-dimensional structure
 - [expressions](src/commonMain/kotlin/kscience/kmath/expressions) : Functional Expressions
 - [domains](src/commonMain/kotlin/kscience/kmath/domains) : Domains
 - [autodif](src/commonMain/kotlin/kscience/kmath/misc/AutoDiff.kt) : Automatic differentiation


> #### Artifact:
> This module artifact: `kscience.kmath:kmath-core:0.2.0-dev-1`.
> 
> **Gradle:**
>
> ```gradle
> repositories {
>     maven { url 'https://dl.bintray.com/mipt-npm/kscience' }
>     maven { url 'https://dl.bintray.com/mipt-npm/dev' }
>     maven { url https://dl.bintray.com/hotkeytlt/maven' }
> }
> 
> dependencies {
>     implementation 'kscience.kmath:kmath-core:0.2.0-dev-1'
> }
> ```
> **Gradle Kotlin DSL:**
>
> ```kotlin
> repositories {
>     maven("https://dl.bintray.com/mipt-npm/kscience")
>     maven("https://dl.bintray.com/mipt-npm/dev")
>     maven("https://dl.bintray.com/hotkeytlt/maven")
> }
> 
> dependencies {
>     implementation("kscience.kmath:kmath-core:0.2.0-dev-1")
> }
> ```
