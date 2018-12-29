# Context-oriented mathematics

## The problem

A known problem for implementing mathematics in statically-typed languages (but not only in them) is that different
sets of mathematical operators can be defined on the same mathematical objects. Sometimes there is no single way to
treat some operations, including basic arithmetic operations, on a Java/Kotlin `Number`. Sometimes there are different ways to
define the same structure, such as Euclidean and elliptic geometry vector spaces over real vectors. Another problem arises when
one wants to add some kind of behavior to an existing entity. In dynamic languages those problems are usually solved
by adding dynamic context-specific behaviors at runtime, but this solution has a lot of drawbacks.

## Context-oriented approach

One possible solution to these problems is to divorce numerical representations from behaviors.
For example in Kotlin one can define a separate class which represents some entity without any operations,
ex. a complex number:

```kotlin
data class Complex(val re: Double, val im: Double)
```

And then to define a separate class or singleton, representing an operation on those complex numbers:

```kotlin
object ComplexOperations {
    operator fun Complex.plus(other: Complex) = Complex(re + other.re, im + other.im)
    operator fun Complex.minus(other: Complex) = Complex(re - other.re, im - other.im)
}
```

In Java, applying such external operations could be very cumbersome, but Kotlin has a unique feature which allows us
implement this naturally: [extensions with receivers](https://kotlinlang.org/docs/reference/extensions.html#extension-functions).
In Kotlin, an operation on complex number could be implemented as:

```kotlin
with(ComplexOperations) { c1 + c2 - c3 }
```

Kotlin also allows the creation of functions with receivers:

```kotlin
fun ComplexOperations.doSomethingWithComplex(c1: Complex, c2: Complex, c3: Complex) = c1 + c2 - c3

ComplexOperations.doComethingWithComplex(c1, c2, c3)
```

In fact, whole parts of a program may be run within a mathematical context or even multiple nested contexts.

In KMath, contexts are not only responsible for operations, but also for raw object creation and advanced features.

## Other possibilities

### Type classes

An obvious candidate to get more or less the same functionality is the type class, which allows one to bind a behavior to
a specific type without modifying the type itself. On the plus side, type classes do not require explicit context
declaration, so the code looks cleaner. On the minus side, if there are different sets of behaviors for the same types,
it is impossible to combine them into one module. Also, unlike type classes, context can have parameters or even
state. For example in KMath, sizes and strides for `NDElement` or `Matrix` could be moved to context to optimize
performance in case of a large amount of structures.

### Wildcard imports and importing-on-demand

Sometimes, one may wish to use a single context throughout a file. In this case, is possible to import all members
from a package or file, via `import context.complex.*`. Effectively, this is the same as enclosing an entire file
with a single context. However when using multiple contexts, this technique can introduce operator ambiguity, due to
namespace pollution. If there are multiple scoped contexts which define the same operation, it is still possible to
to import specific operations as needed, without using an explicit context with extension functions, for example:

```
import context.complex.op1
import context.quaternion.op2
```
