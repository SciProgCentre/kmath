/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS",
    "NO_EXPLICIT_VISIBILITY_IN_API_MODE_WARNING",
    "PropertyName",
    "ClassName",
)

@file:JsModule("binaryen")
@file:JsNonModule

package space.kscience.kmath.internal.binaryen

import org.khronos.webgl.Uint8Array
import kotlin.js.Promise

internal external var isReady: Boolean

internal external var ready: Promise<Any>

internal external var none: Type

internal external var i32: Type

internal external var i64: Type

internal external var f32: Type

internal external var f64: Type

internal external var v128: Type

internal external var funcref: Type

internal external var anyref: Type

internal external var nullref: Type

internal external var exnref: Type

internal external var unreachable: Type

internal external var auto: Type

internal external fun createType(types: Array<Type>): Type

internal external fun expandType(type: Type): Array<Type>

internal external enum class ExpressionIds {
    Invalid,
    Block,
    If,
    Loop,
    Break,
    Switch,
    Call,
    CallIndirect,
    LocalGet,
    LocalSet,
    GlobalGet,
    GlobalSet,
    Load,
    Store,
    Const,
    Unary,
    Binary,
    Select,
    Drop,
    Return,
    Host,
    Nop,
    Unreachable,
    AtomicCmpxchg,
    AtomicRMW,
    AtomicWait,
    AtomicNotify,
    AtomicFence,
    SIMDExtract,
    SIMDReplace,
    SIMDShuffle,
    SIMDTernary,
    SIMDShift,
    SIMDLoad,
    MemoryInit,
    DataDrop,
    MemoryCopy,
    MemoryFill,
    RefNull,
    RefIsNull,
    RefFunc,
    Try,
    Throw,
    Rethrow,
    BrOnExn,
    TupleMake,
    TupleExtract,
    Push,
    Pop
}

internal external var InvalidId: ExpressionIds

internal external var BlockId: ExpressionIds

internal external var IfId: ExpressionIds

internal external var LoopId: ExpressionIds

internal external var BreakId: ExpressionIds

internal external var SwitchId: ExpressionIds

internal external var CallId: ExpressionIds

internal external var CallIndirectId: ExpressionIds

internal external var LocalGetId: ExpressionIds

internal external var LocalSetId: ExpressionIds

internal external var GlobalGetId: ExpressionIds

internal external var GlobalSetId: ExpressionIds

internal external var LoadId: ExpressionIds

internal external var StoreId: ExpressionIds

internal external var ConstId: ExpressionIds

internal external var UnaryId: ExpressionIds

internal external var BinaryId: ExpressionIds

internal external var SelectId: ExpressionIds

internal external var DropId: ExpressionIds

internal external var ReturnId: ExpressionIds

internal external var HostId: ExpressionIds

internal external var NopId: ExpressionIds

internal external var UnreachableId: ExpressionIds

internal external var AtomicCmpxchgId: ExpressionIds

internal external var AtomicRMWId: ExpressionIds

internal external var AtomicWaitId: ExpressionIds

internal external var AtomicNotifyId: ExpressionIds

internal external var AtomicFenceId: ExpressionIds

internal external var SIMDExtractId: ExpressionIds

internal external var SIMDReplaceId: ExpressionIds

internal external var SIMDShuffleId: ExpressionIds

internal external var SIMDTernaryId: ExpressionIds

internal external var SIMDShiftId: ExpressionIds

internal external var SIMDLoadId: ExpressionIds

internal external var MemoryInitId: ExpressionIds

internal external var DataDropId: ExpressionIds

internal external var MemoryCopyId: ExpressionIds

internal external var MemoryFillId: ExpressionIds

internal external var RefNullId: ExpressionIds

internal external var RefIsNullId: ExpressionIds

internal external var RefFuncId: ExpressionIds

internal external var TryId: ExpressionIds

internal external var ThrowId: ExpressionIds

internal external var RethrowId: ExpressionIds

internal external var BrOnExnId: ExpressionIds

internal external var TupleMakeId: ExpressionIds

internal external var TupleExtractId: ExpressionIds

internal external var PushId: ExpressionIds

internal external var PopId: ExpressionIds

internal external enum class ExternalKinds {
    Function,
    Table,
    Memory,
    Global,
    Event
}

internal external var ExternalFunction: ExternalKinds

internal external var ExternalTable: ExternalKinds

internal external var ExternalMemory: ExternalKinds

internal external var ExternalGlobal: ExternalKinds

internal external var ExternalEvent: ExternalKinds

internal external enum class Features {
    MVP,
    Atomics,
    MutableGlobals,
    TruncSat,
    SIMD,
    BulkMemory,
    SignExt,
    ExceptionHandling,
    TailCall,
    ReferenceTypes,
    Multivalue,
    GC,
    Memory64,
    All
}

