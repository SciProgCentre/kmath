# Module kmath-tensors

Common linear algebra operations on tensors.

 - [tensor algebra](src/commonMain/kotlin/space/kscience/kmath/tensors/api/TensorAlgebra.kt) : Basic linear algebra operations on tensors (plus, dot, etc.)
 - [tensor algebra with broadcasting](src/commonMain/kotlin/space/kscience/kmath/tensors/core/BroadcastDoubleTensorAlgebra.kt) : Basic linear algebra operations implemented with broadcasting.
 - [linear algebra operations](src/commonMain/kotlin/space/kscience/kmath/tensors/api/LinearOpsTensorAlgebra.kt) : Advanced linear algebra operations like LU decomposition, SVD, etc.


## Artifact:

The Maven coordinates of this project are `space.kscience:kmath-tensors:0.3.0`.

**Gradle Groovy:**
```groovy
repositories {
    maven { url 'https://repo.kotlin.link' }
    mavenCentral()
}

dependencies {
    implementation 'space.kscience:kmath-tensors:0.3.0'
}
```
**Gradle Kotlin DSL:**
```kotlin
repositories {
    maven("https://repo.kotlin.link")
    mavenCentral()
}

dependencies {
    implementation("space.kscience:kmath-tensors:0.3.0")
}
```
