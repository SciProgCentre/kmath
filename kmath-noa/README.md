# Module kmath-noa

A Bayesian computation library over
[NOA](https://github.com/grinisrit/noa.git)
together with relevant functionality from 
[LibTorch](https://pytorch.org/cppdocs). 

Our aim is to cover a wide set of applications 
from deep learning to particle physics
simulations. In fact, we support any 
differentiable program written on top of 
`AutoGrad` & `ATen`.

## Installation from source

Currently, we support only
the [GNU](https://gcc.gnu.org/) toolchain for the native artifacts.
For `GPU` kernels, we require a compatible
[CUDA](https://docs.nvidia.com/cuda/cuda-installation-guide-linux/index.html)
installation. If you are on Windows, we recommend setting up
everything on [WSL](https://docs.nvidia.com/cuda/wsl-user-guide/index.html).

To install the library, you can simply publish to the local
Maven repository:
```
./gradlew -q :kmath-noa:publishToMavenLocal
```
This will fetch and build the `JNI` wrapper `jnoa`. 

In your own application add the local dependency:
```kotlin
repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("space.kscience:kmath-noa:0.3.0-dev-14")
}
```
To load the native library you will need to add to the VM options:
```
-Djava.library.path=${HOME}/.konan/third-party/noa-v0.0.1/cpp-build/kmath
```

## Usage

We implement the tensor algebra interfaces 
from [kmath-tensors](../kmath-tensors):
```kotlin
NoaFloat {
    val tensor = 
        randNormal(
            shape = intArrayOf(7, 5, 3), 
            device = Device.CPU) // or Device.CUDA(0) for GPU
    
    // Compute SVD
    val (tensorU, tensorS, tensorV) = tensor.svd()
    
    // Reconstruct tensor
    val tensorReg =
        tensorU dot (diagonalEmbedding(tensorS) dot tensorV.transpose(-2, -1))
}
```

The [AutoGrad](https://pytorch.org/tutorials/beginner/blitz/autograd_tutorial.html)
engine is exposed:

```kotlin
NoaFloat {
    // Create a quadratic function
    val dim = 3
    val tensorX = randNormal(shape = intArrayOf(dim))
    val randFeatures = randNormal(shape = intArrayOf(dim, dim))
    val tensorSigma = randFeatures + randFeatures.transpose(0, 1)
    val tensorMu = randNormal(shape = intArrayOf(dim))

    // Create a differentiable expression
    val expressionAtX = withGradAt(tensorX) { x ->
        0.5f * (x dot (tensorSigma dot x)) + (tensorMu dot x) + 25.9f
    }

    // Evaluate the gradient at tensorX
    // retaining the graph for the hessian computation
    val gradientAtX = expressionAtX.autoGradient(tensorX, retainGraph = true)
    
    // Compute the hessian at tensorX
    val hessianAtX = expressionAtX.autoHessian(tensorX)
}
```


Native memory management relies on scoping 
with [NoaScope](src/main/kotlin/space/kscience/kmath/noa/memory/NoaScope.kt)
which is readily within an algebra context.
Manual management is also possible:
```kotlin
// Create a scope
val scope = NoaScope()

val tensor = NoaFloat(scope){
    full(5f, intArrayOf(1))
}!! // the result might be null

// If the computation fails resources will be freed automatically
// Otherwise it's your responsibility:
scope.disposeAll()

// Attempts to use tensor here is undefined behaviour
```