internal external enum class Operations {
    ClzInt32,
    CtzInt32,
    PopcntInt32,
    NegFloat32,
    AbsFloat32,
    CeilFloat32,
    FloorFloat32,
    TruncFloat32,
    NearestFloat32,
    SqrtFloat32,
    EqZInt32,
    ClzInt64,
    CtzInt64,
    PopcntInt64,
    NegFloat64,
    AbsFloat64,
    CeilFloat64,
    FloorFloat64,
    TruncFloat64,
    NearestFloat64,
    SqrtFloat64,
    EqZInt64,
    ExtendSInt32,
    ExtendUInt32,
    WrapInt64,
    TruncSFloat32ToInt32,
    TruncSFloat32ToInt64,
    TruncUFloat32ToInt32,
    TruncUFloat32ToInt64,
    TruncSFloat64ToInt32,
    TruncSFloat64ToInt64,
    TruncUFloat64ToInt32,
    TruncUFloat64ToInt64,
    TruncSatSFloat32ToInt32,
    TruncSatSFloat32ToInt64,
    TruncSatUFloat32ToInt32,
    TruncSatUFloat32ToInt64,
    TruncSatSFloat64ToInt32,
    TruncSatSFloat64ToInt64,
    TruncSatUFloat64ToInt32,
    TruncSatUFloat64ToInt64,
    ReinterpretFloat32,
    ReinterpretFloat64,
    ConvertSInt32ToFloat32,
    ConvertSInt32ToFloat64,
    ConvertUInt32ToFloat32,
    ConvertUInt32ToFloat64,
    ConvertSInt64ToFloat32,
    ConvertSInt64ToFloat64,
    ConvertUInt64ToFloat32,
    ConvertUInt64ToFloat64,
    PromoteFloat32,
    DemoteFloat64,
    ReinterpretInt32,
    ReinterpretInt64,
    ExtendS8Int32,
    ExtendS16Int32,
    ExtendS8Int64,
    ExtendS16Int64,
    ExtendS32Int64,
    AddInt32,
    SubInt32,
    MulInt32,
    DivSInt32,
    DivUInt32,
    RemSInt32,
    RemUInt32,
    AndInt32,
    OrInt32,
    XorInt32,
    ShlInt32,
    ShrUInt32,
    ShrSInt32,
    RotLInt32,
    RotRInt32,
    EqInt32,
    NeInt32,
    LtSInt32,
    LtUInt32,
    LeSInt32,
    LeUInt32,
    GtSInt32,
    GtUInt32,
    GeSInt32,
    GeUInt32,
    AddInt64,
    SubInt64,
    MulInt64,
    DivSInt64,
    DivUInt64,
    RemSInt64,
    RemUInt64,
    AndInt64,
    OrInt64,
    XorInt64,
    ShlInt64,
    ShrUInt64,
    ShrSInt64,
    RotLInt64,
    RotRInt64,
    EqInt64,
    NeInt64,
    LtSInt64,
    LtUInt64,
    LeSInt64,
    LeUInt64,
    GtSInt64,
    GtUInt64,
    GeSInt64,
    GeUInt64,
    AddFloat32,
    SubFloat32,
    MulFloat32,
    DivFloat32,
    CopySignFloat32,
    MinFloat32,
    MaxFloat32,
    EqFloat32,
    NeFloat32,
    LtFloat32,
    LeFloat32,
    GtFloat32,
    GeFloat32,
    AddFloat64,
    SubFloat64,
    MulFloat64,
    DivFloat64,
    CopySignFloat64,
    MinFloat64,
    MaxFloat64,
    EqFloat64,
    NeFloat64,
    LtFloat64,
    LeFloat64,
    GtFloat64,
    GeFloat64,
    MemorySize,
    MemoryGrow,
    AtomicRMWAdd,
    AtomicRMWSub,
    AtomicRMWAnd,
    AtomicRMWOr,
    AtomicRMWXor,
    AtomicRMWXchg,
    SplatVecI8x16,
    ExtractLaneSVecI8x16,
    ExtractLaneUVecI8x16,
    ReplaceLaneVecI8x16,
    SplatVecI16x8,
    ExtractLaneSVecI16x8,
    ExtractLaneUVecI16x8,
    ReplaceLaneVecI16x8,
    SplatVecI32x4,
    ExtractLaneVecI32x4,
    ReplaceLaneVecI32x4,
    SplatVecI64x2,
    ExtractLaneVecI64x2,
    ReplaceLaneVecI64x2,
    SplatVecF32x4,
    ExtractLaneVecF32x4,
    ReplaceLaneVecF32x4,
    SplatVecF64x2,
    ExtractLaneVecF64x2,
    ReplaceLaneVecF64x2,
    EqVecI8x16,
    NeVecI8x16,
    LtSVecI8x16,
    LtUVecI8x16,
    GtSVecI8x16,
    GtUVecI8x16,
    LeSVecI8x16,
    LeUVecI8x16,
    GeSVecI8x16,
    GeUVecI8x16,
    EqVecI16x8,
    NeVecI16x8,
    LtSVecI16x8,
    LtUVecI16x8,
    GtSVecI16x8,
    GtUVecI16x8,
    LeSVecI16x8,
    LeUVecI16x8,
    GeSVecI16x8,
    GeUVecI16x8,
    EqVecI32x4,
    NeVecI32x4,
    LtSVecI32x4,
    LtUVecI32x4,
    GtSVecI32x4,
    GtUVecI32x4,
    LeSVecI32x4,
    LeUVecI32x4,
    GeSVecI32x4,
    GeUVecI32x4,
    EqVecF32x4,
    NeVecF32x4,
    LtVecF32x4,
    GtVecF32x4,
    LeVecF32x4,
    GeVecF32x4,
    EqVecF64x2,
    NeVecF64x2,
    LtVecF64x2,
    GtVecF64x2,
    LeVecF64x2,
    GeVecF64x2,
    NotVec128,
    AndVec128,
    OrVec128,
    XorVec128,
    AndNotVec128,
    BitselectVec128,
    NegVecI8x16,
    AnyTrueVecI8x16,
    AllTrueVecI8x16,
    ShlVecI8x16,
    ShrSVecI8x16,
    ShrUVecI8x16,
    AddVecI8x16,
    AddSatSVecI8x16,
    AddSatUVecI8x16,
    SubVecI8x16,
    SubSatSVecI8x16,
    SubSatUVecI8x16,
    MulVecI8x16,
    MinSVecI8x16,
    MinUVecI8x16,
    MaxSVecI8x16,
    MaxUVecI8x16,
    NegVecI16x8,
    AnyTrueVecI16x8,
    AllTrueVecI16x8,
    ShlVecI16x8,
    ShrSVecI16x8,
    ShrUVecI16x8,
    AddVecI16x8,
    AddSatSVecI16x8,
    AddSatUVecI16x8,
    SubVecI16x8,
    SubSatSVecI16x8,
    SubSatUVecI16x8,
    MulVecI16x8,
    MinSVecI16x8,
    MinUVecI16x8,
    MaxSVecI16x8,
    MaxUVecI16x8,
    DotSVecI16x8ToVecI32x4,
    NegVecI32x4,
    AnyTrueVecI32x4,
    AllTrueVecI32x4,
    ShlVecI32x4,
    ShrSVecI32x4,
    ShrUVecI32x4,
    AddVecI32x4,
    SubVecI32x4,
    MulVecI32x4,
    MinSVecI32x4,
    MinUVecI32x4,
    MaxSVecI32x4,
    MaxUVecI32x4,
    NegVecI64x2,
    AnyTrueVecI64x2,
    AllTrueVecI64x2,
    ShlVecI64x2,
    ShrSVecI64x2,
    ShrUVecI64x2,
    AddVecI64x2,
    SubVecI64x2,
    AbsVecF32x4,
    NegVecF32x4,
    SqrtVecF32x4,
    QFMAVecF32x4,
    QFMSVecF32x4,
    AddVecF32x4,
    SubVecF32x4,
    MulVecF32x4,
    DivVecF32x4,
    MinVecF32x4,
    MaxVecF32x4,
    AbsVecF64x2,
    NegVecF64x2,
    SqrtVecF64x2,
    QFMAVecF64x2,
    QFMSVecF64x2,
    AddVecF64x2,
    SubVecF64x2,
    MulVecF64x2,
    DivVecF64x2,
    MinVecF64x2,
    MaxVecF64x2,
    TruncSatSVecF32x4ToVecI32x4,
    TruncSatUVecF32x4ToVecI32x4,
    TruncSatSVecF64x2ToVecI64x2,
    TruncSatUVecF64x2ToVecI64x2,
    ConvertSVecI32x4ToVecF32x4,
    ConvertUVecI32x4ToVecF32x4,
    ConvertSVecI64x2ToVecF64x2,
    ConvertUVecI64x2ToVecF64x2,
    LoadSplatVec8x16,
    LoadSplatVec16x8,
    LoadSplatVec32x4,
    LoadSplatVec64x2,
    LoadExtSVec8x8ToVecI16x8,
    LoadExtUVec8x8ToVecI16x8,
    LoadExtSVec16x4ToVecI32x4,
    LoadExtUVec16x4ToVecI32x4,
    LoadExtSVec32x2ToVecI64x2,
    LoadExtUVec32x2ToVecI64x2,
    NarrowSVecI16x8ToVecI8x16,
    NarrowUVecI16x8ToVecI8x16,
    NarrowSVecI32x4ToVecI16x8,
    NarrowUVecI32x4ToVecI16x8,
    WidenLowSVecI8x16ToVecI16x8,
    WidenHighSVecI8x16ToVecI16x8,
    WidenLowUVecI8x16ToVecI16x8,
    WidenHighUVecI8x16ToVecI16x8,
    WidenLowSVecI16x8ToVecI32x4,
    WidenHighSVecI16x8ToVecI32x4,
    WidenLowUVecI16x8ToVecI32x4,
    WidenHighUVecI16x8ToVecI32x4,
    SwizzleVec8x16
}

internal external var ClzInt32: Operations

internal external var CtzInt32: Operations

internal external var PopcntInt32: Operations

internal external var NegFloat32: Operations

internal external var AbsFloat32: Operations

internal external var CeilFloat32: Operations

internal external var FloorFloat32: Operations

internal external var TruncFloat32: Operations

internal external var NearestFloat32: Operations

internal external var SqrtFloat32: Operations

internal external var EqZInt32: Operations

internal external var ClzInt64: Operations

internal external var CtzInt64: Operations

internal external var PopcntInt64: Operations

internal external var NegFloat64: Operations

internal external var AbsFloat64: Operations

internal external var CeilFloat64: Operations

internal external var FloorFloat64: Operations

internal external var TruncFloat64: Operations

internal external var NearestFloat64: Operations

internal external var SqrtFloat64: Operations

internal external var EqZInt64: Operations

internal external var ExtendSInt32: Operations

internal external var ExtendUInt32: Operations

internal external var WrapInt64: Operations

internal external var TruncSFloat32ToInt32: Operations

internal external var TruncSFloat32ToInt64: Operations

internal external var TruncUFloat32ToInt32: Operations

internal external var TruncUFloat32ToInt64: Operations

internal external var TruncSFloat64ToInt32: Operations

internal external var TruncSFloat64ToInt64: Operations

internal external var TruncUFloat64ToInt32: Operations

internal external var TruncUFloat64ToInt64: Operations

internal external var TruncSatSFloat32ToInt32: Operations

internal external var TruncSatSFloat32ToInt64: Operations

internal external var TruncSatUFloat32ToInt32: Operations

