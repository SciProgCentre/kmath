#include <torch/torch.h>
#include <iostream>
#include <stdlib.h>

#include "kscience_kmath_torch_JTorch.h"
#include "utils.hh"

JNIEXPORT jint JNICALL Java_kscience_kmath_torch_JTorch_getNumThreads(JNIEnv *, jclass)
{
    return torch::get_num_threads();
}

JNIEXPORT void JNICALL Java_kscience_kmath_torch_JTorch_setNumThreads(JNIEnv *, jclass, jint num_threads)
{
    torch::set_num_threads(num_threads);
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_createTensor(JNIEnv *, jclass)
{
    auto ten = torch::randn({2, 3});
    std::cout << ten << std::endl;
    void *ptr = new torch::Tensor(ten);
    return (long)ptr;
}

JNIEXPORT void JNICALL Java_kscience_kmath_torch_JTorch_printTensor(JNIEnv *, jclass, jlong tensor_handle)
{
    auto ten = ctorch::cast((void *)tensor_handle);
    std::cout << ten << std::endl;
}

JNIEXPORT void JNICALL Java_kscience_kmath_torch_JTorch_disposeTensor(JNIEnv *, jclass, jlong tensor_handle)
{
    delete static_cast<torch::Tensor *>((void *)tensor_handle);
}