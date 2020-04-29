package scientifik.kmath.geometry

data class Line<V: Vector>(val base: V, val direction: V)

typealias Line2D = Line<Vector2D>
typealias Line3D = Line<Vector3D>
