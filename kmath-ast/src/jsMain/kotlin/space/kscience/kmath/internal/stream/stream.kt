/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.internal.stream

import space.kscience.kmath.internal.emitter.Emitter

internal open external class Stream : Emitter {
    open fun pipe(dest: Any, options: Any): Any
}
