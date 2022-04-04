# Algebraic Structures and Algebraic Elements

The mathematical operations in KMath are generally separated from mathematical objects. This means that to perform an
operation, say `+`, one needs two objects of a type `T` and an algebra context, which draws appropriate operation up,
say `Group<T>`. Next one needs to run the actual operation in the context:

```kotlin
import space.kscience.kmath.operations.*

val a: T = ...
val b: T = ...
val group: Group<T> = ...

val c = group { a + b }
```

At first glance, this distinction seems to be a needless complication, but in fact one needs to remember that in
mathematics, one could draw up different operations on same objects. For example, one could use different types of
geometry for vectors.

## Algebraic Structures

Primary mathematical contexts have the following hierarchy:

`Field <: Ring <: Group <: Algebra`

These interfaces follow real algebraic structures:

- [Group](https://mathworld.wolfram.com/Group.html) defines addition, its identity element (i.e., 0) and additive
  inverse (-x);
- [Ring](http://mathworld.wolfram.com/Ring.html) adds multiplication and its identity element (i.e., 1);
- [Field](http://mathworld.wolfram.com/Field.html) adds division operation.

A typical implementation of `Field<T>` is the `DoubleField` which works on doubles, and `VectorSpace` for `Space<T>`.

In some cases algebra context can hold additional operations like `exp` or `sin`, and then it inherits appropriate
interface. Also, contexts may have operations, which produce elements outside the context. For example, `Matrix.dot`
operation produces a matrix with new dimensions, which can be incompatible with initial matrix in linear operations.

## Spaces and Fields

KMath introduces contexts for builtin algebraic structures:

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

## Nested fields

Contexts allow one to build more complex structures. For example, it is possible to create a `Matrix` from complex
elements like so:

```kotlin
val element = NDElement.complex(shape = intArrayOf(2, 2)) { index: IntArray ->
    Complex(index[0].toDouble() - index[1].toDouble(), index[0].toDouble() + index[1].toDouble())
}
```

The `element` in this example is a member of the `Field` of 2D structures, each element of which is a member of its own
`ComplexField`. It is important one does not need to create a special n-d class to hold complex numbers and implement
operations on it, one just needs to provide a field for its elements.

**Note**: Fields themselves do not solve the problem of JVM boxing, but it is possible to solve with special contexts
like
`MemorySpec`.
