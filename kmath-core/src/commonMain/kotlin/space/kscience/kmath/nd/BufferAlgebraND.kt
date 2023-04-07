/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:OptIn(UnstableKMathAPI::class)

package space.kscience.kmath.nd

import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.operations.*

public interface BufferAlgebraND<T, out A : Algebra<T>> : AlgebraND<T, A> {
    public val indexerBuilder: (ShapeND) -> ShapeIndexer
    public val bufferAlgebra: BufferAlgebra<T, A>
    override val elementAlgebra: A get() = bufferAlgebra.elementAlgebra

    override fun structureND(shape: ShapeND, initializer: A.(IntArray) -> T): BufferND<T> {
        val indexer = indexerBuilder(shape)
        return BufferND(
            indexer,
            bufferAlgebra.buffer(indexer.linearSize) { offset ->
                elementAlgebra.initializer(indexer.index(offset))
            }
        )
    }

    @OptIn(PerformancePitfall::class)
    public fun StructureND<T>.toBufferND(): BufferND<T> = when (this) {
        is BufferND -> this
        else -> {
            val indexer = indexerBuilder(shape)
            BufferND(indexer, bufferAlgebra.buffer(indexer.linearSize) { offset -> get(indexer.index(offset)) })
        }
    }

    @PerformancePitfall
    override fun StructureND<T>.map(transform: A.(T) -> T): BufferND<T> = mapInline(toBufferND(), transform)

    @PerformancePitfall
    override fun StructureND<T>.mapIndexed(transform: A.(index: IntArray, T) -> T): BufferND<T> =
        mapIndexedInline(toBufferND(), transform)

    @PerformancePitfall
    override fun zip(left: StructureND<T>, right: StructureND<T>, transform: A.(T, T) -> T): BufferND<T> =
        zipInline(left.toBufferND(), right.toBufferND(), transform)

    public companion object {
        public val defaultIndexerBuilder: (ShapeND) -> ShapeIndexer = ::Strides
    }
}

public inline fun <T, A : Algebra<T>> BufferAlgebraND<T, A>.mapInline(
    arg: BufferND<T>,
    crossinline transform: A.(T) -> T,
): BufferND<T> {
    val indexes = arg.indices
    val buffer = arg.buffer
    return BufferND(
        indexes,
        bufferAlgebra.run {
            elementBufferFactory(buffer.size) { elementAlgebra.transform(buffer[it]) }
        }
    )
}

internal inline fun <T, A : Algebra<T>> BufferAlgebraND<T, A>.mapIndexedInline(
    arg: BufferND<T>,
    crossinline transform: A.(index: IntArray, arg: T) -> T,
): BufferND<T> {
    val indexes = arg.indices
    val buffer = arg.buffer
    return BufferND(
        indexes,
        bufferAlgebra.run {
            elementBufferFactory(buffer.size) { elementAlgebra.transform(indexes.index(it), buffer[it]) }
        }
    )
}

internal inline fun <T, A : Algebra<T>> BufferAlgebraND<T, A>.zipInline(
    l: BufferND<T>,
    r: BufferND<T>,
    crossinline block: A.(l: T, r: T) -> T,
): BufferND<T> {
    require(l.indices == r.indices) { "Zip requires the same shapes, but found ${l.shape} on the left and ${r.shape} on the right" }
    val indexes = l.indices
    val lbuffer = l.buffer
    val rbuffer = r.buffer
    return BufferND(
        indexes,
        bufferAlgebra.run {
            elementBufferFactory(lbuffer.size) { elementAlgebra.block(lbuffer[it], rbuffer[it]) }
        }
    )
}

@OptIn(PerformancePitfall::class)
public open class BufferedGroupNDOps<T, out A : Group<T>>(
    override val bufferAlgebra: BufferAlgebra<T, A>,
    override val indexerBuilder: (ShapeND) -> ShapeIndexer = BufferAlgebraND.defaultIndexerBuilder,
) : GroupOpsND<T, A>, BufferAlgebraND<T, A> {
    override fun StructureND<T>.unaryMinus(): StructureND<T> = map { -it }
}

public open class BufferedRingOpsND<T, out A : Ring<T>>(
    bufferAlgebra: BufferAlgebra<T, A>,
    indexerBuilder: (ShapeND) -> ShapeIndexer = BufferAlgebraND.defaultIndexerBuilder,
) : BufferedGroupNDOps<T, A>(bufferAlgebra, indexerBuilder), RingOpsND<T, A>

