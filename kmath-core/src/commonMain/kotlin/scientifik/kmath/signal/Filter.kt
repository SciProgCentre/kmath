package scientifik.kmath.signal

import scientifik.kmath.structures.NDStructure


interface Filter<T :Any>{
    fun process(input : T) : T
}

interface Convolve<T : Any>{
    fun convolve(input1 : T, input2: T) : T
}

object Convolve1D<Vec>

fun NDStructure<out Number>.convolve(){

}

abstract class SignalProcessing<T : Any>(
    val filter: Filter<T>,
    val convolver : Convolve<T>
){
    fun process()
}
