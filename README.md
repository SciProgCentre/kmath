[![JetBrains Research](https://jb.gg/badges/research.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
[![DOI](https://zenodo.org/badge/129486382.svg)](https://zenodo.org/badge/latestdoi/129486382)
![Gradle build](https://github.com/mipt-npm/kmath/workflows/Gradle%20build/badge.svg)
[![Maven Central](https://img.shields.io/maven-central/v/space.kscience/kmath-core.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22space.kscience%22)
[![Space](https://img.shields.io/badge/dynamic/xml?color=orange&label=Space&query=//metadata/versioning/latest&url=https%3A%2F%2Fmaven.pkg.jetbrains.space%2Fmipt-npm%2Fp%2Fsci%2Fmaven%2Fspace%2Fkscience%2Fkmath-core%2Fmaven-metadata.xml)](https://maven.pkg.jetbrains.space/mipt-npm/p/sci/maven/space/kscience/)

# KMath

Could be pronounced as `key-math`. The **K**otlin **Math**ematics library was initially intended as a Kotlin-based
analog to Python's NumPy library. Later we found that kotlin is much more flexible language and allows superior
architecture designs. In contrast to `numpy` and `scipy` it is modular and has a lightweight core. The `numpy`-like
experience could be achieved with [kmath-for-real](/kmath-for-real) extension module.

[Documentation site (**WIP**)](https://mipt-npm.github.io/kmath/)

## Publications and talks

* [A conceptual article about context-oriented design](https://proandroiddev.com/an-introduction-context-oriented-programming-in-kotlin-2e79d316b0a2)
* [Another article about context-oriented design](https://proandroiddev.com/diving-deeper-into-context-oriented-programming-in-kotlin-3ecb4ec38814)
* [ACAT 2019 conference paper](https://aip.scitation.org/doi/abs/10.1063/1.5130103)

# Goal

* Provide a flexible and powerful API to work with mathematics abstractions in Kotlin-multiplatform (JVM, JS and Native)
  .
* Provide basic multiplatform implementations for those abstractions (without significant performance optimization).
* Provide bindings and wrappers with those abstractions for popular optimized platform libraries.

## Non-goals

* Be like NumPy. It was the idea at the beginning, but we decided that we can do better in API.
* Provide the best performance out of the box. We have specialized libraries for that. Need only API wrappers for them.
* Cover all cases as immediately and in one bundle. We will modularize everything and add new features gradually.
* Provide specialized behavior in the core. API is made generic on purpose, so one needs to specialize for types, like
  for `Double` in the core. For that we will have specialization modules like `kmath-for-real`, which will give better
  experience for those, who want to work with specific types.

## Features and stability

KMath is a modular library. Different modules provide different features with different API stability guarantees. All
core modules are released with the same version, but with different API change policy. The features are described in
module definitions below. The module stability could have the following levels:

* **PROTOTYPE**. On this level there are no compatibility guarantees. All methods and classes form those modules could
  break any moment. You can still use it, but be sure to fix the specific version.
* **EXPERIMENTAL**. The general API is decided, but some changes could be made. Volatile API is marked
  with `@UnstableKmathAPI` or other stability warning annotations.
* **DEVELOPMENT**. API breaking generally follows semantic versioning ideology. There could be changes in minor
  versions, but not in patch versions. API is protected
  with [binary-compatibility-validator](https://github.com/Kotlin/binary-compatibility-validator) tool.
* **STABLE**. The API stabilized. Breaking changes are allowed only in major releases.

## Modules


### [benchmarks](benchmarks)
> 
>
> **Maturity**: EXPERIMENTAL

### [examples](examples)
> 
>
> **Maturity**: EXPERIMENTAL

### [kmath-ast](kmath-ast)
> 
>
> **Maturity**: EXPERIMENTAL
>
> **Features:**
> - [expression-language](kmath-ast/src/commonMain/kotlin/space/kscience/kmath/ast/parser.kt) : Expression language and its parser
> - [mst-jvm-codegen](kmath-ast/src/jvmMain/kotlin/space/kscience/kmath/asm/asm.kt) : Dynamic MST to JVM bytecode compiler
> - [mst-js-codegen](kmath-ast/src/jsMain/kotlin/space/kscience/kmath/estree/estree.kt) : Dynamic MST to JS compiler
> - [rendering](kmath-ast/src/commonMain/kotlin/space/kscience/kmath/ast/rendering/MathRenderer.kt) : Extendable MST rendering


### [kmath-commons](kmath-commons)
> 
>
> **Maturity**: EXPERIMENTAL

### [kmath-complex](kmath-complex)
> Complex numbers and quaternions.
>
> **Maturity**: PROTOTYPE
>
> **Features:**
> - [complex](kmath-complex/src/commonMain/kotlin/space/kscience/kmath/complex/Complex.kt) : Complex Numbers
> - [quaternion](kmath-complex/src/commonMain/kotlin/space/kscience/kmath/complex/Quaternion.kt) : Quaternions


### [kmath-core](kmath-core)
> Core classes, algebra definitions, basic linear algebra
>
> **Maturity**: DEVELOPMENT
>
> **Features:**
> - [algebras](kmath-core/src/commonMain/kotlin/space/kscience/kmath/operations/Algebra.kt) : Algebraic structures like rings, spaces and fields.
> - [nd](kmath-core/src/commonMain/kotlin/space/kscience/kmath/structures/StructureND.kt) : Many-dimensional structures and operations on them.
> - [linear](kmath-core/src/commonMain/kotlin/space/kscience/kmath/operations/Algebra.kt) : Basic linear algebra operations (sums, products, etc.), backed by the `Space` API. Advanced linear algebra operations like matrix inversion and LU decomposition.
> - [buffers](kmath-core/src/commonMain/kotlin/space/kscience/kmath/structures/Buffers.kt) : One-dimensional structure
> - [expressions](kmath-core/src/commonMain/kotlin/space/kscience/kmath/expressions) : By writing a single mathematical expression once, users will be able to apply different types of 
objects to the expression by providing a context. Expressions can be used for a wide variety of purposes from high 
performance calculations to code generation.
> - [domains](kmath-core/src/commonMain/kotlin/space/kscience/kmath/domains) : Domains
> - [autodiff](kmath-core/src/commonMain/kotlin/space/kscience/kmath/expressions/SimpleAutoDiff.kt) : Automatic differentiation


### [kmath-coroutines](kmath-coroutines)
> 
>
> **Maturity**: EXPERIMENTAL

### [kmath-dimensions](kmath-dimensions)
> 
>
> **Maturity**: PROTOTYPE

### [kmath-ejml](kmath-ejml)
> 
>
> **Maturity**: PROTOTYPE
>
> **Features:**
> - [ejml-vector](kmath-ejml/src/main/kotlin/space/kscience/kmath/ejml/EjmlVector.kt) : Point implementations.
> - [ejml-matrix](kmath-ejml/src/main/kotlin/space/kscience/kmath/ejml/EjmlMatrix.kt) : Matrix implementation.
> - [ejml-linear-space](kmath-ejml/src/main/kotlin/space/kscience/kmath/ejml/EjmlLinearSpace.kt) : LinearSpace implementations.


### [kmath-for-real](kmath-for-real)
> Extension module that should be used to achieve numpy-like behavior.
All operations are specialized to work with `Double` numbers without declaring algebraic contexts.
One can still use generic algebras though.
>
> **Maturity**: EXPERIMENTAL
>
> **Features:**
> - [DoubleVector](kmath-for-real/src/commonMain/kotlin/space/kscience/kmath/real/DoubleVector.kt) : Numpy-like operations for Buffers/Points
> - [DoubleMatrix](kmath-for-real/src/commonMain/kotlin/space/kscience/kmath/real/DoubleMatrix.kt) : Numpy-like operations for 2d real structures
> - [grids](kmath-for-real/src/commonMain/kotlin/space/kscience/kmath/structures/grids.kt) : Uniform grid generators


### [kmath-functions](kmath-functions)
> 
>
> **Maturity**: EXPERIMENTAL
>
> **Features:**
> - [piecewise](kmath-functions/src/commonMain/kotlin/space/kscience/kmath/functions/Piecewise.kt) : Piecewise functions.
> - [polynomials](kmath-functions/src/commonMain/kotlin/space/kscience/kmath/functions/Polynomial.kt) : Polynomial functions.
> - [linear interpolation](kmath-functions/src/commonMain/kotlin/space/kscience/kmath/interpolation/LinearInterpolator.kt) : Linear XY interpolator.
> - [spline interpolation](kmath-functions/src/commonMain/kotlin/space/kscience/kmath/interpolation/SplineInterpolator.kt) : Cubic spline XY interpolator.
> - [integration](kmath-functions/#) : Univariate and multivariate quadratures


### [kmath-geometry](kmath-geometry)
> 
>
> **Maturity**: PROTOTYPE

### [kmath-histograms](kmath-histograms)
> 
>
> **Maturity**: PROTOTYPE

### [kmath-jafama](kmath-jafama)
> 
>
> **Maturity**: PROTOTYPE
>
> **Features:**
> - [jafama-double](kmath-jafama/src/main/kotlin/space/kscience/kmath/jafama/) : Double ExtendedField implementations based on Jafama


### [kmath-jupyter](kmath-jupyter)
> 
>
> **Maturity**: PROTOTYPE

### [kmath-kotlingrad](kmath-kotlingrad)
> 
>
> **Maturity**: EXPERIMENTAL
>
> **Features:**
> - [differentiable-mst-expression](kmath-kotlingrad/src/main/kotlin/space/kscience/kmath/kotlingrad/KotlingradExpression.kt) : MST based DifferentiableExpression.
> - [scalars-adapters](kmath-kotlingrad/src/main/kotlin/space/kscience/kmath/kotlingrad/scalarsAdapters.kt) : Conversions between Kotlin∇'s SFun and MST


### [kmath-memory](kmath-memory)
> An API and basic implementation for arranging objects in a continuous memory block.
>
> **Maturity**: DEVELOPMENT

### [kmath-multik](kmath-multik)
> 
>
> **Maturity**: PROTOTYPE

### [kmath-nd4j](kmath-nd4j)
> 
>
> **Maturity**: EXPERIMENTAL
>
> **Features:**
> - [nd4jarraystructure](kmath-nd4j/#) : NDStructure wrapper for INDArray
> - [nd4jarrayrings](kmath-nd4j/#) : Rings over Nd4jArrayStructure of Int and Long
> - [nd4jarrayfields](kmath-nd4j/#) : Fields over Nd4jArrayStructure of Float and Double


### [kmath-optimization](kmath-optimization)
> 
>
> **Maturity**: EXPERIMENTAL

### [kmath-stat](kmath-stat)
> 
>
> **Maturity**: EXPERIMENTAL

### [kmath-symja](kmath-symja)
> 
>
> **Maturity**: PROTOTYPE

### [kmath-tensorflow](kmath-tensorflow)
> 
>
> **Maturity**: PROTOTYPE

### [kmath-tensors](kmath-tensors)
> 
>
> **Maturity**: PROTOTYPE
>
> **Features:**
> - [tensor algebra](kmath-tensors/src/commonMain/kotlin/space/kscience/kmath/tensors/api/TensorAlgebra.kt) : Basic linear algebra operations on tensors (plus, dot, etc.)
> - [tensor algebra with broadcasting](kmath-tensors/src/commonMain/kotlin/space/kscience/kmath/tensors/core/BroadcastDoubleTensorAlgebra.kt) : Basic linear algebra operations implemented with broadcasting.
> - [linear algebra operations](kmath-tensors/src/commonMain/kotlin/space/kscience/kmath/tensors/api/LinearOpsTensorAlgebra.kt) : Advanced linear algebra operations like LU decomposition, SVD, etc.


### [kmath-viktor](kmath-viktor)
> 
>
> **Maturity**: DEVELOPMENT


## Multi-platform support

KMath is developed as a multi-platform library, which means that most of the interfaces are declared in the
[common source sets](/kmath-core/src/commonMain) and implemented there wherever it is possible. In some cases, features
are delegated to platform-specific implementations even if they could be provided in the common module for performance
reasons. Currently, the Kotlin/JVM is the primary platform, however Kotlin/Native and Kotlin/JS contributions and
feedback are also welcome.

## Performance

Calculation performance is one of major goals of KMath in the future, but in some cases it is impossible to achieve both
performance and flexibility.

We expect to focus on creating convenient universal API first and then work on increasing performance for specific
cases. We expect the worst KMath benchmarks will perform better than native Python, but worse than optimized
native/SciPy (mostly due to boxing operations on primitive numbers). The best performance of optimized parts could be
better than SciPy.

## Requirements

KMath currently relies on JDK 11 for compilation and execution of Kotlin-JVM part. We recommend to use GraalVM-CE 11 for
execution to get better performance.

### Repositories

Release and development artifacts are accessible from mipt-npm [Space](https://www.jetbrains.com/space/)
repository `https://maven.pkg.jetbrains.space/mipt-npm/p/sci/maven` (see documentation of
[Kotlin Multiplatform](https://kotlinlang.org/docs/reference/multiplatform.html) for more details). The repository could
be reached through [repo.kotlin.link](https://repo.kotlin.link) proxy:

```kotlin
repositories {
    maven("https://repo.kotlin.link")
}

dependencies {
    api("space.kscience:kmath-core:$version")
    // api("space.kscience:kmath-core-jvm:$version") for jvm-specific version
}
```

Gradle `6.0+` is required for multiplatform artifacts.

## Contributing

The project requires a lot of additional work. The most important thing we need is a feedback about what features are
required the most. Feel free to create feature requests. We are also welcome to code contributions, especially in issues
marked with
[waiting for a hero](https://github.com/mipt-npm/kmath/labels/waiting%20for%20a%20hero) label.
