# Module kmath-tensors

Common linear algebra operations on tensors.

 - [tensor algebra](src/commonMain/kotlin/space/kscience/kmath/tensors/api/TensorAlgebra.kt) : Basic linear algebra operations on tensors (plus, dot, etc.)
 - [tensor algebra with broadcasting](src/commonMain/kotlin/space/kscience/kmath/tensors/core/algebras/BroadcastDoubleTensorAlgebra.kt) : Basic linear algebra operations implemented with broadcasting.
 - [linear algebra operations](src/commonMain/kotlin/space/kscience/kmath/tensors/api/LinearOpsTensorAlgebra.kt) : Advanced linear algebra operations like LU decomposition, SVD, etc.


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
