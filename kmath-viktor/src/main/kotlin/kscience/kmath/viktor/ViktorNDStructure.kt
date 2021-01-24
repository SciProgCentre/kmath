package kscience.kmath.viktor

import kscience.kmath.nd.*
import kscience.kmath.operations.RealField
import org.jetbrains.bio.viktor.F64Array

@Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
public inline class ViktorNDStructure(public val f64Buffer: F64Array) : MutableNDStructure<Double> {
    public override val shape: IntArray get() = f64Buffer.shape

    public override inline fun get(index: IntArray): Double = f64Buffer.get(*index)

    public override inline fun set(index: IntArray, value: Double) {
        f64Buffer.set(*index, value = value)
    }

    public override fun elements(): Sequence<Pair<IntArray, Double>> =
        DefaultStrides(shape).indices().map { it to get(it) }
}

public fun F64Array.asStructure(): ViktorNDStructure = ViktorNDStructure(this)

@Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
public class ViktorNDField(public override val shape: IntArray) : NDField<Double, RealField> {

    public val NDStructure<Double>.f64Buffer: F64Array
        get() = when {
            !shape.contentEquals(this@ViktorNDField.shape) -> throw ShapeMismatchException(
                this@ViktorNDField.shape,
                shape
            )
            this is ViktorNDStructure && this.f64Buffer.shape.contentEquals(this@ViktorNDField.shape) -> this.f64Buffer
            else -> produce { this@f64Buffer[it] }.f64Buffer
        }

    public override val zero: ViktorNDStructure
        get() = F64Array.full(init = 0.0, shape = shape).asStructure()

    public override val one: ViktorNDStructure
        get() = F64Array.full(init = 1.0, shape = shape).asStructure()

    private val strides: Strides = DefaultStrides(shape)

    public override val elementContext: RealField get() = RealField

    public override fun produce(initializer: RealField.(IntArray) -> Double): ViktorNDStructure =
        F64Array(*shape).apply {
            this@ViktorNDField.strides.indices().forEach { index ->
                set(value = RealField.initializer(index), indices = index)
            }
        }.asStructure()

    public override fun map(arg: NDStructure<Double>, transform: RealField.(Double) -> Double): ViktorNDStructure =
        F64Array(*shape).apply {
            this@ViktorNDField.strides.indices().forEach { index ->
                set(value = RealField.transform(arg[index]), indices = index)
            }
        }.asStructure()

    public override fun mapIndexed(
        arg: NDStructure<Double>,
        transform: RealField.(index: IntArray, Double) -> Double
    ): ViktorNDStructure = F64Array(*shape).apply {
        this@ViktorNDField.strides.indices().forEach { index ->
            set(value = RealField.transform(index, arg[index]), indices = index)
        }
    }.asStructure()

    public override fun combine(
        a: NDStructure<Double>,
        b: NDStructure<Double>,
        transform: RealField.(Double, Double) -> Double
    ): ViktorNDStructure = F64Array(*shape).apply {
        this@ViktorNDField.strides.indices().forEach { index ->
            set(value = RealField.transform(a[index], b[index]), indices = index)
        }
    }.asStructure()

    public override fun add(a: NDStructure<Double>, b: NDStructure<Double>): ViktorNDStructure =
        (a.f64Buffer + b.f64Buffer).asStructure()

    public override fun multiply(a: NDStructure<Double>, k: Number): ViktorNDStructure =
        (a.f64Buffer * k.toDouble()).asStructure()

    public override inline fun NDStructure<Double>.plus(b: NDStructure<Double>): ViktorNDStructure =
        (f64Buffer + b.f64Buffer).asStructure()

    public override inline fun NDStructure<Double>.minus(b: NDStructure<Double>): ViktorNDStructure =
        (f64Buffer - b.f64Buffer).asStructure()

    public override inline fun NDStructure<Double>.times(k: Number): ViktorNDStructure =
        (f64Buffer * k.toDouble()).asStructure()

    public override inline fun NDStructure<Double>.plus(arg: Double): ViktorNDStructure =
        (f64Buffer.plus(arg)).asStructure()
}

public fun ViktorNDField(vararg shape: Int): ViktorNDField = ViktorNDField(shape)