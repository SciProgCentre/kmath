package space.kscience.kmath.functions

import kotlin.reflect.KProperty


/**
 * Represents class of labeled variables like usual
 * `x`, `y`, `z`, `a`, `b`, `n`, `m`, etc.
 *
 * Variables does not contain any information about field (or ring, ets.) they are considered in
 * and therefore about coefficient.
 *
 * @property name Is the label or name of variable. For `x` it is `"x"`, for `n` &ndash; `"n"`, etc.
 */
public data class Variable (val name: String) : Comparable<Variable> {
    /**
     * Represents the variable as a string.
     *
     * @return Only name of the variable.
     */
    override fun toString(): String = name
    /**
     * Compares two variables.
     * Comparison is realised by comparison of variables' names.
     *
     * Used in [LabeledPolynomial] and [LabeledRationalFunction] to sort monomials in
     * [LabeledPolynomial.toString] and [LabeledRationalFunction.toString] in lexicographic order.
     *
     * @see Comparable.compareTo
     * @sample LabeledPolynomial.monomialComparator
     * @return Only name of the variable.
     */
    override fun compareTo(other: Variable): Int = name.compareTo(other.name)

    public companion object {
        public operator fun getValue(thisRef: Any?, property: KProperty<*>) : Variable = Variable(property.name)
    }
}