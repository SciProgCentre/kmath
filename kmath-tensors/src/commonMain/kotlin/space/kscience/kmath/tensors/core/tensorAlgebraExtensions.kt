/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.tensors.core

public fun DoubleTensorAlgebra.ones(vararg shape: Int): DoubleTensor = ones(intArrayOf(*shape))