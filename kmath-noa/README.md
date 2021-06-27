# Module kmath-noa

This module provides a `kotlin-jvm` frontend for the 
[NOA](https://github.com/grinisrit/noa.git)
library together with relevant functionality from 
[LibTorch](https://pytorch.org/cppdocs). 

Our aim is to create a Bayesian computational platform 
which covers a wide set of applications from particle physics
simulations to deep learning and general differentiable programs
written on top of `AutoGrad` & `ATen`.

## Installation

Currently, to build native artifacts, we support only 
the [GNU](https://gcc.gnu.org/) toolchain. For `GPU` kernels, we require a compatible 
[CUDA](https://docs.nvidia.com/cuda/cuda-installation-guide-linux/index.html)
installation. If you are on Windows, we recommend setting up
everything on [WSL](https://docs.nvidia.com/cuda/wsl-user-guide/index.html).

To install the library, simply publish it locally:
```
./gradlew -q :kmath-noa:publishToMavenLocal
```

