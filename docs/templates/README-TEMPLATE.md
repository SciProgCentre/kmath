[![JetBrains Research](https://jb.gg/badges/research.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
[![DOI](https://zenodo.org/badge/129486382.svg)](https://zenodo.org/badge/latestdoi/129486382)
![Gradle build](https://github.com/SciProgCentre/kmath/workflows/Gradle%20build/badge.svg)
[![Maven Central](https://img.shields.io/maven-central/v/space.kscience/kmath-core.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22space.kscience%22)

# KMath

Could be pronounced as `key-math`. The **K**otlin **Math**ematics library was initially intended as a Kotlin-based analog to Python's NumPy library. Later we found that kotlin is a much more flexible language and allows superior architecture designs. In contrast to `numpy` and `scipy` it is modular and has a lightweight core. The `numpy`-like experience could be achieved with [kmath-for-real](kmath-for-real) extension module.

[Documentation site](https://SciProgCentre.github.io/kmath/)

## Publications and talks

* [A conceptual article about context-oriented design](https://proandroiddev.com/an-introduction-context-oriented-programming-in-kotlin-2e79d316b0a2)
* [Another article about context-oriented design](https://proandroiddev.com/diving-deeper-into-context-oriented-programming-in-kotlin-3ecb4ec38814)
* [ACAT 2019 conference paper](https://aip.scitation.org/doi/abs/10.1063/1.5130103)
* [A talk at KotlinConf 2019 about using kotlin for science](https://youtu.be/LI_5TZ7tnOE?si=4LknX41gl_YeUbIe)
* [A talk on architecture at Joker-2021 (in Russian)](https://youtu.be/1bZ2doHiRRM?si=9w953ro9yu98X_KJ)
* [The same talk in English](https://youtu.be/yP5DIc2fVwQ?si=louZzQ1dcXV6gP10)
* [A seminar on tensor API](https://youtu.be/0H99wUs0xTM?si=6c__04jrByFQtVpo)

# Goal

* Provide a flexible and powerful API to work with mathematics abstractions in Kotlin-multiplatform (JVM, JS, Native and
  Wasm).
* Provide basic multiplatform implementations for those abstractions (without significant performance optimization).
* Provide bindings and wrappers with those abstractions for popular optimized platform libraries.

## Non-goals

* Be like NumPy. It was the idea at the beginning, but we decided that we can do better in API.
* Provide the best performance out of the box. We have specialized libraries for that. Need only API wrappers for them.
* Cover all cases as immediately and in one bundle. We will modularize everything and add new features gradually.
* Provide specialized behavior in the core. API is made generic on purpose, so one needs to specialize for types, like for `Float64` in the core. For that we will have specialization modules like [kmath-for-real](kmath-for-real), which will give a better experience for those who want to work with specific types.

## Contributing

The project requires a lot of additional work. The most important thing we need is feedback about what features are required the most. Feel free to create feature requests. We are also welcome to code contributions, especially in issues marked with [good first issue](https://github.com/SciProgCentre/kmath/issues?q=is%3Aissue+is%3Aopen+label%3A%22good+first+issue%22) label.

Project roadmap will be available at [GitHub Projects](https://github.com/orgs/SciProgCentre/projects/3).

## Features and stability

KMath is a modular library. Different modules provide different features with different API stability guarantees. All core modules are released with the same version, but with different API change policy. The features are described in module definitions below. The module stability could have the following levels:

* **PROTOTYPE**. On this level there are no compatibility guarantees. All methods and classes form those modules could
  break any moment. You can still use it, but be sure to fix the specific version.
* **EXPERIMENTAL**. The general API is decided, but some changes could be made. Volatile API is marked
  with `@UnstableKMathAPI` or other stability warning annotations.
* **DEVELOPMENT**. API breaking generally follows semantic versioning ideology. There could be changes in minor
  versions, but not in patch versions. API is protected
  with [binary-compatibility-validator](https://kotlinlang.org/docs/gradle-binary-compatibility-validation.html) tool.
* **STABLE**. The API stabilized. Breaking changes are allowed only in major releases.

## Modules

${modules}

## Multi-platform support

KMath is developed as a multi-platform library, which means that most of the interfaces are declared in common source sets like [common source sets](kmath-core/src/commonMain) and implemented there wherever it is possible. In some cases, features are delegated to platform-specific implementations even if they could be provided in the common module for performance reasons. Currently, Kotlin/JVM is the primary platform, however, Kotlin/Native, Kotlin/JS and Kotlin/Wasm contributions and feedback are also welcome.

## Performance

Performance of mathematical operations is hard to achieve without a lot of effort. KMath focus is to provide a reasonable performance for common cases, out of the box and good interoperability with optimized libraries for edge cases. For example, one could prototype an algorithm using KMath core implementations and then use Multik or Ojalgo for performance-critical parts just by adding a dependency and algebra context switch.

As for core implementations, we expect to focus on creating a convenient universal API first and then work on increasing performance for specific cases. We expect the worst KMath benchmarks will perform better than native Python, but worse than optimized native/SciPy (mostly due to boxing operations on primitive numbers). The best performance of optimized parts could be better than SciPy.

## Requirements

KMath currently relies on JDK 21 for compilation and execution of Kotlin-JVM part. 

### Repositories

Intermediate releases are published to [Kotlin.Link](https://repo.kotlin.link) repository.

```kotlin
repositories {
    maven("https://repo.kotlin.link")
}

dependencies {
    api("${group}:kmath-core:$version")
    // api("${group}:kmath-core-jvm:$version") for jvm-specific version
}
```
