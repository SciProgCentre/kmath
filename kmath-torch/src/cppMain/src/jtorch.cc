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

JNIEXPORT jboolean JNICALL Java_kscience_kmath_torch_JTorch_cudaIsAvailable(JNIEnv *, jclass)
{
    return torch::cuda::is_available();
}

JNIEXPORT void JNICALL Java_kscience_kmath_torch_JTorch_setSeed(JNIEnv *, jclass, jint seed)
{
    torch::manual_seed(seed);
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_emptyTensor(JNIEnv *, jclass)
{
    return (long)new torch::Tensor;
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_fromBlobDouble(JNIEnv *env, jclass, jdoubleArray data, jintArray shape, jint device)
{
    return (long)new torch::Tensor(
        ctorch::from_blob<double>(
            env->GetDoubleArrayElements(data, 0),
            ctorch::to_vec_int(env->GetIntArrayElements(shape, 0), env->GetArrayLength(shape)),
            ctorch::int_to_device(device), true));
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_fromBlobFloat(JNIEnv *env, jclass, jfloatArray data, jintArray shape, jint device)
{
    return (long)new torch::Tensor(
        ctorch::from_blob<float>(
            env->GetFloatArrayElements(data, 0),
            ctorch::to_vec_int(env->GetIntArrayElements(shape, 0), env->GetArrayLength(shape)),
            ctorch::int_to_device(device), true));
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_fromBlobLong(JNIEnv *env, jclass, jlongArray data, jintArray shape, jint device)
{
    return (long)new torch::Tensor(
        ctorch::from_blob<long>(
            env->GetLongArrayElements(data, 0),
            ctorch::to_vec_int(env->GetIntArrayElements(shape, 0), env->GetArrayLength(shape)),
            ctorch::int_to_device(device), true));
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_fromBlobInt(JNIEnv *env, jclass, jintArray data, jintArray shape, jint device)
{
    return (long)new torch::Tensor(
        ctorch::from_blob<int>(
            env->GetIntArrayElements(data, 0),
            ctorch::to_vec_int(env->GetIntArrayElements(shape, 0), env->GetArrayLength(shape)),
            ctorch::int_to_device(device), true));
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_copyTensor(JNIEnv *, jclass, jlong tensor_handle)
{
    return (long)new torch::Tensor(ctorch::cast(tensor_handle).clone());
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_copyToDevice(JNIEnv *, jclass, jlong tensor_handle, jint device)
{
    return (long)new torch::Tensor(ctorch::cast(tensor_handle).to(ctorch::int_to_device(device), false, true));
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_copyToDouble(JNIEnv *, jclass, jlong tensor_handle)
{
    return (long)new torch::Tensor(ctorch::cast(tensor_handle).to(ctorch::dtype<double>(), false, true));
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_copyToFloat(JNIEnv *, jclass, jlong tensor_handle)
{
    return (long)new torch::Tensor(ctorch::cast(tensor_handle).to(ctorch::dtype<float>(), false, true));
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_copyToLong(JNIEnv *, jclass, jlong tensor_handle)
{
    return (long)new torch::Tensor(ctorch::cast(tensor_handle).to(ctorch::dtype<long>(), false, true));
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_copyToInt(JNIEnv *, jclass, jlong tensor_handle)
{
    return (long)new torch::Tensor(ctorch::cast(tensor_handle).to(ctorch::dtype<int>(), false, true));
}

JNIEXPORT void JNICALL Java_kscience_kmath_torch_JTorch_swapTensors(JNIEnv *, jclass, jlong lhs_handle, jlong rhs_handle)
{
    std::swap(ctorch::cast(lhs_handle), ctorch::cast(rhs_handle));
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_viewTensor(JNIEnv *env, jclass, jlong tensor_handle, jintArray shape)
{
    return (long)new torch::Tensor(
        ctorch::cast(tensor_handle).view(ctorch::to_vec_int(env->GetIntArrayElements(shape, 0), env->GetArrayLength(shape))));
}

JNIEXPORT jstring JNICALL Java_kscience_kmath_torch_JTorch_tensorToString(JNIEnv *env, jclass, jlong tensor_handle)
{
    return env->NewStringUTF(ctorch::tensor_to_string(ctorch::cast(tensor_handle)).c_str());
}

JNIEXPORT void JNICALL Java_kscience_kmath_torch_JTorch_disposeTensor(JNIEnv *, jclass, jlong tensor_handle)
{
    ctorch::dispose_tensor(tensor_handle);
}

JNIEXPORT jint JNICALL Java_kscience_kmath_torch_JTorch_getDim(JNIEnv *, jclass, jlong tensor_handle)
{
    return ctorch::cast(tensor_handle).dim();
}

JNIEXPORT jint JNICALL Java_kscience_kmath_torch_JTorch_getNumel(JNIEnv *, jclass, jlong tensor_handle)
{
    return ctorch::cast(tensor_handle).numel();
}

JNIEXPORT jint JNICALL Java_kscience_kmath_torch_JTorch_getShapeAt(JNIEnv *, jclass, jlong tensor_handle, jint d)
{
    return ctorch::cast(tensor_handle).size(d);
}

JNIEXPORT jint JNICALL Java_kscience_kmath_torch_JTorch_getStrideAt(JNIEnv *, jclass, jlong tensor_handle, jint d)
{
    return ctorch::cast(tensor_handle).stride(d);
}

JNIEXPORT jint JNICALL Java_kscience_kmath_torch_JTorch_getDevice(JNIEnv *, jclass, jlong tensor_handle)
{
    return ctorch::device_to_int(ctorch::cast(tensor_handle));
}

JNIEXPORT jdouble JNICALL Java_kscience_kmath_torch_JTorch_getItemDouble(JNIEnv *, jclass, jlong tensor_handle)
{
    return ctorch::cast(tensor_handle).item<double>();
}

JNIEXPORT jfloat JNICALL Java_kscience_kmath_torch_JTorch_getItemFloat(JNIEnv *, jclass, jlong tensor_handle)
{
    return ctorch::cast(tensor_handle).item<float>();
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_getItemLong(JNIEnv *, jclass, jlong tensor_handle)
{
    return ctorch::cast(tensor_handle).item<long>();
}

JNIEXPORT jint JNICALL Java_kscience_kmath_torch_JTorch_getItemInt(JNIEnv *, jclass, jlong tensor_handle)
{
    return ctorch::cast(tensor_handle).item<int>();
}

JNIEXPORT jdouble JNICALL Java_kscience_kmath_torch_JTorch_getDouble(JNIEnv *env, jclass, jlong tensor_handle, jintArray index)
{
    return ctorch::get<double>(ctorch::cast(tensor_handle), env->GetIntArrayElements(index, 0));
}

JNIEXPORT jfloat JNICALL Java_kscience_kmath_torch_JTorch_getFloat(JNIEnv *env, jclass, jlong tensor_handle, jintArray index)
{
    return ctorch::get<float>(ctorch::cast(tensor_handle), env->GetIntArrayElements(index, 0));
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_getLong(JNIEnv *env, jclass, jlong tensor_handle, jintArray index)
{
    return ctorch::get<long>(ctorch::cast(tensor_handle), env->GetIntArrayElements(index, 0));
}

JNIEXPORT jint JNICALL Java_kscience_kmath_torch_JTorch_getInt(JNIEnv *env, jclass, jlong tensor_handle, jintArray index)
{
    return ctorch::get<int>(ctorch::cast(tensor_handle), env->GetIntArrayElements(index, 0));
}

JNIEXPORT void JNICALL Java_kscience_kmath_torch_JTorch_setDouble(JNIEnv *env, jclass, jlong tensor_handle, jintArray index, jdouble value)
{
    ctorch::set<double>(ctorch::cast(tensor_handle), env->GetIntArrayElements(index, 0), value);
}

JNIEXPORT void JNICALL Java_kscience_kmath_torch_JTorch_setFloat(JNIEnv *env, jclass, jlong tensor_handle, jintArray index, jfloat value)
{
    ctorch::set<float>(ctorch::cast(tensor_handle), env->GetIntArrayElements(index, 0), value);
}

JNIEXPORT void JNICALL Java_kscience_kmath_torch_JTorch_setLong(JNIEnv *env, jclass, jlong tensor_handle, jintArray index, jlong value)
{
    ctorch::set<long>(ctorch::cast(tensor_handle), env->GetIntArrayElements(index, 0), value);
}

JNIEXPORT void JNICALL Java_kscience_kmath_torch_JTorch_setInt(JNIEnv *env, jclass, jlong tensor_handle, jintArray index, jint value)
{
    ctorch::set<int>(ctorch::cast(tensor_handle), env->GetIntArrayElements(index, 0), value);
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_randDouble(JNIEnv *env, jclass, jintArray shape, jint device)
{
    return (long)new torch::Tensor(
        ctorch::rand<double>(
            ctorch::to_vec_int(env->GetIntArrayElements(shape, 0), env->GetArrayLength(shape)),
            ctorch::int_to_device(device)));
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_randnDouble(JNIEnv *env, jclass, jintArray shape, jint device)
{
    return (long)new torch::Tensor(
        ctorch::randn<double>(
            ctorch::to_vec_int(env->GetIntArrayElements(shape, 0), env->GetArrayLength(shape)),
            ctorch::int_to_device(device)));
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_randFloat(JNIEnv *env, jclass, jintArray shape, jint device)
{
    return (long)new torch::Tensor(
        ctorch::rand<float>(
            ctorch::to_vec_int(env->GetIntArrayElements(shape, 0), env->GetArrayLength(shape)),
            ctorch::int_to_device(device)));
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_randnFloat(JNIEnv *env, jclass, jintArray shape, jint device)
{
    return (long)new torch::Tensor(
        ctorch::randn<float>(
            ctorch::to_vec_int(env->GetIntArrayElements(shape, 0), env->GetArrayLength(shape)),
            ctorch::int_to_device(device)));
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_randintDouble(JNIEnv *env, jclass, jlong low, jlong high, jintArray shape, jint device)
{
    return (long)new torch::Tensor(
        ctorch::randint<double>(low, high,
                                ctorch::to_vec_int(env->GetIntArrayElements(shape, 0), env->GetArrayLength(shape)),
                                ctorch::int_to_device(device)));
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_randintFloat(JNIEnv *env, jclass, jlong low, jlong high, jintArray shape, jint device)
{
    return (long)new torch::Tensor(
        ctorch::randint<float>(low, high,
                               ctorch::to_vec_int(env->GetIntArrayElements(shape, 0), env->GetArrayLength(shape)),
                               ctorch::int_to_device(device)));
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_randintLong(JNIEnv *env, jclass, jlong low, jlong high, jintArray shape, jint device)
{
    return (long)new torch::Tensor(
        ctorch::randint<long>(low, high,
                              ctorch::to_vec_int(env->GetIntArrayElements(shape, 0), env->GetArrayLength(shape)),
                              ctorch::int_to_device(device)));
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_randintInt(JNIEnv *env, jclass, jlong low, jlong high, jintArray shape, jint device)
{
    return (long)new torch::Tensor(
        ctorch::randint<int>(low, high,
                             ctorch::to_vec_int(env->GetIntArrayElements(shape, 0), env->GetArrayLength(shape)),
                             ctorch::int_to_device(device)));
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_randLike(JNIEnv *, jclass, jlong tensor_handle)
{
    return (long)new torch::Tensor(torch::rand_like(ctorch::cast(tensor_handle)));
}

JNIEXPORT void JNICALL Java_kscience_kmath_torch_JTorch_randLikeAssign(JNIEnv *, jclass, jlong tensor_handle)
{
    ctorch::cast(tensor_handle) = torch::rand_like(ctorch::cast(tensor_handle));
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_randnLike(JNIEnv *, jclass, jlong tensor_handle)
{
    return (long)new torch::Tensor(torch::randn_like(ctorch::cast(tensor_handle)));
}

JNIEXPORT void JNICALL Java_kscience_kmath_torch_JTorch_randnLikeAssign(JNIEnv *, jclass, jlong tensor_handle)
{
    ctorch::cast(tensor_handle) = torch::randn_like(ctorch::cast(tensor_handle));
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_randintLike(JNIEnv *, jclass, jlong tensor_handle, jlong low, jlong high)
{
    return (long)new torch::Tensor(torch::randint_like(ctorch::cast(tensor_handle), low, high));
}

JNIEXPORT void JNICALL Java_kscience_kmath_torch_JTorch_randintLikeAssign(JNIEnv *, jclass, jlong tensor_handle, jlong low, jlong high)
{
    ctorch::cast(tensor_handle) = torch::randint_like(ctorch::cast(tensor_handle), low, high);
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_fullDouble(JNIEnv *env, jclass, jdouble value, jintArray shape, jint device)
{
    return (long)new torch::Tensor(
        ctorch::full<double>(
            value,
            ctorch::to_vec_int(env->GetIntArrayElements(shape, 0), env->GetArrayLength(shape)),
            ctorch::int_to_device(device)));
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_fullFloat(JNIEnv *env, jclass, jfloat value, jintArray shape, jint device)
{
    return (long)new torch::Tensor(
        ctorch::full<float>(
            value,
            ctorch::to_vec_int(env->GetIntArrayElements(shape, 0), env->GetArrayLength(shape)),
            ctorch::int_to_device(device)));
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_fullLong(JNIEnv *env, jclass, jlong value, jintArray shape, jint device)
{
    return (long)new torch::Tensor(
        ctorch::full<long>(
            value,
            ctorch::to_vec_int(env->GetIntArrayElements(shape, 0), env->GetArrayLength(shape)),
            ctorch::int_to_device(device)));
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_fullInt(JNIEnv *env, jclass, jint value, jintArray shape, jint device)
{
    return (long)new torch::Tensor(
        ctorch::full<int>(
            value,
            ctorch::to_vec_int(env->GetIntArrayElements(shape, 0), env->GetArrayLength(shape)),
            ctorch::int_to_device(device)));
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_timesDouble(JNIEnv *, jclass, jdouble value, jlong other)
{
    return (long)new torch::Tensor(value * ctorch::cast(other));
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_timesFloat(JNIEnv *, jclass, jfloat value, jlong other)
{
    return (long)new torch::Tensor(value * ctorch::cast(other));
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_timesLong(JNIEnv *, jclass, jlong value, jlong other)
{
    return (long)new torch::Tensor(value * ctorch::cast(other));
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_timesInt(JNIEnv *, jclass, jint value, jlong other)
{
    return (long)new torch::Tensor(value * ctorch::cast(other));
}

JNIEXPORT void JNICALL Java_kscience_kmath_torch_JTorch_timesDoubleAssign(JNIEnv *, jclass, jdouble value, jlong other)
{
    ctorch::cast(other) *= value;
}

JNIEXPORT void JNICALL Java_kscience_kmath_torch_JTorch_timesFloatAssign(JNIEnv *, jclass, jfloat value, jlong other)
{
    ctorch::cast(other) *= value;
}

JNIEXPORT void JNICALL Java_kscience_kmath_torch_JTorch_timesLongAssign(JNIEnv *, jclass, jlong value, jlong other)
{
    ctorch::cast(other) *= value;
}

JNIEXPORT void JNICALL Java_kscience_kmath_torch_JTorch_timesIntAssign(JNIEnv *, jclass, jint value, jlong other)
{
    ctorch::cast(other) *= value;
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_plusDouble(JNIEnv *, jclass, jdouble value, jlong other)
{
    return (long)new torch::Tensor(value + ctorch::cast(other));
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_plusFloat(JNIEnv *, jclass, jfloat value, jlong other)
{
    return (long)new torch::Tensor(value + ctorch::cast(other));
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_plusLong(JNIEnv *, jclass, jlong value, jlong other)
{
    return (long)new torch::Tensor(value + ctorch::cast(other));
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_plusInt(JNIEnv *, jclass, jint value, jlong other)
{
    return (long)new torch::Tensor(value + ctorch::cast(other));
}

JNIEXPORT void JNICALL Java_kscience_kmath_torch_JTorch_plusDoubleAssign(JNIEnv *, jclass, jdouble value, jlong other)
{
    ctorch::cast(other) += value;
}

JNIEXPORT void JNICALL Java_kscience_kmath_torch_JTorch_plusFloatAssign(JNIEnv *, jclass, jfloat value, jlong other)
{
    ctorch::cast(other) += value;
}

JNIEXPORT void JNICALL Java_kscience_kmath_torch_JTorch_plusLongAssign(JNIEnv *, jclass, jlong value, jlong other)
{
    ctorch::cast(other) += value;
}

JNIEXPORT void JNICALL Java_kscience_kmath_torch_JTorch_plusIntAssign(JNIEnv *, jclass, jint value, jlong other)
{
    ctorch::cast(other) += value;
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_timesTensor(JNIEnv *, jclass, jlong lhs, jlong rhs)
{
    return (long)new torch::Tensor(ctorch::cast(lhs) * ctorch::cast(rhs));
}

JNIEXPORT void JNICALL Java_kscience_kmath_torch_JTorch_timesTensorAssign(JNIEnv *, jclass, jlong lhs, jlong rhs)
{
    ctorch::cast(lhs) *= ctorch::cast(rhs);
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_divTensor(JNIEnv *, jclass, jlong lhs, jlong rhs)
{
    return (long)new torch::Tensor(ctorch::cast(lhs) / ctorch::cast(rhs));
}

JNIEXPORT void JNICALL Java_kscience_kmath_torch_JTorch_divTensorAssign(JNIEnv *, jclass, jlong lhs, jlong rhs)
{
    ctorch::cast(lhs) /= ctorch::cast(rhs);
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_plusTensor(JNIEnv *, jclass, jlong lhs, jlong rhs)
{
    return (long)new torch::Tensor(ctorch::cast(lhs) + ctorch::cast(rhs));
}

JNIEXPORT void JNICALL Java_kscience_kmath_torch_JTorch_plusTensorAssign(JNIEnv *, jclass, jlong lhs, jlong rhs)
{
    ctorch::cast(lhs) += ctorch::cast(rhs);
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_minusTensor(JNIEnv *, jclass, jlong lhs, jlong rhs)
{
    return (long)new torch::Tensor(ctorch::cast(lhs) - ctorch::cast(rhs));
}

JNIEXPORT void JNICALL Java_kscience_kmath_torch_JTorch_minusTensorAssign(JNIEnv *, jclass, jlong lhs, jlong rhs)
{
    ctorch::cast(lhs) -= ctorch::cast(rhs);
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_unaryMinus(JNIEnv *, jclass, jlong tensor_handle)
{
    return (long)new torch::Tensor(-ctorch::cast(tensor_handle));
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_absTensor(JNIEnv *, jclass, jlong tensor_handle)
{
    return (long)new torch::Tensor(ctorch::cast(tensor_handle).abs());
}

JNIEXPORT void JNICALL Java_kscience_kmath_torch_JTorch_absTensorAssign(JNIEnv *, jclass, jlong tensor_handle)
{
    ctorch::cast(tensor_handle) = ctorch::cast(tensor_handle).abs();
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_transposeTensor(JNIEnv *, jclass, jlong tensor_handle, jint i, jint j)
{
    return (long)new torch::Tensor(ctorch::cast(tensor_handle).transpose(i, j));
}

JNIEXPORT void JNICALL Java_kscience_kmath_torch_JTorch_transposeTensorAssign(JNIEnv *, jclass, jlong tensor_handle, jint i, jint j)
{
    ctorch::cast(tensor_handle) = ctorch::cast(tensor_handle).transpose(i, j);
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_expTensor(JNIEnv *, jclass, jlong tensor_handle)
{
    return (long)new torch::Tensor(ctorch::cast(tensor_handle).exp());
}

JNIEXPORT void JNICALL Java_kscience_kmath_torch_JTorch_expTensorAssign(JNIEnv *, jclass, jlong tensor_handle)
{
    ctorch::cast(tensor_handle) = ctorch::cast(tensor_handle).exp();
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_logTensor(JNIEnv *, jclass, jlong tensor_handle)
{
    return (long)new torch::Tensor(ctorch::cast(tensor_handle).log());
}

JNIEXPORT void JNICALL Java_kscience_kmath_torch_JTorch_logTensorAssign(JNIEnv *, jclass, jlong tensor_handle)
{
    ctorch::cast(tensor_handle) = ctorch::cast(tensor_handle).log();
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_sumTensor(JNIEnv *, jclass, jlong tensor_handle)
{
    return (long)new torch::Tensor(ctorch::cast(tensor_handle).sum());
}

JNIEXPORT void JNICALL Java_kscience_kmath_torch_JTorch_sumTensorAssign(JNIEnv *, jclass, jlong tensor_handle)
{
    ctorch::cast(tensor_handle) = ctorch::cast(tensor_handle).sum();
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_matmul(JNIEnv *, jclass, jlong lhs, jlong rhs)
{
    return (long)new torch::Tensor(torch::matmul(ctorch::cast(lhs), ctorch::cast(rhs)));
}

JNIEXPORT void JNICALL Java_kscience_kmath_torch_JTorch_matmulAssign(JNIEnv *, jclass, jlong lhs, jlong rhs)
{
    ctorch::cast(lhs) = ctorch::cast(lhs).matmul(ctorch::cast(rhs));
}

JNIEXPORT void JNICALL Java_kscience_kmath_torch_JTorch_matmulRightAssign(JNIEnv *, jclass, jlong lhs, jlong rhs)
{
    ctorch::cast(rhs) = ctorch::cast(lhs).matmul(ctorch::cast(rhs));
}

JNIEXPORT jlong JNICALL
Java_kscience_kmath_torch_JTorch_diagEmbed(JNIEnv *, jclass, jlong diags_handle, jint offset, jint dim1, jint dim2)
{
    return (long)new torch::Tensor(torch::diag_embed(ctorch::cast(diags_handle), offset, dim1, dim2));
}

JNIEXPORT void JNICALL
Java_kscience_kmath_torch_JTorch_svdTensor(JNIEnv *, jclass, jlong tensor_handle, jlong U_handle, jlong S_handle, jlong V_handle)
{
    auto [U, S, V] = torch::svd(ctorch::cast(tensor_handle));
    ctorch::cast(U_handle) = U;
    ctorch::cast(S_handle) = S;
    ctorch::cast(V_handle) = V;
}

JNIEXPORT void JNICALL
Java_kscience_kmath_torch_JTorch_symeigTensor(JNIEnv *, jclass, jlong tensor_handle, jlong S_handle, jlong V_handle, jboolean eigenvectors)
{
    auto [S, V] = torch::symeig(ctorch::cast(tensor_handle), eigenvectors);
    ctorch::cast(S_handle) = S;
    ctorch::cast(V_handle) = V;
}

JNIEXPORT jboolean JNICALL Java_kscience_kmath_torch_JTorch_requiresGrad(JNIEnv *, jclass, jlong tensor_handle)
{
    return ctorch::cast(tensor_handle).requires_grad();
}

JNIEXPORT void JNICALL Java_kscience_kmath_torch_JTorch_setRequiresGrad(JNIEnv *, jclass, jlong tensor_handle, jboolean status)
{
    ctorch::cast(tensor_handle).requires_grad_(status);
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_detachFromGraph(JNIEnv *, jclass, jlong tensor_handle)
{
    return (long)new torch::Tensor(ctorch::cast(tensor_handle).detach());
}

JNIEXPORT jlong JNICALL 
Java_kscience_kmath_torch_JTorch_autogradTensor(JNIEnv *, jclass, jlong value, jlong variable, jboolean retain_graph)
{
    return (long)new torch::Tensor(torch::autograd::grad({ctorch::cast(value)}, {ctorch::cast(variable)}, {}, retain_graph)[0]);
}

JNIEXPORT jlong JNICALL Java_kscience_kmath_torch_JTorch_autohessTensor(JNIEnv *, jclass, jlong value, jlong variable)
{
    return (long)new torch::Tensor(ctorch::hessian(ctorch::cast(value), ctorch::cast(variable)));
}