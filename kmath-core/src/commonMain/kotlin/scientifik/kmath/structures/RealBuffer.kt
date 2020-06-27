package scientifik.kmath.structures

inline class RealBuffer(val array: DoubleArray) : MutableBuffer<Double> {
    override val size: Int get() = array.size

    override fun get(index: Int): Double = array[index]

    override fun set(index: Int, value: Double) {
        array[index] = value
    }

    override fun iterator() = array.iterator()

    override fun copy(): MutableBuffer<Double> =
        RealBuffer(array.copyOf())
}

@Suppress("FunctionName")
inline fun RealBuffer(size: Int, init: (Int) -> Double): RealBuffer = RealBuffer(DoubleArray(size) { init(it) })

@Suppress("FunctionName")
fun RealBuffer(vararg doubles: Double): RealBuffer = RealBuffer(doubles)

/**
 * Transform buffer of doubles into array for high performance operations
 */
val MutableBuffer<out Double>.array: DoubleArray
    get() = if (this is RealBuffer) {
        array
    } else {
        DoubleArray(size) { get(it) }
    }

fun DoubleArray.asBuffer() = RealBuffer(this)