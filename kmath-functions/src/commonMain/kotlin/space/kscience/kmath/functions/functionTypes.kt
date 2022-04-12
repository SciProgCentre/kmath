/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.structures.Buffer

public typealias Function1D<T> = (T) -> T

public typealias FunctionND<T> = (Buffer<T>) -> T