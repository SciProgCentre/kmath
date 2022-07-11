/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions.testUtils

import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


public inline fun <reified T : Throwable> assertFailsWithTypeAndMessage(
    expectedMessage: String? = null,
    assertionMessage: String? = null,
    block: () -> Unit
) {
    assertEquals(
        expectedMessage,
        assertFailsWith(T::class, assertionMessage, block).message,
        assertionMessage
    )
}