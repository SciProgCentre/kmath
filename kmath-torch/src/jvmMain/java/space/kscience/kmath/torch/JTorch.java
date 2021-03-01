package space.kscience.kmath.torch;

class JTorch {

    static {
        System.loadLibrary("jtorch");
    }

    public static native int getNumThreads();

    public static native void setNumThreads(int numThreads);

    public static native boolean cudaIsAvailable();

    public static native void setSeed(int seed);

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

    public static native void swapTensors(long lhsHandle, long rhsHandle);

    public static native long viewTensor(long tensorHandle, int[] shape);

    public static native String tensorToString(long tensorHandle);

    public static native void disposeTensor(long tensorHandle);

    public static native int getDim(long tensorHandle);

    public static native int getNumel(long tensorHandle);

    public static native int getShapeAt(long tensorHandle, int d);

    public static native int getStrideAt(long tensorHandle, int d);

    public static native int getDevice(long tensorHandle);

    public static native double getItemDouble(long tensorHandle);

    public static native float getItemFloat(long tensorHandle);

    public static native long getItemLong(long tensorHandle);

    public static native int getItemInt(long tensorHandle);

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

    public static native long randintDouble(long low, long high, int[] shape, int device);

    public static native long randintFloat(long low, long high, int[] shape, int device);

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

    public static native void absTensorAssign(long tensorHandle);

    public static native long transposeTensor(long tensorHandle, int i, int j);

    public static native void transposeTensorAssign(long tensorHandle, int i, int j);

    public static native long expTensor(long tensorHandle);

    public static native void expTensorAssign(long tensorHandle);

    public static native long logTensor(long tensorHandle);

    public static native void logTensorAssign(long tensorHandle);

    public static native long sumTensor(long tensorHandle);

    public static native void sumTensorAssign(long tensorHandle);

    public static native long matmul(long lhs, long rhs);

    public static native void matmulAssign(long lhs, long rhs);

    public static native void matmulRightAssign(long lhs, long rhs);

    public static native long diagEmbed(long diagsHandle, int offset, int dim1, int dim2);

    public static native void svdTensor(long tensorHandle, long Uhandle, long Shandle, long Vhandle);

    public static native void symeigTensor(long tensorHandle, long Shandle, long Vhandle, boolean eigenvectors);

    public static native boolean requiresGrad(long tensorHandle);

    public static native void setRequiresGrad(long tensorHandle, boolean status);

    public static native long detachFromGraph(long tensorHandle);

    public static native long autogradTensor(long value, long variable, boolean retainGraph);

    public static native long autohessTensor(long value, long variable);
}
