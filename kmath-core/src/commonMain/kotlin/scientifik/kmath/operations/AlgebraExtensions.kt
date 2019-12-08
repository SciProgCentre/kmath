package scientifik.kmath.operations

fun <T> Space<T>.sum(data : Iterable<T>): T = data.fold(zero) { left, right -> add(left,right) }
fun <T> Space<T>.sum(data : Sequence<T>): T = data.fold(zero) { left, right -> add(left, right) }