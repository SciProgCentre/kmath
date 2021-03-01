# Algebraic Structures and Algebraic Elements

The mathematical operations in KMath are generally separated from mathematical objects. This means that to perform an 
operation, say `+`, one needs two objects of a type `T` and an algebra context, which draws appropriate operation up, 
say `Space<T>`. Next one needs to run the actual operation in the context:

```kotlin
import space.kscience.kmath.operations.*

val a: T = ...
val b: T = ...
val space: Space<T> = ...

val c = space { a + b }
```

At first glance, this distinction seems to be a needless complication, but in fact one needs to remember that in 
mathematics, one could draw up different operations on same objects. For example, one could use different types of 
geometry for vectors.

## Algebraic Structures

Mathematical contexts have the following hierarchy:

**Algebra** ← **Space** ← **Ring** ← **Field**

These interfaces follow real algebraic structures:

- [Space](https://mathworld.wolfram.com/VectorSpace.html) defines addition, its neutral element (i.e. 0) and scalar 
multiplication;
- [Ring](http://mathworld.wolfram.com/Ring.html) adds multiplication and its neutral element (i.e. 1);
- [Field](http://mathworld.wolfram.com/Field.html) adds division operation.

A typical implementation of `Field<T>` is the `RealField` which works on doubles, and `VectorSpace` for `Space<T>`.

In some cases algebra context can hold additional operations like `exp` or `sin`, and then it inherits appropriate
interface. Also, contexts may have operations, which produce elements outside of the context. For example, `Matrix.dot` 
operation produces a matrix with new dimensions, which can be incompatible with initial matrix in terms of linear 
operations.

## Algebraic Element

To achieve more familiar behavior (where you apply operations directly to mathematical objects), without involving 
contexts KMath submits special type objects called `MathElement`. A `MathElement` is basically some object coupled to
a mathematical context. For example `Complex` is the pair of real numbers representing real and imaginary parts,
but it also holds reference to the `ComplexField` singleton, which allows performing direct operations on `Complex`
numbers without explicit involving the context like:

```kotlin
import space.kscience.kmath.operations.*

// Using elements
val c1 = Complex(1.0, 1.0)
val c2 = Complex(1.0, -1.0)
val c3 = c1 + c2 + 3.0.toComplex()

// Using context
val c4 = ComplexField { c1 + i - 2.0 }
```

Both notations have their pros and cons.

The hierarchy for algebraic elements follows the hierarchy for the corresponding algebraic structures.

**MathElement** ← **SpaceElement** ← **RingElement** ← **FieldElement**

`MathElement<C>` is the generic common ancestor of the class with context.

One major distinction between algebraic elements and algebraic contexts is that elements have three type 
parameters:

1. The type of elements, the field operates on.
2. The self-type of the element returned from operation (which has to be an algebraic element).
3. The type of the algebra over first type-parameter.

The middle type is needed for of algebra members do not store context. For example, it is impossible to add a context 
to regular `Double`. The element performs automatic conversions from context types and back. One should use context 
operations in all performance-critical places. The performance of element operations is not guaranteed.

## Spaces and Fields

KMath submits both contexts and elements for builtin algebraic structures:

```kotlin
import space.kscience.kmath.operations.*

val c1 = Complex(1.0, 2.0)
val c2 = ComplexField.i

val c3 = c1 + c2
// or
val c3 = ComplexField { c1 + c2 }
```

Also, `ComplexField` features special operations to mix complex and real numbers, for example:

```kotlin
import space.kscience.kmath.operations.*

val c1 = Complex(1.0, 2.0)
val c2 = ComplexField { c1 - 1.0 } // Returns: Complex(re=0.0, im=2.0)
val c3 = ComplexField { c1 - i * 2.0 }
```

**Note**: In theory it is possible to add behaviors directly to the context, but as for now Kotlin does not support 
that. Watch [KT-10468](https://youtrack.jetbrains.com/issue/KT-10468) and 
[KEEP-176](https://github.com/Kotlin/KEEP/pull/176) for updates.

## Nested fields

Contexts allow one to build more complex structures. For example, it is possible to create a `Matrix` from complex 
elements like so:

```kotlin
val element = NDElement.complex(shape = intArrayOf(2, 2)) { index: IntArray ->
    Complex(index[0].toDouble() - index[1].toDouble(), index[0].toDouble() + index[1].toDouble())
}
```

The `element` in this example is a member of the `Field` of 2D structures, each element of which is a member of its own
`ComplexField`. It is important one does not need to create a special n-d class to hold complex
numbers and implement operations on it, one just needs to provide a field for its elements.

**Note**: Fields themselves do not solve the problem of JVM boxing, but it is possible to solve with special contexts like
`MemorySpec`.
