# Module kmath-noa

A Bayesian computation library over
[NOA](https://github.com/grinisrit/noa.git)
together with relevant functionality from 
[LibTorch](https://pytorch.org/cppdocs). 

Our aim is to cover a wide set of applications from particle physics
simulations to deep learning. In fact, we support any 
differentiable program written on top of 
`AutoGrad` & `ATen`.

## Installation

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

