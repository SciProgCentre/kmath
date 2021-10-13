# Module kmath-jafama

Integration with [Jafama](https://github.com/jeffhain/jafama).

${features}

${artifact}

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

${benchmarkJafamaDouble}
