package space.kscience.kmath.geometry

public data class Line<V : Vector>(val base: V, val direction: V)

public typealias Line2D = Line<Vector2D>
public typealias Line3D = Line<Vector3D>
