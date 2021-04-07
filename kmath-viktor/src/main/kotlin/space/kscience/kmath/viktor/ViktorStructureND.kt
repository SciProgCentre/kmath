package space.kscience.kmath.viktor

import org.jetbrains.bio.viktor.F64Array
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.nd.*
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.ExtendedField
import space.kscience.kmath.operations.NumbersAddOperations
import space.kscience.kmath.operations.ScaleOperations

@Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
public inline class ViktorStructureND(public val f64Buffer: F64Array) : MutableStructureND<Double> {
    public override val shape: IntArray get() = f64Buffer.shape

    public override inline fun get(index: IntArray): Double = f64Buffer.get(*index)

    public override inline fun set(index: IntArray, value: Double) {
        f64Buffer.set(*index, value = value)
    }

    public override fun elements(): Sequence<Pair<IntArray, Double>> =
        DefaultStrides(shape).indices().map { it to get(it) }
}

public fun F64Array.asStructure(): ViktorStructureND = ViktorStructureND(this)

@OptIn(UnstableKMathAPI::class)
@Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
public class ViktorFieldND(public override val shape: IntArray) : FieldND<Double, DoubleField>,
    NumbersAddOperations<StructureND<Double>>, ExtendedField<StructureND<Double>>,
    ScaleOperations<StructureND<Double>> {

    public val StructureND<Double>.f64Buffer: F64Array
        get() = when {
            !shape.contentEquals(this@ViktorFieldND.shape) -> throw ShapeMismatchException(
                this@ViktorFieldND.shape,
                shape
            )
            this is ViktorStructureND && this.f64Buffer.shape.contentEquals(this@ViktorFieldND.shape) -> this.f64Buffer
            else -> produce { this@f64Buffer[it] }.f64Buffer
        }

    public override val zero: ViktorStructureND by lazy { F64Array.full(init = 0.0, shape = shape).asStructure() }
    public override val one: ViktorStructureND by lazy { F64Array.full(init = 1.0, shape = shape).asStructure() }

    private val strides: Strides = DefaultStrides(shape)

    public override val elementContext: DoubleField get() = DoubleField

    public override fun produce(initializer: DoubleField.(IntArray) -> Double): ViktorStructureND =
        F64Array(*shape).apply {
            this@ViktorFieldND.strides.indices().forEach { index ->
                set(value = DoubleField.initializer(index), indices = index)
            }
        }.asStructure()

    public override fun StructureND<Double>.unaryMinus(): StructureND<Double> = -1 * this

    public override fun StructureND<Double>.map(transform: DoubleField.(Double) -> Double): ViktorStructureND =
        F64Array(*this@ViktorFieldND.shape).apply {
            this@ViktorFieldND.strides.indices().forEach { index ->
                set(value = DoubleField.transform(this@map[index]), indices = index)
            }
        }.asStructure()

    public override fun StructureND<Double>.mapIndexed(
        transform: DoubleField.(index: IntArray, Double) -> Double,
    ): ViktorStructureND = F64Array(*this@ViktorFieldND.shape).apply {
        this@ViktorFieldND.strides.indices().forEach { index ->
            set(value = DoubleField.transform(index, this@mapIndexed[index]), indices = index)
        }
    }.asStructure()

    public override fun combine(
        a: StructureND<Double>,
        b: StructureND<Double>,
        transform: DoubleField.(Double, Double) -> Double,
    ): ViktorStructureND = F64Array(*shape).apply {
        this@ViktorFieldND.strides.indices().forEach { index ->
            set(value = DoubleField.transform(a[index], b[index]), indices = index)
        }
    }.asStructure()

    public override fun add(a: StructureND<Double>, b: StructureND<Double>): ViktorStructureND =
        (a.f64Buffer + b.f64Buffer).asStructure()

    public override fun scale(a: StructureND<Double>, value: Double): ViktorStructureND =
        (a.f64Buffer * value.toDouble()).asStructure()

    public override inline fun StructureND<Double>.plus(b: StructureND<Double>): ViktorStructureND =
        (f64Buffer + b.f64Buffer).asStructure()

    public override inline fun StructureND<Double>.minus(b: StructureND<Double>): ViktorStructureND =
        (f64Buffer - b.f64Buffer).asStructure()

    public override inline fun StructureND<Double>.times(k: Number): ViktorStructureND =
        (f64Buffer * k.toDouble()).asStructure()

    public override inline fun StructureND<Double>.plus(arg: Double): ViktorStructureND =
        (f64Buffer.plus(arg)).asStructure()

    public override fun number(value: Number): ViktorStructureND =
        F64Array.full(init = value.toDouble(), shape = shape).asStructure()

    public override fun sin(arg: StructureND<Double>): ViktorStructureND = arg.map { sin(it) }
    public override fun cos(arg: StructureND<Double>): ViktorStructureND = arg.map { cos(it) }
    public override fun tan(arg: StructureND<Double>): ViktorStructureND = arg.map { tan(it) }
    public override fun asin(arg: StructureND<Double>): ViktorStructureND = arg.map { asin(it) }
    public override fun acos(arg: StructureND<Double>): ViktorStructureND = arg.map { acos(it) }
    public override fun atan(arg: StructureND<Double>): ViktorStructureND = arg.map { atan(it) }

    public override fun power(arg: StructureND<Double>, pow: Number): ViktorStructureND = arg.map { it.pow(pow) }

    public override fun exp(arg: StructureND<Double>): ViktorStructureND = arg.f64Buffer.exp().asStructure()

    public override fun ln(arg: StructureND<Double>): ViktorStructureND = arg.f64Buffer.log().asStructure()
}

public fun ViktorNDField(vararg shape: Int): ViktorFieldND = ViktorFieldND(shape)
