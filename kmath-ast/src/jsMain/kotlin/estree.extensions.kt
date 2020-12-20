@file:Suppress(
    "NO_EXPLICIT_VISIBILITY_IN_API_MODE_WARNING", "KDocMissingDocumentation",
    "NO_EXPLICIT_RETURN_TYPE_IN_API_MODE_WARNING", "PackageDirectoryMismatch"
)

package estree

fun Program(sourceType: String, vararg body: dynamic) = object : Program {
    override var type = "Program"
    override var sourceType = sourceType
    override var body = body
}

fun VariableDeclaration(kind: String, vararg declarations: VariableDeclarator) = object : VariableDeclaration {
    override var type = "VariableDeclaration"
    override var declarations = declarations.toList().toTypedArray()
    override var kind = kind
}

fun VariableDeclarator(id: dynamic, init: dynamic) = object : VariableDeclarator {
    override var type = "VariableDeclarator"
    override var id = id
    override var init = init
}

fun Identifier(name: String) = object : Identifier {
    override var type = "Identifier"
    override var name = name
}

fun FunctionExpression(params: Array<dynamic>, body: BlockStatement) = object : FunctionExpression {
    override var params = params
    override var type = "FunctionExpression"
    override var body = body
}

fun BlockStatement(vararg body: dynamic) = object : BlockStatement {
    override var type = "BlockStatement"
    override var body = body
}

fun ReturnStatement(argument: dynamic) = object : ReturnStatement {
    override var type = "ReturnStatement"
    override var argument = argument
}

fun SimpleLiteral(value: dynamic) = object : SimpleLiteral {
    override var type = "Literal"
    override var value = value
}

fun MemberExpression(computed: Boolean, optional: Boolean, `object`: dynamic, property: dynamic) =
    object : MemberExpression {
        override var type = "MemberExpression"
        override var computed = computed
        override var optional = optional
        override var `object` = `object`
        override var property = property
    }

fun SimpleCallExpression(optional: Boolean, callee: dynamic, vararg arguments: dynamic) =
    object : SimpleCallExpression {
        override var type = "CallExpression"
        override var optional = optional
        override var callee = callee
        override var arguments = arguments
    }
