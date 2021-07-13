/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.torchscript.compiler.resolve

import org.jetbrains.kotlin.name.FqName

public object TorchscriptAnnotations {
    public val moduleAnnotationFqName: FqName = FqName("space.kscience.kmath.torchscript.Module")
    public val intrinsicAnnotationFqName: FqName = FqName("space.kscience.kmath.torchscript.Intrinsic")
}
