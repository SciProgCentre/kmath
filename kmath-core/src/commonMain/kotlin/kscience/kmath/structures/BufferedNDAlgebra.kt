package kscience.kmath.structures

import kscience.kmath.operations.*

public interface BufferedNDAlgebra<T, C> : NDAlgebra<T, C, NDBuffer<T>> {
    public val strides: Strides

    public override fun check(vararg elements: NDBuffer<T>): Array<out NDBuffer<T>> {
        require(elements.all { it.strides == strides }) { "Strides mismatch" }
        return elements
    }

    /**
     * Convert any [NDStructure] to buffered structure using strides from this context.
     * If the structure is already [NDBuffer], conversion is free. If not, it could be expensive because iteration over
     * indices.
     *
     * If the argument is [NDBuffer] with different strides structure, the new element will be produced.
     */
    public fun NDStructure<T>.toBuffer(): NDBuffer<T> =
        if (this is NDBuffer<T> && this.strides == this@BufferedNDAlgebra.strides)
            this
        else
            produce { index -> this@toBuffer[index] }

    /**
     * Convert a buffer to element of this algebra
     */
    public fun NDBuffer<T>.toElement(): MathElement<out BufferedNDAlgebra<T, C>>
}


public interface BufferedNDSpace<T, S : Space<T>> : NDSpace<T, S, NDBuffer<T>>, BufferedNDAlgebra<T, S> {
    public override fun NDBuffer<T>.toElement(): SpaceElement<NDBuffer<T>, *, out BufferedNDSpace<T, S>>
}

public interface BufferedNDRing<T, R : Ring<T>> : NDRing<T, R, NDBuffer<T>>, BufferedNDSpace<T, R> {
    override fun NDBuffer<T>.toElement(): RingElement<NDBuffer<T>, *, out BufferedNDRing<T, R>>
}

public interface BufferedNDField<T, F : Field<T>> : NDField<T, F, NDBuffer<T>>, BufferedNDRing<T, F> {
    override fun NDBuffer<T>.toElement(): FieldElement<NDBuffer<T>, *, out BufferedNDField<T, F>>
}
