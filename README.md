[![JetBrains Research](https://jb.gg/badges/research.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
[![DOI](https://zenodo.org/badge/129486382.svg)](https://zenodo.org/badge/latestdoi/129486382)

![Gradle build](https://github.com/mipt-npm/kmath/workflows/Gradle%20build/badge.svg)

Bintray:        [ ![Download](https://api.bintray.com/packages/mipt-npm/scientifik/kmath-core/images/download.svg) ](https://bintray.com/mipt-npm/scientifik/kmath-core/_latestVersion)

Bintray-dev:    [ ![Download](https://api.bintray.com/packages/mipt-npm/dev/kmath-core/images/download.svg) ](https://bintray.com/mipt-npm/dev/kmath-core/_latestVersion)

# KMath
Could be pronounced as `key-math`.
The Kotlin MATHematics library is intended as a Kotlin-based analog to Python's `numpy` library. In contrast to `numpy` and `scipy` it is modular and has a lightweight core.

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

Actual feature list is [here](doc/features.md)

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
                           
* **Koma wrapper** [Koma](https://github.com/kyonifer/koma) is a well established numerics library in Kotlin, specifically linear algebra.
The plan is to have wrappers for koma implementations for compatibility with kmath API.

## Planned features

* **Messaging** A mathematical notation to support multi-language and multi-node communication for mathematical tasks.

* **Array statistics** 

* **Integration** Univariate and multivariate integration framework.

* **Probability and distributions**

* **Fitting** Non-linear curve fitting facilities

## Multi-platform support

KMath is developed as a multi-platform library, which means that most of interfaces are declared in the [common module](kmath-core/src/commonMain). Implementation is also done in the common module wherever possible. In some cases, features are delegated to platform-specific implementations even if they could be done in the common module for performance reasons. Currently, the JVM is the main focus of development, however Kotlin/Native and Kotlin/JS contributions are also welcome.

## Performance

Calculation performance is one of major goals of KMath in the future, but in some cases it is not possible to achieve both performance and flexibility. We expect to focus on creating convenient universal API first and then work on increasing performance for specific cases. We expect the worst KMath benchmarks will perform better than native Python, but worse than optimized native/SciPy (mostly due to boxing operations on primitive numbers). The best performance of optimized parts could be better than SciPy.

### Dependency

Release artifacts are accessible from bintray with following configuration (see documentation for [kotlin-multiplatform](https://kotlinlang.org/docs/reference/multiplatform.html) form more details):

```kotlin
repositories{
    maven("https://dl.bintray.com/mipt-npm/scientifik")
}

dependencies{
    api("scientifik:kmath-core:${kmathVersion}")
    //api("scientifik:kmath-core-jvm:${kmathVersion}") for jvm-specific version
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
