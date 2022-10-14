/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.nd

import kotlin.test.Test

class StridesTest {
    @Test
    fun checkRowBasedStrides() {
        val strides = RowStrides(ShapeND(3, 3))
        var counter = 0
        for(i in 0..2){
            for(j in 0..2){
//                print(strides.offset(intArrayOf(i,j)).toString() + "\t")
                require(strides.offset(intArrayOf(i,j)) == counter)
                counter++
            }
            println()
        }
    }

    @Test
    fun checkColumnBasedStrides() {
        val strides = ColumnStrides(ShapeND(3, 3))
        var counter = 0
        for(i in 0..2){
            for(j in 0..2){
//                print(strides.offset(intArrayOf(i,j)).toString() + "\t")
                require(strides.offset(intArrayOf(j,i)) == counter)
                counter++
            }
            println()
        }
    }
}