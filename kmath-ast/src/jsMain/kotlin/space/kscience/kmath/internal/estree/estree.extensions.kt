/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("unused")

package space.kscience.kmath.internal.estree

internal fun Program(sourceType: String, vararg body: dynamic) = object : Program {
    override var type = "Program"
    override var sourceType = sourceType
    override var body = body
}

internal fun VariableDeclaration(kind: String, vararg declarations: VariableDeclarator) = object : VariableDeclaration {
    override var type = "VariableDeclaration"
    override var declarations = declarations.toList().toTypedArray()
    override var kind = kind
}

internal fun VariableDeclarator(id: dynamic, init: dynamic) = object : VariableDeclarator {
    override var type = "VariableDeclarator"
    override var id = id
    override var init = init
}

internal fun Identifier(name: String) = object : Identifier {
    override var type = "Identifier"
    override var name = name
}

internal fun FunctionExpression(id: Identifier?, params: Array<dynamic>, body: BlockStatement) = object : FunctionExpression {
    override var params = params
    override var type = "FunctionExpression"
    override var id: Identifier? = id
    override var body = body
}

internal fun BlockStatement(vararg body: dynamic) = object : BlockStatement {
    override var type = "BlockStatement"
    override var body = body
}

internal fun ReturnStatement(argument: dynamic) = object : ReturnStatement {
    override var type = "ReturnStatement"
    override var argument = argument
}

internal fun SimpleLiteral(value: dynamic) = object : SimpleLiteral {
    override var type = "Literal"
    override var value = value
}

internal fun MemberExpression(computed: Boolean, optional: Boolean, `object`: dynamic, property: dynamic) =
    object : MemberExpression {
        override var type = "MemberExpression"
        override var computed = computed
        override var optional = optional
        override var `object` = `object`
        override var property = property
    }

internal fun SimpleCallExpression(optional: Boolean, callee: dynamic, vararg arguments: dynamic) =
    object : SimpleCallExpression {
        override var type = "CallExpression"
        override var optional = optional
        override var callee = callee
        override var arguments = arguments
    }
