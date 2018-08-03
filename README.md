# KMath
Kotlin MATHematics library is intended as a kotlin based analog of numpy python library. Contrary to `numpy`
and `scipy` it is modular and has a lightweight core.

## Features

* **Algebra**
    * Mathematical operation entities like rings, spaces and fields with (**TODO** add example to wiki) 
    * Basic linear algebra operations (summs products, etc) backed by `Space` API.
    * [In progress] advanced linear algebra operations like matrix inversions.
* **Array-like structures** Full support of numpy-like ndarray including mixed ariphmetic operations and function operations
on arrays and numbers just like it works in python (with benefit of static type checking).

## Multi-platform support
KMath is developed as a multi-platform library, which means that most of interfaces are declared in common module.
Implementation is also done in common module wherever it is possible. In some cases features are delegated to 
platform even if they could be done in common module because of platform performance optimization.

## Performance
The calculation performance is one of major goals of KMath in the future, but in some cases it is not possible to achieve 
both performance and flexibility. We expect to firstly focus on creating convenient universal API and then work on 
increasing performance for specific cases. We expect the worst KMath performance still be better than natural python,
but worse than optimized native/scipy (mostly due to boxing operations on primitive numbers). The best performance 
of optimized parts should be better than scipy.

## Releases
The project is currently in pre-release stage. Work builds could be obtained with 
[![](https://jitpack.io/v/altavir/kmath.svg)](https://jitpack.io/#altavir/kmath). 

## Contributing
The project requires a lot of additional work. Please fill free to contribute in any way and propose new features.