# Performance for n-dimensional structures operations

One of the most sought after features of mathematical libraries is the high-performance operations on n-dimensional
structures. In `kmath` performance depends on which particular context was used for operation.

Let us consider following contexts:
```kotlin
    // automatically build context
    val bufferedField = NDField.auto(intArrayOf(dim, dim), RealField)
    val specializedField = NDField.real(intArrayOf(dim, dim))
    val genericField = NDField.buffered(intArrayOf(dim, dim), RealField)
    val lazyNDField = NDField.lazy(intArrayOf(dim, dim), RealField)
```