@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS",
    "NO_EXPLICIT_VISIBILITY_IN_API_MODE_WARNING", "KDocMissingDocumentation", "ClassName",
)

import kotlin.js.RegExp

external interface BaseNodeWithoutComments {
    var type: String
    var loc: SourceLocation?
        get() = definedExternally
        set(value) = definedExternally
    var range: dynamic /* JsTuple<Number, Number> */
        get() = definedExternally
        set(value) = definedExternally
}

external interface BaseNode : BaseNodeWithoutComments {
    var leadingComments: Array<Comment>?
        get() = definedExternally
        set(value) = definedExternally
    var trailingComments: Array<Comment>?
        get() = definedExternally
        set(value) = definedExternally
}

external interface Comment : BaseNodeWithoutComments {
    override var type: String /* "Line" | "Block" */
    var value: String
}

external interface SourceLocation {
    var source: String?
        get() = definedExternally
        set(value) = definedExternally
    var start: Position
    var end: Position
}

external interface Position {
    var line: Number
    var column: Number
}

external interface Program : BaseNode {
    override var type: String /* "Program" */
    var sourceType: String /* "script" | "module" */
    var body: Array<dynamic /* Directive | ExpressionStatement | BlockStatement | EmptyStatement | DebuggerStatement | WithStatement | ReturnStatement | LabeledStatement | BreakStatement | ContinueStatement | IfStatement | SwitchStatement | ThrowStatement | TryStatement | WhileStatement | DoWhileStatement | ForStatement | ForInStatement | ForOfStatement | FunctionDeclaration | VariableDeclaration | ClassDeclaration | ImportDeclaration | ExportNamedDeclaration | ExportDefaultDeclaration | ExportAllDeclaration */>
    var comments: Array<Comment>?
        get() = definedExternally
        set(value) = definedExternally
}

external interface Directive : BaseNode {
    override var type: String /* "ExpressionStatement" */
    var expression: dynamic /* SimpleLiteral | RegExpLiteral */
        get() = definedExternally
        set(value) = definedExternally
    var directive: String
}

external interface BaseFunction : BaseNode {
    var params: Array<dynamic /* Identifier | ObjectPattern | ArrayPattern | RestElement | AssignmentPattern | MemberExpression */>
    var generator: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var async: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var body: dynamic /* BlockStatement | ThisExpression | ArrayExpression | ObjectExpression | FunctionExpression | ArrowFunctionExpression | YieldExpression | SimpleLiteral | RegExpLiteral | UnaryExpression | UpdateExpression | BinaryExpression | AssignmentExpression | LogicalExpression | MemberExpression | ConditionalExpression | SimpleCallExpression | NewExpression | SequenceExpression | TemplateLiteral | TaggedTemplateExpression | ClassExpression | MetaProperty | Identifier | AwaitExpression | ImportExpression | ChainExpression */
        get() = definedExternally
        set(value) = definedExternally
}

external interface BaseStatement : BaseNode

external interface EmptyStatement : BaseStatement {
    override var type: String /* "EmptyStatement" */
}

