Bintray: [ ![Download](https://api.bintray.com/packages/mipt-npm/scientifik/kmath-core/images/download.svg) ](https://bintray.com/mipt-npm/scientifik/kmath-core/_latestVersion)

# KMath
The Kotlin MATHematics library is intended as a Kotlin-based analog to Python's `numpy` library. In contrast to `numpy` and `scipy` it is modular and has a lightweight core.

## Features

Actual feature list is [here](doc/features.md)

* **Algebra**
    * Algebraic structures like rings, spaces and field (**TODO** add example to wiki)
    * Basic linear algebra operations (sums, products, etc.), backed by the `Space` API.
    * Complex numbers backed by the `Field` API (meaning that they will be usable in any structure like vectors and N-dimensional arrays).
    * Advanced linear algebra operations like matrix inversion and LU decomposition.

* **Array-like structures** Full support of many-dimenstional array-like structures 
including mixed arithmetic operations and function operations over arrays and numbers (with the added benefit of static type checking).

* **Expressions** By writing a single mathematical expression
once, users will be able to apply different types of objects to the expression by providing a context. Expressions
can be used for a wide variety of purposes from high performance calculations to code generation.

* **Histograms** Fast multi-dimensional histograms.

* **Streaming** Streaming operations on mathematica objects and objects buffers.

* **Commons-math wrapper** It is planned to gradually wrap most parts of [Apache commons-math](http://commons.apache.org/proper/commons-math/)
                           library in Kotlin code and maybe rewrite some parts to better suit the Kotlin programming paradigm, however there is no fixed roadmap for that. Feel free
                           to submit a feature request if you want something to be done first.
                           
* **Koma wrapper** [Koma](https://github.com/kyonifer/koma) is a well established numerics library in kotlin, specifically linear algebra.
The plan is to have wrappers for koma implementations for compatibility with kmath API.

## Planned features

* **Messaging** A mathematical notation to support multi-language and multi-node communication for mathematical tasks.

* **Array statistics** 

* **Integration** Univariate and multivariate integration framework.

* **Probability and distributions**

* **Fitting** Non-linear curve fitting facilities

## Multi-platform support

KMath is developed as a multi-platform library, which means that most of interfaces are declared in the [common module](kmath-core/src/commonMain).
Implementation is also done in the common module wherever possible. In some cases, features are delegated to
platform-specific implementations even if they could be done in the common module for performance reasons.
Currently, the JVM is the main focus of development, however Kotlin/Native and Kotlin/JS contributions are also welcome.

## Performance

Calculation performance is one of major goals of KMath in the future, but in some cases it is not possible to achieve
both performance and flexibility. We expect to focus on creating convenient universal API first and then work on
increasing performance for specific cases. We expect the worst KMath benchmarks will perform better than native Python,
but worse than optimized native/SciPy (mostly due to boxing operations on primitive numbers). The best performance
of optimized parts should be better than SciPy.

## Releases

Working builds can be obtained here: [![](https://jitpack.io/v/altavir/kmath.svg)](https://jitpack.io/#altavir/kmath).

### Development

The project is currently in pre-release stage. Nightly builds can be used by adding an additional repository to the Gradle config like so:

```groovy
repositories {
    maven { url = "http://npm.mipt.ru:8081/artifactory/gradle-dev" }
    mavenCentral()
} 
```

or for the Gradle Kotlin DSL:

```kotlin
repositories {
    maven("http://npm.mipt.ru:8081/artifactory/gradle-dev")
    mavenCentral()
} 
```

Then use a regular dependency like so:

```groovy
api "scientifik:kmath-core-jvm:0.1.0-dev"
```

or in the Gradle Kotlin DSL:

```kotlin
api("scientifik:kmath-core-jvm:0.1.0-dev")
```

### Release

Release artifacts are accessible from bintray with following configuration:

```kotlin
repositories{
    maven("https://dl.bintray.com/mipt-npm/scientifik")
}

dependencies{
    api("scientifik:kmath-core-jvm:0.1.0")
}
```

## Contributing

The project requires a lot of additional work. Please fill free to contribute in any way and propose new features.
