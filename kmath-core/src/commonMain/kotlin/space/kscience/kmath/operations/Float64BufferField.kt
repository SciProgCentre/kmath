/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.operations

import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.Float64
import space.kscience.kmath.structures.Float64Buffer

/**
 * [ExtendedField] over [Float64Buffer].
 *
 * @property size the size of buffers to operate on.
 */
public class Float64BufferField(public val size: Int) : ExtendedField<Buffer<Float64>>, Float64BufferOps() {
    override val zero: Buffer<Float64> by lazy { Float64Buffer(size) { 0.0 } }
    override val one: Buffer<Float64> by lazy { Float64Buffer(size) { 1.0 } }

    override fun sinh(arg: Buffer<Float64>): Float64Buffer = super<Float64BufferOps>.sinh(arg)

    override fun cosh(arg: Buffer<Float64>): Float64Buffer = super<Float64BufferOps>.cosh(arg)

    override fun tanh(arg: Buffer<Float64>): Float64Buffer = super<Float64BufferOps>.tanh(arg)

    override fun asinh(arg: Buffer<Float64>): Float64Buffer = super<Float64BufferOps>.asinh(arg)

    override fun acosh(arg: Buffer<Float64>): Float64Buffer = super<Float64BufferOps>.acosh(arg)

    override fun atanh(arg: Buffer<Float64>): Float64Buffer = super<Float64BufferOps>.atanh(arg)

    override fun power(arg: Buffer<Float64>, pow: Number): Float64Buffer = if (pow.isInteger()) {
        arg.map { it.pow(pow.toInt()) }
    } else {
        arg.map {
            if (it < 0) throw IllegalArgumentException("Negative argument $it could not be raised to the fractional power")
            it.pow(pow.toDouble())
        }
    }

    override fun unaryOperationFunction(operation: String): (arg: Buffer<Float64>) -> Buffer<Float64> =
        super<ExtendedField>.unaryOperationFunction(operation)
}