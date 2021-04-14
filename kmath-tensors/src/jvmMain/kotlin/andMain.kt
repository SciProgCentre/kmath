import space.kscience.kmath.nd.*
import space.kscience.kmath.tensors.core.DoubleLinearOpsTensorAlgebra
import space.kscience.kmath.tensors.core.DoubleTensorAlgebra
import space.kscience.kmath.tensors.core.array
import kotlin.math.abs
import kotlin.math.sqrt


fun main() {

    DoubleTensorAlgebra {
        val tensor = fromArray(
            intArrayOf(2, 2, 2),
            doubleArrayOf(
                1.0, 3.0,
                1.0, 2.0,
                1.5, 1.0,
                10.0, 2.0
            )
        )
        val tensor2 = fromArray(
            intArrayOf(2, 2),
            doubleArrayOf(
                0.0, 0.0,
                0.0, 0.0
            )
        )
        DoubleLinearOpsTensorAlgebra {
            println(tensor2.det().value())
        }
    }
}
