/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions.testUtils

import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.asBuffer


fun <T> bufferOf(vararg elements: T): Buffer<T> = elements.asBuffer()