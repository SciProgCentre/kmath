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

Warm-up 1: 384414358.081 ops/s
Iteration 1: 374827571.951 ops/s
Iteration 2: 479335182.332 ops/s
Iteration 3: 475483069.577 ops/s
Iteration 4: 478235949.414 ops/s
Iteration 5: 472256385.114 ops/s

456027631.678 ±(99.9%) 175106815.384 ops/s [Average]
  (min, avg, max) = (374827571.951, 456027631.678, 479335182.332), stdev = 45474683.880
  CI (99.9%): [280920816.294, 631134447.061] (assumes normal distribution)

jvm: space.kscience.kmath.benchmarks.JafamaBenchmark.jafamaBench

Warm-up 1: 359418665.041 ops/s
Iteration 1: 335704885.798 ops/s
Iteration 2: 427684801.542 ops/s
Iteration 3: 452193034.265 ops/s
Iteration 4: 433855064.931 ops/s
Iteration 5: 453863386.566 ops/s

420660234.620 ±(99.9%) 188028426.875 ops/s [Average]
  (min, avg, max) = (335704885.798, 420660234.620, 453863386.566), stdev = 48830385.349
  CI (99.9%): [232631807.746, 608688661.495] (assumes normal distribution)

jvm: space.kscience.kmath.benchmarks.JafamaBenchmark.kotlinMathBench

Warm-up 1: 371570418.113 ops/s
Iteration 1: 379281146.127 ops/s
Iteration 2: 465234403.109 ops/s
Iteration 3: 470621634.240 ops/s
Iteration 4: 467074553.006 ops/s
Iteration 5: 466424840.144 ops/s

449727315.325 ±(99.9%) 151837475.500 ops/s [Average]
  (min, avg, max) = (379281146.127, 449727315.325, 470621634.240), stdev = 39431710.207
  CI (99.9%): [297889839.825, 601564790.825] (assumes normal distribution)

jvm: space.kscience.kmath.benchmarks.JafamaBenchmark.strictJafamaBench

Warm-up 1: 371241281.065 ops/s
Iteration 1: 374490259.387 ops/s
Iteration 2: 464995837.424 ops/s
Iteration 3: 469788706.385 ops/s
Iteration 4: 469528470.682 ops/s
Iteration 5: 456727921.978 ops/s

447106239.171 ±(99.9%) 157629035.980 ops/s [Average]
  (min, avg, max) = (374490259.387, 447106239.171, 469788706.385), stdev = 40935760.071
  CI (99.9%): [289477203.192, 604735275.151] (assumes normal distribution)
```
