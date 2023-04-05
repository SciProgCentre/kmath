/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.operations

import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.DoubleBuffer

/**
 * [ExtendedField] over [DoubleBuffer].
 *
 * @property size the size of buffers to operate on.
 */
public class DoubleBufferField(public val size: Int) : ExtendedField<Buffer<Double>>, DoubleBufferOps() {
    override val zero: Buffer<Double> by lazy { DoubleBuffer(size) { 0.0 } }
    override val one: Buffer<Double> by lazy { DoubleBuffer(size) { 1.0 } }

    override fun sinh(arg: Buffer<Double>): DoubleBuffer = super<DoubleBufferOps>.sinh(arg)

    override fun cosh(arg: Buffer<Double>): DoubleBuffer = super<DoubleBufferOps>.cosh(arg)

    override fun tanh(arg: Buffer<Double>): DoubleBuffer = super<DoubleBufferOps>.tanh(arg)

    override fun asinh(arg: Buffer<Double>): DoubleBuffer = super<DoubleBufferOps>.asinh(arg)

    override fun acosh(arg: Buffer<Double>): DoubleBuffer = super<DoubleBufferOps>.acosh(arg)

    override fun atanh(arg: Buffer<Double>): DoubleBuffer = super<DoubleBufferOps>.atanh(arg)

    override fun power(arg: Buffer<Double>, pow: Number): DoubleBuffer = if (pow.isInteger()) {
        arg.map { it.pow(pow.toInt()) }
    } else {
        arg.map {
            if(it<0) throw IllegalArgumentException("Negative argument $it could not be raised to the fractional power")
            it.pow(pow.toDouble())
        }
    }

    override fun unaryOperationFunction(operation: String): (arg: Buffer<Double>) -> Buffer<Double> =
        super<ExtendedField>.unaryOperationFunction(operation)
}