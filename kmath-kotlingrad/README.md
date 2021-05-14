# Module kmath-kotlingrad

[Kotlin∇](https://github.com/breandan/kotlingrad) integration module.

 - [differentiable-mst-expression](src/main/kotlin/space/kscience/kmath/kotlingrad/DifferentiableMstExpression.kt) : MST based DifferentiableExpression.
 - [differentiable-mst-expression](src/main/kotlin/space/kscience/kmath/kotlingrad/DifferentiableMstExpression.kt) : Conversions between Kotlin∇'s SFun and MST


## Artifact:

The Maven coordinates of this project are `space.kscience:kmath-kotlingrad:0.3.0-dev-8`.

**Gradle:**
```gradle
repositories {
    maven { url 'https://repo.kotlin.link' }
    mavenCentral()
}

dependencies {
    implementation 'space.kscience:kmath-kotlingrad:0.3.0-dev-8'
}
```
**Gradle Kotlin DSL:**
```kotlin
repositories {
    maven("https://repo.kotlin.link")
    mavenCentral()
}

dependencies {
    implementation("space.kscience:kmath-kotlingrad:0.3.0-dev-8")
}
```
