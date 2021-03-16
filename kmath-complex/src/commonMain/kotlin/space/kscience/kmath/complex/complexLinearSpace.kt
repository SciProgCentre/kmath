/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.complex

import space.kscience.kmath.linear.BufferedLinearSpace
import space.kscience.kmath.operations.ExtendedField
import space.kscience.kmath.operations.NumericAlgebra
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.BufferFactory

public class ComplexLinearSpace<T : Any, out A>(
    elementContext: A,
    bufferFactory: BufferFactory<T>,
) : BufferedLinearSpace<Complex<T>, ComplexRing<T, A>>(
    ComplexRing(elementContext),
    { size, init -> Buffer.complex(bufferFactory, size, init) },
) where A : Ring<T>, A : NumericAlgebra<T>

public fun <T : Any, A> BufferedLinearSpace<T, A>.complex(): ComplexLinearSpace<T, A> where A : ExtendedField<T>, A : NumericAlgebra<T> =
    ComplexLinearSpace(elementAlgebra, bufferFactory)