internal external var TruncSatUFloat32ToInt64: Operations

internal external var TruncSatSFloat64ToInt32: Operations

internal external var TruncSatSFloat64ToInt64: Operations

internal external var TruncSatUFloat64ToInt32: Operations

internal external var TruncSatUFloat64ToInt64: Operations

internal external var ReinterpretFloat32: Operations

internal external var ReinterpretFloat64: Operations

internal external var ConvertSInt32ToFloat32: Operations

internal external var ConvertSInt32ToFloat64: Operations

internal external var ConvertUInt32ToFloat32: Operations

internal external var ConvertUInt32ToFloat64: Operations

internal external var ConvertSInt64ToFloat32: Operations

internal external var ConvertSInt64ToFloat64: Operations

internal external var ConvertUInt64ToFloat32: Operations

internal external var ConvertUInt64ToFloat64: Operations

internal external var PromoteFloat32: Operations

internal external var DemoteFloat64: Operations

internal external var ReinterpretInt32: Operations

internal external var ReinterpretInt64: Operations

internal external var ExtendS8Int32: Operations

internal external var ExtendS16Int32: Operations

internal external var ExtendS8Int64: Operations

internal external var ExtendS16Int64: Operations

internal external var ExtendS32Int64: Operations

internal external var AddInt32: Operations

internal external var SubInt32: Operations

internal external var MulInt32: Operations

internal external var DivSInt32: Operations

internal external var DivUInt32: Operations

internal external var RemSInt32: Operations

internal external var RemUInt32: Operations

internal external var AndInt32: Operations

internal external var OrInt32: Operations

internal external var XorInt32: Operations

internal external var ShlInt32: Operations

internal external var ShrUInt32: Operations

internal external var ShrSInt32: Operations

internal external var RotLInt32: Operations

internal external var RotRInt32: Operations

internal external var EqInt32: Operations

internal external var NeInt32: Operations

internal external var LtSInt32: Operations

internal external var LtUInt32: Operations

internal external var LeSInt32: Operations

internal external var LeUInt32: Operations

internal external var GtSInt32: Operations

internal external var GtUInt32: Operations

internal external var GeSInt32: Operations

internal external var GeUInt32: Operations

internal external var AddInt64: Operations

internal external var SubInt64: Operations

internal external var MulInt64: Operations

internal external var DivSInt64: Operations

internal external var DivUInt64: Operations

internal external var RemSInt64: Operations

internal external var RemUInt64: Operations

internal external var AndInt64: Operations

internal external var OrInt64: Operations

internal external var XorInt64: Operations

internal external var ShlInt64: Operations

internal external var ShrUInt64: Operations

internal external var ShrSInt64: Operations

internal external var RotLInt64: Operations

internal external var RotRInt64: Operations

internal external var EqInt64: Operations

internal external var NeInt64: Operations

internal external var LtSInt64: Operations

internal external var LtUInt64: Operations

internal external var LeSInt64: Operations

internal external var LeUInt64: Operations

internal external var GtSInt64: Operations

internal external var GtUInt64: Operations

internal external var GeSInt64: Operations

internal external var GeUInt64: Operations

internal external var AddFloat32: Operations

internal external var SubFloat32: Operations

internal external var MulFloat32: Operations

internal external var DivFloat32: Operations

internal external var CopySignFloat32: Operations

internal external var MinFloat32: Operations

internal external var MaxFloat32: Operations

internal external var EqFloat32: Operations

internal external var NeFloat32: Operations

internal external var LtFloat32: Operations

internal external var LeFloat32: Operations

internal external var GtFloat32: Operations

internal external var GeFloat32: Operations

internal external var AddFloat64: Operations

internal external var SubFloat64: Operations

internal external var MulFloat64: Operations

internal external var DivFloat64: Operations

internal external var CopySignFloat64: Operations

internal external var MinFloat64: Operations

internal external var MaxFloat64: Operations

internal external var EqFloat64: Operations

internal external var NeFloat64: Operations

internal external var LtFloat64: Operations

internal external var LeFloat64: Operations

internal external var GtFloat64: Operations

internal external var GeFloat64: Operations

internal external var MemorySize: Operations

internal external var MemoryGrow: Operations

internal external var AtomicRMWAdd: Operations

internal external var AtomicRMWSub: Operations

internal external var AtomicRMWAnd: Operations

internal external var AtomicRMWOr: Operations

internal external var AtomicRMWXor: Operations

internal external var AtomicRMWXchg: Operations

internal external var SplatVecI8x16: Operations

internal external var ExtractLaneSVecI8x16: Operations

internal external var ExtractLaneUVecI8x16: Operations

internal external var ReplaceLaneVecI8x16: Operations

internal external var SplatVecI16x8: Operations

internal external var ExtractLaneSVecI16x8: Operations

internal external var ExtractLaneUVecI16x8: Operations

internal external var ReplaceLaneVecI16x8: Operations

internal external var SplatVecI32x4: Operations

internal external var ExtractLaneVecI32x4: Operations

internal external var ReplaceLaneVecI32x4: Operations

internal external var SplatVecI64x2: Operations

internal external var ExtractLaneVecI64x2: Operations

internal external var ReplaceLaneVecI64x2: Operations

internal external var SplatVecF32x4: Operations

internal external var ExtractLaneVecF32x4: Operations

internal external var ReplaceLaneVecF32x4: Operations

internal external var SplatVecF64x2: Operations

internal external var ExtractLaneVecF64x2: Operations

internal external var ReplaceLaneVecF64x2: Operations

internal external var EqVecI8x16: Operations

internal external var NeVecI8x16: Operations

internal external var LtSVecI8x16: Operations

internal external var LtUVecI8x16: Operations

internal external var GtSVecI8x16: Operations

internal external var GtUVecI8x16: Operations

internal external var LeSVecI8x16: Operations

internal external var LeUVecI8x16: Operations

internal external var GeSVecI8x16: Operations

internal external var GeUVecI8x16: Operations

internal external var EqVecI16x8: Operations

internal external var NeVecI16x8: Operations

internal external var LtSVecI16x8: Operations

internal external var LtUVecI16x8: Operations

internal external var GtSVecI16x8: Operations

internal external var GtUVecI16x8: Operations

internal external var LeSVecI16x8: Operations

internal external var LeUVecI16x8: Operations

internal external var GeSVecI16x8: Operations

internal external var GeUVecI16x8: Operations

internal external var EqVecI32x4: Operations

internal external var NeVecI32x4: Operations

internal external var LtSVecI32x4: Operations

internal external var LtUVecI32x4: Operations

internal external var GtSVecI32x4: Operations

internal external var GtUVecI32x4: Operations

internal external var LeSVecI32x4: Operations

internal external var LeUVecI32x4: Operations

internal external var GeSVecI32x4: Operations

internal external var GeUVecI32x4: Operations

internal external var EqVecF32x4: Operations

internal external var NeVecF32x4: Operations

internal external var LtVecF32x4: Operations

internal external var GtVecF32x4: Operations

internal external var LeVecF32x4: Operations

internal external var GeVecF32x4: Operations

internal external var EqVecF64x2: Operations

internal external var NeVecF64x2: Operations

internal external var LtVecF64x2: Operations

internal external var GtVecF64x2: Operations

internal external var LeVecF64x2: Operations

internal external var GeVecF64x2: Operations

internal external var NotVec128: Operations

internal external var AndVec128: Operations

internal external var OrVec128: Operations

internal external var XorVec128: Operations

internal external var AndNotVec128: Operations

internal external var BitselectVec128: Operations

internal external var NegVecI8x16: Operations

internal external var AnyTrueVecI8x16: Operations

internal external var AllTrueVecI8x16: Operations

internal external var ShlVecI8x16: Operations

internal external var ShrSVecI8x16: Operations

internal external var ShrUVecI8x16: Operations

internal external var AddVecI8x16: Operations

internal external var AddSatSVecI8x16: Operations

internal external var AddSatUVecI8x16: Operations

internal external var SubVecI8x16: Operations

internal external var SubSatSVecI8x16: Operations

internal external var SubSatUVecI8x16: Operations

internal external var MulVecI8x16: Operations

internal external var MinSVecI8x16: Operations

internal external var MinUVecI8x16: Operations