public open class BufferedFieldOpsND<T, out A : Field<T>>(
    bufferAlgebra: BufferAlgebra<T, A>,
    indexerBuilder: (ShapeND) -> ShapeIndexer = BufferAlgebraND.defaultIndexerBuilder,
) : BufferedRingOpsND<T, A>(bufferAlgebra, indexerBuilder), FieldOpsND<T, A> {

    public constructor(
        elementAlgebra: A,
        indexerBuilder: (ShapeND) -> ShapeIndexer = BufferAlgebraND.defaultIndexerBuilder,
    ) : this(BufferFieldOps(elementAlgebra), indexerBuilder)

    @OptIn(PerformancePitfall::class)
    override fun scale(a: StructureND<T>, value: Double): StructureND<T> = a.map { it * value }
}

public val <T, A : Group<T>> BufferAlgebra<T, A>.nd: BufferedGroupNDOps<T, A> get() = BufferedGroupNDOps(this)
public val <T, A : Ring<T>> BufferAlgebra<T, A>.nd: BufferedRingOpsND<T, A> get() = BufferedRingOpsND(this)
public val <T, A : Field<T>> BufferAlgebra<T, A>.nd: BufferedFieldOpsND<T, A> get() = BufferedFieldOpsND(this)


public fun <T, A : Algebra<T>> BufferAlgebraND<T, A>.structureND(
    vararg shape: Int,
    initializer: A.(IntArray) -> T,
): BufferND<T> = structureND(ShapeND(shape), initializer)

public fun <T, EA : Algebra<T>, A> A.structureND(
    initializer: EA.(IntArray) -> T,
): BufferND<T> where A : BufferAlgebraND<T, EA>, A : WithShape = structureND(shape, initializer)

//// group factories
//public fun <T, A : Group<T>> A.ndAlgebra(
//    bufferAlgebra: BufferAlgebra<T, A>,
//    vararg shape: Int,
//): BufferedGroupNDOps<T, A> = BufferedGroupNDOps(bufferAlgebra)
//
//@JvmName("withNdGroup")
//public inline fun <T, A : Group<T>, R> A.withNdAlgebra(
//    noinline bufferFactory: BufferFactory<T>,
//    vararg shape: Int,
//    action: BufferedGroupNDOps<T, A>.() -> R,
//): R {
//    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
//    return ndAlgebra(bufferFactory, *shape).run(action)
//}

////ring factories
//public fun <T, A : Ring<T>> A.ndAlgebra(
//    bufferFactory: BufferFactory<T>,
//    vararg shape: Int,
//): BufferedRingNDOps<T, A> = BufferedRingNDOps(shape, this, bufferFactory)
//
//@JvmName("withNdRing")
//public inline fun <T, A : Ring<T>, R> A.withNdAlgebra(
//    noinline bufferFactory: BufferFactory<T>,
//    vararg shape: Int,
//    action: BufferedRingNDOps<T, A>.() -> R,
//): R {
//    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
//    return ndAlgebra(bufferFactory, *shape).run(action)
//}
//
////field factories
//public fun <T, A : Field<T>> A.ndAlgebra(
//    bufferFactory: BufferFactory<T>,
//    vararg shape: Int,
//): BufferedFieldNDOps<T, A> = BufferedFieldNDOps(shape, this, bufferFactory)
//
///**
// * Create a [FieldND] for this [Field] inferring proper buffer factory from the type
// */
//@UnstableKMathAPI
//@Suppress("UNCHECKED_CAST")
//public inline fun <reified T : Any, A : Field<T>> A.autoNdAlgebra(
//    vararg shape: Int,
//): FieldND<T, A> = when (this) {
//    DoubleField -> DoubleFieldND(shape) as FieldND<T, A>
//    else -> BufferedFieldNDOps(shape, this, Buffer.Companion::auto)
//}
//
//@JvmName("withNdField")
//public inline fun <T, A : Field<T>, R> A.withNdAlgebra(
//    noinline bufferFactory: BufferFactory<T>,
//    vararg shape: Int,
//    action: BufferedFieldNDOps<T, A>.() -> R,
//): R {
//    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
//    return ndAlgebra(bufferFactory, *shape).run(action)
//}