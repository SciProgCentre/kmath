/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.complex

import space.kscience.kmath.nd.BufferedExtendedFieldND
import space.kscience.kmath.nd.BufferedFieldND
import space.kscience.kmath.nd.BufferedGroupND
import space.kscience.kmath.nd.BufferedRingND
import space.kscience.kmath.operations.*
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.BufferFactory

public open class ComplexGroupND<T : Any, out A>(
    shape: IntArray,
    elementContext: A,
    bufferFactory: BufferFactory<T>,
) : BufferedGroupND<Complex<T>, ComplexGroup<T, A>>(
    shape,
    ComplexGroup(elementContext),
    { size, init -> Buffer.complex(bufferFactory, size, init) },
) where A : Group<T>, A : NumericAlgebra<T>

public fun <T : Any, A> BufferedGroupND<T, A>.complex(): ComplexGroupND<T, A> where A : Group<T>, A : NumericAlgebra<T> =
    ComplexGroupND(shape, elementContext, bufferFactory)


public open class ComplexRingND<T : Any, out A>(
    shape: IntArray,
    elementContext: A,
    bufferFactory: BufferFactory<T>,
) : BufferedRingND<Complex<T>, ComplexRing<T, A>>(
    shape,
    ComplexRing(elementContext),
    { size, init -> Buffer.complex(bufferFactory, size, init) },
) where A : Ring<T>, A : NumericAlgebra<T>

public fun <T : Any, A> BufferedRingND<T, A>.complex(): ComplexRingND<T, A> where A : Ring<T>, A : NumericAlgebra<T> =
    ComplexRingND(shape, elementContext, bufferFactory)


public open class ComplexFieldND<T : Any, out A>(
    shape: IntArray,
    elementContext: A,
    bufferFactory: BufferFactory<T>,
) : BufferedFieldND<Complex<T>, ComplexField<T, A>>(
    shape,
    ComplexField(elementContext),
    { size, init -> Buffer.complex(bufferFactory, size, init) },
) where A : Field<T>, A : NumericAlgebra<T>

public fun <T : Any, A> BufferedFieldND<T, A>.complex(): ComplexFieldND<T, A> where A : Field<T>, A : NumericAlgebra<T> =
    ComplexFieldND(shape, elementContext, bufferFactory)

public open class ComplexExtendedFieldND<T : Any, out A>(
    shape: IntArray,
    elementContext: A,
    bufferFactory: BufferFactory<T>,
) : BufferedExtendedFieldND<Complex<T>, ComplexExtendedField<T, A>>(
    shape,
    ComplexExtendedField(elementContext),
    { size, init -> Buffer.complex(bufferFactory, size, init) },
) where A : ExtendedField<T>, A : NumericAlgebra<T>

public fun <T : Any, A> BufferedExtendedFieldND<T, A>.complex(): ComplexExtendedFieldND<T, A> where A : ExtendedField<T>, A : NumericAlgebra<T> =
    ComplexExtendedFieldND(shape, elementContext, bufferFactory)
