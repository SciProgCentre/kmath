/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */


#include <noa/ghmc.hh>
#include <torch/torch.h>


#include "space_kscience_kmath_noa_JNoa.h"


JNIEXPORT jboolean JNICALL Java_space_kscience_kmath_noa_JNoa_cudaIsAvailable(JNIEnv *, jclass)
{
    return torch::cuda::is_available();
}