internal external var MaxSVecI8x16: Operations

internal external var MaxUVecI8x16: Operations

internal external var NegVecI16x8: Operations

internal external var AnyTrueVecI16x8: Operations

internal external var AllTrueVecI16x8: Operations

internal external var ShlVecI16x8: Operations

internal external var ShrSVecI16x8: Operations

internal external var ShrUVecI16x8: Operations

internal external var AddVecI16x8: Operations

internal external var AddSatSVecI16x8: Operations

internal external var AddSatUVecI16x8: Operations

internal external var SubVecI16x8: Operations

internal external var SubSatSVecI16x8: Operations

internal external var SubSatUVecI16x8: Operations

internal external var MulVecI16x8: Operations

internal external var MinSVecI16x8: Operations

internal external var MinUVecI16x8: Operations

internal external var MaxSVecI16x8: Operations

internal external var MaxUVecI16x8: Operations

internal external var DotSVecI16x8ToVecI32x4: Operations

internal external var NegVecI32x4: Operations

internal external var AnyTrueVecI32x4: Operations

internal external var AllTrueVecI32x4: Operations

internal external var ShlVecI32x4: Operations

internal external var ShrSVecI32x4: Operations

internal external var ShrUVecI32x4: Operations

internal external var AddVecI32x4: Operations

internal external var SubVecI32x4: Operations

internal external var MulVecI32x4: Operations

internal external var MinSVecI32x4: Operations

internal external var MinUVecI32x4: Operations

internal external var MaxSVecI32x4: Operations

internal external var MaxUVecI32x4: Operations

internal external var NegVecI64x2: Operations

internal external var AnyTrueVecI64x2: Operations

internal external var AllTrueVecI64x2: Operations

internal external var ShlVecI64x2: Operations

internal external var ShrSVecI64x2: Operations

internal external var ShrUVecI64x2: Operations

internal external var AddVecI64x2: Operations

internal external var SubVecI64x2: Operations

internal external var AbsVecF32x4: Operations

internal external var NegVecF32x4: Operations

internal external var SqrtVecF32x4: Operations

internal external var QFMAVecF32x4: Operations

internal external var QFMSVecF32x4: Operations

internal external var AddVecF32x4: Operations

internal external var SubVecF32x4: Operations

internal external var MulVecF32x4: Operations

internal external var DivVecF32x4: Operations

internal external var MinVecF32x4: Operations

internal external var MaxVecF32x4: Operations

internal external var AbsVecF64x2: Operations

internal external var NegVecF64x2: Operations

internal external var SqrtVecF64x2: Operations

internal external var QFMAVecF64x2: Operations

internal external var QFMSVecF64x2: Operations

internal external var AddVecF64x2: Operations

internal external var SubVecF64x2: Operations

internal external var MulVecF64x2: Operations

internal external var DivVecF64x2: Operations

internal external var MinVecF64x2: Operations

internal external var MaxVecF64x2: Operations

internal external var TruncSatSVecF32x4ToVecI32x4: Operations

internal external var TruncSatUVecF32x4ToVecI32x4: Operations

internal external var TruncSatSVecF64x2ToVecI64x2: Operations

internal external var TruncSatUVecF64x2ToVecI64x2: Operations

internal external var ConvertSVecI32x4ToVecF32x4: Operations

internal external var ConvertUVecI32x4ToVecF32x4: Operations

internal external var ConvertSVecI64x2ToVecF64x2: Operations

internal external var ConvertUVecI64x2ToVecF64x2: Operations

internal external var LoadSplatVec8x16: Operations

internal external var LoadSplatVec16x8: Operations

internal external var LoadSplatVec32x4: Operations

internal external var LoadSplatVec64x2: Operations

internal external var LoadExtSVec8x8ToVecI16x8: Operations

internal external var LoadExtUVec8x8ToVecI16x8: Operations

internal external var LoadExtSVec16x4ToVecI32x4: Operations

internal external var LoadExtUVec16x4ToVecI32x4: Operations

internal external var LoadExtSVec32x2ToVecI64x2: Operations

internal external var LoadExtUVec32x2ToVecI64x2: Operations

internal external var NarrowSVecI16x8ToVecI8x16: Operations

internal external var NarrowUVecI16x8ToVecI8x16: Operations

internal external var NarrowSVecI32x4ToVecI16x8: Operations

internal external var NarrowUVecI32x4ToVecI16x8: Operations

internal external var WidenLowSVecI8x16ToVecI16x8: Operations

internal external var WidenHighSVecI8x16ToVecI16x8: Operations

internal external var WidenLowUVecI8x16ToVecI16x8: Operations

internal external var WidenHighUVecI8x16ToVecI16x8: Operations

internal external var WidenLowSVecI16x8ToVecI32x4: Operations

internal external var WidenHighSVecI16x8ToVecI32x4: Operations

internal external var WidenLowUVecI16x8ToVecI32x4: Operations

internal external var WidenHighUVecI16x8ToVecI32x4: Operations

internal external var SwizzleVec8x16: Operations

internal external interface `T$2` {
    fun get(index: Number, type: Type): ExpressionRef
    fun set(index: Number, value: ExpressionRef): ExpressionRef
    fun tee(index: Number, value: ExpressionRef, type: Type): ExpressionRef
}

internal external interface `T$3` {
    fun get(name: String, type: Type): ExpressionRef
    fun set(name: String, value: ExpressionRef): ExpressionRef
}

internal external interface `T$4` {
    fun size(): ExpressionRef
    fun grow(value: ExpressionRef): ExpressionRef
    fun init(segment: Number, dest: ExpressionRef, offset: ExpressionRef, size: ExpressionRef): ExpressionRef
    fun copy(dest: ExpressionRef, source: ExpressionRef, size: ExpressionRef): ExpressionRef
    fun fill(dest: ExpressionRef, value: ExpressionRef, size: ExpressionRef): ExpressionRef
}

internal external interface `T$5` {
    fun drop(segment: Number): ExpressionRef
}

internal external interface `T$6` {
    fun f32(value: ExpressionRef): ExpressionRef
    fun f64(value: ExpressionRef): ExpressionRef
}

internal external interface `T$7` {
    fun add(offset: Number, ptr: ExpressionRef, value: ExpressionRef): ExpressionRef
    fun sub(offset: Number, ptr: ExpressionRef, value: ExpressionRef): ExpressionRef
    fun and(offset: Number, ptr: ExpressionRef, value: ExpressionRef): ExpressionRef
    fun or(offset: Number, ptr: ExpressionRef, value: ExpressionRef): ExpressionRef
    fun xor(offset: Number, ptr: ExpressionRef, value: ExpressionRef): ExpressionRef
    fun xchg(offset: Number, ptr: ExpressionRef, value: ExpressionRef): ExpressionRef
    fun cmpxchg(offset: Number, ptr: ExpressionRef, expected: ExpressionRef, replacement: ExpressionRef): ExpressionRef
}

internal external interface `T$8` {
    fun load(offset: Number, ptr: ExpressionRef): ExpressionRef
    fun load8_u(offset: Number, ptr: ExpressionRef): ExpressionRef
    fun load16_u(offset: Number, ptr: ExpressionRef): ExpressionRef
    fun store(offset: Number, ptr: ExpressionRef, value: ExpressionRef): ExpressionRef
    fun store8(offset: Number, ptr: ExpressionRef, value: ExpressionRef): ExpressionRef
    fun store16(offset: Number, ptr: ExpressionRef, value: ExpressionRef): ExpressionRef
    var rmw: `T$7`
    var rmw8_u: `T$7`
    var rmw16_u: `T$7`
    fun wait(ptr: ExpressionRef, expected: ExpressionRef, timeout: ExpressionRef): ExpressionRef
}

