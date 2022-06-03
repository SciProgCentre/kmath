# Module kmath-core

The core interfaces of KMath.

 - [algebras](src/commonMain/kotlin/space/kscience/kmath/operations/Algebra.kt) : Algebraic structures like rings, spaces and fields.
 - [nd](src/commonMain/kotlin/space/kscience/kmath/structures/StructureND.kt) : Many-dimensional structures and operations on them.
 - [linear](src/commonMain/kotlin/space/kscience/kmath/operations/Algebra.kt) : Basic linear algebra operations (sums, products, etc.), backed by the `Space` API. Advanced linear algebra operations like matrix inversion and LU decomposition.
 - [buffers](src/commonMain/kotlin/space/kscience/kmath/structures/Buffers.kt) : One-dimensional structure
 - [expressions](src/commonMain/kotlin/space/kscience/kmath/expressions) : By writing a single mathematical expression once, users will be able to apply different types of 
objects to the expression by providing a context. Expressions can be used for a wide variety of purposes from high 
performance calculations to code generation.
 - [domains](src/commonMain/kotlin/space/kscience/kmath/domains) : Domains
 - [autodiff](src/commonMain/kotlin/space/kscience/kmath/expressions/SimpleAutoDiff.kt) : Automatic differentiation


## Artifact:

The Maven coordinates of this project are `space.kscience:kmath-core:0.3.0`.

**Gradle Groovy:**
```groovy
repositories {
    maven { url 'https://repo.kotlin.link' }
    mavenCentral()
}

dependencies {
    implementation 'space.kscience:kmath-core:0.3.0'
}
```
**Gradle Kotlin DSL:**
```kotlin
repositories {
    maven("https://repo.kotlin.link")
    mavenCentral()
}

dependencies {
    implementation("space.kscience:kmath-core:0.3.0")
}
```
