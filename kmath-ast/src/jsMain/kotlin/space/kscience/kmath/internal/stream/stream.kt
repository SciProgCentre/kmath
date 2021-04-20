package space.kscience.kmath.internal.stream

import space.kscience.kmath.internal.emitter.Emitter

internal open external class Stream : Emitter {
    open fun pipe(dest: Any, options: Any): Any
}
