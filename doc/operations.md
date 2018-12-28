## Spaces and fields

An obvious first choice of mathematical objects to implement in a context-oriented style are algebraic elements like spaces,
rings and fields. Those are located in the `scientifik.kmath.operations.Algebra.kt` file. Alongside common contexts, the file includes definitions for algebra elements like `FieldElement`. A `FieldElement` object
stores a reference to the `Field` which contains additive and multiplicative operations, meaning
it has one fixed context attached and does not require explicit external context. So those `MathElements` can be operated without context:

```kotlin
val c1 = Complex(1.0, 2.0)
val c2 = ComplexField.i
val c3 = c1 + c2
```

`ComplexField` also features special operations to mix complex and real numbers, for example:

```kotlin
val c1 = Complex(1.0,2.0)
val c2 = ComplexField.run{ c1 - 1.0} // Returns: [re:0.0, im: 2.0]
val c3 = ComplexField.run{ c1 - i*2.0}
```

**Note**: In theory it is possible to add behaviors directly to the context, but currently kotlin syntax does not support
that. Watch [KT-10468](https://youtrack.jetbrains.com/issue/KT-10468) for updates.

## Nested fields

Contexts allow one to build more complex structures. For example, it is possible to create a `Matrix` from complex elements like so:

```kotlin
val element = NDElements.create(field = ComplexField, shape = intArrayOf(2,2)){index: IntArray ->
    Complex(index[0] - index[1], index[0] + index[1])
}
```

The `element` in this example is a member of the `Field` of 2-d structures, each element of which is a member of its own
`ComplexField`. The important thing is one does not need to create a special n-d class to hold complex
numbers and implement operations on it, one just needs to provide a field for its elements.

**Note**: Fields themselves do not solve the problem of JVM boxing, but it is possible to solve with special contexts like
`BufferSpec`. This feature is in development phase.