package scientifik.kmath.viktor

import org.jetbrains.bio.viktor.F64FlatArray
import scientifik.kmath.structures.MutableBuffer

@Suppress("NOTHING_TO_INLINE", "OVERRIDE_BY_INLINE")
inline class ViktorBuffer(val flatArray: F64FlatArray) : MutableBuffer<Double> {
    override val size: Int get() = flatArray.size

    override inline fun get(index: Int): Double = flatArray[index]
    override inline fun set(index: Int, value: Double) {
        flatArray[index] = value
    }

    override fun copy(): MutableBuffer<Double> {
        return ViktorBuffer(flatArray.copy().flatten())
    }

    override operator fun iterator(): Iterator<Double> = flatArray.data.iterator()
}
