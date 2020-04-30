package scientifik.kmath.viktor

import org.jetbrains.bio.viktor.F64Array
import scientifik.kmath.operations.RealField
import scientifik.kmath.structures.DefaultStrides
import scientifik.kmath.structures.MutableNDStructure
import scientifik.kmath.structures.NDField

@Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
inline class ViktorNDStructure(val f64Buffer: F64Array) : MutableNDStructure<Double> {

    override val shape: IntArray get() = f64Buffer.shape

    override inline fun get(index: IntArray): Double = f64Buffer.get(*index)

    override inline fun set(index: IntArray, value: Double) {
        f64Buffer.set(*index, value = value)
    }

    override fun elements(): Sequence<Pair<IntArray, Double>> {
        return DefaultStrides(shape).indices().map { it to get(it) }
    }
}

fun F64Array.asStructure(): ViktorNDStructure = ViktorNDStructure(this)

@Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
class ViktorNDField(override val shape: IntArray) : NDField<Double, RealField, ViktorNDStructure> {
    override val zero: ViktorNDStructure
        get() = F64Array.full(init = 0.0, shape = *shape).asStructure()
    override val one: ViktorNDStructure
        get() = F64Array.full(init = 1.0, shape = *shape).asStructure()

    val strides = DefaultStrides(shape)

    override val elementContext: RealField get() = RealField

    override fun produce(initializer: RealField.(IntArray) -> Double): ViktorNDStructure = F64Array(*shape).apply {
        this@ViktorNDField.strides.indices().forEach { index ->
            set(value = RealField.initializer(index), indices = *index)
        }
    }.asStructure()

    override fun map(arg: ViktorNDStructure, transform: RealField.(Double) -> Double): ViktorNDStructure =
        F64Array(*shape).apply {
            this@ViktorNDField.strides.indices().forEach { index ->
                set(value = RealField.transform(arg[index]), indices = *index)
            }
        }.asStructure()

    override fun mapIndexed(
        arg: ViktorNDStructure,
        transform: RealField.(index: IntArray, Double) -> Double
    ): ViktorNDStructure = F64Array(*shape).apply {
        this@ViktorNDField.strides.indices().forEach { index ->
            set(value = RealField.transform(index, arg[index]), indices = *index)
        }
    }.asStructure()

    override fun combine(
        a: ViktorNDStructure,
        b: ViktorNDStructure,
        transform: RealField.(Double, Double) -> Double
    ): ViktorNDStructure = F64Array(*shape).apply {
        this@ViktorNDField.strides.indices().forEach { index ->
            set(value = RealField.transform(a[index], b[index]), indices = *index)
        }
    }.asStructure()

    override fun add(a: ViktorNDStructure, b: ViktorNDStructure): ViktorNDStructure {
        return (a.f64Buffer + b.f64Buffer).asStructure()
    }

    override fun multiply(a: ViktorNDStructure, k: Number): ViktorNDStructure =
        (a.f64Buffer * k.toDouble()).asStructure()

    override inline fun ViktorNDStructure.plus(b: ViktorNDStructure): ViktorNDStructure =
        (f64Buffer + b.f64Buffer).asStructure()

    override inline fun ViktorNDStructure.minus(b: ViktorNDStructure): ViktorNDStructure =
        (f64Buffer - b.f64Buffer).asStructure()

    override inline fun ViktorNDStructure.times(k: Number): ViktorNDStructure = (f64Buffer * k.toDouble()).asStructure()

    override inline fun ViktorNDStructure.plus(arg: Double): ViktorNDStructure = (f64Buffer.plus(arg)).asStructure()
}