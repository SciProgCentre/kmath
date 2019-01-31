package scientifik.kmath.stat

import scientifik.kmath.operations.Field
import scientifik.kmath.operations.RealField
import scientifik.kmath.operations.Ring
import scientifik.kmath.operations.Space
import scientifik.kmath.structures.NDStructure
import kotlin.math.pow




// TODO tailrec
fun <T> Ring<T>.pow(element : T, n : Int) : T {
    if (n == 0 ){
        return one
    }
    if (n==1){
        return element
    }
    val temp = pow(element, n / 2)
    return if (n%2==0) temp*temp else element*temp*temp
}

//fun <T,R> NDStructure<T>.map(transform : (T) -> R) : NDStructure<R>{
//
//}


/**
 * Context for sequence-like operations
 */
class CollectionsOperations<T>(val context: Space<T>){
    fun sum(structure: NDStructure<T>): T {
        return with(context){
            var sum = zero
            for (element in structure.elements()) {
                sum += element.second
            }
            sum
        }
    }

}

/**
 * Context for statistical operations
 */
open class Statistical<T>(val context : Field<T>){
        fun mean(data : NDStructure<T>) = moment(data, 1)

        fun variance(data: NDStructure<T>) = centralMomentum(data, 2)

        fun moment(data: NDStructure<T>, k : Int) : T{
            return with(context){
                var result = zero
                val number = data.shape.reduce { acc, i -> acc*i }
                for (element in data.elements()){
                    result += pow(element.second, k)
                }
                result/number
            }
        }

        fun centralMomentum(data: NDStructure<T>, k: Int) = with(context){moment(data, k) - pow(mean(data), k)}

}

class RealStatistical : Statistical<Double>(RealField){
    fun std(data : NDStructure<Double>) = with(context){variance(data).pow(0.5)}
}