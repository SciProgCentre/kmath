/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.optimization

public interface Optimizer<T, P : OptimizationProblem<T>> {
    public suspend fun optimize(problem: P): P
}