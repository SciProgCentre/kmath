## Basic linear algebra layout

KMath support for linear algebra organized in a context-oriented way, which means that operations are in most cases declared in context classes, and are not the members of classes that store data. This allows more flexible approach to maintain multiple back-ends. The new operations added as extensions to contexts instead of being member functions of data structures.

The main context for linear algebra over matrices and vectors is `LinearSpace`, which defines addition and dot products of matrices and vectors:

```kotlin
import space.kscience.kmath.linear.*

LinearSpace.Companion.real {
    val vec = buildVector(10) { i -> i.toDouble() }
    val mat = buildMatrix(10, 10) { i, j -> i.toDouble() + j }

    // Addition
    vec + vec
    mat + mat

    // Multiplication by scalar
    vec * 2.0
    mat * 2.0

    // Dot product
    mat dot vec
    mat dot mat
}
```

## Backends overview

### EJML
### Commons Math
