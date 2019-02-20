package scientifik.kmath.operations

import scientifik.kmath.structures.Buffer
import scientifik.kmath.structures.asSequence

fun <T> Space<T>.sum(data : Iterable<T>): T = data.fold(zero) { left, right -> add(left,right) }
fun <T> Space<T>.sum(data : Sequence<T>): T = data.fold(zero) { left, right -> add(left, right) }