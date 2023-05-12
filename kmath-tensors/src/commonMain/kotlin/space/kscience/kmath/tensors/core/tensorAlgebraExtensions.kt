/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:OptIn(PerformancePitfall::class)

package space.kscience.kmath.tensors.core

import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.nd.ShapeND
import kotlin.jvm.JvmName

@JvmName("varArgOne")
public fun DoubleTensorAlgebra.one(vararg shape: Int): DoubleTensor = ones(ShapeND(shape))

public fun DoubleTensorAlgebra.one(shape: ShapeND): DoubleTensor = ones(shape)

@JvmName("varArgZero")
public fun DoubleTensorAlgebra.zero(vararg shape: Int): DoubleTensor = zeros(ShapeND(shape))

public fun DoubleTensorAlgebra.zero(shape: ShapeND): DoubleTensor = zeros(shape)
