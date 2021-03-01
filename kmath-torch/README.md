# LibTorch extension (`kmath-torch`)

This is a `Kotlin/Native` & `JVM` module, with only `linuxX64` supported so far. The library wraps some of
the [PyTorch C++ API](https://pytorch.org/cppdocs), focusing on integrating `Aten` & `Autograd` with `KMath`.

## Installation

To install the library, you have to build & publish locally `kmath-core`, `kmath-memory` with `kmath-torch`:

```
./gradlew -q :kmath-core:publishToMavenLocal :kmath-memory:publishToMavenLocal :kmath-torch:publishToMavenLocal
```

This builds `ctorch` a C wrapper and `jtorch` a JNI wrapper for `LibTorch`, placed inside:

`~/.konan/third-party/kmath-torch-0.2.0/cpp-build`

You will have to link against it in your own project.

## Usage

Tensors are implemented over the `MutableNDStructure`. They can only be created through provided factory methods
and require scoping within a `TensorAlgebra` instance:

```kotlin
TorchTensorRealAlgebra {

    val realTensor: TorchTensorReal = copyFromArray(
        array = (1..10).map { it + 50.0 }.toList().toDoubleArray(),
        shape = intArrayOf(2, 5)
    )
    println(realTensor)

    val gpuRealTensor: TorchTensorReal = copyFromArray(
        array = (1..8).map { it * 2.5 }.toList().toDoubleArray(),
        shape = intArrayOf(2, 2, 2),
        device = Device.CUDA(0)
    )
    println(gpuRealTensor)
}
```

High performance automatic differentiation engine is available:

```kotlin
TorchTensorRealAlgebra {
    val dim = 10
    val device = Device.CPU //or Device.CUDA(0)
    
    val tensorX = randNormal(shape = intArrayOf(dim), device = device)
    val randFeatures = randNormal(shape = intArrayOf(dim, dim), device = device)
    val tensorSigma = randFeatures + randFeatures.transpose(0, 1)
    val tensorMu = randNormal(shape = intArrayOf(dim), device = device)

    // expression to differentiate w.r.t. x evaluated at x = tensorX
    val expressionAtX = withGradAt(tensorX, { x ->
        0.5 * (x dot (tensorSigma dot x)) + (tensorMu dot x) + 25.9
    })

    // value of the gradient at x = tensorX
    val gradientAtX = expressionAtX.grad(tensorX, retainGraph = true)
    // value of the hessian at x = tensorX
    val hessianAtX = expressionAtX hess tensorX
}
```
Contributed by [Roland Grinis](https://github.com/rgrit91)
