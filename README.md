# KMath
The Kotlin MATHematics library is intended as a Kotlin-based analog to Python's `numpy` library. In contrast to `numpy` and `scipy` it is modular and has a lightweight core.

## Features

* **Algebra**
    * Algebraic structures like rings, spaces and field (**TODO** add example to wiki)
    * Basic linear algebra operations (sums, products, etc.), backed by the `Space` API.
    * Complex numbers backed by the `Field` API (meaning that they will be usable in any structure like vectors and N-dimensional arrays).
    * [In progress] advanced linear algebra operations like matrix inversion and LU decomposition.
* **Array-like structures** Full support of [numpy-like ndarrays](https://docs.scipy.org/doc/numpy-1.13.0/reference/generated/numpy.ndarray.html) including mixed arithmetic operations and function operations over arrays and numbers just like in Python (with the added benefit of static type checking).

* **Expressions** Expressions are one of the ultimate goals of KMath. By writing a single mathematical expression
once, users will be able to apply different types of objects to the expression by providing a context. Exceptions
can be used for a wide variety of purposes from high performance calculations to code generation.

## Planned features

* **Common mathematics** It is planned to gradually wrap most parts of [Apache commons-math](http://commons.apache.org/proper/commons-math/) 
library in Kotlin code and maybe rewrite some parts to better suit the Kotlin programming paradigm, however there is no fixed roadmap for that. Feel free
to submit a feature request if you want something to be done first.

* **Messaging** A mathematical notation to support multi-language and multi-node communication for mathematical tasks.

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
    maven { setUrl("http://npm.mipt.ru:8081/artifactory/gradle-dev") }
    mavenCentral()
} 
```

Then use a regular dependency like so:

```groovy
compile(group: 'scientifik', name: 'kmath-core', version: '0.0.1-SNAPSHOT')
```

or in the Gradle Kotlin DSL:

```kotlin
compile(group = "scientifik", name = "kmath-core", version = "0.0.1-SNAPSHOT")
```

Working builds can be obtained here: [![](https://jitpack.io/v/altavir/kmath.svg)](https://jitpack.io/#altavir/kmath).

## Contributing

The project requires a lot of additional work. Please fill free to contribute in any way and propose new features.