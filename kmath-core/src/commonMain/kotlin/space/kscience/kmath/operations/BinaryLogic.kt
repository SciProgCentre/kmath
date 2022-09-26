/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.operations

import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.expressions.symbol

 interface BinaryLogic<T : Any> {
    /**
     * Logic 'not'
     */
    public operator fun T.not(): T

    /**
     * Logic 'and'
     */
    public infix fun T.and(other: T): T

    /**
     * Logic 'or'
     */
    public infix fun T.or(other: T): T

    /**
     * Logic 'xor'
     */
    public infix fun T.xor(other: T): T

    companion object {
        public val TRUE: Symbol by symbol
        public val FALSE: Symbol by symbol
    }
}