# Module kmath-tensors

Common linear algebra operations on tensors.

 - [tensor algebra](src/commonMain/kotlin/space/kscience/kmath/tensors/api/TensorAlgebra.kt) : Interface for basic linear algebra operations on tensors (plus, dot, etc.)
 - [linear algebra operations](src/commonMain/kotlin/space/kscience/kmath/tensors/api/LinearOpsTensorAlgebra.kt) : Interface for advanced linear algebra operations like LU decomposition, SVD, etc.
 - [tensor algebra over Double](src/commonMain/kotlin/space/kscience/kmath/tensors/core/DoubleTensorAlgebra.kt): Full implementation of operations for tensors over `Double`'s
 - [tensor algebra with broadcasting](src/commonMain/kotlin/space/kscience/kmath/tensors/core/BroadcastDoubleTensorAlgebra.kt) : Basic linear algebra operations implemented with broadcasting for tensors over `Double`'s.

## Artifact:

The Maven coordinates of this project are `space.kscience:kmath-tensors:0.3.0-dev-8`.

**Gradle:**
```gradle
repositories {
    maven { url 'https://repo.kotlin.link' }
    mavenCentral()
}

dependencies {
    implementation 'space.kscience:kmath-tensors:0.3.0-dev-8'
}
```
**Gradle Kotlin DSL:**
```kotlin
repositories {
    maven("https://repo.kotlin.link")
    mavenCentral()
}

dependencies {
    implementation("space.kscience:kmath-tensors:0.3.0-dev-8")
}
```
