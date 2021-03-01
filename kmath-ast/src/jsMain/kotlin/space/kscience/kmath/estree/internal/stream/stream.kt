package space.kscience.kmath.estree.internal.stream

import space.kscience.kmath.estree.internal.emitter.Emitter

internal open external class Stream : Emitter {
    open fun pipe(dest: Any, options: Any): Any
}
