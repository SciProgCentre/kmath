/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.noa

public fun cudaAvailable(): Boolean {
    return JNoa.cudaIsAvailable()
}

public fun getNumThreads(): Int {
    return JNoa.getNumThreads()
}

public fun setNumThreads(numThreads: Int): Unit {
    JNoa.setNumThreads(numThreads)
}

public fun setSeed(seed: Int): Unit {
    JNoa.setSeed(seed)
}

public inline fun <T, ArrayT,
        GradTensorT : NoaTensorOverField<T>,
        GradAlgebraT : NoaPartialDivisionAlgebra<T, ArrayT, GradTensorT>>
        GradAlgebraT.withGradAt(
    tensor: GradTensorT,
    block: GradAlgebraT.(GradTensorT) -> GradTensorT
): GradTensorT {
    tensor.requiresGrad = true
    return this.block(tensor)
}
