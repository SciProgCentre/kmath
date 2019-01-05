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
`Matrix` `dot` operation produces a matrix with new dimensions which could not be compatible with initial matrix in
terms of linear operations.

## Algebra element

In order to achieve more familiar behavior (where you apply operations directly to mathematica objects), without involving contexts
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
