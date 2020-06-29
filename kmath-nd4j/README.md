# ND4J NDStructure implementation (`kmath-nd4j`)

This subproject implements the following features:

- NDStructure wrapper for INDArray.
- Optimized NDRing implementation for INDArray storing Ints.
- Optimized NDField implementation for INDArray storing Floats and Doubles.

> #### Artifact:
> This module is distributed in the artifact `scientifik:kmath-nd4j:0.1.4-dev-8`.
> 
> **Gradle:**
>
> ```gradle
> repositories {
>     maven { url 'https://dl.bintray.com/mipt-npm/scientifik' }
>     maven { url 'https://dl.bintray.com/mipt-npm/dev' }
> }
> 
> dependencies {
>     implementation 'scientifik:kmath-nd4j:0.1.4-dev-8'
> }
> ```
> **Gradle Kotlin DSL:**
>
> ```kotlin
> repositories {
>     maven("https://dl.bintray.com/mipt-npm/scientifik")
>     maven("https://dl.bintray.com/mipt-npm/dev")
> }
> 
> dependencies {
>     implementation("scientifik:kmath-nd4j:0.1.4-dev-8")
> }
> ```
>

## Examples

NDStructure wrapper for INDArray:

```kotlin
import org.nd4j.linalg.factory.*
import scientifik.kmath.nd4j.*
import scientifik.kmath.structures.*

val array = Nd4j.ones(2, 2)!!.asRealStructure()
println(array[0, 0]) // 1.0
array[intArrayOf(0, 0)] = 24.0
println(array[0, 0]) // 24.0
```

Fast element-wise arithmetics for INDArray:

```kotlin
import org.nd4j.linalg.factory.*
import scientifik.kmath.nd4j.*
import scientifik.kmath.operations.*

val field = RealINDArrayField(intArrayOf(2, 2))
val array = Nd4j.rand(2, 2)!!.asRealStructure()

val res = field {
    (25.0 / array + 20) * 4
}

println(res.ndArray)
// [[  250.6449,  428.5840], 
//  [  269.7913,  202.2077]]
```


Contributed by [Iaroslav Postovalov](https://github.com/CommanderTvis).
