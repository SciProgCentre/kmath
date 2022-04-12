# Module kmath-kotlingrad

[Kotlin∇](https://github.com/breandan/kotlingrad) integration module.

 - [differentiable-mst-expression](src/main/kotlin/space/kscience/kmath/kotlingrad/KotlingradExpression.kt) : MST based DifferentiableExpression.
 - [scalars-adapters](src/main/kotlin/space/kscience/kmath/kotlingrad/scalarsAdapters.kt) : Conversions between Kotlin∇'s SFun and MST


## Artifact:

The Maven coordinates of this project are `space.kscience:kmath-kotlingrad:0.3.0`.

**Gradle Groovy:**
```groovy
repositories {
    maven { url 'https://repo.kotlin.link' }
    mavenCentral()
}

dependencies {
    implementation 'space.kscience:kmath-kotlingrad:0.3.0'
}
```
**Gradle Kotlin DSL:**
```kotlin
repositories {
    maven("https://repo.kotlin.link")
    mavenCentral()
}

dependencies {
    implementation("space.kscience:kmath-kotlingrad:0.3.0")
}
```
