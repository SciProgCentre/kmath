package scientifik.kmath.operations

fun <T> Space<T>.sum(data: Iterable<T>): T = data.fold(zero) { left, right -> add(left, right) }
fun <T> Space<T>.sum(data: Sequence<T>): T = data.fold(zero) { left, right -> add(left, right) }

fun <T : Any, S : Space<T>> Iterable<T>.sumWith(space: S): T = space.sum(this)

//TODO optimized power operation
fun <T> RingOperations<T>.power(arg: T, power: Int): T {
    var res = arg
    repeat(power - 1) {
        res *= arg
    }
    return res
}
