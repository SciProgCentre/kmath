# ND-structure generation and operations

**TODO**

# Performance for n-dimensional structures operations

One of the most sought after features of mathematical libraries is the high-performance operations on n-dimensional
structures. In `kmath` performance depends on which particular context was used for operation.

Let us consider following contexts:
```kotlin
    // automatically build context most suited for given type.
    val autoField = NDField.auto(RealField, dim, dim)
    // specialized nd-field for Double. It works as generic Double field as well
    val specializedField = NDField.real(dim, dim)
    //A generic boxing field. It should be used for objects, not primitives.
    val genericField = NDField.buffered(RealField, dim, dim)
```
Now let us perform several tests and see which implementation is best suited for each case:

## Test case

In order to test performance we will take 2d-structures with `dim = 1000` and add a structure filled with `1.0`
to it `n = 1000` times.

## Specialized
The code to run this looks like:
```kotlin
    specializedField.run {
        var res: NDBuffer<Double> = one
        repeat(n) {
            res += 1.0
        }
    }
```
The performance of this code is the best of all tests since it inlines all operations and is specialized for operation
with doubles. We will measure everything else relative to this one, so time for this test will be `1x` (real time
on my computer is about 4.5 seconds). The only problem with this approach is that it requires to specify type
from the beginning. Everyone do so anyway, so it is the recommended approach.

## Automatic
Let's do the same with automatic field inference:
```kotlin
    autoField.run {
        var res = one
        repeat(n) {
            res += 1.0
        }
    }
```
Ths speed of this operation is approximately the same as for specialized case since `NDField.auto` just
returns the same `RealNDField` in this case. Of course it is usually better to use specialized method to be sure.

## Lazy
Lazy field does not produce a structure when asked, instead it generates an empty structure and fills it on-demand
using coroutines to parallelize computations.
When one calls
```kotlin
    lazyField.run {
        var res = one
        repeat(n) {
            res += 1.0
        }
    }
```
The result will be calculated almost immediately but the result will be empty. In order to get the full result
structure one needs to call all its elements. In this case computation overhead will be huge. So this field never
should be used if one expects to use the full result structure. Though if one wants only small fraction, it could
save a lot of time.

This field still could be used with reasonable performance if call code is changed:
```kotlin
    lazyField.run {
        val res = one.map {
            var c = 0.0
            repeat(n) {
                c += 1.0
            }
            c
        }

        res.elements().forEach { it.second }
    }
```
In this case it completes in about `4x-5x` time due to boxing.

## Boxing
The boxing field produced by
```kotlin
    genericField.run {
        var res: NDBuffer<Double> = one
        repeat(n) {
            res += 1.0
        }
    }
```
obviously is the slowest one, because it requires to box and unbox the `double` on each operation. It takes about
`15x` time (**TODO: there seems to be a problem here, it should be slow, but not that slow**). This field should
never be used for primitives.

## Element operation
Let us also check the speed for direct operations on elements:
```kotlin
    var res = genericField.one
    repeat(n) {
        res += 1.0
    }
```
One would expect to be at least as slow as field operation, but in fact, this one takes only `2x` time to complete.
It happens, because in this particular case it does not use actual `NDField` but instead calculated directly
via extension function.

## What about python?

Usually it is bad idea to compare the direct numerical operation performance in different languages, but it hard to
work completely without frame of reference. In this case, simple numpy code:
```python
res = np.ones((1000,1000))
for i in range(1000):
    res = res + 1.0
```
gives the completion time of about `1.1x`, which means that specialized kotlin code in fact is working faster (I think it is
because better memory management). Of course if one writes `res += 1.0`, the performance will be different,
but it would be differenc case, because numpy overrides `+=` with in-place operations. In-place operations are
available in `kmath` with `MutableNDStructure` but there is no field for it (one can still work with mapping
functions).