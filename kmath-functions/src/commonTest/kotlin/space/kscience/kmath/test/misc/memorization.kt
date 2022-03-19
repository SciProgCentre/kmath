/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.test.misc

sealed interface OperationsMemory

interface Endpoint: OperationsMemory

interface Negation: OperationsMemory {
    val negated: OperationsMemory
}

interface Sum: OperationsMemory {
    val augend: OperationsMemory
    val addend: OperationsMemory
}

interface Difference: OperationsMemory {
    val minuend: OperationsMemory
    val subtrahend: OperationsMemory
}

interface Product: OperationsMemory {
    val multiplicand: OperationsMemory
    val multiplier: OperationsMemory
}

interface Quotient: OperationsMemory {
    val dividend: OperationsMemory
    val divisor: OperationsMemory
}


fun equalMemories(one: OperationsMemory, other: OperationsMemory) : Boolean =
    when(one) {
        is Negation -> other is Negation && equalMemories(one.negated, other.negated)
        is Sum -> other is Sum && equalMemories(one.augend, other.augend) && equalMemories(one.addend, other.addend)
        is Difference -> other is Difference && equalMemories(one.minuend, other.minuend) && equalMemories(one.subtrahend, other.subtrahend)
        is Product -> other is Product && equalMemories(one.multiplicand, other.multiplicand) && equalMemories(one.multiplier, other.multiplier)
        is Quotient -> other is Quotient && equalMemories(one.dividend, other.dividend) && equalMemories(one.divisor, other.divisor)
        is Endpoint -> one === other
    }

interface WithMemorization {
    val memory: OperationsMemory
}

fun equalMemories(one: WithMemorization, other: WithMemorization) : Boolean = equalMemories(one.memory, other.memory)