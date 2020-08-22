package scientifik.kmath.structures

import scientifik.kmath.operations.*

interface BufferedNDAlgebra<T, C> : NDAlgebra<T, C, NDBuffer<T>> {
    val strides: Strides

    override fun check(vararg elements: NDBuffer<T>): Unit =
        require(elements.all { it.strides == strides }) { ("Strides mismatch") }

    /**
     * Convert any [NDStructure] to buffered structure using strides from this context.
     * If the structure is already [NDBuffer], conversion is free. If not, it could be expensive because iteration over
     * indices.
     *
     * If the argument is [NDBuffer] with different strides structure, the new element will be produced.
     */
    fun NDStructure<T>.toBuffer(): NDBuffer<T> {
        return if (this is NDBuffer<T> && this.strides == this@BufferedNDAlgebra.strides) {
            this
        } else {
            produce { index -> get(index) }
        }
    }

    /**
     * Convert a buffer to element of this algebra
     */
    fun NDBuffer<T>.toElement(): MathElement<out BufferedNDAlgebra<T, C>>
}


interface BufferedNDSpace<T, S : Space<T>> : NDSpace<T, S, NDBuffer<T>>, BufferedNDAlgebra<T, S> {
    override fun NDBuffer<T>.toElement(): SpaceElement<NDBuffer<T>, *, out BufferedNDSpace<T, S>>
}

interface BufferedNDRing<T, R : Ring<T>> : NDRing<T, R, NDBuffer<T>>, BufferedNDSpace<T, R> {
    override fun NDBuffer<T>.toElement(): RingElement<NDBuffer<T>, *, out BufferedNDRing<T, R>>
}

interface BufferedNDField<T, F : Field<T>> : NDField<T, F, NDBuffer<T>>, BufferedNDRing<T, F> {
    override fun NDBuffer<T>.toElement(): FieldElement<NDBuffer<T>, *, out BufferedNDField<T, F>>
}
