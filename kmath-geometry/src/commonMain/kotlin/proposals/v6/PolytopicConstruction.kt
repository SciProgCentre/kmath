/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package proposals.v6

import space.kscience.attributes.Attribute
import space.kscience.attributes.AttributeContainer
import space.kscience.attributes.Attributes
import space.kscience.attributes.AttributesBuilder
import space.kscience.attributes.PolymorphicAttribute
import space.kscience.attributes.SafeType
import space.kscience.attributes.safeTypeOf
import space.kscience.kmath.geometry.GeometrySpace
import space.kscience.kmath.geometry.projectAlong


public data class Polytope(
    public val dimension: Int,
    public val faces: List<Set<Polytope>>,
    override val attributes: Attributes,
) : AttributeContainer

public fun Polytope(dimension: Int, faces: List<Set<Polytope>>, attributesBuilder: AttributesBuilder<Polytope>.() -> Unit = {}): Polytope =
    Polytope(dimension = dimension, faces = faces, attributes = Attributes(attributesBuilder))

public interface PolytopeAttribute<T> : Attribute<T>

public class PositionAttribute<Vector>(type: SafeType<Vector>) : PolymorphicAttribute<Vector>(type), PolytopeAttribute<Vector>

@Suppress("FunctionName")
public fun <Vector> Position(type: SafeType<Vector>): PositionAttribute<Vector> = PositionAttribute(type)
@Suppress("FunctionName")
public inline fun <reified Vector> Position(): PositionAttribute<Vector> = PositionAttribute(safeTypeOf())

public inline fun <reified Vector: Any> GeometrySpace<Vector, *>.projectAlong(polytope: Polytope, normal: Vector, base: Vector) : Polytope {
    val polytopesToProject = buildList {
        addAll(polytope.faces)
        add(setOf(polytope))
    }
    
    val polytopesMapping = mutableListOf<Map<Polytope, Polytope>>()
    
    val Position = Position<Vector>()
    polytopesMapping.add(
        polytopesToProject[0].associateWith { vertex ->
            Polytope(
                dimension = 0,
                faces = emptyList(),
            ) {
                putFrom(vertex.attributes)
                val currentVector = vertex.attributes[Position] ?: error("Position attribute not found in polytope $vertex")
                Position(projectAlong(currentVector, normal, base))
            }
        }
    )
    
    for (dimension in 1 ..< polytopesToProject.size)
        polytopesMapping.add(
            polytopesToProject[dimension].associateWith { polytope ->
                val newFaces = polytope.faces.mapIndexed { subdimension, faces ->
                    faces.mapTo(mutableSetOf()) { polytopesMapping[subdimension][it]!! }
                }
                Polytope(
                    dimension = dimension,
                    faces = newFaces
                ) {
                    putFrom(polytope.attributes)
                }
            }
        )
    
    return polytopesMapping.last().values.single()
}