# Module kmath-noa

This module provides a `kotlin-jvm` frontend for the 
[NOA](https://github.com/grinisrit/noa.git)
library together with relevant functionality from 
[LibTorch](https://pytorch.org/cppdocs). 

Our aim is to create a Bayesian computational platform 
which covers a wide set of applications from particle physics
simulations to deep learning and general differentiable programs
written on top of `AutoGrad` & `ATen`.

Currently, the native artifacts support only `GNU` and 
`CUDA` for GPU acceleration. 