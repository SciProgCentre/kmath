[![JetBrains Research](https://jb.gg/badges/research.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
[![DOI](https://zenodo.org/badge/129486382.svg)](https://zenodo.org/badge/latestdoi/129486382)

![Gradle build](https://github.com/mipt-npm/kmath/workflows/Gradle%20build/badge.svg)

Bintray:        [ ![Download](https://api.bintray.com/packages/mipt-npm/kscience/kmath-core/images/download.svg) ](https://bintray.com/mipt-npm/kscience/kmath-core/_latestVersion)

Bintray-dev:    [ ![Download](https://api.bintray.com/packages/mipt-npm/dev/kmath-core/images/download.svg) ](https://bintray.com/mipt-npm/dev/kmath-core/_latestVersion)

# KMath
Could be pronounced as `key-math`.
The Kotlin MATHematics library  was initially intended as a Kotlin-based analog to Python's `numpy` library. Later we found that kotlin is much more flexible language and allows superior architecture designs. In contrast to `numpy` and `scipy` it is modular and has a lightweight core. The `numpy`-like experience could be achieved with [kmath-for-real](/kmath-for-real) extension module.

## Publications and talks
* [A conceptual article about context-oriented design](https://proandroiddev.com/an-introduction-context-oriented-programming-in-kotlin-2e79d316b0a2)
* [Another article about context-oriented design](https://proandroiddev.com/diving-deeper-into-context-oriented-programming-in-kotlin-3ecb4ec38814)
* [ACAT 2019 conference paper](https://aip.scitation.org/doi/abs/10.1063/1.5130103)

# Goal
* Provide a flexible and powerful API to work with mathematics abstractions in Kotlin-multiplatform (JVM and JS for now and Native in future). 
* Provide basic multiplatform implementations for those abstractions (without significant performance optimization).
* Provide bindings and wrappers with those abstractions for popular optimized platform libraries.

## Non-goals
* Be like Numpy. It was the idea at the beginning, but we decided that we can do better in terms of API.
* Provide best performance out of the box. We have specialized libraries for that. Need only API wrappers for them.
* Cover all cases as immediately and in one bundle. We will modularize everything and add new features gradually.
* Provide specialized behavior in the core. API is made generic on purpose, so one needs to specialize for types, like for `Double` in the core. For that we will have specialization modules like `for-real`, which will give better experience for those, who want to work with specific types.

## Features

Actual feature list is [here](/docs/features.md)

* **Algebra**
    * Algebraic structures like rings, spaces and field (**TODO** add example to wiki)
    * Basic linear algebra operations (sums, products, etc.), backed by the `Space` API.
    * Complex numbers backed by the `Field` API (meaning that they will be usable in any structure like vectors and N-dimensional arrays).
    * Advanced linear algebra operations like matrix inversion and LU decomposition.

* **Array-like structures** Full support of many-dimensional array-like structures 
including mixed arithmetic operations and function operations over arrays and numbers (with the added benefit of static type checking).

* **Expressions** By writing a single mathematical expression
once, users will be able to apply different types of objects to the expression by providing a context. Expressions
can be used for a wide variety of purposes from high performance calculations to code generation.

* **Histograms** Fast multi-dimensional histograms.

* **Streaming** Streaming operations on mathematical objects and objects buffers.

* **Type-safe dimensions** Type-safe dimensions for matrix operations.

* **Commons-math wrapper** It is planned to gradually wrap most parts of [Apache commons-math](http://commons.apache.org/proper/commons-math/)
                           library in Kotlin code and maybe rewrite some parts to better suit the Kotlin programming paradigm, however there is no fixed roadmap for that. Feel free
                           to submit a feature request if you want something to be done first.

* **EJML wrapper** Provides EJML `SimpleMatrix` wrapper consistent with the core matrix structures.

## Planned features

* **Messaging** A mathematical notation to support multi-language and multi-node communication for mathematical tasks.

* **Array statistics** 

* **Integration** Univariate and multivariate integration framework.

* **Probability and distributions**

* **Fitting** Non-linear curve fitting facilities

## Modules

<hr/>

* ### [examples](examples)
> 
>
> **Maturity**: EXPERIMENTAL
<hr/>

* ### [kmath-ast](kmath-ast)
> 
>
> **Maturity**: EXPERIMENTAL
<hr/>

* ### [kmath-commons](kmath-commons)
> 
>
> **Maturity**: EXPERIMENTAL
<hr/>

* ### [kmath-core](kmath-core)
> Core classes, algebra definitions, basic linear algebra
>
> **Maturity**: DEVELOPMENT
>
> **Features:**
> - [algebras](kmath-core/src/commonMain/kotlin/kscience/kmath/operations/Algebra.kt) : Algebraic structures: contexts and elements
> - [nd](kmath-core/src/commonMain/kotlin/kscience/kmath/structures/NDStructure.kt) : Many-dimensional structures
> - [buffers](kmath-core/src/commonMain/kotlin/kscience/kmath/structures/Buffers.kt) : One-dimensional structure
> - [expressions](kmath-core/src/commonMain/kotlin/kscience/kmath/expressions) : Functional Expressions
> - [domains](kmath-core/src/commonMain/kotlin/kscience/kmath/domains) : Domains
> - [autodif](kmath-core/src/commonMain/kotlin/kscience/kmath/misc/AutoDiff.kt) : Automatic differentiation

<hr/>

* ### [kmath-coroutines](kmath-coroutines)
> 
>
> **Maturity**: EXPERIMENTAL
<hr/>

* ### [kmath-dimensions](kmath-dimensions)
> 
>
> **Maturity**: EXPERIMENTAL
<hr/>

* ### [kmath-for-real](kmath-for-real)
> 
>
> **Maturity**: EXPERIMENTAL
<hr/>

* ### [kmath-functions](kmath-functions)
> 
>
> **Maturity**: EXPERIMENTAL
<hr/>

* ### [kmath-geometry](kmath-geometry)
> 
>
> **Maturity**: EXPERIMENTAL
<hr/>

* ### [kmath-histograms](kmath-histograms)
> 
>
> **Maturity**: EXPERIMENTAL
<hr/>

* ### [kmath-memory](kmath-memory)
> 
>
> **Maturity**: EXPERIMENTAL
<hr/>

* ### [kmath-prob](kmath-prob)
> 
>
> **Maturity**: EXPERIMENTAL
<hr/>

* ### [kmath-viktor](kmath-viktor)
> 
>
> **Maturity**: EXPERIMENTAL
<hr/>


## Multi-platform support

KMath is developed as a multi-platform library, which means that most of the interfaces are declared in the [common module](/kmath-core/src/commonMain). Implementation is also done in the common module wherever possible. In some cases, features are delegated to platform-specific implementations even if they could be done in the common module for performance reasons. Currently, the JVM is the main focus of development, however Kotlin/Native and Kotlin/JS contributions are also welcome.

## Performance

Calculation performance is one of major goals of KMath in the future, but in some cases it is not possible to achieve both performance and flexibility. We expect to focus on creating convenient universal API first and then work on increasing performance for specific cases. We expect the worst KMath benchmarks will perform better than native Python, but worse than optimized native/SciPy (mostly due to boxing operations on primitive numbers). The best performance of optimized parts could be better than SciPy.

### Dependency

Release artifacts are accessible from bintray with following configuration (see documentation for [kotlin-multiplatform](https://kotlinlang.org/docs/reference/multiplatform.html) form more details):

```kotlin
repositories{
    maven("https://dl.bintray.com/mipt-npm/kscience")
}

dependencies{
    api("kscience.kmath:kmath-core:0.2.0-dev-1")
    //api("kscience.kmath:kmath-core-jvm:0.2.0-dev-1") for jvm-specific version
}
```

Gradle `6.0+` is required for multiplatform artifacts.

### Development

Development builds are accessible from the reposirtory 
```kotlin
repositories{
    maven("https://dl.bintray.com/mipt-npm/dev")
}
```
with the same artifact names.

## Contributing

The project requires a lot of additional work. Please feel free to contribute in any way and propose new features.
