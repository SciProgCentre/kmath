/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensorflow

import org.tensorflow.types.family.TNumber
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.TrigonometricOperations

//

// TODO add other operations

public fun <T, TT : TNumber, A> TensorFlowAlgebra<T, TT, A>.sin(
    arg: StructureND<T>,
): TensorFlowOutput<T, TT> where A : TrigonometricOperations<T>, A : Ring<T> = arg.operate { ops.math.sin(it) }

public fun <T, TT : TNumber, A> TensorFlowAlgebra<T, TT, A>.cos(
    arg: StructureND<T>,
): TensorFlowOutput<T, TT> where A : TrigonometricOperations<T>, A : Ring<T> = arg.operate { ops.math.cos(it) }