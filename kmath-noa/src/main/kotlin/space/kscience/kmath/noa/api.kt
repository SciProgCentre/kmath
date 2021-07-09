/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.noa

import space.kscience.kmath.noa.memory.NoaScope
import space.kscience.kmath.noa.memory.withNoaScope

public class NoaDouble
internal constructor(scope: NoaScope) :
        NoaDoubleAlgebra(scope)

public fun <R> NoaDouble(block: NoaDouble.() -> R): R? =
    withNoaScope { NoaDouble(this).block() }

public fun <R> NoaDouble(scope: NoaScope, block: NoaDouble.() -> R): R? =
    withNoaScope(scope) { NoaDouble(this).block() }

public class NoaFloat
internal constructor(scope: NoaScope) :
    NoaFloatAlgebra(scope)

public fun <R> NoaFloat(block: NoaFloat.() -> R): R? =
    withNoaScope { NoaFloat(this).block() }

public fun <R> NoaFloat(scope: NoaScope, block: NoaFloat.() -> R): R? =
    withNoaScope(scope) { NoaFloat(this).block() }

public class NoaLong
internal constructor(scope: NoaScope) :
    NoaLongAlgebra(scope)

public fun <R> NoaLong(block: NoaLong.() -> R): R? =
    withNoaScope { NoaLong(this).block() }

public fun <R> NoaLong(scope: NoaScope, block: NoaLong.() -> R): R? =
    withNoaScope(scope) { NoaLong(this).block() }

public class NoaInt
internal constructor(scope: NoaScope) :
    NoaIntAlgebra(scope)

public fun <R> NoaInt(block: NoaInt.() -> R): R? =
    withNoaScope { NoaInt(this).block() }

public fun <R> NoaInt(scope: NoaScope, block: NoaInt.() -> R): R? =
    withNoaScope(scope) { NoaInt(this).block() }
