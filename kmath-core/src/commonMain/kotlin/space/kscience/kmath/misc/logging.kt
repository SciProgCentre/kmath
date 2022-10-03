/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.misc

import space.kscience.kmath.misc.Loggable.Companion.INFO

public fun interface Loggable {
    public fun log(tag: String, block: () -> String)

    public companion object {
        public const val INFO: String = "INFO"

        public val console: Loggable = Loggable { tag, block ->
            println("[$tag] ${block()}")
        }
    }
}

public fun Loggable.log(block: () -> String): Unit = log(INFO, block)