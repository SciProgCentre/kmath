/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.misc

public interface Loggable {
    public fun log(tag: String = INFO, block: () -> String)

    public companion object {
        public const val INFO: String = "INFO"
    }
}