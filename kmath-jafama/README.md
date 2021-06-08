# Module kmath-jafama

Jafama based implementation of DoubleField of kmath-operations.

- JafamaDoubleField : DoubleField implementation using FastMath
- StrictJafamaDoubleField - DoubleField implementation using StrictFastMath

## Examples

Different Operations on DoubleField

```kotlin
package space.kscience.kmath.jafama

import net.jafama.FastMath


fun main(){
    val a = JafamaDoubleField.number(2.0)
    val b = StrictJafamaDoubleField.power(FastMath.E,a)
    println(JafamaDoubleField.add(b,a))
    println(StrictJafamaDoubleField.ln(b))
}
```

## Benchmarks
Comparing Operations on DoubleField and JafamaDoubleField 
```bash
jvm: space.kscience.kmath.benchmarks.JafamaBenchmark.coreBench

Warm-up 1: 242556635.528 ops/s
Iteration 1: 236249893.335 ops/s
Iteration 2: 294526940.968 ops/s
Iteration 3: 295973752.533 ops/s
Iteration 4: 296467676.763 ops/s
Iteration 5: 295929441.421 ops/s

283829541.004 ±(99.9%) 102456604.440 ops/s [Average]
  (min, avg, max) = (236249893.335, 283829541.004, 296467676.763), stdev = 26607654.808
  CI (99.9%): [181372936.564, 386286145.444] (assumes normal distribution)

jvm: space.kscience.kmath.benchmarks.JafamaBenchmark.jafamaBench

Warm-up 1: 234737640.196 ops/s
Iteration 1: 231689614.905 ops/s
Iteration 2: 296629612.909 ops/s
Iteration 3: 297456237.453 ops/s
Iteration 4: 296754794.513 ops/s
Iteration 5: 293722557.848 ops/s

283250563.526 ±(99.9%) 111125582.233 ops/s [Average]
  (min, avg, max) = (231689614.905, 283250563.526, 297456237.453), stdev = 28858960.811
  CI (99.9%): [172124981.293, 394376145.759] (assumes normal distribution)

jvm: space.kscience.kmath.benchmarks.JafamaBenchmark.strictJafamaBench

Warm-up 1: 234895701.589 ops/s
Iteration 1: 236061284.195 ops/s
Iteration 2: 296894799.416 ops/s
Iteration 3: 286852020.677 ops/s
Iteration 4: 284021863.614 ops/s
Iteration 5: 284404358.656 ops/s

277646865.312 ±(99.9%) 91748868.927 ops/s [Average]
  (min, avg, max) = (236061284.195, 277646865.312, 296894799.416), stdev = 23826889.899
  CI (99.9%): [185897996.385, 369395734.239] (assumes normal distribution)

```
