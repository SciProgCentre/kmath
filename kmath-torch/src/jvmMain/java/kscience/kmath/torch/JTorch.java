package kscience.kmath.torch;

class JTorch {

    static {
        System.loadLibrary("jtorch");
    }

    public static native int getNumThreads();
    public static native void setNumThreads(int numThreads);
    public static native long createTensor();
    public static native void printTensor(long tensorHandle);
    public static native void disposeTensor(long tensorHandle);
}