/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensorflow

import org.tensorflow.Graph
import org.tensorflow.Output
import org.tensorflow.ndarray.NdArray
import org.tensorflow.types.TInt32
import org.tensorflow.types.TInt64

public class IntTensorFlowOutput(
    graph: Graph,
    output: Output<TInt32>,
) : TensorFlowOutput<Int, TInt32>(graph, output) {
    override fun org.tensorflow.Tensor.actualizeTensor(): NdArray<Int> = this as TInt32
}

public class LongTensorFlowOutput(
    graph: Graph,
    output: Output<TInt64>,
) : TensorFlowOutput<Long, TInt64>(graph, output) {
    override fun org.tensorflow.Tensor.actualizeTensor(): NdArray<Long> = this as TInt64
}