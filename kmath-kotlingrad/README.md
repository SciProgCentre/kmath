# Module kmath-kotlingrad

[Kotlin∇](https://www.htmlsymbols.xyz/unicode/U+2207) integration module.

 - [differentiable-mst-expression](src/main/kotlin/space/kscience/kmath/kotlingrad/KotlingradExpression.kt) : MST based DifferentiableExpression.
 - [scalars-adapters](src/main/kotlin/space/kscience/kmath/kotlingrad/scalarsAdapters.kt) : Conversions between Kotlin∇'s SFun and MST


## Artifact:

The Maven coordinates of this project are `space.kscience:kmath-kotlingrad:0.3.0-dev-14`.

**Gradle:**
```gradle
repositories {
    maven { url 'https://repo.kotlin.link' }
    mavenCentral()
}

dependencies {
    implementation 'space.kscience:kmath-kotlingrad:0.3.0-dev-14'
}
```
**Gradle Kotlin DSL:**
```kotlin
repositories {
    maven("https://repo.kotlin.link")
    mavenCentral()
}

dependencies {
    implementation("space.kscience:kmath-kotlingrad:0.3.0-dev-14")
}
```
