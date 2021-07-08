/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.noa;

class JNoa {

    static {
        String jNoaPath = System.getProperty("user.home") +
                "/devspace/noa/cmake-build-release/kmath/libjnoa.so";
        //"/.konan/third-party/kmath-noa-0.3.0-dev-14/cpp-build/kmath/libjnoa.so";

        try {
            System.load(jNoaPath);
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Failed to load native NOA library from:\n" +
                    jNoaPath + "\n" + e);
            System.exit(1);
        }
    }

    public static native int testException(int seed);

    public static native boolean cudaIsAvailable();

    public static native int getNumThreads();

    public static native void setNumThreads(int numThreads);

    public static native void setSeed(int seed);

    public static native void disposeTensor(long tensorHandle);

    public static native long emptyTensor();

    public static native long fromBlobDouble(double[] data, int[] shape, int device);

    public static native long fromBlobFloat(float[] data, int[] shape, int device);

    public static native long fromBlobLong(long[] data, int[] shape, int device);

    public static native long fromBlobInt(int[] data, int[] shape, int device);

    public static native long copyTensor(long tensorHandle);

    public static native long copyToDevice(long tensorHandle, int device);

    public static native long copyToDouble(long tensorHandle);

    public static native long copyToFloat(long tensorHandle);

    public static native long copyToLong(long tensorHandle);

    public static native long copyToInt(long tensorHandle);

    public static native long viewTensor(long tensorHandle, int[] shape);

    public static native long viewAsTensor(long tensorHandle, long asTensorHandle);

    public static native String tensorToString(long tensorHandle);

    public static native int getDim(long tensorHandle);

    public static native int getNumel(long tensorHandle);

    public static native int getShapeAt(long tensorHandle, int d);

    public static native int getStrideAt(long tensorHandle, int d);

    public static native int getDevice(long tensorHandle);

    public static native double getItemDouble(long tensorHandle);

    public static native float getItemFloat(long tensorHandle);

    public static native long getItemLong(long tensorHandle);

    public static native int getItemInt(long tensorHandle);

    public static native long getIndex(long tensorHandle, int index);

    public static native long getIndexTensor(long tensorHandle, long indexTensorHandle);

    public static native double getDouble(long tensorHandle, int[] index);

    public static native float getFloat(long tensorHandle, int[] index);

    public static native long getLong(long tensorHandle, int[] index);

    public static native int getInt(long tensorHandle, int[] index);

    public static native void setDouble(long tensorHandle, int[] index, double value);

    public static native void setFloat(long tensorHandle, int[] index, float value);

    public static native void setLong(long tensorHandle, int[] index, long value);

    public static native void setInt(long tensorHandle, int[] index, int value);

    public static native long randDouble(int[] shape, int device);

    public static native long randnDouble(int[] shape, int device);

    public static native long randFloat(int[] shape, int device);

    public static native long randnFloat(int[] shape, int device);

    public static native long randintLong(long low, long high, int[] shape, int device);

    public static native long randintInt(long low, long high, int[] shape, int device);

    public static native long randLike(long tensorHandle);

    public static native void randLikeAssign(long tensorHandle);

    public static native long randnLike(long tensorHandle);

    public static native void randnLikeAssign(long tensorHandle);

    public static native long randintLike(long tensorHandle, long low, long high);

    public static native void randintLikeAssign(long tensorHandle, long low, long high);

    public static native long fullDouble(double value, int[] shape, int device);

    public static native long fullFloat(float value, int[] shape, int device);

    public static native long fullLong(long value, int[] shape, int device);

    public static native long fullInt(int value, int[] shape, int device);

    public static native long timesDouble(double value, long other);

    public static native long timesFloat(float value, long other);

    public static native long timesLong(long value, long other);

    public static native long timesInt(int value, long other);

    public static native void timesDoubleAssign(double value, long other);

    public static native void timesFloatAssign(float value, long other);

    public static native void timesLongAssign(long value, long other);

    public static native void timesIntAssign(int value, long other);

    public static native long plusDouble(double value, long other);

    public static native long plusFloat(float value, long other);

    public static native long plusLong(long value, long other);

    public static native long plusInt(int value, long other);

    public static native void plusDoubleAssign(double value, long other);

    public static native void plusFloatAssign(float value, long other);

    public static native void plusLongAssign(long value, long other);

    public static native void plusIntAssign(int value, long other);

    public static native long timesTensor(long lhs, long rhs);

    public static native void timesTensorAssign(long lhs, long rhs);

    public static native long divTensor(long lhs, long rhs);

    public static native void divTensorAssign(long lhs, long rhs);

    public static native long plusTensor(long lhs, long rhs);

    public static native void plusTensorAssign(long lhs, long rhs);

    public static native long minusTensor(long lhs, long rhs);

    public static native void minusTensorAssign(long lhs, long rhs);

    public static native long unaryMinus(long tensorHandle);

    public static native long absTensor(long tensorHandle);

    public static native long transposeTensor(long tensorHandle, int i, int j);

    public static native long expTensor(long tensorHandle);

    public static native long lnTensor(long tensorHandle);

    public static native long sqrtTensor(long tensorHandle);

    public static native long cosTensor(long tensorHandle);

    public static native long acosTensor(long tensorHandle);

    public static native long coshTensor(long tensorHandle);

    public static native long acoshTensor(long tensorHandle);

    public static native long sinTensor(long tensorHandle);

    public static native long asinTensor(long tensorHandle);

    public static native long sinhTensor(long tensorHandle);

    public static native long asinhTensor(long tensorHandle);

    public static native long tanTensor(long tensorHandle);

    public static native long atanTensor(long tensorHandle);

    public static native long tanhTensor(long tensorHandle);

    public static native long atanhTensor(long tensorHandle);

    public static native long ceilTensor(long tensorHandle);

    public static native long floorTensor(long tensorHandle);

    public static native long sumTensor(long tensorHandle);

    public static native long sumDimTensor(long tensorHandle, int dim, boolean keepDim);

    public static native long minTensor(long tensorHandle);

    public static native long minDimTensor(long tensorHandle, int dim, boolean keepDim);

    public static native long maxTensor(long tensorHandle);

    public static native long maxDimTensor(long tensorHandle, int dim, boolean keepDim);

    public static native long meanTensor(long tensorHandle);

    public static native long meanDimTensor(long tensorHandle, int dim, boolean keepDim);

    public static native long stdTensor(long tensorHandle);

    public static native long stdDimTensor(long tensorHandle, int dim, boolean keepDim);

    public static native long varTensor(long tensorHandle);

    public static native long varDimTensor(long tensorHandle, int dim, boolean keepDim);

    public static native long argMaxTensor(long tensorHandle, int dim, boolean keepDim);

    public static native long flattenTensor(long tensorHandle);

    public static native long matmul(long lhs, long rhs);

    public static native void matmulAssign(long lhs, long rhs);

    public static native void matmulRightAssign(long lhs, long rhs);

    public static native long diagEmbed(long diagsHandle, int offset, int dim1, int dim2);

    public static native long detTensor(long tensorHandle);

    public static native long invTensor(long tensorHandle);

    public static native long choleskyTensor(long tensorHandle);

    public static native void qrTensor(long tensorHandle, long Qhandle, long Rhandle);

    public static native void luTensor(long tensorHandle, long Phandle, long Lhandle, long Uhandle);

    public static native void svdTensor(long tensorHandle, long Uhandle, long Shandle, long Vhandle);

    public static native void symEigTensor(long tensorHandle, long Shandle, long Vhandle);

    public static native boolean requiresGrad(long tensorHandle);

    public static native void setRequiresGrad(long tensorHandle, boolean status);

    public static native long detachFromGraph(long tensorHandle);

    public static native long autoGradTensor(long value, long variable, boolean retainGraph);

    public static native long autoHessTensor(long value, long variable);

}
