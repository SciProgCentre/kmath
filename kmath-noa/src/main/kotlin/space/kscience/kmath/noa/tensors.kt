/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.noa

import space.kscience.kmath.noa.memory.NoaResource
import space.kscience.kmath.noa.memory.NoaScope
import space.kscience.kmath.tensors.api.Tensor

internal typealias TensorHandle = Long

public sealed class NoaTensor<T>
constructor(scope: NoaScope, internal val tensorHandle: TensorHandle) :
    NoaResource(scope){

    override fun dispose(): Unit = JNoa.disposeTensor(tensorHandle)
}

public class NoaDoubleTensor
internal constructor(scope: NoaScope, tensorHandle: TensorHandle) :
        NoaTensor<Double>(scope, tensorHandle)
