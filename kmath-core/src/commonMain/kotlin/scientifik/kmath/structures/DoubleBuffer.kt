package scientifik.kmath.structures

inline class DoubleBuffer(val array: DoubleArray) : MutableBuffer<Double> {
    override val size: Int get() = array.size

    override fun get(index: Int): Double = array[index]

    override fun set(index: Int, value: Double) {
        array[index] = value
    }

    override fun iterator() = array.iterator()

    override fun copy(): MutableBuffer<Double> =
        DoubleBuffer(array.copyOf())
}

@Suppress("FunctionName")
inline fun DoubleBuffer(size: Int, init: (Int) -> Double): DoubleBuffer = DoubleBuffer(DoubleArray(size) { init(it) })

@Suppress("FunctionName")
fun DoubleBuffer(vararg doubles: Double): DoubleBuffer = DoubleBuffer(doubles)

/**
 * Transform buffer of doubles into array for high performance operations
 */
val MutableBuffer<out Double>.array: DoubleArray
    get() = if (this is DoubleBuffer) {
        array
    } else {
        DoubleArray(size) { get(it) }
    }

fun DoubleArray.asBuffer() = DoubleBuffer(this)