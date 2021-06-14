# Module kmath-jafama

Integration with [Jafama](https://github.com/jeffhain/jafama).

${features}

${artifact}

## Example usage

All the `DoubleField` uses can be replaced with `JafamaDoubleField` or `StrictJafamaDoubleField`.

```kotlin
import space.kscience.kmath.jafama.*
import space.kscience.kmath.operations.*

fun main() {
    val a = 2.0
    val b = StrictJafamaDoubleField { exp(a) }
    println(JafamaDoubleField { b + a })
    println(StrictJafamaDoubleField { ln(b) })
}
```

## Performance

According to benchmarking data, on Hotspot Jafama functions are 20% faster than JDK math. On GraalVM, they are slower.

<details>
<summary>Raw data:</summary>

**Hotspot**

```
jvm: space.kscience.kmath.benchmarks.JafamaBenchmark.coreBench

Warm-up 1: 11.447 ops/s
Iteration 1: 13.354 ops/s
Iteration 2: 14.237 ops/s
Iteration 3: 14.708 ops/s
Iteration 4: 14.629 ops/s
Iteration 5: 14.692 ops/s

14.324 ±(99.9%) 2.217 ops/s [Average]
  (min, avg, max) = (13.354, 14.324, 14.708), stdev = 0.576
  CI (99.9%): [12.107, 16.541] (assumes normal distribution)

jvm: space.kscience.kmath.benchmarks.JafamaBenchmark.jafamaBench

Warm-up 1: 15.628 ops/s
Iteration 1: 15.991 ops/s
Iteration 2: 16.633 ops/s
Iteration 3: 16.583 ops/s
Iteration 4: 16.716 ops/s
Iteration 5: 16.762 ops/s

16.537 ±(99.9%) 1.205 ops/s [Average]
  (min, avg, max) = (15.991, 16.537, 16.762), stdev = 0.313
  CI (99.9%): [15.332, 17.743] (assumes normal distribution)

jvm: space.kscience.kmath.benchmarks.JafamaBenchmark.strictJafamaBench

Warm-up 1: 13.378 ops/s
Iteration 1: 15.049 ops/s
Iteration 2: 14.468 ops/s
Iteration 3: 14.469 ops/s
Iteration 4: 14.753 ops/s
Iteration 5: 14.958 ops/s

14.739 ±(99.9%) 1.038 ops/s [Average]
  (min, avg, max) = (14.468, 14.739, 15.049), stdev = 0.269
  CI (99.9%): [13.701, 15.777] (assumes normal distribution)
```

**GraalVM**

```
jvm: space.kscience.kmath.benchmarks.JafamaBenchmark.coreBench

Warm-up 1: 14.357 ops/s
Iteration 1: 14.768 ops/s
Iteration 2: 14.922 ops/s
Iteration 3: 14.966 ops/s
Iteration 4: 14.805 ops/s
Iteration 5: 14.520 ops/s

14.796 ±(99.9%) 0.671 ops/s [Average]
  (min, avg, max) = (14.520, 14.796, 14.966), stdev = 0.174
  CI (99.9%): [14.125, 15.468] (assumes normal distribution)

jvm: space.kscience.kmath.benchmarks.JafamaBenchmark.jafamaBench

Warm-up 1: 11.592 ops/s
Iteration 1: 12.174 ops/s
Iteration 2: 11.734 ops/s
Iteration 3: 11.939 ops/s
Iteration 4: 12.026 ops/s
Iteration 5: 12.221 ops/s

12.019 ±(99.9%) 0.752 ops/s [Average]
  (min, avg, max) = (11.734, 12.019, 12.221), stdev = 0.195
  CI (99.9%): [11.267, 12.771] (assumes normal distribution)

jvm: space.kscience.kmath.benchmarks.JafamaBenchmark.strictJafamaBench

Warm-up 1: 12.097 ops/s
Iteration 1: 13.072 ops/s
Iteration 2: 13.112 ops/s
Iteration 3: 13.103 ops/s
Iteration 4: 12.950 ops/s
Iteration 5: 13.011 ops/s

13.049 ±(99.9%) 0.263 ops/s [Average]
  (min, avg, max) = (12.950, 13.049, 13.112), stdev = 0.068
  CI (99.9%): [12.787, 13.312] (assumes normal distribution)
```

</details>
