# ND4J NDStructure implementation (`kmath-nd4j`)

This subproject implements the following features:

 - [nd4jarraystructure](src/commonMain/kotlin/kscience/kmath/operations/Algebra.kt) : NDStructure wrapper for INDArray
 - [nd4jarrayrings](src/commonMain/kotlin/kscience/kmath/structures/NDStructure.kt) : Rings over Nd4jArrayStructure of Int and Long
 - [nd4jarrayfields](src/commonMain/kotlin/kscience/kmath/structures/Buffers.kt) : Fields over Nd4jArrayStructure of Float and Double


> #### Artifact:
>
> This module artifact: `kscience.kmath:kmath-nd4j:0.2.0-dev-4`.
>
> Bintray release version:        [ ![Download](https://api.bintray.com/packages/mipt-npm/kscience/kmath-nd4j/images/download.svg) ](https://bintray.com/mipt-npm/kscience/kmath-nd4j/_latestVersion)
>
> Bintray development version:    [ ![Download](https://api.bintray.com/packages/mipt-npm/dev/kmath-nd4j/images/download.svg) ](https://bintray.com/mipt-npm/dev/kmath-nd4j/_latestVersion)
>
> **Gradle:**
>
> ```gradle
> repositories {
>     maven { url "https://dl.bintray.com/kotlin/kotlin-eap" }
>     maven { url 'https://dl.bintray.com/mipt-npm/kscience' }
>     maven { url 'https://dl.bintray.com/mipt-npm/dev' }
>     maven { url 'https://dl.bintray.com/hotkeytlt/maven' }
> 
> }
> 
> dependencies {
>     implementation 'kscience.kmath:kmath-nd4j:0.2.0-dev-4'
> }
> ```
> **Gradle Kotlin DSL:**
>
> ```kotlin
> repositories {
>     maven("https://dl.bintray.com/kotlin/kotlin-eap")
>     maven("https://dl.bintray.com/mipt-npm/kscience")
>     maven("https://dl.bintray.com/mipt-npm/dev")
>     maven("https://dl.bintray.com/hotkeytlt/maven")
> }
> 
> dependencies {
>     implementation("kscience.kmath:kmath-nd4j:0.2.0-dev-4")
> }
> ```

## Examples

NDStructure wrapper for INDArray:

```kotlin
import org.nd4j.linalg.factory.*
import scientifik.kmath.nd4j.*
import scientifik.kmath.structures.*

val array = Nd4j.ones(2, 2).asRealStructure()
println(array[0, 0]) // 1.0
array[intArrayOf(0, 0)] = 24.0
println(array[0, 0]) // 24.0
```

Fast element-wise and in-place arithmetics for INDArray:

```kotlin
import org.nd4j.linalg.factory.*
import scientifik.kmath.nd4j.*
import scientifik.kmath.operations.*

val field = RealNd4jArrayField(intArrayOf(2, 2))
val array = Nd4j.rand(2, 2).asRealStructure()

val res = field {
    (25.0 / array + 20) * 4
}

println(res.ndArray)
// [[  250.6449,  428.5840], 
//  [  269.7913,  202.2077]]
```

Contributed by [Iaroslav Postovalov](https://github.com/CommanderTvis).
