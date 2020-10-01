package space.kscience.kmath.ojalgo

import org.ojalgo.scalar.*
import space.kscience.kmath.complex.Complex
import space.kscience.kmath.operations.Field

public val OjalgoMoneyField: Field<Scalar<Money>> = OjalgoField<Scalar<Money>>(Money.ZERO, Money.ONE)

public val OjalgoComplexField: Field<Complex> =
    OjalgoField<Scalar<ComplexNumber>>(ComplexNumber.ZERO, ComplexNumber.ONE).convert(
        { Complex(it.get().real, it.get().imaginary) },
        { ComplexNumber.of(it.re, it.im) },
    )

public val OjalgoRationalField: Field<Double> =
    OjalgoField<Scalar<RationalNumber>>(RationalNumber.ZERO, RationalNumber.ONE).convert(
        Scalar<RationalNumber>::doubleValue,
        RationalNumber::valueOf,
    )

public val OjalgoQuaternionField: Field<space.kscience.kmath.complex.Quaternion> =
    OjalgoField<Scalar<Quaternion>>(Quaternion.ZERO, Quaternion.ONE).convert(
        { x -> space.kscience.kmath.complex.Quaternion(x.get().scalar(), x.get().i, x.get().j, x.get().k) },
        { (s, i, j, k) -> Quaternion.of(s, i, j, k) },
    )

public val OjalgoPrimitiveField: Field<Double> =
    OjalgoField<Scalar<Double>>(PrimitiveScalar.ZERO, PrimitiveScalar.ONE).convert(
        Scalar<Double>::doubleValue,
        PrimitiveScalar::valueOf,
    )
