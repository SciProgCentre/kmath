@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS",
    "PackageDirectoryMismatch",
    "NO_EXPLICIT_VISIBILITY_IN_API_MODE_WARNING",
    "KDocMissingDocumentation",
    "PropertyName",
    "ClassName",
)

@file:JsModule("binaryen")
@file:JsNonModule

package binaryen

import org.khronos.webgl.Uint8Array
import kotlin.js.Promise

external var isReady: Boolean

external var ready: Promise<Any>

external var none: Type

external var i32: Type

external var i64: Type

external var f32: Type

external var f64: Type

external var v128: Type

external var funcref: Type

external var anyref: Type

external var nullref: Type

external var exnref: Type

external var unreachable: Type

external var auto: Type

external fun createType(types: Array<Type>): Type

external fun expandType(type: Type): Array<Type>

external enum class ExpressionIds {
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

external var InvalidId: ExpressionIds

external var BlockId: ExpressionIds

external var IfId: ExpressionIds

external var LoopId: ExpressionIds

external var BreakId: ExpressionIds

external var SwitchId: ExpressionIds

external var CallId: ExpressionIds

external var CallIndirectId: ExpressionIds

external var LocalGetId: ExpressionIds

external var LocalSetId: ExpressionIds

external var GlobalGetId: ExpressionIds

external var GlobalSetId: ExpressionIds

external var LoadId: ExpressionIds

external var StoreId: ExpressionIds

external var ConstId: ExpressionIds

external var UnaryId: ExpressionIds

external var BinaryId: ExpressionIds

external var SelectId: ExpressionIds

external var DropId: ExpressionIds

external var ReturnId: ExpressionIds

external var HostId: ExpressionIds

external var NopId: ExpressionIds

external var UnreachableId: ExpressionIds

external var AtomicCmpxchgId: ExpressionIds

external var AtomicRMWId: ExpressionIds

external var AtomicWaitId: ExpressionIds

external var AtomicNotifyId: ExpressionIds

external var AtomicFenceId: ExpressionIds

external var SIMDExtractId: ExpressionIds

external var SIMDReplaceId: ExpressionIds

external var SIMDShuffleId: ExpressionIds

external var SIMDTernaryId: ExpressionIds

external var SIMDShiftId: ExpressionIds

external var SIMDLoadId: ExpressionIds

external var MemoryInitId: ExpressionIds

external var DataDropId: ExpressionIds

external var MemoryCopyId: ExpressionIds

external var MemoryFillId: ExpressionIds

external var RefNullId: ExpressionIds

external var RefIsNullId: ExpressionIds

external var RefFuncId: ExpressionIds

external var TryId: ExpressionIds

external var ThrowId: ExpressionIds

external var RethrowId: ExpressionIds

external var BrOnExnId: ExpressionIds

external var TupleMakeId: ExpressionIds

external var TupleExtractId: ExpressionIds

external var PushId: ExpressionIds

external var PopId: ExpressionIds

external enum class ExternalKinds {
    Function,
    Table,
    Memory,
    Global,
    Event
}

external var ExternalFunction: ExternalKinds

external var ExternalTable: ExternalKinds

external var ExternalMemory: ExternalKinds

external var ExternalGlobal: ExternalKinds

external var ExternalEvent: ExternalKinds

external enum class Features {
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

external enum class Operations {
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

external var ClzInt32: Operations

external var CtzInt32: Operations

external var PopcntInt32: Operations

external var NegFloat32: Operations

external var AbsFloat32: Operations

external var CeilFloat32: Operations

external var FloorFloat32: Operations

external var TruncFloat32: Operations

external var NearestFloat32: Operations

external var SqrtFloat32: Operations

external var EqZInt32: Operations

external var ClzInt64: Operations

external var CtzInt64: Operations

external var PopcntInt64: Operations

external var NegFloat64: Operations

external var AbsFloat64: Operations

external var CeilFloat64: Operations

external var FloorFloat64: Operations

external var TruncFloat64: Operations

external var NearestFloat64: Operations

external var SqrtFloat64: Operations

external var EqZInt64: Operations

external var ExtendSInt32: Operations

external var ExtendUInt32: Operations

external var WrapInt64: Operations

external var TruncSFloat32ToInt32: Operations

external var TruncSFloat32ToInt64: Operations

external var TruncUFloat32ToInt32: Operations

external var TruncUFloat32ToInt64: Operations

external var TruncSFloat64ToInt32: Operations

external var TruncSFloat64ToInt64: Operations

external var TruncUFloat64ToInt32: Operations

external var TruncUFloat64ToInt64: Operations

external var TruncSatSFloat32ToInt32: Operations

external var TruncSatSFloat32ToInt64: Operations

external var TruncSatUFloat32ToInt32: Operations

external var TruncSatUFloat32ToInt64: Operations

external var TruncSatSFloat64ToInt32: Operations

external var TruncSatSFloat64ToInt64: Operations

external var TruncSatUFloat64ToInt32: Operations

external var TruncSatUFloat64ToInt64: Operations

external var ReinterpretFloat32: Operations

external var ReinterpretFloat64: Operations

external var ConvertSInt32ToFloat32: Operations

external var ConvertSInt32ToFloat64: Operations

external var ConvertUInt32ToFloat32: Operations

external var ConvertUInt32ToFloat64: Operations

external var ConvertSInt64ToFloat32: Operations

external var ConvertSInt64ToFloat64: Operations

external var ConvertUInt64ToFloat32: Operations

external var ConvertUInt64ToFloat64: Operations

external var PromoteFloat32: Operations

external var DemoteFloat64: Operations

external var ReinterpretInt32: Operations

external var ReinterpretInt64: Operations

external var ExtendS8Int32: Operations

external var ExtendS16Int32: Operations

external var ExtendS8Int64: Operations

external var ExtendS16Int64: Operations

external var ExtendS32Int64: Operations

external var AddInt32: Operations

external var SubInt32: Operations

external var MulInt32: Operations

external var DivSInt32: Operations

external var DivUInt32: Operations

external var RemSInt32: Operations

external var RemUInt32: Operations

external var AndInt32: Operations

external var OrInt32: Operations

external var XorInt32: Operations

external var ShlInt32: Operations

external var ShrUInt32: Operations

external var ShrSInt32: Operations

external var RotLInt32: Operations

external var RotRInt32: Operations

external var EqInt32: Operations

external var NeInt32: Operations

external var LtSInt32: Operations

external var LtUInt32: Operations

external var LeSInt32: Operations

external var LeUInt32: Operations

external var GtSInt32: Operations

external var GtUInt32: Operations

external var GeSInt32: Operations

external var GeUInt32: Operations

external var AddInt64: Operations

external var SubInt64: Operations

external var MulInt64: Operations

external var DivSInt64: Operations

external var DivUInt64: Operations

external var RemSInt64: Operations

external var RemUInt64: Operations

external var AndInt64: Operations

external var OrInt64: Operations

external var XorInt64: Operations

external var ShlInt64: Operations

external var ShrUInt64: Operations

external var ShrSInt64: Operations

external var RotLInt64: Operations

external var RotRInt64: Operations

external var EqInt64: Operations

external var NeInt64: Operations

external var LtSInt64: Operations

external var LtUInt64: Operations

external var LeSInt64: Operations

external var LeUInt64: Operations

external var GtSInt64: Operations

external var GtUInt64: Operations

external var GeSInt64: Operations

external var GeUInt64: Operations

external var AddFloat32: Operations

external var SubFloat32: Operations

external var MulFloat32: Operations

external var DivFloat32: Operations

external var CopySignFloat32: Operations

external var MinFloat32: Operations

external var MaxFloat32: Operations

external var EqFloat32: Operations

external var NeFloat32: Operations

external var LtFloat32: Operations

external var LeFloat32: Operations

external var GtFloat32: Operations

external var GeFloat32: Operations

external var AddFloat64: Operations

external var SubFloat64: Operations

external var MulFloat64: Operations

external var DivFloat64: Operations

external var CopySignFloat64: Operations

external var MinFloat64: Operations

external var MaxFloat64: Operations

external var EqFloat64: Operations

external var NeFloat64: Operations

external var LtFloat64: Operations

external var LeFloat64: Operations

external var GtFloat64: Operations

external var GeFloat64: Operations

external var MemorySize: Operations

external var MemoryGrow: Operations

external var AtomicRMWAdd: Operations

external var AtomicRMWSub: Operations

external var AtomicRMWAnd: Operations

external var AtomicRMWOr: Operations

external var AtomicRMWXor: Operations

external var AtomicRMWXchg: Operations

external var SplatVecI8x16: Operations

external var ExtractLaneSVecI8x16: Operations

external var ExtractLaneUVecI8x16: Operations

external var ReplaceLaneVecI8x16: Operations

external var SplatVecI16x8: Operations

external var ExtractLaneSVecI16x8: Operations

external var ExtractLaneUVecI16x8: Operations

external var ReplaceLaneVecI16x8: Operations

external var SplatVecI32x4: Operations

external var ExtractLaneVecI32x4: Operations

external var ReplaceLaneVecI32x4: Operations

external var SplatVecI64x2: Operations

external var ExtractLaneVecI64x2: Operations

external var ReplaceLaneVecI64x2: Operations

external var SplatVecF32x4: Operations

external var ExtractLaneVecF32x4: Operations

external var ReplaceLaneVecF32x4: Operations

external var SplatVecF64x2: Operations

external var ExtractLaneVecF64x2: Operations

external var ReplaceLaneVecF64x2: Operations

external var EqVecI8x16: Operations

external var NeVecI8x16: Operations

external var LtSVecI8x16: Operations

external var LtUVecI8x16: Operations

external var GtSVecI8x16: Operations

external var GtUVecI8x16: Operations

external var LeSVecI8x16: Operations

external var LeUVecI8x16: Operations

external var GeSVecI8x16: Operations

external var GeUVecI8x16: Operations

external var EqVecI16x8: Operations

external var NeVecI16x8: Operations

external var LtSVecI16x8: Operations

external var LtUVecI16x8: Operations

external var GtSVecI16x8: Operations

external var GtUVecI16x8: Operations

external var LeSVecI16x8: Operations

external var LeUVecI16x8: Operations

external var GeSVecI16x8: Operations

external var GeUVecI16x8: Operations

external var EqVecI32x4: Operations

external var NeVecI32x4: Operations

external var LtSVecI32x4: Operations

external var LtUVecI32x4: Operations

external var GtSVecI32x4: Operations

external var GtUVecI32x4: Operations

external var LeSVecI32x4: Operations

external var LeUVecI32x4: Operations

external var GeSVecI32x4: Operations

external var GeUVecI32x4: Operations

external var EqVecF32x4: Operations

external var NeVecF32x4: Operations

external var LtVecF32x4: Operations

external var GtVecF32x4: Operations

external var LeVecF32x4: Operations

external var GeVecF32x4: Operations

external var EqVecF64x2: Operations

external var NeVecF64x2: Operations

external var LtVecF64x2: Operations

external var GtVecF64x2: Operations

external var LeVecF64x2: Operations

external var GeVecF64x2: Operations

external var NotVec128: Operations

external var AndVec128: Operations

external var OrVec128: Operations

external var XorVec128: Operations

external var AndNotVec128: Operations

external var BitselectVec128: Operations

external var NegVecI8x16: Operations

external var AnyTrueVecI8x16: Operations

external var AllTrueVecI8x16: Operations

external var ShlVecI8x16: Operations

external var ShrSVecI8x16: Operations

external var ShrUVecI8x16: Operations

external var AddVecI8x16: Operations

external var AddSatSVecI8x16: Operations

external var AddSatUVecI8x16: Operations

external var SubVecI8x16: Operations

external var SubSatSVecI8x16: Operations

external var SubSatUVecI8x16: Operations

external var MulVecI8x16: Operations

external var MinSVecI8x16: Operations

external var MinUVecI8x16: Operations

external var MaxSVecI8x16: Operations

external var MaxUVecI8x16: Operations

external var NegVecI16x8: Operations

external var AnyTrueVecI16x8: Operations

external var AllTrueVecI16x8: Operations

external var ShlVecI16x8: Operations

external var ShrSVecI16x8: Operations

external var ShrUVecI16x8: Operations

external var AddVecI16x8: Operations

external var AddSatSVecI16x8: Operations

external var AddSatUVecI16x8: Operations

external var SubVecI16x8: Operations

external var SubSatSVecI16x8: Operations

external var SubSatUVecI16x8: Operations

external var MulVecI16x8: Operations

external var MinSVecI16x8: Operations

external var MinUVecI16x8: Operations

external var MaxSVecI16x8: Operations

external var MaxUVecI16x8: Operations

external var DotSVecI16x8ToVecI32x4: Operations

external var NegVecI32x4: Operations

external var AnyTrueVecI32x4: Operations

external var AllTrueVecI32x4: Operations

external var ShlVecI32x4: Operations

external var ShrSVecI32x4: Operations

external var ShrUVecI32x4: Operations

external var AddVecI32x4: Operations

external var SubVecI32x4: Operations

external var MulVecI32x4: Operations

external var MinSVecI32x4: Operations

external var MinUVecI32x4: Operations

external var MaxSVecI32x4: Operations

external var MaxUVecI32x4: Operations

external var NegVecI64x2: Operations

external var AnyTrueVecI64x2: Operations

external var AllTrueVecI64x2: Operations

external var ShlVecI64x2: Operations

external var ShrSVecI64x2: Operations

external var ShrUVecI64x2: Operations

external var AddVecI64x2: Operations

external var SubVecI64x2: Operations

external var AbsVecF32x4: Operations

external var NegVecF32x4: Operations

external var SqrtVecF32x4: Operations

external var QFMAVecF32x4: Operations

external var QFMSVecF32x4: Operations

external var AddVecF32x4: Operations

external var SubVecF32x4: Operations

external var MulVecF32x4: Operations

external var DivVecF32x4: Operations

external var MinVecF32x4: Operations

external var MaxVecF32x4: Operations

external var AbsVecF64x2: Operations

external var NegVecF64x2: Operations

external var SqrtVecF64x2: Operations

external var QFMAVecF64x2: Operations

external var QFMSVecF64x2: Operations

external var AddVecF64x2: Operations

external var SubVecF64x2: Operations

external var MulVecF64x2: Operations

external var DivVecF64x2: Operations

external var MinVecF64x2: Operations

external var MaxVecF64x2: Operations

external var TruncSatSVecF32x4ToVecI32x4: Operations

external var TruncSatUVecF32x4ToVecI32x4: Operations

external var TruncSatSVecF64x2ToVecI64x2: Operations

external var TruncSatUVecF64x2ToVecI64x2: Operations

external var ConvertSVecI32x4ToVecF32x4: Operations

external var ConvertUVecI32x4ToVecF32x4: Operations

external var ConvertSVecI64x2ToVecF64x2: Operations

external var ConvertUVecI64x2ToVecF64x2: Operations

external var LoadSplatVec8x16: Operations

external var LoadSplatVec16x8: Operations

external var LoadSplatVec32x4: Operations

external var LoadSplatVec64x2: Operations

external var LoadExtSVec8x8ToVecI16x8: Operations

external var LoadExtUVec8x8ToVecI16x8: Operations

external var LoadExtSVec16x4ToVecI32x4: Operations

external var LoadExtUVec16x4ToVecI32x4: Operations

external var LoadExtSVec32x2ToVecI64x2: Operations

external var LoadExtUVec32x2ToVecI64x2: Operations

external var NarrowSVecI16x8ToVecI8x16: Operations

external var NarrowUVecI16x8ToVecI8x16: Operations

external var NarrowSVecI32x4ToVecI16x8: Operations

external var NarrowUVecI32x4ToVecI16x8: Operations

external var WidenLowSVecI8x16ToVecI16x8: Operations

external var WidenHighSVecI8x16ToVecI16x8: Operations

external var WidenLowUVecI8x16ToVecI16x8: Operations

external var WidenHighUVecI8x16ToVecI16x8: Operations

external var WidenLowSVecI16x8ToVecI32x4: Operations

external var WidenHighSVecI16x8ToVecI32x4: Operations

external var WidenLowUVecI16x8ToVecI32x4: Operations

external var WidenHighUVecI16x8ToVecI32x4: Operations

external var SwizzleVec8x16: Operations

external interface `T$2` {
    fun get(index: Number, type: Type): ExpressionRef
    fun set(index: Number, value: ExpressionRef): ExpressionRef
    fun tee(index: Number, value: ExpressionRef, type: Type): ExpressionRef
}

external interface `T$3` {
    fun get(name: String, type: Type): ExpressionRef
    fun set(name: String, value: ExpressionRef): ExpressionRef
}

external interface `T$4` {
    fun size(): ExpressionRef
    fun grow(value: ExpressionRef): ExpressionRef
    fun init(segment: Number, dest: ExpressionRef, offset: ExpressionRef, size: ExpressionRef): ExpressionRef
    fun copy(dest: ExpressionRef, source: ExpressionRef, size: ExpressionRef): ExpressionRef
    fun fill(dest: ExpressionRef, value: ExpressionRef, size: ExpressionRef): ExpressionRef
}

external interface `T$5` {
    fun drop(segment: Number): ExpressionRef
}

external interface `T$6` {
    fun f32(value: ExpressionRef): ExpressionRef
    fun f64(value: ExpressionRef): ExpressionRef
}

external interface `T$7` {
    fun add(offset: Number, ptr: ExpressionRef, value: ExpressionRef): ExpressionRef
    fun sub(offset: Number, ptr: ExpressionRef, value: ExpressionRef): ExpressionRef
    fun and(offset: Number, ptr: ExpressionRef, value: ExpressionRef): ExpressionRef
    fun or(offset: Number, ptr: ExpressionRef, value: ExpressionRef): ExpressionRef
    fun xor(offset: Number, ptr: ExpressionRef, value: ExpressionRef): ExpressionRef
    fun xchg(offset: Number, ptr: ExpressionRef, value: ExpressionRef): ExpressionRef
    fun cmpxchg(offset: Number, ptr: ExpressionRef, expected: ExpressionRef, replacement: ExpressionRef): ExpressionRef
}

external interface `T$8` {
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

external interface `T$9` {
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

external interface `T$10` {
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

external interface `T$11` {
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

external interface `T$12` {
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

external interface `T$13` {
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

external interface `T$14` {
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

external interface `T$15` {
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

external interface `T$16` {
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

external interface `T$17` {
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

external interface `T$18` {
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

external interface `T$19` {
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

external interface `T$20` {
    fun shuffle(left: ExpressionRef, right: ExpressionRef, mask: Array<Number>): ExpressionRef
    fun swizzle(left: ExpressionRef, right: ExpressionRef): ExpressionRef
    fun load_splat(offset: Number, align: Number, ptr: ExpressionRef): ExpressionRef
}

external interface `T$21` {
    fun load_splat(offset: Number, align: Number, ptr: ExpressionRef): ExpressionRef
}

external interface `T$22` {
    fun pop(): ExpressionRef
}

external interface `T$23` {
    fun `null`(): ExpressionRef
    fun is_null(value: ExpressionRef): ExpressionRef
    fun func(name: String): ExpressionRef
}

external interface `T$24` {
    fun notify(ptr: ExpressionRef, notifyCount: ExpressionRef): ExpressionRef
    fun fence(): ExpressionRef
}

external interface `T$25` {
    fun make(elements: Array<ExportRef>): ExpressionRef
    fun extract(tuple: ExpressionRef, index: Number): ExpressionRef
}

external interface `T$26` {
    var imported: Boolean
    var segments: Array<TableElement>
}

external interface `T$27` {
    var binary: Uint8Array
    var sourceMap: String?
}

open external class Module {
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

external interface MemorySegment {
    var offset: ExpressionRef
    var data: Uint8Array
    var passive: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface TableElement {
    var offset: ExpressionRef
    var names: Array<String>
}

external fun wrapModule(ptr: Number): Module

external fun getExpressionId(expression: ExpressionRef): Number

external fun getExpressionType(expression: ExpressionRef): Type

external fun getExpressionInfo(expression: ExpressionRef): ExpressionInfo

external interface MemorySegmentInfo {
    var offset: ExpressionRef
    var data: Uint8Array
    var passive: Boolean
}

external interface ExpressionInfo {
    var id: ExpressionIds
    var type: Type
}

external interface BlockInfo : ExpressionInfo {
    var name: String
    var children: Array<ExpressionRef>
}

external interface IfInfo : ExpressionInfo {
    var condition: ExpressionRef
    var ifTrue: ExpressionRef
    var ifFalse: ExpressionRef
}

external interface LoopInfo : ExpressionInfo {
    var name: String
    var body: ExpressionRef
}

external interface BreakInfo : ExpressionInfo {
    var name: String
    var condition: ExpressionRef
    var value: ExpressionRef
}

external interface SwitchInfo : ExpressionInfo {
    var names: Array<String>
    var defaultName: String?
    var condition: ExpressionRef
    var value: ExpressionRef
}

external interface CallInfo : ExpressionInfo {
    var isReturn: Boolean
    var target: String
    var operands: Array<ExpressionRef>
}

external interface CallIndirectInfo : ExpressionInfo {
    var isReturn: Boolean
    var target: ExpressionRef
    var operands: Array<ExpressionRef>
}

external interface LocalGetInfo : ExpressionInfo {
    var index: Number
}

external interface LocalSetInfo : ExpressionInfo {
    var isTee: Boolean
    var index: Number
    var value: ExpressionRef
}

external interface GlobalGetInfo : ExpressionInfo {
    var name: String
}

external interface GlobalSetInfo : ExpressionInfo {
    var name: String
    var value: ExpressionRef
}

external interface LoadInfo : ExpressionInfo {
    var isAtomic: Boolean
    var isSigned: Boolean
    var offset: Number
    var bytes: Number
    var align: Number
    var ptr: ExpressionRef
}

external interface StoreInfo : ExpressionInfo {
    var isAtomic: Boolean
    var offset: Number
    var bytes: Number
    var align: Number
    var ptr: ExpressionRef
    var value: ExpressionRef
}

external interface `T$28` {
    var low: Number
    var high: Number
}

external interface ConstInfo : ExpressionInfo {
    var value: dynamic /* Number | `T$28` */
        get() = definedExternally
        set(value) = definedExternally
}

external interface UnaryInfo : ExpressionInfo {
    var op: Operations
    var value: ExpressionRef
}

external interface BinaryInfo : ExpressionInfo {
    var op: Operations
    var left: ExpressionRef
    var right: ExpressionRef
}

external interface SelectInfo : ExpressionInfo {
    var ifTrue: ExpressionRef
    var ifFalse: ExpressionRef
    var condition: ExpressionRef
}

external interface DropInfo : ExpressionInfo {
    var value: ExpressionRef
}

external interface ReturnInfo : ExpressionInfo {
    var value: ExpressionRef
}

external interface NopInfo : ExpressionInfo

external interface UnreachableInfo : ExpressionInfo

external interface HostInfo : ExpressionInfo {
    var op: Operations
    var nameOperand: String?
    var operands: Array<ExpressionRef>
}

external interface AtomicRMWInfo : ExpressionInfo {
    var op: Operations
    var bytes: Number
    var offset: Number
    var ptr: ExpressionRef
    var value: ExpressionRef
}

external interface AtomicCmpxchgInfo : ExpressionInfo {
    var bytes: Number
    var offset: Number
    var ptr: ExpressionRef
    var expected: ExpressionRef
    var replacement: ExpressionRef
}

external interface AtomicWaitInfo : ExpressionInfo {
    var ptr: ExpressionRef
    var expected: ExpressionRef
    var timeout: ExpressionRef
    var expectedType: Type
}

external interface AtomicNotifyInfo : ExpressionInfo {
    var ptr: ExpressionRef
    var notifyCount: ExpressionRef
}

external interface AtomicFenceInfo : ExpressionInfo {
    var order: Number
}

external interface SIMDExtractInfo : ExpressionInfo {
    var op: Operations
    var vec: ExpressionRef
    var index: ExpressionRef
}

external interface SIMDReplaceInfo : ExpressionInfo {
    var op: Operations
    var vec: ExpressionRef
    var index: ExpressionRef
    var value: ExpressionRef
}

external interface SIMDShuffleInfo : ExpressionInfo {
    var left: ExpressionRef
    var right: ExpressionRef
    var mask: Array<Number>
}

external interface SIMDTernaryInfo : ExpressionInfo {
    var op: Operations
    var a: ExpressionRef
    var b: ExpressionRef
    var c: ExpressionRef
}

external interface SIMDShiftInfo : ExpressionInfo {
    var op: Operations
    var vec: ExpressionRef
    var shift: ExpressionRef
}

external interface SIMDLoadInfo : ExpressionInfo {
    var op: Operations
    var offset: Number
    var align: Number
    var ptr: ExpressionRef
}

external interface MemoryInitInfo : ExpressionInfo {
    var segment: Number
    var dest: ExpressionRef
    var offset: ExpressionRef
    var size: ExpressionRef
}

external interface MemoryDropInfo : ExpressionInfo {
    var segment: Number
}

external interface MemoryCopyInfo : ExpressionInfo {
    var dest: ExpressionRef
    var source: ExpressionRef
    var size: ExpressionRef
}

external interface MemoryFillInfo : ExpressionInfo {
    var dest: ExpressionRef
    var value: ExpressionRef
    var size: ExpressionRef
}

external interface RefNullInfo : ExpressionInfo

external interface RefIsNullInfo : ExpressionInfo {
    var value: ExpressionRef
}

external interface RefFuncInfo : ExpressionInfo {
    var func: String
}

external interface TryInfo : ExpressionInfo {
    var body: ExpressionRef
    var catchBody: ExpressionRef
}

external interface ThrowInfo : ExpressionInfo {
    var event: String
    var operands: Array<ExpressionRef>
}

external interface RethrowInfo : ExpressionInfo {
    var exnref: ExpressionRef
}

external interface BrOnExnInfo : ExpressionInfo {
    var name: String
    var event: String
    var exnref: ExpressionRef
}

external interface PopInfo : ExpressionInfo

external interface PushInfo : ExpressionInfo {
    var value: ExpressionRef
}

external fun getFunctionInfo(func: FunctionRef): FunctionInfo

external interface FunctionInfo {
    var name: String
    var module: String?
    var base: String?
    var params: Type
    var results: Type
    var vars: Array<Type>
    var body: ExpressionRef
}

external fun getGlobalInfo(global: GlobalRef): GlobalInfo

external interface GlobalInfo {
    var name: String
    var module: String?
    var base: String?
    var type: Type
    var mutable: Boolean
    var init: ExpressionRef
}

external fun getExportInfo(export_: ExportRef): ExportInfo

external interface ExportInfo {
    var kind: ExternalKinds
    var name: String
    var value: String
}

external fun getEventInfo(event: EventRef): EventInfo

external interface EventInfo {
    var name: String
    var module: String?
    var base: String?
    var attribute: Number
    var params: Type
    var results: Type
}

external fun getSideEffects(expr: ExpressionRef, features: Features): SideEffects

external enum class SideEffects {
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

external fun emitText(expression: ExpressionRef): String

external fun emitText(expression: Module): String

external fun readBinary(data: Uint8Array): Module

external fun parseText(text: String): Module

external fun getOptimizeLevel(): Number

external fun setOptimizeLevel(level: Number): Number

external fun getShrinkLevel(): Number

external fun setShrinkLevel(level: Number): Number

external fun getDebugInfo(): Boolean

external fun setDebugInfo(on: Boolean)

external fun getLowMemoryUnused(): Boolean

external fun setLowMemoryUnused(on: Boolean)

external fun getPassArgument(key: String): String?

external fun setPassArgument(key: String, value: String?)

external fun clearPassArguments()

external fun getAlwaysInlineMaxSize(): Number

external fun setAlwaysInlineMaxSize(size: Number)

external fun getFlexibleInlineMaxSize(): Number

external fun setFlexibleInlineMaxSize(size: Number)

external fun getOneCallerInlineMaxSize(): Number

external fun setOneCallerInlineMaxSize(size: Number)

external fun exit(status: Number)

open external class Relooper(module: Module) {
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
