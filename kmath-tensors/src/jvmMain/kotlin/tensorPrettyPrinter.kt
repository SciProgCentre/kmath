
import space.kscience.kmath.tensors.core.DoubleTensor
import space.kscience.kmath.tensors.core.vectorSequence
import java.lang.StringBuilder
/*
internal fun format(value: Double, digits: Int = 4): String {
    val res = "%.${digits}e".format(value).replace(',', '.')
    if (value < 0.0) {
        return res
    }
    return StringBuilder().append(" ").append(res).toString()
}

public fun DoubleTensor.toPrettyString(): String {
    val builder = StringBuilder()
    with(builder) {
        var offset = 0
        val shape = this@toPrettyString.shape
        val linearStructure = this@toPrettyString.linearStructure
        var vectorSize = shape.last()
        val initString = "DoubleTensor(\n"
        append(initString)
        var charOffset = 3
        for (vector in vectorSequence()) {
            append(" ".repeat(charOffset))
            val index = linearStructure.index(offset)
            for (ind in index.reversed()) {
                if (ind != 0) {
                    break
                }
                append("[")
                charOffset += 1
            }
            // todo refactor
            val values = mutableListOf<Double>()
            for (i in 0 until vectorSize) {
                values.add(vector[intArrayOf(i)])
            }
            append(values.map { format(it) }.joinToString(", "))
            append("]")
            charOffset -= 1
            for (i in shape.size - 2 downTo 0){
                val ind = index[i]
                val maxInd = shape[i]
                if (ind != maxInd - 1) {
                    break
                }
                append("]")
                charOffset -=1
            }
            offset += vectorSize
            // todo refactor
            if (this@toPrettyString.numel == offset) {
                break
            }
            append(",\n")
        }
        append("\n)")
    }
    return builder.toString()
}
*/


