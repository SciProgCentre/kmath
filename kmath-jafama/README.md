# Module kmath-jafama

Integration with [Jafama](https://github.com/jeffhain/jafama).

 - [jafama-double](src/main/kotlin/space/kscience/kmath/jafama/) : Double ExtendedField implementations based on Jafama


## Artifact:

The Maven coordinates of this project are `space.kscience:kmath-jafama:0.3.0`.

**Gradle Groovy:**
```groovy
repositories {
    maven { url 'https://repo.kotlin.link' }
    mavenCentral()
}

dependencies {
    implementation 'space.kscience:kmath-jafama:0.3.0'
}
```
**Gradle Kotlin DSL:**
```kotlin
repositories {
    maven("https://repo.kotlin.link")
    mavenCentral()
}

dependencies {
    implementation("space.kscience:kmath-jafama:0.3.0")
}
```

## Example usage

All the `DoubleField` uses can be replaced with `JafamaDoubleField` or `StrictJafamaDoubleField`.

```kotlin
import space.kscience.kmath.jafama.*
import space.kscience.kmath.operations.*

fun main() {
    val a = 2.0
    val b = StrictJafamaDoubleField { exp(a) }
    println(JafamaDoubleField { b + a })
    println(StrictJafamaDoubleField { ln(b) })
}
```

## Performance

According to KMath benchmarks on GraalVM, Jafama functions are slower than JDK math; however, there are indications that on Hotspot Jafama is a bit faster.

> **Can't find appropriate benchmark data. Try generating readme files after running benchmarks**.
