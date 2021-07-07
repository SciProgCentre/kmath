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

}

