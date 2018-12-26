## Spaces and fields

An obvious first choice of mathematical objects to implement in context-oriented style are algebra elements like spaces,
rings and fields. Those are located in a `scientifik.kmath.operations.Algebra.kt` file. Alongside algebric context
themselves, the file includes definitions for algebra elements such as `FieldElement`. A `FieldElement` object
stores a reference to the `Field` which contains a additive and multiplicative operations for it, meaning
it has one fixed context attached to it and does not require explicit external context. So those `MathElements` could be
operated without context:
```kotlin
val c1 = Complex(1.0, 2.0)
val c2 = ComplexField.i
val c3 = c1 + c2
```
`ComplexField` also features special operations to mix complex numbers with real numbers like:
```kotlin
val c1 = Complex(1.0,2.0)
val c2 = ComplexField.run{ c1 - 1.0} //returns [re:0.0, im: 2.0]
val c3 = ComplexField.run{ c1 - i*2.0}
```

**Note**: In theory it is possible to add behaviors directly to the context, but currently kotlin syntax does not support
that. Watch [KT-10468](https://youtrack.jetbrains.com/issue/KT-10468) for news.

## Nested fields

Algebra contexts allow to create more complex structures. For example, it is possible to create a `Matrix` from complex
elements like this:
```kotlin
val element = NDElements.create(field = ComplexField, shape = intArrayOf(2,2)){index: IntArray ->
    Complex(index[0] - index[1], index[0] + index[1])
}
```
The `element` in this example is a member of `Field` of 2-d structures, each element of which is a member of its own
`ComplexField`. The important thing is that one does not need to create a special nd-structure to hold complex
numbers and implements operations on it, one need just to provide a field for its elements.

**Note**: Fields themselves do not solve problem of JVM boxing, but it is possible to solve with special contexts like
`BufferSpec`. This feature is in development phase.