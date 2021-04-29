# Module kmath-tensors

Common operations on tensors, the API consists of:

 - [TensorAlgebra](src/commonMain/kotlin/space/kscience/kmath/tensors/api/TensorAlgebra.kt) : Basic algebra operations on tensors (plus, dot, etc.)
 - [TensorPartialDivisionAlgebra](src/commonMain/kotlin/space/kscience/kmath/tensors/api/TensorPartialDivisionAlgebra.kt) : Emulates an algebra over a field
 - [LinearOpsTensorAlgebra](src/commonMain/kotlin/space/kscience/kmath/tensors/api/LinearOpsTensorAlgebra.kt) : Linear algebra operations including LU, QR, Cholesky LL and SVD decompositions
 - [AnalyticTensorAlgebra](src/commonMain/kotlin/space/kscience/kmath/tensors/api/AnalyticTensorAlgebra.kt) : Element-wise analytic operations

The library offers a multiplatform implementation for this interface over the `Double`'s. As the highlight, the user can find:
 - [BroadcastDoubleTensorAlgebra](src/commonMain/kotlin/space/kscience/kmath/tensors/core/algebras/BroadcastDoubleTensorAlgebra.kt) : Basic algebra operations implemented with broadcasting.
 - [DoubleLinearOpsTensorAlgebra](src/commonMain/kotlin/space/kscience/kmath/tensors/core/algebras/DoubleLinearOpsTensorAlgebra.kt) : Includes the power method for SVD and the spectrum of symmetric matrices.
## Artifact:

The Maven coordinates of this project are `space.kscience:kmath-tensors:0.3.0-dev-7`.

**Gradle:**
```gradle
repositories {
    maven { url 'https://repo.kotlin.link' }
    maven { url 'https://dl.bintray.com/hotkeytlt/maven' }
    maven { url "https://dl.bintray.com/kotlin/kotlin-eap" } // include for builds based on kotlin-eap
}

dependencies {
    implementation 'space.kscience:kmath-tensors:0.3.0-dev-7'
}
```
**Gradle Kotlin DSL:**
```kotlin
repositories {
    maven("https://repo.kotlin.link")
    maven("https://dl.bintray.com/kotlin/kotlin-eap") // include for builds based on kotlin-eap
    maven("https://dl.bintray.com/hotkeytlt/maven") // required for a
}

dependencies {
    implementation("space.kscience:kmath-tensors:0.3.0-dev-7")
}
```