external interface BlockStatement : BaseStatement {
    override var type: String /* "BlockStatement" */
    var body: Array<dynamic /* ExpressionStatement | BlockStatement | EmptyStatement | DebuggerStatement | WithStatement | ReturnStatement | LabeledStatement | BreakStatement | ContinueStatement | IfStatement | SwitchStatement | ThrowStatement | TryStatement | WhileStatement | DoWhileStatement | ForStatement | ForInStatement | ForOfStatement | FunctionDeclaration | VariableDeclaration | ClassDeclaration */>
    var innerComments: Array<Comment>?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ExpressionStatement : BaseStatement {
    override var type: String /* "ExpressionStatement" */
    var expression: dynamic /* ThisExpression | ArrayExpression | ObjectExpression | FunctionExpression | ArrowFunctionExpression | YieldExpression | SimpleLiteral | RegExpLiteral | UnaryExpression | UpdateExpression | BinaryExpression | AssignmentExpression | LogicalExpression | MemberExpression | ConditionalExpression | SimpleCallExpression | NewExpression | SequenceExpression | TemplateLiteral | TaggedTemplateExpression | ClassExpression | MetaProperty | Identifier | AwaitExpression | ImportExpression | ChainExpression */
        get() = definedExternally
        set(value) = definedExternally
}

external interface IfStatement : BaseStatement {
    override var type: String /* "IfStatement" */
    var test: dynamic /* ThisExpression | ArrayExpression | ObjectExpression | FunctionExpression | ArrowFunctionExpression | YieldExpression | SimpleLiteral | RegExpLiteral | UnaryExpression | UpdateExpression | BinaryExpression | AssignmentExpression | LogicalExpression | MemberExpression | ConditionalExpression | SimpleCallExpression | NewExpression | SequenceExpression | TemplateLiteral | TaggedTemplateExpression | ClassExpression | MetaProperty | Identifier | AwaitExpression | ImportExpression | ChainExpression */
        get() = definedExternally
        set(value) = definedExternally
    var consequent: dynamic /* ExpressionStatement | BlockStatement | EmptyStatement | DebuggerStatement | WithStatement | ReturnStatement | LabeledStatement | BreakStatement | ContinueStatement | IfStatement | SwitchStatement | ThrowStatement | TryStatement | WhileStatement | DoWhileStatement | ForStatement | ForInStatement | ForOfStatement | FunctionDeclaration | VariableDeclaration | ClassDeclaration */
        get() = definedExternally
        set(value) = definedExternally
    var alternate: dynamic /* ExpressionStatement? | BlockStatement? | EmptyStatement? | DebuggerStatement? | WithStatement? | ReturnStatement? | LabeledStatement? | BreakStatement? | ContinueStatement? | IfStatement? | SwitchStatement? | ThrowStatement? | TryStatement? | WhileStatement? | DoWhileStatement? | ForStatement? | ForInStatement? | ForOfStatement? | FunctionDeclaration? | VariableDeclaration? | ClassDeclaration? */
        get() = definedExternally
        set(value) = definedExternally
}

external interface LabeledStatement : BaseStatement {
    override var type: String /* "LabeledStatement" */
    var label: Identifier
    var body: dynamic /* ExpressionStatement | BlockStatement | EmptyStatement | DebuggerStatement | WithStatement | ReturnStatement | LabeledStatement | BreakStatement | ContinueStatement | IfStatement | SwitchStatement | ThrowStatement | TryStatement | WhileStatement | DoWhileStatement | ForStatement | ForInStatement | ForOfStatement | FunctionDeclaration | VariableDeclaration | ClassDeclaration */
        get() = definedExternally
        set(value) = definedExternally
}

external interface BreakStatement : BaseStatement {
    override var type: String /* "BreakStatement" */
    var label: Identifier?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ContinueStatement : BaseStatement {
    override var type: String /* "ContinueStatement" */
    var label: Identifier?
        get() = definedExternally
        set(value) = definedExternally
}

external interface WithStatement : BaseStatement {
    override var type: String /* "WithStatement" */
    var `object`: dynamic /* ThisExpression | ArrayExpression | ObjectExpression | FunctionExpression | ArrowFunctionExpression | YieldExpression | SimpleLiteral | RegExpLiteral | UnaryExpression | UpdateExpression | BinaryExpression | AssignmentExpression | LogicalExpression | MemberExpression | ConditionalExpression | SimpleCallExpression | NewExpression | SequenceExpression | TemplateLiteral | TaggedTemplateExpression | ClassExpression | MetaProperty | Identifier | AwaitExpression | ImportExpression | ChainExpression */
        get() = definedExternally
        set(value) = definedExternally
    var body: dynamic /* ExpressionStatement | BlockStatement | EmptyStatement | DebuggerStatement | WithStatement | ReturnStatement | LabeledStatement | BreakStatement | ContinueStatement | IfStatement | SwitchStatement | ThrowStatement | TryStatement | WhileStatement | DoWhileStatement | ForStatement | ForInStatement | ForOfStatement | FunctionDeclaration | VariableDeclaration | ClassDeclaration */
        get() = definedExternally
        set(value) = definedExternally
}

external interface SwitchStatement : BaseStatement {
    override var type: String /* "SwitchStatement" */
    var discriminant: dynamic /* ThisExpression | ArrayExpression | ObjectExpression | FunctionExpression | ArrowFunctionExpression | YieldExpression | SimpleLiteral | RegExpLiteral | UnaryExpression | UpdateExpression | BinaryExpression | AssignmentExpression | LogicalExpression | MemberExpression | ConditionalExpression | SimpleCallExpression | NewExpression | SequenceExpression | TemplateLiteral | TaggedTemplateExpression | ClassExpression | MetaProperty | Identifier | AwaitExpression | ImportExpression | ChainExpression */
        get() = definedExternally
        set(value) = definedExternally
    var cases: Array<SwitchCase>
}

external interface ReturnStatement : BaseStatement {
    override var type: String /* "ReturnStatement" */
    var argument: dynamic /* ThisExpression? | ArrayExpression? | ObjectExpression? | FunctionExpression? | ArrowFunctionExpression? | YieldExpression? | SimpleLiteral? | RegExpLiteral? | UnaryExpression? | UpdateExpression? | BinaryExpression? | AssignmentExpression? | LogicalExpression? | MemberExpression? | ConditionalExpression? | SimpleCallExpression? | NewExpression? | SequenceExpression? | TemplateLiteral? | TaggedTemplateExpression? | ClassExpression? | MetaProperty? | Identifier? | AwaitExpression? | ImportExpression? | ChainExpression? */
        get() = definedExternally
        set(value) = definedExternally
}

external interface ThrowStatement : BaseStatement {
    override var type: String /* "ThrowStatement" */
    var argument: dynamic /* ThisExpression | ArrayExpression | ObjectExpression | FunctionExpression | ArrowFunctionExpression | YieldExpression | SimpleLiteral | RegExpLiteral | UnaryExpression | UpdateExpression | BinaryExpression | AssignmentExpression | LogicalExpression | MemberExpression | ConditionalExpression | SimpleCallExpression | NewExpression | SequenceExpression | TemplateLiteral | TaggedTemplateExpression | ClassExpression | MetaProperty | Identifier | AwaitExpression | ImportExpression | ChainExpression */
        get() = definedExternally
        set(value) = definedExternally
}

external interface TryStatement : BaseStatement {
    override var type: String /* "TryStatement" */
    var block: BlockStatement
    var handler: CatchClause?
        get() = definedExternally
        set(value) = definedExternally
    var finalizer: BlockStatement?
        get() = definedExternally
        set(value) = definedExternally
}

external interface WhileStatement : BaseStatement {
    override var type: String /* "WhileStatement" */
    var test: dynamic /* ThisExpression | ArrayExpression | ObjectExpression | FunctionExpression | ArrowFunctionExpression | YieldExpression | SimpleLiteral | RegExpLiteral | UnaryExpression | UpdateExpression | BinaryExpression | AssignmentExpression | LogicalExpression | MemberExpression | ConditionalExpression | SimpleCallExpression | NewExpression | SequenceExpression | TemplateLiteral | TaggedTemplateExpression | ClassExpression | MetaProperty | Identifier | AwaitExpression | ImportExpression | ChainExpression */
        get() = definedExternally
        set(value) = definedExternally
    var body: dynamic /* ExpressionStatement | BlockStatement | EmptyStatement | DebuggerStatement | WithStatement | ReturnStatement | LabeledStatement | BreakStatement | ContinueStatement | IfStatement | SwitchStatement | ThrowStatement | TryStatement | WhileStatement | DoWhileStatement | ForStatement | ForInStatement | ForOfStatement | FunctionDeclaration | VariableDeclaration | ClassDeclaration */
        get() = definedExternally
        set(value) = definedExternally
}

external interface DoWhileStatement : BaseStatement {
    override var type: String /* "DoWhileStatement" */
    var body: dynamic /* ExpressionStatement | BlockStatement | EmptyStatement | DebuggerStatement | WithStatement | ReturnStatement | LabeledStatement | BreakStatement | ContinueStatement | IfStatement | SwitchStatement | ThrowStatement | TryStatement | WhileStatement | DoWhileStatement | ForStatement | ForInStatement | ForOfStatement | FunctionDeclaration | VariableDeclaration | ClassDeclaration */
        get() = definedExternally
        set(value) = definedExternally
    var test: dynamic /* ThisExpression | ArrayExpression | ObjectExpression | FunctionExpression | ArrowFunctionExpression | YieldExpression | SimpleLiteral | RegExpLiteral | UnaryExpression | UpdateExpression | BinaryExpression | AssignmentExpression | LogicalExpression | MemberExpression | ConditionalExpression | SimpleCallExpression | NewExpression | SequenceExpression | TemplateLiteral | TaggedTemplateExpression | ClassExpression | MetaProperty | Identifier | AwaitExpression | ImportExpression | ChainExpression */
        get() = definedExternally
        set(value) = definedExternally
}

external interface ForStatement : BaseStatement {
    override var type: String /* "ForStatement" */
    var init: dynamic /* VariableDeclaration? | ThisExpression? | ArrayExpression? | ObjectExpression? | FunctionExpression? | ArrowFunctionExpression? | YieldExpression? | SimpleLiteral? | RegExpLiteral? | UnaryExpression? | UpdateExpression? | BinaryExpression? | AssignmentExpression? | LogicalExpression? | MemberExpression? | ConditionalExpression? | SimpleCallExpression? | NewExpression? | SequenceExpression? | TemplateLiteral? | TaggedTemplateExpression? | ClassExpression? | MetaProperty? | Identifier? | AwaitExpression? | ImportExpression? | ChainExpression? */
        get() = definedExternally
        set(value) = definedExternally
    var test: dynamic /* ThisExpression? | ArrayExpression? | ObjectExpression? | FunctionExpression? | ArrowFunctionExpression? | YieldExpression? | SimpleLiteral? | RegExpLiteral? | UnaryExpression? | UpdateExpression? | BinaryExpression? | AssignmentExpression? | LogicalExpression? | MemberExpression? | ConditionalExpression? | SimpleCallExpression? | NewExpression? | SequenceExpression? | TemplateLiteral? | TaggedTemplateExpression? | ClassExpression? | MetaProperty? | Identifier? | AwaitExpression? | ImportExpression? | ChainExpression? */
        get() = definedExternally
        set(value) = definedExternally
    var update: dynamic /* ThisExpression? | ArrayExpression? | ObjectExpression? | FunctionExpression? | ArrowFunctionExpression? | YieldExpression? | SimpleLiteral? | RegExpLiteral? | UnaryExpression? | UpdateExpression? | BinaryExpression? | AssignmentExpression? | LogicalExpression? | MemberExpression? | ConditionalExpression? | SimpleCallExpression? | NewExpression? | SequenceExpression? | TemplateLiteral? | TaggedTemplateExpression? | ClassExpression? | MetaProperty? | Identifier? | AwaitExpression? | ImportExpression? | ChainExpression? */
        get() = definedExternally
        set(value) = definedExternally
    var body: dynamic /* ExpressionStatement | BlockStatement | EmptyStatement | DebuggerStatement | WithStatement | ReturnStatement | LabeledStatement | BreakStatement | ContinueStatement | IfStatement | SwitchStatement | ThrowStatement | TryStatement | WhileStatement | DoWhileStatement | ForStatement | ForInStatement | ForOfStatement | FunctionDeclaration | VariableDeclaration | ClassDeclaration */
        get() = definedExternally
        set(value) = definedExternally
}

external interface BaseForXStatement : BaseStatement {
    var left: dynamic /* VariableDeclaration | Identifier | ObjectPattern | ArrayPattern | RestElement | AssignmentPattern | MemberExpression */
        get() = definedExternally
        set(value) = definedExternally
    var right: dynamic /* ThisExpression | ArrayExpression | ObjectExpression | FunctionExpression | ArrowFunctionExpression | YieldExpression | SimpleLiteral | RegExpLiteral | UnaryExpression | UpdateExpression | BinaryExpression | AssignmentExpression | LogicalExpression | MemberExpression | ConditionalExpression | SimpleCallExpression | NewExpression | SequenceExpression | TemplateLiteral | TaggedTemplateExpression | ClassExpression | MetaProperty | Identifier | AwaitExpression | ImportExpression | ChainExpression */
        get() = definedExternally
        set(value) = definedExternally
    var body: dynamic /* ExpressionStatement | BlockStatement | EmptyStatement | DebuggerStatement | WithStatement | ReturnStatement | LabeledStatement | BreakStatement | ContinueStatement | IfStatement | SwitchStatement | ThrowStatement | TryStatement | WhileStatement | DoWhileStatement | ForStatement | ForInStatement | ForOfStatement | FunctionDeclaration | VariableDeclaration | ClassDeclaration */
        get() = definedExternally
        set(value) = definedExternally
}

external interface ForInStatement : BaseForXStatement {
    override var type: String /* "ForInStatement" */
}

external interface DebuggerStatement : BaseStatement {
    override var type: String /* "DebuggerStatement" */
}

external interface BaseDeclaration : BaseStatement

external interface FunctionDeclaration : BaseFunction, BaseDeclaration {
    override var type: String /* "FunctionDeclaration" */
    var id: Identifier?
    override var body: BlockStatement
}

external interface VariableDeclaration : BaseDeclaration {
    override var type: String /* "VariableDeclaration" */
    var declarations: Array<VariableDeclarator>
    var kind: String /* "var" | "let" | "const" */
}

external interface VariableDeclarator : BaseNode {
    override var type: String /* "VariableDeclarator" */
    var id: dynamic /* Identifier | ObjectPattern | ArrayPattern | RestElement | AssignmentPattern | MemberExpression */
        get() = definedExternally
        set(value) = definedExternally
    var init: dynamic /* ThisExpression? | ArrayExpression? | ObjectExpression? | FunctionExpression? | ArrowFunctionExpression? | YieldExpression? | SimpleLiteral? | RegExpLiteral? | UnaryExpression? | UpdateExpression? | BinaryExpression? | AssignmentExpression? | LogicalExpression? | MemberExpression? | ConditionalExpression? | SimpleCallExpression? | NewExpression? | SequenceExpression? | TemplateLiteral? | TaggedTemplateExpression? | ClassExpression? | MetaProperty? | Identifier? | AwaitExpression? | ImportExpression? | ChainExpression? */
        get() = definedExternally
        set(value) = definedExternally
}

external interface BaseExpression : BaseNode

external interface ChainExpression : BaseExpression {
    override var type: String /* "ChainExpression" */
    var expression: dynamic /* SimpleCallExpression | MemberExpression */
        get() = definedExternally
        set(value) = definedExternally
}

external interface ThisExpression : BaseExpression {
    override var type: String /* "ThisExpression" */
}

external interface ArrayExpression : BaseExpression {
    override var type: String /* "ArrayExpression" */
    var elements: Array<dynamic /* ThisExpression | ArrayExpression | ObjectExpression | FunctionExpression | ArrowFunctionExpression | YieldExpression | SimpleLiteral | RegExpLiteral | UnaryExpression | UpdateExpression | BinaryExpression | AssignmentExpression | LogicalExpression | MemberExpression | ConditionalExpression | SimpleCallExpression | NewExpression | SequenceExpression | TemplateLiteral | TaggedTemplateExpression | ClassExpression | MetaProperty | Identifier | AwaitExpression | ImportExpression | ChainExpression | SpreadElement */>
}

external interface ObjectExpression : BaseExpression {
    override var type: String /* "ObjectExpression" */
    var properties: Array<dynamic /* Property | SpreadElement */>
}

external interface Property : BaseNode {
    override var type: String /* "Property" */
    var key: dynamic /* ThisExpression | ArrayExpression | ObjectExpression | FunctionExpression | ArrowFunctionExpression | YieldExpression | SimpleLiteral | RegExpLiteral | UnaryExpression | UpdateExpression | BinaryExpression | AssignmentExpression | LogicalExpression | MemberExpression | ConditionalExpression | SimpleCallExpression | NewExpression | SequenceExpression | TemplateLiteral | TaggedTemplateExpression | ClassExpression | MetaProperty | Identifier | AwaitExpression | ImportExpression | ChainExpression */
        get() = definedExternally
        set(value) = definedExternally
    var value: dynamic /* ThisExpression | ArrayExpression | ObjectExpression | FunctionExpression | ArrowFunctionExpression | YieldExpression | SimpleLiteral | RegExpLiteral | UnaryExpression | UpdateExpression | BinaryExpression | AssignmentExpression | LogicalExpression | MemberExpression | ConditionalExpression | SimpleCallExpression | NewExpression | SequenceExpression | TemplateLiteral | TaggedTemplateExpression | ClassExpression | MetaProperty | Identifier | AwaitExpression | ImportExpression | ChainExpression | ObjectPattern | ArrayPattern | RestElement | AssignmentPattern */
        get() = definedExternally
        set(value) = definedExternally
    var kind: String /* "init" | "get" | "set" */
    var method: Boolean
    var shorthand: Boolean
    var computed: Boolean
}

external interface FunctionExpression : BaseFunction, BaseExpression {
    var id: Identifier?
        get() = definedExternally
        set(value) = definedExternally
    override var type: String /* "FunctionExpression" */
    override var body: BlockStatement
}

external interface SequenceExpression : BaseExpression {
    override var type: String /* "SequenceExpression" */
    var expressions: Array<dynamic /* ThisExpression | ArrayExpression | ObjectExpression | FunctionExpression | ArrowFunctionExpression | YieldExpression | SimpleLiteral | RegExpLiteral | UnaryExpression | UpdateExpression | BinaryExpression | AssignmentExpression | LogicalExpression | MemberExpression | ConditionalExpression | SimpleCallExpression | NewExpression | SequenceExpression | TemplateLiteral | TaggedTemplateExpression | ClassExpression | MetaProperty | Identifier | AwaitExpression | ImportExpression | ChainExpression */>
}

external interface UnaryExpression : BaseExpression {
    override var type: String /* "UnaryExpression" */
    var operator: String /* "-" | "+" | "!" | "~" | "typeof" | "void" | "delete" */
    var prefix: Boolean
    var argument: dynamic /* ThisExpression | ArrayExpression | ObjectExpression | FunctionExpression | ArrowFunctionExpression | YieldExpression | SimpleLiteral | RegExpLiteral | UnaryExpression | UpdateExpression | BinaryExpression | AssignmentExpression | LogicalExpression | MemberExpression | ConditionalExpression | SimpleCallExpression | NewExpression | SequenceExpression | TemplateLiteral | TaggedTemplateExpression | ClassExpression | MetaProperty | Identifier | AwaitExpression | ImportExpression | ChainExpression */
        get() = definedExternally
        set(value) = definedExternally
}

external interface BinaryExpression : BaseExpression {
    override var type: String /* "BinaryExpression" */
    var operator: String /* "==" | "!=" | "===" | "!==" | "<" | "<=" | ">" | ">=" | "<<" | ">>" | ">>>" | "+" | "-" | "*" | "/" | "%" | "**" | "|" | "^" | "&" | "in" | "instanceof" */
    var left: dynamic /* ThisExpression | ArrayExpression | ObjectExpression | FunctionExpression | ArrowFunctionExpression | YieldExpression | SimpleLiteral | RegExpLiteral | UnaryExpression | UpdateExpression | BinaryExpression | AssignmentExpression | LogicalExpression | MemberExpression | ConditionalExpression | SimpleCallExpression | NewExpression | SequenceExpression | TemplateLiteral | TaggedTemplateExpression | ClassExpression | MetaProperty | Identifier | AwaitExpression | ImportExpression | ChainExpression */
        get() = definedExternally
        set(value) = definedExternally
    var right: dynamic /* ThisExpression | ArrayExpression | ObjectExpression | FunctionExpression | ArrowFunctionExpression | YieldExpression | SimpleLiteral | RegExpLiteral | UnaryExpression | UpdateExpression | BinaryExpression | AssignmentExpression | LogicalExpression | MemberExpression | ConditionalExpression | SimpleCallExpression | NewExpression | SequenceExpression | TemplateLiteral | TaggedTemplateExpression | ClassExpression | MetaProperty | Identifier | AwaitExpression | ImportExpression | ChainExpression */
        get() = definedExternally
        set(value) = definedExternally
}

external interface AssignmentExpression : BaseExpression {
    override var type: String /* "AssignmentExpression" */
    var operator: String /* "=" | "+=" | "-=" | "*=" | "/=" | "%=" | "**=" | "<<=" | ">>=" | ">>>=" | "|=" | "^=" | "&=" */
    var left: dynamic /* Identifier | ObjectPattern | ArrayPattern | RestElement | AssignmentPattern | MemberExpression */
        get() = definedExternally
        set(value) = definedExternally
    var right: dynamic /* ThisExpression | ArrayExpression | ObjectExpression | FunctionExpression | ArrowFunctionExpression | YieldExpression | SimpleLiteral | RegExpLiteral | UnaryExpression | UpdateExpression | BinaryExpression | AssignmentExpression | LogicalExpression | MemberExpression | ConditionalExpression | SimpleCallExpression | NewExpression | SequenceExpression | TemplateLiteral | TaggedTemplateExpression | ClassExpression | MetaProperty | Identifier | AwaitExpression | ImportExpression | ChainExpression */
        get() = definedExternally
        set(value) = definedExternally
}

external interface UpdateExpression : BaseExpression {
    override var type: String /* "UpdateExpression" */
    var operator: String /* "++" | "--" */
    var argument: dynamic /* ThisExpression | ArrayExpression | ObjectExpression | FunctionExpression | ArrowFunctionExpression | YieldExpression | SimpleLiteral | RegExpLiteral | UnaryExpression | UpdateExpression | BinaryExpression | AssignmentExpression | LogicalExpression | MemberExpression | ConditionalExpression | SimpleCallExpression | NewExpression | SequenceExpression | TemplateLiteral | TaggedTemplateExpression | ClassExpression | MetaProperty | Identifier | AwaitExpression | ImportExpression | ChainExpression */
        get() = definedExternally
        set(value) = definedExternally
    var prefix: Boolean
}

external interface LogicalExpression : BaseExpression {
    override var type: String /* "LogicalExpression" */
    var operator: String /* "||" | "&&" | "??" */
    var left: dynamic /* ThisExpression | ArrayExpression | ObjectExpression | FunctionExpression | ArrowFunctionExpression | YieldExpression | SimpleLiteral | RegExpLiteral | UnaryExpression | UpdateExpression | BinaryExpression | AssignmentExpression | LogicalExpression | MemberExpression | ConditionalExpression | SimpleCallExpression | NewExpression | SequenceExpression | TemplateLiteral | TaggedTemplateExpression | ClassExpression | MetaProperty | Identifier | AwaitExpression | ImportExpression | ChainExpression */
        get() = definedExternally
        set(value) = definedExternally
    var right: dynamic /* ThisExpression | ArrayExpression | ObjectExpression | FunctionExpression | ArrowFunctionExpression | YieldExpression | SimpleLiteral | RegExpLiteral | UnaryExpression | UpdateExpression | BinaryExpression | AssignmentExpression | LogicalExpression | MemberExpression | ConditionalExpression | SimpleCallExpression | NewExpression | SequenceExpression | TemplateLiteral | TaggedTemplateExpression | ClassExpression | MetaProperty | Identifier | AwaitExpression | ImportExpression | ChainExpression */
        get() = definedExternally
        set(value) = definedExternally
}

external interface ConditionalExpression : BaseExpression {
    override var type: String /* "ConditionalExpression" */
    var test: dynamic /* ThisExpression | ArrayExpression | ObjectExpression | FunctionExpression | ArrowFunctionExpression | YieldExpression | SimpleLiteral | RegExpLiteral | UnaryExpression | UpdateExpression | BinaryExpression | AssignmentExpression | LogicalExpression | MemberExpression | ConditionalExpression | SimpleCallExpression | NewExpression | SequenceExpression | TemplateLiteral | TaggedTemplateExpression | ClassExpression | MetaProperty | Identifier | AwaitExpression | ImportExpression | ChainExpression */
        get() = definedExternally
        set(value) = definedExternally
    var alternate: dynamic /* ThisExpression | ArrayExpression | ObjectExpression | FunctionExpression | ArrowFunctionExpression | YieldExpression | SimpleLiteral | RegExpLiteral | UnaryExpression | UpdateExpression | BinaryExpression | AssignmentExpression | LogicalExpression | MemberExpression | ConditionalExpression | SimpleCallExpression | NewExpression | SequenceExpression | TemplateLiteral | TaggedTemplateExpression | ClassExpression | MetaProperty | Identifier | AwaitExpression | ImportExpression | ChainExpression */
        get() = definedExternally
        set(value) = definedExternally
    var consequent: dynamic /* ThisExpression | ArrayExpression | ObjectExpression | FunctionExpression | ArrowFunctionExpression | YieldExpression | SimpleLiteral | RegExpLiteral | UnaryExpression | UpdateExpression | BinaryExpression | AssignmentExpression | LogicalExpression | MemberExpression | ConditionalExpression | SimpleCallExpression | NewExpression | SequenceExpression | TemplateLiteral | TaggedTemplateExpression | ClassExpression | MetaProperty | Identifier | AwaitExpression | ImportExpression | ChainExpression */
        get() = definedExternally
        set(value) = definedExternally
}

external interface BaseCallExpression : BaseExpression {
    var callee: dynamic /* ThisExpression | ArrayExpression | ObjectExpression | FunctionExpression | ArrowFunctionExpression | YieldExpression | SimpleLiteral | RegExpLiteral | UnaryExpression | UpdateExpression | BinaryExpression | AssignmentExpression | LogicalExpression | MemberExpression | ConditionalExpression | SimpleCallExpression | NewExpression | SequenceExpression | TemplateLiteral | TaggedTemplateExpression | ClassExpression | MetaProperty | Identifier | AwaitExpression | ImportExpression | ChainExpression | Super */
        get() = definedExternally
        set(value) = definedExternally
    var arguments: Array<dynamic /* ThisExpression | ArrayExpression | ObjectExpression | FunctionExpression | ArrowFunctionExpression | YieldExpression | SimpleLiteral | RegExpLiteral | UnaryExpression | UpdateExpression | BinaryExpression | AssignmentExpression | LogicalExpression | MemberExpression | ConditionalExpression | SimpleCallExpression | NewExpression | SequenceExpression | TemplateLiteral | TaggedTemplateExpression | ClassExpression | MetaProperty | Identifier | AwaitExpression | ImportExpression | ChainExpression | SpreadElement */>
}

external interface SimpleCallExpression : BaseCallExpression {
    override var type: String /* "CallExpression" */
    var optional: Boolean
}

external interface NewExpression : BaseCallExpression {
    override var type: String /* "NewExpression" */
}

external interface MemberExpression : BaseExpression, BasePattern {
    override var type: String /* "MemberExpression" */
    var `object`: dynamic /* ThisExpression | ArrayExpression | ObjectExpression | FunctionExpression | ArrowFunctionExpression | YieldExpression | SimpleLiteral | RegExpLiteral | UnaryExpression | UpdateExpression | BinaryExpression | AssignmentExpression | LogicalExpression | MemberExpression | ConditionalExpression | SimpleCallExpression | NewExpression | SequenceExpression | TemplateLiteral | TaggedTemplateExpression | ClassExpression | MetaProperty | Identifier | AwaitExpression | ImportExpression | ChainExpression | Super */
        get() = definedExternally
        set(value) = definedExternally
    var property: dynamic /* ThisExpression | ArrayExpression | ObjectExpression | FunctionExpression | ArrowFunctionExpression | YieldExpression | SimpleLiteral | RegExpLiteral | UnaryExpression | UpdateExpression | BinaryExpression | AssignmentExpression | LogicalExpression | MemberExpression | ConditionalExpression | SimpleCallExpression | NewExpression | SequenceExpression | TemplateLiteral | TaggedTemplateExpression | ClassExpression | MetaProperty | Identifier | AwaitExpression | ImportExpression | ChainExpression */
        get() = definedExternally
        set(value) = definedExternally
    var computed: Boolean
    var optional: Boolean
}

external interface BasePattern : BaseNode

external interface SwitchCase : BaseNode {
    override var type: String /* "SwitchCase" */
    var test: dynamic /* ThisExpression? | ArrayExpression? | ObjectExpression? | FunctionExpression? | ArrowFunctionExpression? | YieldExpression? | SimpleLiteral? | RegExpLiteral? | UnaryExpression? | UpdateExpression? | BinaryExpression? | AssignmentExpression? | LogicalExpression? | MemberExpression? | ConditionalExpression? | SimpleCallExpression? | NewExpression? | SequenceExpression? | TemplateLiteral? | TaggedTemplateExpression? | ClassExpression? | MetaProperty? | Identifier? | AwaitExpression? | ImportExpression? | ChainExpression? */
        get() = definedExternally
        set(value) = definedExternally
    var consequent: Array<dynamic /* ExpressionStatement | BlockStatement | EmptyStatement | DebuggerStatement | WithStatement | ReturnStatement | LabeledStatement | BreakStatement | ContinueStatement | IfStatement | SwitchStatement | ThrowStatement | TryStatement | WhileStatement | DoWhileStatement | ForStatement | ForInStatement | ForOfStatement | FunctionDeclaration | VariableDeclaration | ClassDeclaration */>
}

external interface CatchClause : BaseNode {
    override var type: String /* "CatchClause" */
    var param: dynamic /* Identifier? | ObjectPattern? | ArrayPattern? | RestElement? | AssignmentPattern? | MemberExpression? */
        get() = definedExternally
        set(value) = definedExternally
    var body: BlockStatement
}

external interface Identifier : BaseNode, BaseExpression, BasePattern {
    override var type: String /* "Identifier" */
    var name: String
}

external interface SimpleLiteral : BaseNode, BaseExpression {
    override var type: String /* "Literal" */
    var value: dynamic /* String? | Boolean? | Number? */
        get() = definedExternally
        set(value) = definedExternally
    var raw: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `T$1` {
    var pattern: String
    var flags: String
}

external interface RegExpLiteral : BaseNode, BaseExpression {
    override var type: String /* "Literal" */
    var value: RegExp?
        get() = definedExternally
        set(value) = definedExternally
    var regex: `T$1`
    var raw: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ForOfStatement : BaseForXStatement {
    override var type: String /* "ForOfStatement" */
    var await: Boolean
}

external interface Super : BaseNode {
    override var type: String /* "Super" */
}

external interface SpreadElement : BaseNode {
    override var type: String /* "SpreadElement" */
    var argument: dynamic /* ThisExpression | ArrayExpression | ObjectExpression | FunctionExpression | ArrowFunctionExpression | YieldExpression | SimpleLiteral | RegExpLiteral | UnaryExpression | UpdateExpression | BinaryExpression | AssignmentExpression | LogicalExpression | MemberExpression | ConditionalExpression | SimpleCallExpression | NewExpression | SequenceExpression | TemplateLiteral | TaggedTemplateExpression | ClassExpression | MetaProperty | Identifier | AwaitExpression | ImportExpression | ChainExpression */
        get() = definedExternally
        set(value) = definedExternally
}

external interface ArrowFunctionExpression : BaseExpression, BaseFunction {
    override var type: String /* "ArrowFunctionExpression" */
    var expression: Boolean
    override var body: dynamic /* BlockStatement | ThisExpression | ArrayExpression | ObjectExpression | FunctionExpression | ArrowFunctionExpression | YieldExpression | SimpleLiteral | RegExpLiteral | UnaryExpression | UpdateExpression | BinaryExpression | AssignmentExpression | LogicalExpression | MemberExpression | ConditionalExpression | SimpleCallExpression | NewExpression | SequenceExpression | TemplateLiteral | TaggedTemplateExpression | ClassExpression | MetaProperty | Identifier | AwaitExpression | ImportExpression | ChainExpression */
        get() = definedExternally
        set(value) = definedExternally
}

external interface YieldExpression : BaseExpression {
    override var type: String /* "YieldExpression" */
    var argument: dynamic /* ThisExpression? | ArrayExpression? | ObjectExpression? | FunctionExpression? | ArrowFunctionExpression? | YieldExpression? | SimpleLiteral? | RegExpLiteral? | UnaryExpression? | UpdateExpression? | BinaryExpression? | AssignmentExpression? | LogicalExpression? | MemberExpression? | ConditionalExpression? | SimpleCallExpression? | NewExpression? | SequenceExpression? | TemplateLiteral? | TaggedTemplateExpression? | ClassExpression? | MetaProperty? | Identifier? | AwaitExpression? | ImportExpression? | ChainExpression? */
        get() = definedExternally
        set(value) = definedExternally
    var delegate: Boolean
}

external interface TemplateLiteral : BaseExpression {
    override var type: String /* "TemplateLiteral" */
    var quasis: Array<TemplateElement>
    var expressions: Array<dynamic /* ThisExpression | ArrayExpression | ObjectExpression | FunctionExpression | ArrowFunctionExpression | YieldExpression | SimpleLiteral | RegExpLiteral | UnaryExpression | UpdateExpression | BinaryExpression | AssignmentExpression | LogicalExpression | MemberExpression | ConditionalExpression | SimpleCallExpression | NewExpression | SequenceExpression | TemplateLiteral | TaggedTemplateExpression | ClassExpression | MetaProperty | Identifier | AwaitExpression | ImportExpression | ChainExpression */>
}

external interface TaggedTemplateExpression : BaseExpression {
    override var type: String /* "TaggedTemplateExpression" */
    var tag: dynamic /* ThisExpression | ArrayExpression | ObjectExpression | FunctionExpression | ArrowFunctionExpression | YieldExpression | SimpleLiteral | RegExpLiteral | UnaryExpression | UpdateExpression | BinaryExpression | AssignmentExpression | LogicalExpression | MemberExpression | ConditionalExpression | SimpleCallExpression | NewExpression | SequenceExpression | TemplateLiteral | TaggedTemplateExpression | ClassExpression | MetaProperty | Identifier | AwaitExpression | ImportExpression | ChainExpression */
        get() = definedExternally
        set(value) = definedExternally
    var quasi: TemplateLiteral
}

external interface `T$2` {
    var cooked: String
    var raw: String
}

external interface TemplateElement : BaseNode {
    override var type: String /* "TemplateElement" */
    var tail: Boolean
    var value: `T$2`
}

external interface AssignmentProperty : Property {
    override var value: dynamic /* Identifier | ObjectPattern | ArrayPattern | RestElement | AssignmentPattern | MemberExpression */
        get() = definedExternally
        set(value) = definedExternally
    override var kind: String /* "init" */
    override var method: Boolean
}

external interface ObjectPattern : BasePattern {
    override var type: String /* "ObjectPattern" */
    var properties: Array<dynamic /* AssignmentProperty | RestElement */>
}

external interface ArrayPattern : BasePattern {
    override var type: String /* "ArrayPattern" */
    var elements: Array<dynamic /* Identifier | ObjectPattern | ArrayPattern | RestElement | AssignmentPattern | MemberExpression */>
}

external interface RestElement : BasePattern {
    override var type: String /* "RestElement" */
    var argument: dynamic /* Identifier | ObjectPattern | ArrayPattern | RestElement | AssignmentPattern | MemberExpression */
        get() = definedExternally
        set(value) = definedExternally
}

external interface AssignmentPattern : BasePattern {
    override var type: String /* "AssignmentPattern" */
    var left: dynamic /* Identifier | ObjectPattern | ArrayPattern | RestElement | AssignmentPattern | MemberExpression */
        get() = definedExternally
        set(value) = definedExternally
    var right: dynamic /* ThisExpression | ArrayExpression | ObjectExpression | FunctionExpression | ArrowFunctionExpression | YieldExpression | SimpleLiteral | RegExpLiteral | UnaryExpression | UpdateExpression | BinaryExpression | AssignmentExpression | LogicalExpression | MemberExpression | ConditionalExpression | SimpleCallExpression | NewExpression | SequenceExpression | TemplateLiteral | TaggedTemplateExpression | ClassExpression | MetaProperty | Identifier | AwaitExpression | ImportExpression | ChainExpression */
        get() = definedExternally
        set(value) = definedExternally
}

external interface BaseClass : BaseNode {
    var superClass: dynamic /* ThisExpression? | ArrayExpression? | ObjectExpression? | FunctionExpression? | ArrowFunctionExpression? | YieldExpression? | SimpleLiteral? | RegExpLiteral? | UnaryExpression? | UpdateExpression? | BinaryExpression? | AssignmentExpression? | LogicalExpression? | MemberExpression? | ConditionalExpression? | SimpleCallExpression? | NewExpression? | SequenceExpression? | TemplateLiteral? | TaggedTemplateExpression? | ClassExpression? | MetaProperty? | Identifier? | AwaitExpression? | ImportExpression? | ChainExpression? */
        get() = definedExternally
        set(value) = definedExternally
    var body: ClassBody
}

external interface ClassBody : BaseNode {
    override var type: String /* "ClassBody" */
    var body: Array<MethodDefinition>
}

external interface MethodDefinition : BaseNode {
    override var type: String /* "MethodDefinition" */
    var key: dynamic /* ThisExpression | ArrayExpression | ObjectExpression | FunctionExpression | ArrowFunctionExpression | YieldExpression | SimpleLiteral | RegExpLiteral | UnaryExpression | UpdateExpression | BinaryExpression | AssignmentExpression | LogicalExpression | MemberExpression | ConditionalExpression | SimpleCallExpression | NewExpression | SequenceExpression | TemplateLiteral | TaggedTemplateExpression | ClassExpression | MetaProperty | Identifier | AwaitExpression | ImportExpression | ChainExpression */
        get() = definedExternally
        set(value) = definedExternally
    var value: FunctionExpression
    var kind: String /* "constructor" | "method" | "get" | "set" */
    var computed: Boolean
    var static: Boolean
}

external interface ClassDeclaration : BaseClass, BaseDeclaration {
    override var type: String /* "ClassDeclaration" */
    var id: Identifier?
}

external interface ClassExpression : BaseClass, BaseExpression {
    override var type: String /* "ClassExpression" */
    var id: Identifier?
        get() = definedExternally
        set(value) = definedExternally
}

external interface MetaProperty : BaseExpression {
    override var type: String /* "MetaProperty" */
    var meta: Identifier
    var property: Identifier
}

external interface BaseModuleDeclaration : BaseNode

external interface BaseModuleSpecifier : BaseNode {
    var local: Identifier
}

external interface ImportDeclaration : BaseModuleDeclaration {
    override var type: String /* "ImportDeclaration" */
    var specifiers: Array<dynamic /* ImportSpecifier | ImportDefaultSpecifier | ImportNamespaceSpecifier */>
    var source: dynamic /* SimpleLiteral | RegExpLiteral */
        get() = definedExternally
        set(value) = definedExternally
}

external interface ImportSpecifier : BaseModuleSpecifier {
    override var type: String /* "ImportSpecifier" */
    var imported: Identifier
}

external interface ImportExpression : BaseExpression {
    override var type: String /* "ImportExpression" */
    var source: dynamic /* ThisExpression | ArrayExpression | ObjectExpression | FunctionExpression | ArrowFunctionExpression | YieldExpression | SimpleLiteral | RegExpLiteral | UnaryExpression | UpdateExpression | BinaryExpression | AssignmentExpression | LogicalExpression | MemberExpression | ConditionalExpression | SimpleCallExpression | NewExpression | SequenceExpression | TemplateLiteral | TaggedTemplateExpression | ClassExpression | MetaProperty | Identifier | AwaitExpression | ImportExpression | ChainExpression */
        get() = definedExternally
        set(value) = definedExternally
}

external interface ImportDefaultSpecifier : BaseModuleSpecifier {
    override var type: String /* "ImportDefaultSpecifier" */
}

external interface ImportNamespaceSpecifier : BaseModuleSpecifier {
    override var type: String /* "ImportNamespaceSpecifier" */
}

external interface ExportNamedDeclaration : BaseModuleDeclaration {
    override var type: String /* "ExportNamedDeclaration" */
    var declaration: dynamic /* FunctionDeclaration? | VariableDeclaration? | ClassDeclaration? */
        get() = definedExternally
        set(value) = definedExternally
    var specifiers: Array<ExportSpecifier>
    var source: dynamic /* SimpleLiteral? | RegExpLiteral? */
        get() = definedExternally
        set(value) = definedExternally
}

external interface ExportSpecifier : BaseModuleSpecifier {
    override var type: String /* "ExportSpecifier" */
    var exported: Identifier
}

external interface ExportDefaultDeclaration : BaseModuleDeclaration {
    override var type: String /* "ExportDefaultDeclaration" */
    var declaration: dynamic /* FunctionDeclaration | VariableDeclaration | ClassDeclaration | ThisExpression | ArrayExpression | ObjectExpression | FunctionExpression | ArrowFunctionExpression | YieldExpression | SimpleLiteral | RegExpLiteral | UnaryExpression | UpdateExpression | BinaryExpression | AssignmentExpression | LogicalExpression | MemberExpression | ConditionalExpression | SimpleCallExpression | NewExpression | SequenceExpression | TemplateLiteral | TaggedTemplateExpression | ClassExpression | MetaProperty | Identifier | AwaitExpression | ImportExpression | ChainExpression */
        get() = definedExternally
        set(value) = definedExternally
}

external interface ExportAllDeclaration : BaseModuleDeclaration {
    override var type: String /* "ExportAllDeclaration" */
    var source: dynamic /* SimpleLiteral | RegExpLiteral */
        get() = definedExternally
        set(value) = definedExternally
}

external interface AwaitExpression : BaseExpression {
    override var type: String /* "AwaitExpression" */
    var argument: dynamic /* ThisExpression | ArrayExpression | ObjectExpression | FunctionExpression | ArrowFunctionExpression | YieldExpression | SimpleLiteral | RegExpLiteral | UnaryExpression | UpdateExpression | BinaryExpression | AssignmentExpression | LogicalExpression | MemberExpression | ConditionalExpression | SimpleCallExpression | NewExpression | SequenceExpression | TemplateLiteral | TaggedTemplateExpression | ClassExpression | MetaProperty | Identifier | AwaitExpression | ImportExpression | ChainExpression */
        get() = definedExternally
        set(value) = definedExternally
}