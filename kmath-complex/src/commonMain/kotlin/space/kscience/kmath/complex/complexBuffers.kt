/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.complex

import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.BufferFactory
import space.kscience.kmath.structures.MutableBuffer
import space.kscience.kmath.structures.MutableBufferFactory


private class ComplexBuffer<out T : Any>(factory: BufferFactory<T>, override val size: Int, init: (Int) -> Complex<T>) :
    Buffer<Complex<T>> {
    private val re: Buffer<T>
    private val im: Buffer<T>

    init {
        val tmp = Array(size, init)
        re = factory(size) { tmp[it].re }
        im = factory(size) { tmp[it].im }
    }

    override fun get(index: Int): Complex<T> = Complex(re[index], im[index])

    override fun iterator(): Iterator<Complex<T>> = object : AbstractIterator<Complex<T>>() {
        private val a = re.iterator()
        private val b = im.iterator()

        override fun computeNext() = if (a.hasNext() && b.hasNext())
            setNext(Complex(a.next(), b.next()))
        else
            done()
    }
}

/**
 * Creates a new buffer of complex elements with the specified [size], where each element is calculated by calling the
 * specified [init] function.
 */
public fun <T : Any> Buffer.Companion.complex(
    factory: BufferFactory<T>,
    size: Int,
    init: (Int) -> Complex<T>,
): Buffer<Complex<T>> = ComplexBuffer(factory, size, init)

private class MutableComplexBuffer<T : Any> private constructor(
    override val size: Int,
    private val re: MutableBuffer<T>,
    private val im: MutableBuffer<T>,
) : MutableBuffer<Complex<T>> {
    private constructor(
        factory: MutableBufferFactory<T>,
        size: Int,
        tmp: Array<Complex<T>>,
    ) : this(size, factory(size) { tmp[it].re }, factory(size) { tmp[it].im })

    constructor(
        factory: MutableBufferFactory<T>,
        size: Int,
        init: (Int) -> Complex<T>,
    ) : this(factory, size, Array(size, init))

    override fun get(index: Int): Complex<T> = Complex(re[index], im[index])

    override fun set(index: Int, value: Complex<T>) {
        re[index] = value.re
        im[index] = value.im
    }

    override fun iterator(): Iterator<Complex<T>> = object : AbstractIterator<Complex<T>>() {
        private val a = re.iterator()
        private val b = im.iterator()

        override fun computeNext() = if (a.hasNext() && b.hasNext())
            setNext(Complex(a.next(), b.next()))
        else
            done()
    }

    override fun copy(): MutableBuffer<Complex<T>> = MutableComplexBuffer(size, re.copy(), im.copy())
}


/**
 * Creates a new buffer of complex elements with the specified [size], where each element is calculated by calling the
 * specified [init] function.
 */
public fun <T : Any> MutableBuffer.Companion.complex(
    factory: MutableBufferFactory<T>,
    size: Int,
    init: (Int) -> Complex<T>,
): MutableBuffer<Complex<T>> = MutableComplexBuffer(factory, size, init)