internal external interface `T$9` {
    fun load(offset: Number, align: Number, ptr: ExpressionRef): ExpressionRef
    fun load8_s(offset: Number, align: Number, ptr: ExpressionRef): ExpressionRef
    fun load8_u(offset: Number, align: Number, ptr: ExpressionRef): ExpressionRef
    fun load16_s(offset: Number, align: Number, ptr: ExpressionRef): ExpressionRef
    fun load16_u(offset: Number, align: Number, ptr: ExpressionRef): ExpressionRef
    fun store(offset: Number, align: Number, ptr: ExpressionRef, value: ExpressionRef): ExpressionRef
    fun store8(offset: Number, align: Number, ptr: ExpressionRef, value: ExpressionRef): ExpressionRef
    fun store16(offset: Number, align: Number, ptr: ExpressionRef, value: ExpressionRef): ExpressionRef
    fun const(value: Number): ExpressionRef
    fun clz(value: ExpressionRef): ExpressionRef
    fun ctz(value: ExpressionRef): ExpressionRef
    fun popcnt(value: ExpressionRef): ExpressionRef
    fun eqz(value: ExpressionRef): ExpressionRef
    var trunc_s: `T$6`
    var trunc_u: `T$6`
    var trunc_s_sat: `T$6`
    var trunc_u_sat: `T$6`
    fun reinterpret(value: ExpressionRef): ExpressionRef
    fun extend8_s(value: ExpressionRef): ExpressionRef
    fun extend16_s(value: ExpressionRef): ExpressionRef
    fun wrap(value: ExpressionRef): ExpressionRef
    fun add(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun sub(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun mul(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun div_s(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun div_u(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun rem_s(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun rem_u(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun and(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun or(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun xor(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun shl(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun shr_u(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun shr_s(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun rotl(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun rotr(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun eq(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun ne(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun lt_s(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun lt_u(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun le_s(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun le_u(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun gt_s(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun gt_u(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun ge_s(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun ge_u(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    var atomic: `T$8`
    fun pop(): ExpressionRef
}

internal external interface `T$10` {
    fun load(offset: Number, ptr: ExpressionRef): ExpressionRef
    fun load8_u(offset: Number, ptr: ExpressionRef): ExpressionRef
    fun load16_u(offset: Number, ptr: ExpressionRef): ExpressionRef
    fun load32_u(offset: Number, ptr: ExpressionRef): ExpressionRef
    fun store(offset: Number, ptr: ExpressionRef, value: ExpressionRef): ExpressionRef
    fun store8(offset: Number, ptr: ExpressionRef, value: ExpressionRef): ExpressionRef
    fun store16(offset: Number, ptr: ExpressionRef, value: ExpressionRef): ExpressionRef
    fun store32(offset: Number, ptr: ExpressionRef, value: ExpressionRef): ExpressionRef
    var rmw: `T$7`
    var rmw8_u: `T$7`
    var rmw16_u: `T$7`
    var rmw32_u: `T$7`
    fun wait(ptr: ExpressionRef, expected: ExpressionRef, timeout: ExpressionRef): ExpressionRef
}

internal external interface `T$11` {
    fun load(offset: Number, align: Number, ptr: ExpressionRef): ExpressionRef
    fun load8_s(offset: Number, align: Number, ptr: ExpressionRef): ExpressionRef
    fun load8_u(offset: Number, align: Number, ptr: ExpressionRef): ExpressionRef
    fun load16_s(offset: Number, align: Number, ptr: ExpressionRef): ExpressionRef
    fun load16_u(offset: Number, align: Number, ptr: ExpressionRef): ExpressionRef
    fun load32_s(offset: Number, align: Number, ptr: ExpressionRef): ExpressionRef
    fun load32_u(offset: Number, align: Number, ptr: ExpressionRef): ExpressionRef
    fun store(offset: Number, align: Number, ptr: ExpressionRef, value: ExpressionRef): ExpressionRef
    fun store8(offset: Number, align: Number, ptr: ExpressionRef, value: ExpressionRef): ExpressionRef
    fun store16(offset: Number, align: Number, ptr: ExpressionRef, value: ExpressionRef): ExpressionRef
    fun store32(offset: Number, align: Number, ptr: ExpressionRef, value: ExpressionRef): ExpressionRef
    fun const(low: Number, high: Number): ExpressionRef
    fun clz(value: ExpressionRef): ExpressionRef
    fun ctz(value: ExpressionRef): ExpressionRef
    fun popcnt(value: ExpressionRef): ExpressionRef
    fun eqz(value: ExpressionRef): ExpressionRef
    var trunc_s: `T$6`
    var trunc_u: `T$6`
    var trunc_s_sat: `T$6`
    var trunc_u_sat: `T$6`
    fun reinterpret(value: ExpressionRef): ExpressionRef
    fun extend8_s(value: ExpressionRef): ExpressionRef
    fun extend16_s(value: ExpressionRef): ExpressionRef
    fun extend32_s(value: ExpressionRef): ExpressionRef
    fun extend_s(value: ExpressionRef): ExpressionRef
    fun extend_u(value: ExpressionRef): ExpressionRef
    fun add(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun sub(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun mul(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun div_s(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun div_u(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun rem_s(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun rem_u(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun and(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun or(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun xor(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun shl(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun shr_u(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun shr_s(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun rotl(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun rotr(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun eq(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun ne(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun lt_s(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun lt_u(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun le_s(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun le_u(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun gt_s(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun gt_u(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun ge_s(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun ge_u(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    var atomic: `T$10`
    fun pop(): ExpressionRef
}

internal external interface `T$12` {
    fun load(offset: Number, align: Number, ptr: ExpressionRef): ExpressionRef
    fun store(offset: Number, align: Number, ptr: ExpressionRef, value: ExpressionRef): ExpressionRef
    fun const(value: Number): ExpressionRef
    fun const_bits(value: Number): ExpressionRef
    fun neg(value: ExpressionRef): ExpressionRef
    fun abs(value: ExpressionRef): ExpressionRef
    fun ceil(value: ExpressionRef): ExpressionRef
    fun floor(value: ExpressionRef): ExpressionRef
    fun trunc(value: ExpressionRef): ExpressionRef
    fun nearest(value: ExpressionRef): ExpressionRef
    fun sqrt(value: ExpressionRef): ExpressionRef
    fun reinterpret(value: ExpressionRef): ExpressionRef
    var convert_s: `T$6`
    var convert_u: `T$6`
    fun demote(value: ExpressionRef): ExpressionRef
    fun add(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun sub(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun mul(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun div(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun copysign(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun min(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun max(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun eq(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun ne(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun lt(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun le(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun gt(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun ge(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun pop(): ExpressionRef
}

internal external interface `T$13` {
    fun load(offset: Number, align: Number, ptr: ExpressionRef): ExpressionRef
    fun store(offset: Number, align: Number, ptr: ExpressionRef, value: ExpressionRef): ExpressionRef
    fun const(value: Number): ExpressionRef
    fun const_bits(low: Number, high: Number): ExpressionRef
    fun neg(value: ExpressionRef): ExpressionRef
    fun abs(value: ExpressionRef): ExpressionRef
    fun ceil(value: ExpressionRef): ExpressionRef
    fun floor(value: ExpressionRef): ExpressionRef
    fun trunc(value: ExpressionRef): ExpressionRef
    fun nearest(value: ExpressionRef): ExpressionRef
    fun sqrt(value: ExpressionRef): ExpressionRef
    fun reinterpret(value: ExpressionRef): ExpressionRef
    var convert_s: `T$6`
    var convert_u: `T$6`
    fun promote(value: ExpressionRef): ExpressionRef
    fun add(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun sub(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun mul(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun div(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun copysign(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun min(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun max(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun eq(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun ne(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun lt(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun le(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun gt(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun ge(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun pop(): ExpressionRef
}

internal external interface `T$14` {
    fun load(offset: Number, align: Number, ptr: ExpressionRef): ExpressionRef
    fun store(offset: Number, align: Number, ptr: ExpressionRef, value: ExpressionRef): ExpressionRef
    fun const(value: Number): ExpressionRef
    fun not(value: ExpressionRef): ExpressionRef
    fun and(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun or(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun xor(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun andnot(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun bitselect(left: ExpressionRef, right: ExpressionRef, cond: ExpressionRef): ExpressionRef
    fun pop(): ExpressionRef
}

internal external interface `T$15` {
    fun splat(value: ExpressionRef): ExpressionRef
    fun extract_lane_s(vec: ExpressionRef, index: ExpressionRef): ExpressionRef
    fun extract_lane_u(vec: ExpressionRef, index: ExpressionRef): ExpressionRef
    fun replace_lane(vec: ExpressionRef, index: ExpressionRef, value: ExpressionRef): ExpressionRef
    fun eq(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun ne(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun lt_s(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun lt_u(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun gt_s(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun gt_u(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun le_s(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun le_u(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun ge_s(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun ge_u(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun neg(value: ExpressionRef): ExpressionRef
    fun any_true(value: ExpressionRef): ExpressionRef
    fun all_true(value: ExpressionRef): ExpressionRef
    fun shl(vec: ExpressionRef, shift: ExpressionRef): ExpressionRef
    fun shr_s(vec: ExpressionRef, shift: ExpressionRef): ExpressionRef
    fun shr_u(vec: ExpressionRef, shift: ExpressionRef): ExpressionRef
    fun add(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun add_saturate_s(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun add_saturate_u(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun sub(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun sub_saturate_s(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun sub_saturate_u(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun mul(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun min_s(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun min_u(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun max_s(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun max_u(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun avgr_u(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun narrow_i16x8_s(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun narrow_i16x8_u(left: ExpressionRef, right: ExpressionRef): ExpressionRef
}

internal external interface `T$16` {
    fun splat(value: ExpressionRef): ExpressionRef
    fun extract_lane_s(vec: ExpressionRef, index: ExpressionRef): ExpressionRef
    fun extract_lane_u(vec: ExpressionRef, index: ExpressionRef): ExpressionRef
    fun replace_lane(vec: ExpressionRef, index: ExpressionRef, value: ExpressionRef): ExpressionRef
    fun eq(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun ne(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun lt_s(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun lt_u(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun gt_s(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun gt_u(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun le_s(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun le_u(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun ge_s(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun ge_u(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun neg(value: ExpressionRef): ExpressionRef
    fun any_true(value: ExpressionRef): ExpressionRef
    fun all_true(value: ExpressionRef): ExpressionRef
    fun shl(vec: ExpressionRef, shift: ExpressionRef): ExpressionRef
    fun shr_s(vec: ExpressionRef, shift: ExpressionRef): ExpressionRef
    fun shr_u(vec: ExpressionRef, shift: ExpressionRef): ExpressionRef
    fun add(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun add_saturate_s(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun add_saturate_u(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun sub(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun sub_saturate_s(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun sub_saturate_u(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun mul(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun min_s(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun min_u(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun max_s(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun max_u(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun avgr_u(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun narrow_i32x4_s(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun narrow_i32x4_u(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun widen_low_i8x16_s(value: ExpressionRef): ExpressionRef
    fun widen_high_i8x16_s(value: ExpressionRef): ExpressionRef
    fun widen_low_i8x16_u(value: ExpressionRef): ExpressionRef
    fun widen_high_i8x16_u(value: ExpressionRef): ExpressionRef
    fun load8x8_s(offset: Number, align: Number, ptr: ExpressionRef): ExpressionRef
    fun load8x8_u(offset: Number, align: Number, ptr: ExpressionRef): ExpressionRef
}

internal external interface `T$17` {
    fun splat(value: ExpressionRef): ExpressionRef
    fun extract_lane(vec: ExpressionRef, index: ExpressionRef): ExpressionRef
    fun replace_lane(vec: ExpressionRef, index: ExpressionRef, value: ExpressionRef): ExpressionRef
    fun eq(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun ne(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun lt_s(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun lt_u(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun gt_s(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun gt_u(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun le_s(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun le_u(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun ge_s(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun ge_u(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun neg(value: ExpressionRef): ExpressionRef
    fun any_true(value: ExpressionRef): ExpressionRef
    fun all_true(value: ExpressionRef): ExpressionRef
    fun shl(vec: ExpressionRef, shift: ExpressionRef): ExpressionRef
    fun shr_s(vec: ExpressionRef, shift: ExpressionRef): ExpressionRef
    fun shr_u(vec: ExpressionRef, shift: ExpressionRef): ExpressionRef
    fun add(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun sub(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun mul(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun trunc_sat_f32x4_s(value: ExpressionRef): ExpressionRef
    fun trunc_sat_f32x4_u(value: ExpressionRef): ExpressionRef
    fun widen_low_i16x8_s(value: ExpressionRef): ExpressionRef
    fun widen_high_i16x8_s(value: ExpressionRef): ExpressionRef
    fun widen_low_i16x8_u(value: ExpressionRef): ExpressionRef
    fun widen_high_i16x8_u(value: ExpressionRef): ExpressionRef
    fun load16x4_s(offset: Number, align: Number, ptr: ExpressionRef): ExpressionRef
    fun load16x4_u(offset: Number, align: Number, ptr: ExpressionRef): ExpressionRef
}

internal external interface `T$18` {
    fun splat(value: ExpressionRef): ExpressionRef
    fun extract_lane(vec: ExpressionRef, index: ExpressionRef): ExpressionRef
    fun replace_lane(vec: ExpressionRef, index: ExpressionRef, value: ExpressionRef): ExpressionRef
    fun neg(value: ExpressionRef): ExpressionRef
    fun any_true(value: ExpressionRef): ExpressionRef
    fun all_true(value: ExpressionRef): ExpressionRef
    fun shl(vec: ExpressionRef, shift: ExpressionRef): ExpressionRef
    fun shr_s(vec: ExpressionRef, shift: ExpressionRef): ExpressionRef
    fun shr_u(vec: ExpressionRef, shift: ExpressionRef): ExpressionRef
    fun add(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun sub(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun trunc_sat_f64x2_s(value: ExpressionRef): ExpressionRef
    fun trunc_sat_f64x2_u(value: ExpressionRef): ExpressionRef
    fun load32x2_s(offset: Number, align: Number, ptr: ExpressionRef): ExpressionRef
    fun load32x2_u(offset: Number, align: Number, ptr: ExpressionRef): ExpressionRef
}

internal external interface `T$19` {
    fun splat(value: ExpressionRef): ExpressionRef
    fun extract_lane(vec: ExpressionRef, index: ExpressionRef): ExpressionRef
    fun replace_lane(vec: ExpressionRef, index: ExpressionRef, value: ExpressionRef): ExpressionRef
    fun eq(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun ne(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun lt(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun gt(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun le(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun ge(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun abs(value: ExpressionRef): ExpressionRef
    fun neg(value: ExpressionRef): ExpressionRef
    fun sqrt(value: ExpressionRef): ExpressionRef
    fun qfma(a: ExpressionRef, b: ExpressionRef, c: ExpressionRef): ExpressionRef
    fun qfms(a: ExpressionRef, b: ExpressionRef, c: ExpressionRef): ExpressionRef
    fun add(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun sub(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun mul(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun div(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun min(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun max(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun convert_i32x4_s(value: ExpressionRef): ExpressionRef
    fun convert_i32x4_u(value: ExpressionRef): ExpressionRef
}

internal external interface `T$20` {
    fun shuffle(left: ExpressionRef, right: ExpressionRef, mask: Array<Number>): ExpressionRef
    fun swizzle(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun load_splat(offset: Number, align: Number, ptr: ExpressionRef): ExpressionRef
}

internal external interface `T$21` {
    fun load_splat(offset: Number, align: Number, ptr: ExpressionRef): ExpressionRef
}

internal external interface `T$22` {
    fun pop(): ExpressionRef
}

internal external interface `T$23` {
    fun `null`(): ExpressionRef
    fun is_null(value: ExpressionRef): ExpressionRef
    fun func(name: String): ExpressionRef
}

internal external interface `T$24` {
    fun notify(ptr: ExpressionRef, notifyCount: ExpressionRef): ExpressionRef
    fun fence(): ExpressionRef
}

internal external interface `T$25` {
    fun make(elements: Array<ExportRef>): ExpressionRef
    fun extract(tuple: ExpressionRef, index: Number): ExpressionRef
}

internal external interface `T$26` {
    var imported: Boolean
    var segments: Array<TableElement>
}

internal external interface `T$27` {
    var binary: Uint8Array
    var sourceMap: String?
}

internal open external class Module {
    open var ptr: Number
    open fun block(label: String, children: Array<ExpressionRef>, resultType: Type = definedExternally): ExpressionRef
    open fun `if`(
        condition: ExpressionRef,
        ifTrue: ExpressionRef,
        ifFalse: ExpressionRef = definedExternally
    ): ExpressionRef

    open fun loop(label: String, body: ExpressionRef): ExpressionRef
    open fun br(
        label: String,
        condition: ExpressionRef = definedExternally,
        value: ExpressionRef = definedExternally
    ): ExpressionRef

    open fun br_if(
        label: String,
        condition: ExpressionRef = definedExternally,
        value: ExpressionRef = definedExternally
    ): ExpressionRef

    open fun switch(
        labels: Array<String>,
        defaultLabel: String,
        condition: ExpressionRef,
        value: ExpressionRef = definedExternally
    ): ExpressionRef

    open fun call(name: String, operands: Array<ExpressionRef>, returnType: Type): ExpressionRef
    open fun return_call(name: String, operands: Array<ExpressionRef>, returnType: Type): ExpressionRef
    open fun call_indirect(
        target: ExpressionRef,
        operands: Array<ExpressionRef>,
        params: Type,
        results: Type
    ): ExpressionRef

    open fun return_call_indirect(
        target: ExpressionRef,
        operands: Array<ExpressionRef>,
        params: Type,
        results: Type
    ): ExpressionRef

    open var local: `T$2`
    open var global: `T$3`
    open var memory: `T$4`
    open var data: `T$5`
    open var i32: `T$9`
    open var i64: `T$11`
    open var f32: `T$12`
    open var f64: `T$13`
    open var v128: `T$14`
    open var i8x16: `T$15`
    open var i16x8: `T$16`
    open var i32x4: `T$17`
    open var i64x2: `T$18`
    open var f32x4: `T$19`
    open var f64x2: `T$19`
    open var v8x16: `T$20`
    open var v16x8: `T$21`
    open var v32x4: `T$21`
    open var v64x2: `T$21`
    open var funcref: `T$22`
    open var anyref: `T$22`
    open var nullref: `T$22`
    open var exnref: `T$22`
    open var ref: `T$23`
    open var atomic: `T$24`
    open var tuple: `T$25`
    open fun `try`(body: ExpressionRef, catchBody: ExpressionRef): ExpressionRef
    open fun `throw`(event: String, operands: Array<ExpressionRef>): ExpressionRef
    open fun rethrow(exnref: ExpressionRef): ExpressionRef
    open fun br_on_exn(label: String, event: String, exnref: ExpressionRef): ExpressionRef
    open fun push(value: ExpressionRef): ExpressionRef
    open fun select(
        condition: ExpressionRef,
        ifTrue: ExpressionRef,
        ifFalse: ExpressionRef,
        type: Type = definedExternally
    ): ExpressionRef

    open fun drop(value: ExpressionRef): ExpressionRef
    open fun `return`(value: ExpressionRef = definedExternally): ExpressionRef
    open fun host(op: Operations, name: String, operands: Array<ExpressionRef>): ExpressionRef
    open fun nop(): ExpressionRef
    open fun unreachable(): ExpressionRef
    open fun addFunction(name: String, params: Type, results: Type, vars: Array<Type>, body: ExpressionRef): FunctionRef
    open fun getFunction(name: String): FunctionRef
    open fun removeFunction(name: String)
    open fun getNumFunctions(): Number
    open fun getFunctionByIndex(index: Number): FunctionRef
    open fun addGlobal(name: String, type: Type, mutable: Boolean, init: ExpressionRef): GlobalRef
    open fun getGlobal(name: String): GlobalRef
    open fun removeGlobal(name: String)
    open fun addEvent(name: String, attribute: Number, params: Type, results: Type): EventRef
    open fun getEvent(name: String): EventRef
    open fun removeEvent(name: String)
    open fun addFunctionImport(
        internalName: String,
        externalModuleName: String,
        externalBaseName: String,
        params: Type,
        results: Type
    )

    open fun addTableImport(internalName: String, externalModuleName: String, externalBaseName: String)
    open fun addMemoryImport(internalName: String, externalModuleName: String, externalBaseName: String)
    open fun addGlobalImport(
        internalName: String,
        externalModuleName: String,
        externalBaseName: String,
        globalType: Type
    )

    open fun addEventImport(
        internalName: String,
        externalModuleName: String,
        externalBaseName: String,
        attribute: Number,
        params: Type,
        results: Type
    )

    open fun addFunctionExport(internalName: String, externalName: String): ExportRef
    open fun addTableExport(internalName: String, externalName: String): ExportRef
    open fun addMemoryExport(internalName: String, externalName: String): ExportRef
    open fun addGlobalExport(internalName: String, externalName: String): ExportRef
    open fun removeExport(externalName: String)
    open fun getNumExports(): Number
    open fun getExportByIndex(index: Number): ExportRef
    open fun setFunctionTable(
        initial: Number,
        maximum: Number,
        funcNames: Array<Number>,
        offset: ExpressionRef = definedExternally
    )

    open fun getFunctionTable(): `T$26`
    open fun setMemory(
        initial: Number,
        maximum: Number,
        exportName: String? = definedExternally,
        segments: Array<MemorySegment>? = definedExternally,
        flags: Array<Number>? = definedExternally,
        shared: Boolean = definedExternally
    )

    open fun getNumMemorySegments(): Number
    open fun getMemorySegmentInfoByIndex(index: Number): MemorySegmentInfo
    open fun setStart(start: FunctionRef)
    open fun getFeatures(): Features
    open fun setFeatures(features: Features)
    open fun addCustomSection(name: String, contents: Uint8Array)
    open fun emitText(): String
    open fun emitStackIR(optimize: Boolean = definedExternally): String
    open fun emitAsmjs(): String
    open fun validate(): Number
    open fun optimize()
    open fun optimizeFunction(func: String)
    open fun optimizeFunction(func: FunctionRef)
    open fun runPasses(passes: Array<String>)
    open fun runPassesOnFunction(func: String, passes: Array<String>)
    open fun runPassesOnFunction(func: FunctionRef, passes: Array<String>)
    open fun autoDrop()
    open fun dispose()
    open fun emitBinary(): Uint8Array
    open fun emitBinary(sourceMapUrl: String?): `T$27`
    open fun interpret()
    open fun addDebugInfoFileName(filename: String): Number
    open fun getDebugInfoFileName(index: Number): String?
    open fun setDebugLocation(
        func: FunctionRef,
        expr: ExpressionRef,
        fileIndex: Number,
        lineNumber: Number,
        columnNumber: Number
    )

    open fun copyExpression(expr: ExpressionRef): ExpressionRef
}

internal external interface MemorySegment {
    var offset: ExpressionRef
    var data: Uint8Array
    var passive: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

internal external interface TableElement {
    var offset: ExpressionRef
    var names: Array<String>
}

internal external fun wrapModule(ptr: Number): Module

internal external fun getExpressionId(expression: ExpressionRef): Number

internal external fun getExpressionType(expression: ExpressionRef): Type

internal external fun getExpressionInfo(expression: ExpressionRef): ExpressionInfo

internal external interface MemorySegmentInfo {
    var offset: ExpressionRef
    var data: Uint8Array
    var passive: Boolean
}

internal external interface ExpressionInfo {
    var id: ExpressionIds
    var type: Type
}

internal external interface BlockInfo : ExpressionInfo {
    var name: String
    var children: Array<ExpressionRef>
}

internal external interface IfInfo : ExpressionInfo {
    var condition: ExpressionRef
    var ifTrue: ExpressionRef
    var ifFalse: ExpressionRef
}

internal external interface LoopInfo : ExpressionInfo {
    var name: String
    var body: ExpressionRef
}

internal external interface BreakInfo : ExpressionInfo {
    var name: String
    var condition: ExpressionRef
    var value: ExpressionRef
}

internal external interface SwitchInfo : ExpressionInfo {
    var names: Array<String>
    var defaultName: String?
    var condition: ExpressionRef
    var value: ExpressionRef
}

internal external interface CallInfo : ExpressionInfo {
    var isReturn: Boolean
    var target: String
    var operands: Array<ExpressionRef>
}

internal external interface CallIndirectInfo : ExpressionInfo {
    var isReturn: Boolean
    var target: ExpressionRef
    var operands: Array<ExpressionRef>
}

internal external interface LocalGetInfo : ExpressionInfo {
    var index: Number
}

internal external interface LocalSetInfo : ExpressionInfo {
    var isTee: Boolean
    var index: Number
    var value: ExpressionRef
}

internal external interface GlobalGetInfo : ExpressionInfo {
    var name: String
}

internal external interface GlobalSetInfo : ExpressionInfo {
    var name: String
    var value: ExpressionRef
}

internal external interface LoadInfo : ExpressionInfo {
    var isAtomic: Boolean
    var isSigned: Boolean
    var offset: Number
    var bytes: Number
    var align: Number
    var ptr: ExpressionRef
}

internal external interface StoreInfo : ExpressionInfo {
    var isAtomic: Boolean
    var offset: Number
    var bytes: Number
    var align: Number
    var ptr: ExpressionRef
    var value: ExpressionRef
}

internal external interface `T$28` {
    var low: Number
    var high: Number
}

internal external interface ConstInfo : ExpressionInfo {
    var value: dynamic /* Number | `T$28` */
        get() = definedExternally
        set(value) = definedExternally
}

internal external interface UnaryInfo : ExpressionInfo {
    var op: Operations
    var value: ExpressionRef
}

internal external interface BinaryInfo : ExpressionInfo {
    var op: Operations
    var left: ExpressionRef
    var right: ExpressionRef
}

internal external interface SelectInfo : ExpressionInfo {
    var ifTrue: ExpressionRef
    var ifFalse: ExpressionRef
    var condition: ExpressionRef
}

internal external interface DropInfo : ExpressionInfo {
    var value: ExpressionRef
}

internal external interface ReturnInfo : ExpressionInfo {
    var value: ExpressionRef
}

internal external interface NopInfo : ExpressionInfo

internal external interface UnreachableInfo : ExpressionInfo

internal external interface HostInfo : ExpressionInfo {
    var op: Operations
    var nameOperand: String?
    var operands: Array<ExpressionRef>
}

internal external interface AtomicRMWInfo : ExpressionInfo {
    var op: Operations
    var bytes: Number
    var offset: Number
    var ptr: ExpressionRef
    var value: ExpressionRef
}

internal external interface AtomicCmpxchgInfo : ExpressionInfo {
    var bytes: Number
    var offset: Number
    var ptr: ExpressionRef
    var expected: ExpressionRef
    var replacement: ExpressionRef
}

internal external interface AtomicWaitInfo : ExpressionInfo {
    var ptr: ExpressionRef
    var expected: ExpressionRef
    var timeout: ExpressionRef
    var expectedType: Type
}

internal external interface AtomicNotifyInfo : ExpressionInfo {
    var ptr: ExpressionRef
    var notifyCount: ExpressionRef
}

internal external interface AtomicFenceInfo : ExpressionInfo {
    var order: Number
}

internal external interface SIMDExtractInfo : ExpressionInfo {
    var op: Operations
    var vec: ExpressionRef
    var index: ExpressionRef
}

internal external interface SIMDReplaceInfo : ExpressionInfo {
    var op: Operations
    var vec: ExpressionRef
    var index: ExpressionRef
    var value: ExpressionRef
}

internal external interface SIMDShuffleInfo : ExpressionInfo {
    var left: ExpressionRef
    var right: ExpressionRef
    var mask: Array<Number>
}

internal external interface SIMDTernaryInfo : ExpressionInfo {
    var op: Operations
    var a: ExpressionRef
    var b: ExpressionRef
    var c: ExpressionRef
}

internal external interface SIMDShiftInfo : ExpressionInfo {
    var op: Operations
    var vec: ExpressionRef
    var shift: ExpressionRef
}

internal external interface SIMDLoadInfo : ExpressionInfo {
    var op: Operations
    var offset: Number
    var align: Number
    var ptr: ExpressionRef
}

internal external interface MemoryInitInfo : ExpressionInfo {
    var segment: Number
    var dest: ExpressionRef
    var offset: ExpressionRef
    var size: ExpressionRef
}

internal external interface MemoryDropInfo : ExpressionInfo {
    var segment: Number
}

internal external interface MemoryCopyInfo : ExpressionInfo {
    var dest: ExpressionRef
    var source: ExpressionRef
    var size: ExpressionRef
}

internal external interface MemoryFillInfo : ExpressionInfo {
    var dest: ExpressionRef
    var value: ExpressionRef
    var size: ExpressionRef
}

internal external interface RefNullInfo : ExpressionInfo

internal external interface RefIsNullInfo : ExpressionInfo {
    var value: ExpressionRef
}

internal external interface RefFuncInfo : ExpressionInfo {
    var func: String
}

internal external interface TryInfo : ExpressionInfo {
    var body: ExpressionRef
    var catchBody: ExpressionRef
}

internal external interface ThrowInfo : ExpressionInfo {
    var event: String
    var operands: Array<ExpressionRef>
}

internal external interface RethrowInfo : ExpressionInfo {
    var exnref: ExpressionRef
}

internal external interface BrOnExnInfo : ExpressionInfo {
    var name: String
    var event: String
    var exnref: ExpressionRef
}

internal external interface PopInfo : ExpressionInfo

internal external interface PushInfo : ExpressionInfo {
    var value: ExpressionRef
}

internal external fun getFunctionInfo(func: FunctionRef): FunctionInfo

internal external interface FunctionInfo {
    var name: String
    var module: String?
    var base: String?
    var params: Type
    var results: Type
    var vars: Array<Type>
    var body: ExpressionRef
}

internal external fun getGlobalInfo(global: GlobalRef): GlobalInfo

internal external interface GlobalInfo {
    var name: String
    var module: String?
    var base: String?
    var type: Type
    var mutable: Boolean
    var init: ExpressionRef
}

internal external fun getExportInfo(export_: ExportRef): ExportInfo

internal external interface ExportInfo {
    var kind: ExternalKinds
    var name: String
    var value: String
}

internal external fun getEventInfo(event: EventRef): EventInfo

internal external interface EventInfo {
    var name: String
    var module: String?
    var base: String?
    var attribute: Number
    var params: Type
    var results: Type
}

internal external fun getSideEffects(expr: ExpressionRef, features: Features): SideEffects

internal external enum class SideEffects {
    None,
    Branches,
    Calls,
    ReadsLocal,
    WritesLocal,
    ReadsGlobal,
    WritesGlobal,
    ReadsMemory,
    WritesMemory,
    ImplicitTrap,
    IsAtomic,
    Throws,
    Any
}

internal external fun emitText(expression: ExpressionRef): String

internal external fun emitText(expression: Module): String

internal external fun readBinary(data: Uint8Array): Module

internal external fun parseText(text: String): Module

internal external fun getOptimizeLevel(): Number

internal external fun setOptimizeLevel(level: Number): Number

internal external fun getShrinkLevel(): Number

internal external fun setShrinkLevel(level: Number): Number

internal external fun getDebugInfo(): Boolean

internal external fun setDebugInfo(on: Boolean)

internal external fun getLowMemoryUnused(): Boolean

internal external fun setLowMemoryUnused(on: Boolean)

internal external fun getPassArgument(key: String): String?

internal external fun setPassArgument(key: String, value: String?)

internal external fun clearPassArguments()

internal external fun getAlwaysInlineMaxSize(): Number

internal external fun setAlwaysInlineMaxSize(size: Number)

internal external fun getFlexibleInlineMaxSize(): Number

internal external fun setFlexibleInlineMaxSize(size: Number)

internal external fun getOneCallerInlineMaxSize(): Number

internal external fun setOneCallerInlineMaxSize(size: Number)

internal external fun exit(status: Number)

internal open external class Relooper(module: Module) {
    open fun addBlock(expression: ExpressionRef): RelooperBlockRef
    open fun addBranch(from: RelooperBlockRef, to: RelooperBlockRef, condition: ExpressionRef, code: ExpressionRef)
    open fun addBlockWithSwitch(code: ExpressionRef, condition: ExpressionRef): RelooperBlockRef
    open fun addBranchForSwitch(
        from: RelooperBlockRef,
        to: RelooperBlockRef,
        indexes: Array<Number>,
        code: ExpressionRef
    )

    open fun renderAndDispose(entry: RelooperBlockRef, labelHelper: Number): ExpressionRef
}
