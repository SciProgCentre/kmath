# Context-oriented mathematics

## The problem
A known problem for implementing mathematics in statically-typed languages (and not only in them) is that different 
sets of mathematical operation could be defined on the same mathematical objects. Sometimes there is not single way to
treat some operations like basic arithmetic operations on Java/Kotlin `Number`. Sometimes there are different ways to do
the same thing like Euclidean and elliptic geometry vector spaces defined over real vectors. Another problem arises when 
one wants to add some kind of behavior to existing entity. In dynamic languages those problems are usually solved
by adding dynamic context-specific behaviors in runtime, but this solution has a lot of drawbacks.

## Context-oriented approach
One of possible solutions to those problems is to completely separate object numerical representations from behaviors.
In terms of kotlin it means to have separate class to represent some entity without any operations, 
for example a complex number:

```kotlin
data class Complex(val re: Double, val im: Double)
```
And a separate class or singleton, representing operation on those complex numbers:
```kotlin
object: ComplexOperations{
    operator fun Complex.plus(other: Complex) = Complex(re + other.re, im + other.im)
    operator fun Complex.minus(other: Complex) = Complex(re - other.re, im - other.im)
}
```

In Java, application of such external operations could be very cumbersome, but Kotlin has a unique feature which allows
to treat this situation: blocks with receivers. So in kotlin, operation on complex number could beimplemented as:
```kotlin
with(ComplexOperations){c1 + c2 - c3}
```
Kotlin also allows to create functions with receivers:
```kotlin
fun ComplexOperations.doSomethingWithComplex(c1: Complex, c2: Complex, c3: Complex) = c1 + c2 - c3

ComplexOperations.doComethingWithComplex(c1,c2,c3)
```

In fact, whole parts of proram could run in a mathematical context or even multiple nested contexts. 

In `kmath` contexts are responsible not only for operations, but also for raw object creation and advanced features.

## Other possibilities

An obvious candidate to get more or less the same functionality is type-class feature. It allows to bind a behavior to
a specific type without modifying the type itself. On a plus side, type-classes do not require explicit context 
declaration, so the code looks cleaner. On the minus side, if there are different sets of behaviors for the same types,
it is impossible to combine them in the single module. Also, unlike type-classes, context could have parameters or even
state. For example in `kmath`, sizes and strides for `NDElement` or `Matrix` could be moved to context to optimize 
performance in case of large amount of structures.