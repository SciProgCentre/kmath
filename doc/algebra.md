# Algebra and algebra elements

The mathematical operations in `kmath` are generally separated from mathematical objects.
This means that in order to perform an operation, say `+`, one needs two objects of a type `T` and
and algebra context which defines appropriate operation, say `Space<T>`. Next one needs to run actual operation
in the context:

```kotlin
val a: T
val b: T
val space: Space<T>

val c = space.run{a + b}
```

From the first glance, this distinction seems to be a needless complication, but in fact one needs
to remember that in mathematics, one could define different operations on the same objects. For example,
one could use different types of geometry for vectors.

## Algebra hierarchy

Mathematical contexts have the following hierarchy:

**Space** <- **Ring** <- **Field**

All classes follow abstract mathematical constructs.
[Space](http://mathworld.wolfram.com/Space.html) defines `zero` element, addition operation and multiplication by constant,
[Ring](http://mathworld.wolfram.com/Ring.html) adds multiplication and unit `one` element,
[Field](http://mathworld.wolfram.com/Field.html) adds division operation.

Typical case of `Field` is the `RealField` which works on doubles. And typical case of `Space` is a `VectorSpace`.

In some cases algebra context could hold additional operation like `exp` or `sin`, in this case it inherits appropriate
interface. Also a context could have an operation which produces an element outside of its context. For example
`Matrix` `dot` operation produces a matrix with new dimensions which can be incompatible with initial matrix in
terms of linear operations.

## Algebra element

In order to achieve more familiar behavior (where you apply operations directly to mathematical objects), without involving contexts
`kmath` introduces special type objects called `MathElement`. A `MathElement` is basically some object coupled to
a mathematical context. For example `Complex` is the pair of real numbers representing real and imaginary parts,
but it also holds reference to the `ComplexField` singleton which allows to perform direct operations on `Complex`
numbers without explicit involving the context like:

```kotlin
    val c1 = Complex(1.0, 1.0)
    val c2 = Complex(1.0, -1.0)
    val c3 = c1 + c2 + 3.0.toComplex()
    //or with field notation:
    val c4 = ComplexField.run{c1 + i - 2.0}
```

Both notations have their pros and cons.

The hierarchy for algebra elements follows the hierarchy for the corresponding algebra.

**MathElement** <- **SpaceElement** <- **RingElement** <- **FieldElement**

**MathElement** is the generic common ancestor of the class with context.

One important distinction between algebra elements and algebra contexts is that algebra element has three type parameters:

1. The type of elements, field operates on.
2. The self-type of the element returned from operation (must be algebra element).
3. The type of the algebra over first type-parameter.

The middle type is needed in case algebra members do not store context. For example, it is not possible to add
a context to regular `Double`. The element performs automatic conversions from context types and back.
One should used context operations in all important places. The performance of element operations is not guaranteed.

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
val c1 = Complex(1.0, 2.0)
val c2 = ComplexField.run{ c1 - 1.0} // Returns: [re:0.0, im: 2.0]
val c3 = ComplexField.run{ c1 - i*2.0}
```

**Note**: In theory it is possible to add behaviors directly to the context, but currently kotlin syntax does not support
that. Watch [KT-10468](https://youtrack.jetbrains.com/issue/KT-10468) and [KEEP-176](https://github.com/Kotlin/KEEP/pull/176) for updates.

## Nested fields

Contexts allow one to build more complex structures. For example, it is possible to create a `Matrix` from complex elements like so:

```kotlin
val element = NDElement.complex(shape = intArrayOf(2,2)){ index: IntArray ->
    Complex(index[0].toDouble() - index[1].toDouble(), index[0].toDouble() + index[1].toDouble())
}
```

The `element` in this example is a member of the `Field` of 2-d structures, each element of which is a member of its own
`ComplexField`. The important thing is one does not need to create a special n-d class to hold complex
numbers and implement operations on it, one just needs to provide a field for its elements.

**Note**: Fields themselves do not solve the problem of JVM boxing, but it is possible to solve with special contexts like
`MemorySpec`.
