package kscience.kmath.ast

internal const val INITIAL = "(module\n" +
        "  (type \$t0 (func))\n" +
        "  (type \$t1 (func (param f64) (result f64)))\n" +
        "  (type \$t2 (func (param f64 f64) (result f64)))\n" +
        "  (type \$t3 (func (param i32 i32 i32 i32 i32) (result i32)))\n" +
        "  (type \$t4 (func (param f64 i32) (result i32)))\n" +
        "  (type \$t5 (func (param f64 f64 i32) (result f64)))\n" +
        "  (type \$t6 (func (param f64 i32) (result f64)))\n" +
        "  (type \$t7 (func (param i32 i32 i32) (result i32)))\n" +
        "  (func \$__wasm_call_ctors (type \$t0))\n" +
        "  (func \$acos (type \$t1) (param \$p0 f64) (result f64)\n" +
        "    (local \$l0 i64) (local \$l1 i32) (local \$l2 f64) (local \$l3 f64)\n" +
        "    block \$B0\n" +
        "      block \$B1\n" +
        "        block \$B2\n" +
        "          get_local \$p0\n" +
        "          i64.reinterpret/f64\n" +
        "          tee_local \$l0\n" +
        "          i64.const 32\n" +
        "          i64.shr_u\n" +
        "          i32.wrap/i64\n" +
        "          i32.const 2147483647\n" +
        "          i32.and\n" +
        "          tee_local \$l1\n" +
        "          i32.const 1072693248\n" +
        "          i32.lt_u\n" +
        "          br_if \$B2\n" +
        "          get_local \$l1\n" +
        "          i32.const -1072693248\n" +
        "          i32.add\n" +
        "          get_local \$l0\n" +
        "          i32.wrap/i64\n" +
        "          i32.or\n" +
        "          i32.eqz\n" +
        "          br_if \$B1\n" +
        "          f64.const 0x0p+0 (;=0;)\n" +
        "          get_local \$p0\n" +
        "          get_local \$p0\n" +
        "          f64.sub\n" +
        "          f64.div\n" +
        "          return\n" +
        "        end\n" +
        "        block \$B3\n" +
        "          block \$B4\n" +
        "            get_local \$l1\n" +
        "            i32.const 1071644671\n" +
        "            i32.gt_u\n" +
        "            br_if \$B4\n" +
        "            f64.const 0x1.921fb54442d18p+0 (;=1.5708;)\n" +
        "            set_local \$l2\n" +
        "            get_local \$l1\n" +
        "            i32.const 1012924417\n" +
        "            i32.lt_u\n" +
        "            br_if \$B3\n" +
        "            f64.const 0x1.1a62633145c07p-54 (;=6.12323e-17;)\n" +
        "            get_local \$p0\n" +
        "            get_local \$p0\n" +
        "            f64.mul\n" +
        "            tee_local \$l2\n" +
        "            get_local \$l2\n" +
        "            get_local \$l2\n" +
        "            get_local \$l2\n" +
        "            get_local \$l2\n" +
        "            get_local \$l2\n" +
        "            f64.const 0x1.23de10dfdf709p-15 (;=3.47933e-05;)\n" +
        "            f64.mul\n" +
        "            f64.const 0x1.9efe07501b288p-11 (;=0.000791535;)\n" +
        "            f64.add\n" +
        "            f64.mul\n" +
        "            f64.const -0x1.48228b5688f3bp-5 (;=-0.0400555;)\n" +
        "            f64.add\n" +
        "            f64.mul\n" +
        "            f64.const 0x1.9c1550e884455p-3 (;=0.201213;)\n" +
        "            f64.add\n" +
        "            f64.mul\n" +
        "            f64.const -0x1.4d61203eb6f7dp-2 (;=-0.325566;)\n" +
        "            f64.add\n" +
        "            f64.mul\n" +
        "            f64.const 0x1.5555555555555p-3 (;=0.166667;)\n" +
        "            f64.add\n" +
        "            f64.mul\n" +
        "            get_local \$l2\n" +
        "            get_local \$l2\n" +
        "            get_local \$l2\n" +
        "            get_local \$l2\n" +
        "            f64.const 0x1.3b8c5b12e9282p-4 (;=0.0770382;)\n" +
        "            f64.mul\n" +
        "            f64.const -0x1.6066c1b8d0159p-1 (;=-0.688284;)\n" +
        "            f64.add\n" +
        "            f64.mul\n" +
        "            f64.const 0x1.02ae59c598ac8p+1 (;=2.02095;)\n" +
        "            f64.add\n" +
        "            f64.mul\n" +
        "            f64.const -0x1.33a271c8a2d4bp+1 (;=-2.40339;)\n" +
        "            f64.add\n" +
        "            f64.mul\n" +
        "            f64.const 0x1p+0 (;=1;)\n" +
        "            f64.add\n" +
        "            f64.div\n" +
        "            get_local \$p0\n" +
        "            f64.mul\n" +
        "            f64.sub\n" +
        "            get_local \$p0\n" +
        "            f64.sub\n" +
        "            f64.const 0x1.921fb54442d18p+0 (;=1.5708;)\n" +
        "            f64.add\n" +
        "            return\n" +
        "          end\n" +
        "          get_local \$l0\n" +
        "          i64.const -1\n" +
        "          i64.le_s\n" +
        "          br_if \$B0\n" +
        "          f64.const 0x1p+0 (;=1;)\n" +
        "          get_local \$p0\n" +
        "          f64.sub\n" +
        "          f64.const 0x1p-1 (;=0.5;)\n" +
        "          f64.mul\n" +
        "          tee_local \$p0\n" +
        "          get_local \$p0\n" +
        "          f64.sqrt\n" +
        "          tee_local \$l3\n" +
        "          i64.reinterpret/f64\n" +
        "          i64.const -4294967296\n" +
        "          i64.and\n" +
        "          f64.reinterpret/i64\n" +
        "          tee_local \$l2\n" +
        "          get_local \$l2\n" +
        "          f64.mul\n" +
        "          f64.sub\n" +
        "          get_local \$l3\n" +
        "          get_local \$l2\n" +
        "          f64.add\n" +
        "          f64.div\n" +
        "          get_local \$l3\n" +
        "          get_local \$p0\n" +
        "          get_local \$p0\n" +
        "          get_local \$p0\n" +
        "          get_local \$p0\n" +
        "          get_local \$p0\n" +
        "          get_local \$p0\n" +
        "          f64.const 0x1.23de10dfdf709p-15 (;=3.47933e-05;)\n" +
        "          f64.mul\n" +
        "          f64.const 0x1.9efe07501b288p-11 (;=0.000791535;)\n" +
        "          f64.add\n" +
        "          f64.mul\n" +
        "          f64.const -0x1.48228b5688f3bp-5 (;=-0.0400555;)\n" +
        "          f64.add\n" +
        "          f64.mul\n" +
        "          f64.const 0x1.9c1550e884455p-3 (;=0.201213;)\n" +
        "          f64.add\n" +
        "          f64.mul\n" +
        "          f64.const -0x1.4d61203eb6f7dp-2 (;=-0.325566;)\n" +
        "          f64.add\n" +
        "          f64.mul\n" +
        "          f64.const 0x1.5555555555555p-3 (;=0.166667;)\n" +
        "          f64.add\n" +
        "          f64.mul\n" +
        "          get_local \$p0\n" +
        "          get_local \$p0\n" +
        "          get_local \$p0\n" +
        "          get_local \$p0\n" +
        "          f64.const 0x1.3b8c5b12e9282p-4 (;=0.0770382;)\n" +
        "          f64.mul\n" +
        "          f64.const -0x1.6066c1b8d0159p-1 (;=-0.688284;)\n" +
        "          f64.add\n" +
        "          f64.mul\n" +
        "          f64.const 0x1.02ae59c598ac8p+1 (;=2.02095;)\n" +
        "          f64.add\n" +
        "          f64.mul\n" +
        "          f64.const -0x1.33a271c8a2d4bp+1 (;=-2.40339;)\n" +
        "          f64.add\n" +
        "          f64.mul\n" +
        "          f64.const 0x1p+0 (;=1;)\n" +
        "          f64.add\n" +
        "          f64.div\n" +
        "          f64.mul\n" +
        "          f64.add\n" +
        "          get_local \$l2\n" +
        "          f64.add\n" +
        "          tee_local \$p0\n" +
        "          get_local \$p0\n" +
        "          f64.add\n" +
        "          set_local \$l2\n" +
        "        end\n" +
        "        get_local \$l2\n" +
        "        return\n" +
        "      end\n" +
        "      f64.const 0x1.921fb54442d18p+1 (;=3.14159;)\n" +
        "      f64.const 0x0p+0 (;=0;)\n" +
        "      get_local \$l0\n" +
        "      i64.const 0\n" +
        "      i64.lt_s\n" +
        "      select\n" +
        "      return\n" +
        "    end\n" +
        "    f64.const 0x1.921fb54442d18p+0 (;=1.5708;)\n" +
        "    get_local \$p0\n" +
        "    f64.const 0x1p+0 (;=1;)\n" +
        "    f64.add\n" +
        "    f64.const 0x1p-1 (;=0.5;)\n" +
        "    f64.mul\n" +
        "    tee_local \$p0\n" +
        "    f64.sqrt\n" +
        "    tee_local \$l2\n" +
        "    get_local \$l2\n" +
        "    get_local \$p0\n" +
        "    get_local \$p0\n" +
        "    get_local \$p0\n" +
        "    get_local \$p0\n" +
        "    get_local \$p0\n" +
        "    get_local \$p0\n" +
        "    f64.const 0x1.23de10dfdf709p-15 (;=3.47933e-05;)\n" +
        "    f64.mul\n" +
        "    f64.const 0x1.9efe07501b288p-11 (;=0.000791535;)\n" +
        "    f64.add\n" +
        "    f64.mul\n" +
        "    f64.const -0x1.48228b5688f3bp-5 (;=-0.0400555;)\n" +
        "    f64.add\n" +
        "    f64.mul\n" +
        "    f64.const 0x1.9c1550e884455p-3 (;=0.201213;)\n" +
        "    f64.add\n" +
        "    f64.mul\n" +
        "    f64.const -0x1.4d61203eb6f7dp-2 (;=-0.325566;)\n" +
        "    f64.add\n" +
        "    f64.mul\n" +
        "    f64.const 0x1.5555555555555p-3 (;=0.166667;)\n" +
        "    f64.add\n" +
        "    f64.mul\n" +
        "    get_local \$p0\n" +
        "    get_local \$p0\n" +
        "    get_local \$p0\n" +
        "    get_local \$p0\n" +
        "    f64.const 0x1.3b8c5b12e9282p-4 (;=0.0770382;)\n" +
        "    f64.mul\n" +
        "    f64.const -0x1.6066c1b8d0159p-1 (;=-0.688284;)\n" +
        "    f64.add\n" +
        "    f64.mul\n" +
        "    f64.const 0x1.02ae59c598ac8p+1 (;=2.02095;)\n" +
        "    f64.add\n" +
        "    f64.mul\n" +
        "    f64.const -0x1.33a271c8a2d4bp+1 (;=-2.40339;)\n" +
        "    f64.add\n" +
        "    f64.mul\n" +
        "    f64.const 0x1p+0 (;=1;)\n" +
        "    f64.add\n" +
        "    f64.div\n" +
        "    f64.mul\n" +
        "    f64.const -0x1.1a62633145c07p-54 (;=-6.12323e-17;)\n" +
        "    f64.add\n" +
        "    f64.add\n" +
        "    f64.sub\n" +
        "    tee_local \$p0\n" +
        "    get_local \$p0\n" +
        "    f64.add)\n" +
        "  (func \$acosh (type \$t1) (param \$p0 f64) (result f64)\n" +
        "    (local \$l0 i32)\n" +
        "    block \$B0\n" +
        "      get_local \$p0\n" +
        "      i64.reinterpret/f64\n" +
        "      i64.const 52\n" +
        "      i64.shr_u\n" +
        "      i32.wrap/i64\n" +
        "      i32.const 2047\n" +
        "      i32.and\n" +
        "      tee_local \$l0\n" +
        "      i32.const 1023\n" +
        "      i32.gt_u\n" +
        "      br_if \$B0\n" +
        "      get_local \$p0\n" +
        "      f64.const -0x1p+0 (;=-1;)\n" +
        "      f64.add\n" +
        "      tee_local \$p0\n" +
        "      get_local \$p0\n" +
        "      get_local \$p0\n" +
        "      f64.mul\n" +
        "      get_local \$p0\n" +
        "      get_local \$p0\n" +
        "      f64.add\n" +
        "      f64.add\n" +
        "      f64.sqrt\n" +
        "      f64.add\n" +
        "      call \$log1p\n" +
        "      return\n" +
        "    end\n" +
        "    block \$B1\n" +
        "      get_local \$l0\n" +
        "      i32.const 1048\n" +
        "      i32.gt_u\n" +
        "      br_if \$B1\n" +
        "      get_local \$p0\n" +
        "      get_local \$p0\n" +
        "      f64.add\n" +
        "      f64.const -0x1p+0 (;=-1;)\n" +
        "      get_local \$p0\n" +
        "      get_local \$p0\n" +
        "      f64.mul\n" +
        "      f64.const -0x1p+0 (;=-1;)\n" +
        "      f64.add\n" +
        "      f64.sqrt\n" +
        "      get_local \$p0\n" +
        "      f64.add\n" +
        "      f64.div\n" +
        "      f64.add\n" +
        "      call \$log\n" +
        "      return\n" +
        "    end\n" +
        "    get_local \$p0\n" +
        "    call \$log\n" +
        "    f64.const 0x1.62e42fefa39efp-1 (;=0.693147;)\n" +
        "    f64.add)\n" +
        "  (func \$asin (type \$t1) (param \$p0 f64) (result f64)\n" +
        "    (local \$l0 i64) (local \$l1 i32) (local \$l2 f64) (local \$l3 f64) (local \$l4 f64)\n" +
        "    block \$B0\n" +
        "      block \$B1\n" +
        "        block \$B2\n" +
        "          block \$B3\n" +
        "            block \$B4\n" +
        "              get_local \$p0\n" +
        "              i64.reinterpret/f64\n" +
        "              tee_local \$l0\n" +
        "              i64.const 32\n" +
        "              i64.shr_u\n" +
        "              i32.wrap/i64\n" +
        "              i32.const 2147483647\n" +
        "              i32.and\n" +
        "              tee_local \$l1\n" +
        "              i32.const 1072693248\n" +
        "              i32.lt_u\n" +
        "              br_if \$B4\n" +
        "              get_local \$l1\n" +
        "              i32.const -1072693248\n" +
        "              i32.add\n" +
        "              get_local \$l0\n" +
        "              i32.wrap/i64\n" +
        "              i32.or\n" +
        "              i32.eqz\n" +
        "              br_if \$B3\n" +
        "              f64.const 0x0p+0 (;=0;)\n" +
        "              get_local \$p0\n" +
        "              get_local \$p0\n" +
        "              f64.sub\n" +
        "              f64.div\n" +
        "              return\n" +
        "            end\n" +
        "            block \$B5\n" +
        "              get_local \$l1\n" +
        "              i32.const 1071644671\n" +
        "              i32.gt_u\n" +
        "              br_if \$B5\n" +
        "              get_local \$l1\n" +
        "              i32.const -1048576\n" +
        "              i32.add\n" +
        "              i32.const 1044381696\n" +
        "              i32.ge_u\n" +
        "              br_if \$B2\n" +
        "              get_local \$p0\n" +
        "              return\n" +
        "            end\n" +
        "            f64.const 0x1p+0 (;=1;)\n" +
        "            get_local \$p0\n" +
        "            f64.abs\n" +
        "            f64.sub\n" +
        "            f64.const 0x1p-1 (;=0.5;)\n" +
        "            f64.mul\n" +
        "            tee_local \$p0\n" +
        "            get_local \$p0\n" +
        "            get_local \$p0\n" +
        "            get_local \$p0\n" +
        "            get_local \$p0\n" +
        "            get_local \$p0\n" +
        "            f64.const 0x1.23de10dfdf709p-15 (;=3.47933e-05;)\n" +
        "            f64.mul\n" +
        "            f64.const 0x1.9efe07501b288p-11 (;=0.000791535;)\n" +
        "            f64.add\n" +
        "            f64.mul\n" +
        "            f64.const -0x1.48228b5688f3bp-5 (;=-0.0400555;)\n" +
        "            f64.add\n" +
        "            f64.mul\n" +
        "            f64.const 0x1.9c1550e884455p-3 (;=0.201213;)\n" +
        "            f64.add\n" +
        "            f64.mul\n" +
        "            f64.const -0x1.4d61203eb6f7dp-2 (;=-0.325566;)\n" +
        "            f64.add\n" +
        "            f64.mul\n" +
        "            f64.const 0x1.5555555555555p-3 (;=0.166667;)\n" +
        "            f64.add\n" +
        "            f64.mul\n" +
        "            get_local \$p0\n" +
        "            get_local \$p0\n" +
        "            get_local \$p0\n" +
        "            get_local \$p0\n" +
        "            f64.const 0x1.3b8c5b12e9282p-4 (;=0.0770382;)\n" +
        "            f64.mul\n" +
        "            f64.const -0x1.6066c1b8d0159p-1 (;=-0.688284;)\n" +
        "            f64.add\n" +
        "            f64.mul\n" +
        "            f64.const 0x1.02ae59c598ac8p+1 (;=2.02095;)\n" +
        "            f64.add\n" +
        "            f64.mul\n" +
        "            f64.const -0x1.33a271c8a2d4bp+1 (;=-2.40339;)\n" +
        "            f64.add\n" +
        "            f64.mul\n" +
        "            f64.const 0x1p+0 (;=1;)\n" +
        "            f64.add\n" +
        "            f64.div\n" +
        "            set_local \$l2\n" +
        "            get_local \$p0\n" +
        "            f64.sqrt\n" +
        "            set_local \$l3\n" +
        "            get_local \$l1\n" +
        "            i32.const 1072640819\n" +
        "            i32.lt_u\n" +
        "            br_if \$B1\n" +
        "            f64.const 0x1.921fb54442d18p+0 (;=1.5708;)\n" +
        "            get_local \$l3\n" +
        "            get_local \$l3\n" +
        "            get_local \$l2\n" +
        "            f64.mul\n" +
        "            f64.add\n" +
        "            tee_local \$p0\n" +
        "            get_local \$p0\n" +
        "            f64.add\n" +
        "            f64.const -0x1.1a62633145c07p-54 (;=-6.12323e-17;)\n" +
        "            f64.add\n" +
        "            f64.sub\n" +
        "            set_local \$p0\n" +
        "            br \$B0\n" +
        "          end\n" +
        "          get_local \$p0\n" +
        "          f64.const 0x1.921fb54442d18p+0 (;=1.5708;)\n" +
        "          f64.mul\n" +
        "          f64.const 0x1p-120 (;=7.52316e-37;)\n" +
        "          f64.add\n" +
        "          return\n" +
        "        end\n" +
        "        get_local \$p0\n" +
        "        get_local \$p0\n" +
        "        f64.mul\n" +
        "        tee_local \$l3\n" +
        "        get_local \$l3\n" +
        "        get_local \$l3\n" +
        "        get_local \$l3\n" +
        "        get_local \$l3\n" +
        "        get_local \$l3\n" +
        "        f64.const 0x1.23de10dfdf709p-15 (;=3.47933e-05;)\n" +
        "        f64.mul\n" +
        "        f64.const 0x1.9efe07501b288p-11 (;=0.000791535;)\n" +
        "        f64.add\n" +
        "        f64.mul\n" +
        "        f64.const -0x1.48228b5688f3bp-5 (;=-0.0400555;)\n" +
        "        f64.add\n" +
        "        f64.mul\n" +
        "        f64.const 0x1.9c1550e884455p-3 (;=0.201213;)\n" +
        "        f64.add\n" +
        "        f64.mul\n" +
        "        f64.const -0x1.4d61203eb6f7dp-2 (;=-0.325566;)\n" +
        "        f64.add\n" +
        "        f64.mul\n" +
        "        f64.const 0x1.5555555555555p-3 (;=0.166667;)\n" +
        "        f64.add\n" +
        "        f64.mul\n" +
        "        get_local \$l3\n" +
        "        get_local \$l3\n" +
        "        get_local \$l3\n" +
        "        get_local \$l3\n" +
        "        f64.const 0x1.3b8c5b12e9282p-4 (;=0.0770382;)\n" +
        "        f64.mul\n" +
        "        f64.const -0x1.6066c1b8d0159p-1 (;=-0.688284;)\n" +
        "        f64.add\n" +
        "        f64.mul\n" +
        "        f64.const 0x1.02ae59c598ac8p+1 (;=2.02095;)\n" +
        "        f64.add\n" +
        "        f64.mul\n" +
        "        f64.const -0x1.33a271c8a2d4bp+1 (;=-2.40339;)\n" +
        "        f64.add\n" +
        "        f64.mul\n" +
        "        f64.const 0x1p+0 (;=1;)\n" +
        "        f64.add\n" +
        "        f64.div\n" +
        "        get_local \$p0\n" +
        "        f64.mul\n" +
        "        get_local \$p0\n" +
        "        f64.add\n" +
        "        return\n" +
        "      end\n" +
        "      f64.const 0x1.921fb54442d18p-1 (;=0.785398;)\n" +
        "      get_local \$l3\n" +
        "      i64.reinterpret/f64\n" +
        "      i64.const -4294967296\n" +
        "      i64.and\n" +
        "      f64.reinterpret/i64\n" +
        "      tee_local \$l4\n" +
        "      get_local \$l4\n" +
        "      f64.add\n" +
        "      f64.sub\n" +
        "      get_local \$l3\n" +
        "      get_local \$l3\n" +
        "      f64.add\n" +
        "      get_local \$l2\n" +
        "      f64.mul\n" +
        "      f64.const 0x1.1a62633145c07p-54 (;=6.12323e-17;)\n" +
        "      get_local \$p0\n" +
        "      get_local \$l4\n" +
        "      get_local \$l4\n" +
        "      f64.mul\n" +
        "      f64.sub\n" +
        "      get_local \$l3\n" +
        "      get_local \$l4\n" +
        "      f64.add\n" +
        "      f64.div\n" +
        "      tee_local \$p0\n" +
        "      get_local \$p0\n" +
        "      f64.add\n" +
        "      f64.sub\n" +
        "      f64.sub\n" +
        "      f64.sub\n" +
        "      f64.const 0x1.921fb54442d18p-1 (;=0.785398;)\n" +
        "      f64.add\n" +
        "      set_local \$p0\n" +
        "    end\n" +
        "    get_local \$p0\n" +
        "    f64.neg\n" +
        "    get_local \$p0\n" +
        "    get_local \$l0\n" +
        "    i64.const 0\n" +
        "    i64.lt_s\n" +
        "    select)\n" +
        "  (func \$asinh (type \$t1) (param \$p0 f64) (result f64)\n" +
        "    (local \$l0 i32) (local \$l1 i64) (local \$l2 i32) (local \$l3 f64)\n" +
        "    get_global \$g0\n" +
        "    i32.const 16\n" +
        "    i32.sub\n" +
        "    tee_local \$l0\n" +
        "    set_global \$g0\n" +
        "    get_local \$p0\n" +
        "    i64.reinterpret/f64\n" +
        "    tee_local \$l1\n" +
        "    i64.const 9223372036854775807\n" +
        "    i64.and\n" +
        "    f64.reinterpret/i64\n" +
        "    set_local \$p0\n" +
        "    block \$B0\n" +
        "      block \$B1\n" +
        "        get_local \$l1\n" +
        "        i64.const 52\n" +
        "        i64.shr_u\n" +
        "        i32.wrap/i64\n" +
        "        i32.const 2047\n" +
        "        i32.and\n" +
        "        tee_local \$l2\n" +
        "        i32.const 1049\n" +
        "        i32.lt_u\n" +
        "        br_if \$B1\n" +
        "        get_local \$p0\n" +
        "        call \$log\n" +
        "        f64.const 0x1.62e42fefa39efp-1 (;=0.693147;)\n" +
        "        f64.add\n" +
        "        set_local \$p0\n" +
        "        br \$B0\n" +
        "      end\n" +
        "      block \$B2\n" +
        "        get_local \$l2\n" +
        "        i32.const 1024\n" +
        "        i32.lt_u\n" +
        "        br_if \$B2\n" +
        "        get_local \$p0\n" +
        "        get_local \$p0\n" +
        "        f64.add\n" +
        "        f64.const 0x1p+0 (;=1;)\n" +
        "        get_local \$p0\n" +
        "        get_local \$p0\n" +
        "        f64.mul\n" +
        "        f64.const 0x1p+0 (;=1;)\n" +
        "        f64.add\n" +
        "        f64.sqrt\n" +
        "        get_local \$p0\n" +
        "        f64.add\n" +
        "        f64.div\n" +
        "        f64.add\n" +
        "        call \$log\n" +
        "        set_local \$p0\n" +
        "        br \$B0\n" +
        "      end\n" +
        "      block \$B3\n" +
        "        get_local \$l2\n" +
        "        i32.const 997\n" +
        "        i32.lt_u\n" +
        "        br_if \$B3\n" +
        "        get_local \$p0\n" +
        "        get_local \$p0\n" +
        "        f64.mul\n" +
        "        tee_local \$l3\n" +
        "        get_local \$l3\n" +
        "        f64.const 0x1p+0 (;=1;)\n" +
        "        f64.add\n" +
        "        f64.sqrt\n" +
        "        f64.const 0x1p+0 (;=1;)\n" +
        "        f64.add\n" +
        "        f64.div\n" +
        "        get_local \$p0\n" +
        "        f64.add\n" +
        "        call \$log1p\n" +
        "        set_local \$p0\n" +
        "        br \$B0\n" +
        "      end\n" +
        "      get_local \$l0\n" +
        "      get_local \$p0\n" +
        "      f64.const 0x1p+120 (;=1.32923e+36;)\n" +
        "      f64.add\n" +
        "      f64.store offset=8\n" +
        "    end\n" +
        "    get_local \$l0\n" +
        "    i32.const 16\n" +
        "    i32.add\n" +
        "    set_global \$g0\n" +
        "    get_local \$p0\n" +
        "    f64.neg\n" +
        "    get_local \$p0\n" +
        "    get_local \$l1\n" +
        "    i64.const 0\n" +
        "    i64.lt_s\n" +
        "    select)\n" +
        "  (func \$atan (type \$t1) (param \$p0 f64) (result f64)\n" +
        "    (local \$l0 i32) (local \$l1 i64) (local \$l2 i32) (local \$l3 i32) (local \$l4 i32) (local \$l5 f64) (local \$l6 f64)\n" +
        "    get_global \$g0\n" +
        "    i32.const 16\n" +
        "    i32.sub\n" +
        "    set_local \$l0\n" +
        "    get_local \$p0\n" +
        "    i64.reinterpret/f64\n" +
        "    tee_local \$l1\n" +
        "    i64.const 63\n" +
        "    i64.shr_u\n" +
        "    i32.wrap/i64\n" +
        "    set_local \$l2\n" +
        "    block \$B0\n" +
        "      block \$B1\n" +
        "        block \$B2\n" +
        "          get_local \$l1\n" +
        "          i64.const 32\n" +
        "          i64.shr_u\n" +
        "          i32.wrap/i64\n" +
        "          i32.const 2147483647\n" +
        "          i32.and\n" +
        "          tee_local \$l3\n" +
        "          i32.const 1141899264\n" +
        "          i32.lt_u\n" +
        "          br_if \$B2\n" +
        "          get_local \$l1\n" +
        "          i64.const 9223372036854775807\n" +
        "          i64.and\n" +
        "          i64.const 9218868437227405312\n" +
        "          i64.gt_u\n" +
        "          br_if \$B1\n" +
        "          f64.const -0x1.921fb54442d18p+0 (;=-1.5708;)\n" +
        "          f64.const 0x1.921fb54442d18p+0 (;=1.5708;)\n" +
        "          get_local \$l2\n" +
        "          select\n" +
        "          return\n" +
        "        end\n" +
        "        block \$B3\n" +
        "          block \$B4\n" +
        "            get_local \$l3\n" +
        "            i32.const 1071382527\n" +
        "            i32.gt_u\n" +
        "            br_if \$B4\n" +
        "            i32.const -1\n" +
        "            set_local \$l4\n" +
        "            get_local \$l3\n" +
        "            i32.const 1044381695\n" +
        "            i32.gt_u\n" +
        "            br_if \$B3\n" +
        "            get_local \$l3\n" +
        "            i32.const 1048575\n" +
        "            i32.gt_u\n" +
        "            br_if \$B1\n" +
        "            get_local \$l0\n" +
        "            get_local \$p0\n" +
        "            f32.demote/f64\n" +
        "            f32.store offset=12\n" +
        "            get_local \$p0\n" +
        "            return\n" +
        "          end\n" +
        "          get_local \$p0\n" +
        "          f64.abs\n" +
        "          set_local \$p0\n" +
        "          block \$B5\n" +
        "            block \$B6\n" +
        "              block \$B7\n" +
        "                get_local \$l3\n" +
        "                i32.const 1072889855\n" +
        "                i32.gt_u\n" +
        "                br_if \$B7\n" +
        "                get_local \$l3\n" +
        "                i32.const 1072037887\n" +
        "                i32.gt_u\n" +
        "                br_if \$B6\n" +
        "                get_local \$p0\n" +
        "                get_local \$p0\n" +
        "                f64.add\n" +
        "                f64.const -0x1p+0 (;=-1;)\n" +
        "                f64.add\n" +
        "                get_local \$p0\n" +
        "                f64.const 0x1p+1 (;=2;)\n" +
        "                f64.add\n" +
        "                f64.div\n" +
        "                set_local \$p0\n" +
        "                i32.const 0\n" +
        "                set_local \$l4\n" +
        "                br \$B3\n" +
        "              end\n" +
        "              get_local \$l3\n" +
        "              i32.const 1073971199\n" +
        "              i32.gt_u\n" +
        "              br_if \$B5\n" +
        "              get_local \$p0\n" +
        "              f64.const -0x1.8p+0 (;=-1.5;)\n" +
        "              f64.add\n" +
        "              get_local \$p0\n" +
        "              f64.const 0x1.8p+0 (;=1.5;)\n" +
        "              f64.mul\n" +
        "              f64.const 0x1p+0 (;=1;)\n" +
        "              f64.add\n" +
        "              f64.div\n" +
        "              set_local \$p0\n" +
        "              i32.const 2\n" +
        "              set_local \$l4\n" +
        "              br \$B3\n" +
        "            end\n" +
        "            get_local \$p0\n" +
        "            f64.const -0x1p+0 (;=-1;)\n" +
        "            f64.add\n" +
        "            get_local \$p0\n" +
        "            f64.const 0x1p+0 (;=1;)\n" +
        "            f64.add\n" +
        "            f64.div\n" +
        "            set_local \$p0\n" +
        "            i32.const 1\n" +
        "            set_local \$l4\n" +
        "            br \$B3\n" +
        "          end\n" +
        "          f64.const -0x1p+0 (;=-1;)\n" +
        "          get_local \$p0\n" +
        "          f64.div\n" +
        "          set_local \$p0\n" +
        "          i32.const 3\n" +
        "          set_local \$l4\n" +
        "        end\n" +
        "        get_local \$p0\n" +
        "        get_local \$p0\n" +
        "        get_local \$p0\n" +
        "        f64.mul\n" +
        "        tee_local \$l5\n" +
        "        get_local \$l5\n" +
        "        f64.mul\n" +
        "        tee_local \$l6\n" +
        "        get_local \$l6\n" +
        "        get_local \$l6\n" +
        "        get_local \$l6\n" +
        "        get_local \$l6\n" +
        "        f64.const -0x1.2b4442c6a6c2fp-5 (;=-0.0365316;)\n" +
        "        f64.mul\n" +
        "        f64.const -0x1.dde2d52defd9ap-5 (;=-0.0583357;)\n" +
        "        f64.add\n" +
        "        f64.mul\n" +
        "        f64.const -0x1.3b0f2af749a6dp-4 (;=-0.0769188;)\n" +
        "        f64.add\n" +
        "        f64.mul\n" +
        "        f64.const -0x1.c71c6fe231671p-4 (;=-0.111111;)\n" +
        "        f64.add\n" +
        "        f64.mul\n" +
        "        f64.const -0x1.999999998ebc4p-3 (;=-0.2;)\n" +
        "        f64.add\n" +
        "        f64.mul\n" +
        "        get_local \$l5\n" +
        "        get_local \$l6\n" +
        "        get_local \$l6\n" +
        "        get_local \$l6\n" +
        "        get_local \$l6\n" +
        "        get_local \$l6\n" +
        "        f64.const 0x1.0ad3ae322da11p-6 (;=0.0162858;)\n" +
        "        f64.mul\n" +
        "        f64.const 0x1.97b4b24760debp-5 (;=0.0497688;)\n" +
        "        f64.add\n" +
        "        f64.mul\n" +
        "        f64.const 0x1.10d66a0d03d51p-4 (;=0.0666107;)\n" +
        "        f64.add\n" +
        "        f64.mul\n" +
        "        f64.const 0x1.745cdc54c206ep-4 (;=0.0909089;)\n" +
        "        f64.add\n" +
        "        f64.mul\n" +
        "        f64.const 0x1.24924920083ffp-3 (;=0.142857;)\n" +
        "        f64.add\n" +
        "        f64.mul\n" +
        "        f64.const 0x1.555555555550dp-2 (;=0.333333;)\n" +
        "        f64.add\n" +
        "        f64.mul\n" +
        "        f64.add\n" +
        "        f64.mul\n" +
        "        set_local \$l6\n" +
        "        get_local \$l4\n" +
        "        i32.const -1\n" +
        "        i32.le_s\n" +
        "        br_if \$B0\n" +
        "        get_local \$l4\n" +
        "        i32.const 3\n" +
        "        i32.shl\n" +
        "        tee_local \$l3\n" +
        "        i32.const 1024\n" +
        "        i32.add\n" +
        "        f64.load\n" +
        "        get_local \$l6\n" +
        "        get_local \$l3\n" +
        "        i32.const 1056\n" +
        "        i32.add\n" +
        "        f64.load\n" +
        "        f64.sub\n" +
        "        get_local \$p0\n" +
        "        f64.sub\n" +
        "        f64.sub\n" +
        "        tee_local \$p0\n" +
        "        f64.neg\n" +
        "        get_local \$p0\n" +
        "        get_local \$l2\n" +
        "        select\n" +
        "        set_local \$p0\n" +
        "      end\n" +
        "      get_local \$p0\n" +
        "      return\n" +
        "    end\n" +
        "    get_local \$p0\n" +
        "    get_local \$l6\n" +
        "    f64.sub)\n" +
        "  (func \$atanh (type \$t1) (param \$p0 f64) (result f64)\n" +
        "    (local \$l0 i32) (local \$l1 i64) (local \$l2 i32) (local \$l3 f64)\n" +
        "    get_global \$g0\n" +
        "    i32.const 16\n" +
        "    i32.sub\n" +
        "    tee_local \$l0\n" +
        "    set_global \$g0\n" +
        "    get_local \$p0\n" +
        "    i64.reinterpret/f64\n" +
        "    tee_local \$l1\n" +
        "    i64.const 9223372036854775807\n" +
        "    i64.and\n" +
        "    f64.reinterpret/i64\n" +
        "    set_local \$p0\n" +
        "    block \$B0\n" +
        "      block \$B1\n" +
        "        block \$B2\n" +
        "          get_local \$l1\n" +
        "          i64.const 52\n" +
        "          i64.shr_u\n" +
        "          i32.wrap/i64\n" +
        "          i32.const 2047\n" +
        "          i32.and\n" +
        "          tee_local \$l2\n" +
        "          i32.const 1021\n" +
        "          i32.gt_u\n" +
        "          br_if \$B2\n" +
        "          get_local \$l2\n" +
        "          i32.const 990\n" +
        "          i32.gt_u\n" +
        "          br_if \$B1\n" +
        "          get_local \$l2\n" +
        "          br_if \$B0\n" +
        "          get_local \$l0\n" +
        "          get_local \$p0\n" +
        "          f32.demote/f64\n" +
        "          f32.store offset=12\n" +
        "          br \$B0\n" +
        "        end\n" +
        "        get_local \$p0\n" +
        "        f64.const 0x1p+0 (;=1;)\n" +
        "        get_local \$p0\n" +
        "        f64.sub\n" +
        "        f64.div\n" +
        "        tee_local \$p0\n" +
        "        get_local \$p0\n" +
        "        f64.add\n" +
        "        call \$log1p\n" +
        "        f64.const 0x1p-1 (;=0.5;)\n" +
        "        f64.mul\n" +
        "        set_local \$p0\n" +
        "        br \$B0\n" +
        "      end\n" +
        "      get_local \$p0\n" +
        "      get_local \$p0\n" +
        "      f64.add\n" +
        "      tee_local \$l3\n" +
        "      get_local \$l3\n" +
        "      get_local \$p0\n" +
        "      f64.mul\n" +
        "      f64.const 0x1p+0 (;=1;)\n" +
        "      get_local \$p0\n" +
        "      f64.sub\n" +
        "      f64.div\n" +
        "      f64.add\n" +
        "      call \$log1p\n" +
        "      f64.const 0x1p-1 (;=0.5;)\n" +
        "      f64.mul\n" +
        "      set_local \$p0\n" +
        "    end\n" +
        "    get_local \$l0\n" +
        "    i32.const 16\n" +
        "    i32.add\n" +
        "    set_global \$g0\n" +
        "    get_local \$p0\n" +
        "    f64.neg\n" +
        "    get_local \$p0\n" +
        "    get_local \$l1\n" +
        "    i64.const 0\n" +
        "    i64.lt_s\n" +
        "    select)\n" +
        "  (func \$__cos (type \$t2) (param \$p0 f64) (param \$p1 f64) (result f64)\n" +
        "    (local \$l0 f64) (local \$l1 f64) (local \$l2 f64)\n" +
        "    f64.const 0x1p+0 (;=1;)\n" +
        "    get_local \$p0\n" +
        "    get_local \$p0\n" +
        "    f64.mul\n" +
        "    tee_local \$l0\n" +
        "    f64.const 0x1p-1 (;=0.5;)\n" +
        "    f64.mul\n" +
        "    tee_local \$l1\n" +
        "    f64.sub\n" +
        "    tee_local \$l2\n" +
        "    f64.const 0x1p+0 (;=1;)\n" +
        "    get_local \$l2\n" +
        "    f64.sub\n" +
        "    get_local \$l1\n" +
        "    f64.sub\n" +
        "    get_local \$l0\n" +
        "    get_local \$l0\n" +
        "    get_local \$l0\n" +
        "    get_local \$l0\n" +
        "    f64.const 0x1.a01a019cb159p-16 (;=2.48016e-05;)\n" +
        "    f64.mul\n" +
        "    f64.const -0x1.6c16c16c15177p-10 (;=-0.00138889;)\n" +
        "    f64.add\n" +
        "    f64.mul\n" +
        "    f64.const 0x1.555555555554cp-5 (;=0.0416667;)\n" +
        "    f64.add\n" +
        "    f64.mul\n" +
        "    get_local \$l0\n" +
        "    get_local \$l0\n" +
        "    f64.mul\n" +
        "    tee_local \$l1\n" +
        "    get_local \$l1\n" +
        "    f64.mul\n" +
        "    get_local \$l0\n" +
        "    get_local \$l0\n" +
        "    f64.const -0x1.8fae9be8838d4p-37 (;=-1.13596e-11;)\n" +
        "    f64.mul\n" +
        "    f64.const 0x1.1ee9ebdb4b1c4p-29 (;=2.08757e-09;)\n" +
        "    f64.add\n" +
        "    f64.mul\n" +
        "    f64.const -0x1.27e4f809c52adp-22 (;=-2.75573e-07;)\n" +
        "    f64.add\n" +
        "    f64.mul\n" +
        "    f64.add\n" +
        "    f64.mul\n" +
        "    get_local \$p0\n" +
        "    get_local \$p1\n" +
        "    f64.mul\n" +
        "    f64.sub\n" +
        "    f64.add\n" +
        "    f64.add)\n" +
        "  (func \$__rem_pio2_large (type \$t3) (param \$p0 i32) (param \$p1 i32) (param \$p2 i32) (param \$p3 i32) (param \$p4 i32) (result i32)\n" +
        "    (local \$l0 i32) (local \$l1 i32) (local \$l2 i32) (local \$l3 i32) (local \$l4 i32) (local \$l5 i32) (local \$l6 i32) (local \$l7 f64) (local \$l8 i32) (local \$l9 i32) (local \$l10 i32) (local \$l11 i32) (local \$l12 i32) (local \$l13 i32) (local \$l14 i32) (local \$l15 i32) (local \$l16 i32) (local \$l17 i32) (local \$l18 f64) (local \$l19 i32) (local \$l20 i32) (local \$l21 i32) (local \$l22 f64) (local \$l23 f64)\n" +
        "    get_global \$g0\n" +
        "    i32.const 560\n" +
        "    i32.sub\n" +
        "    tee_local \$l0\n" +
        "    set_global \$g0\n" +
        "    get_local \$p2\n" +
        "    get_local \$p2\n" +
        "    i32.const -3\n" +
        "    i32.add\n" +
        "    i32.const 24\n" +
        "    i32.div_s\n" +
        "    tee_local \$l1\n" +
        "    i32.const 0\n" +
        "    get_local \$l1\n" +
        "    i32.const 0\n" +
        "    i32.gt_s\n" +
        "    select\n" +
        "    tee_local \$l2\n" +
        "    i32.const -24\n" +
        "    i32.mul\n" +
        "    i32.add\n" +
        "    set_local \$l3\n" +
        "    block \$B0\n" +
        "      get_local \$p4\n" +
        "      i32.const 2\n" +
        "      i32.shl\n" +
        "      i32.const 1088\n" +
        "      i32.add\n" +
        "      i32.load\n" +
        "      tee_local \$l4\n" +
        "      get_local \$p3\n" +
        "      i32.const -1\n" +
        "      i32.add\n" +
        "      tee_local \$p2\n" +
        "      i32.add\n" +
        "      i32.const 0\n" +
        "      i32.lt_s\n" +
        "      br_if \$B0\n" +
        "      get_local \$l4\n" +
        "      get_local \$p3\n" +
        "      i32.add\n" +
        "      set_local \$l5\n" +
        "      get_local \$l2\n" +
        "      get_local \$p2\n" +
        "      i32.sub\n" +
        "      set_local \$p2\n" +
        "      get_local \$l2\n" +
        "      i32.const 1\n" +
        "      i32.add\n" +
        "      get_local \$p3\n" +
        "      i32.sub\n" +
        "      i32.const 2\n" +
        "      i32.shl\n" +
        "      i32.const 1104\n" +
        "      i32.add\n" +
        "      set_local \$l6\n" +
        "      get_local \$l0\n" +
        "      i32.const 320\n" +
        "      i32.add\n" +
        "      set_local \$l1\n" +
        "      loop \$L1\n" +
        "        block \$B2\n" +
        "          block \$B3\n" +
        "            get_local \$p2\n" +
        "            i32.const 0\n" +
        "            i32.lt_s\n" +
        "            br_if \$B3\n" +
        "            get_local \$l6\n" +
        "            i32.load\n" +
        "            f64.convert_s/i32\n" +
        "            set_local \$l7\n" +
        "            br \$B2\n" +
        "          end\n" +
        "          f64.const 0x0p+0 (;=0;)\n" +
        "          set_local \$l7\n" +
        "        end\n" +
        "        get_local \$l1\n" +
        "        get_local \$l7\n" +
        "        f64.store\n" +
        "        get_local \$l1\n" +
        "        i32.const 8\n" +
        "        i32.add\n" +
        "        set_local \$l1\n" +
        "        get_local \$l6\n" +
        "        i32.const 4\n" +
        "        i32.add\n" +
        "        set_local \$l6\n" +
        "        get_local \$p2\n" +
        "        i32.const 1\n" +
        "        i32.add\n" +
        "        set_local \$p2\n" +
        "        get_local \$l5\n" +
        "        i32.const -1\n" +
        "        i32.add\n" +
        "        tee_local \$l5\n" +
        "        br_if \$L1\n" +
        "      end\n" +
        "    end\n" +
        "    get_local \$l3\n" +
        "    i32.const -24\n" +
        "    i32.add\n" +
        "    set_local \$l8\n" +
        "    block \$B4\n" +
        "      block \$B5\n" +
        "        get_local \$p3\n" +
        "        i32.const 1\n" +
        "        i32.lt_s\n" +
        "        br_if \$B5\n" +
        "        get_local \$l0\n" +
        "        i32.const 320\n" +
        "        i32.add\n" +
        "        get_local \$p3\n" +
        "        i32.const 3\n" +
        "        i32.shl\n" +
        "        i32.add\n" +
        "        i32.const -8\n" +
        "        i32.add\n" +
        "        set_local \$l9\n" +
        "        i32.const 0\n" +
        "        set_local \$l5\n" +
        "        loop \$L6\n" +
        "          f64.const 0x0p+0 (;=0;)\n" +
        "          set_local \$l7\n" +
        "          get_local \$p0\n" +
        "          set_local \$p2\n" +
        "          get_local \$p3\n" +
        "          set_local \$l6\n" +
        "          get_local \$l9\n" +
        "          set_local \$l1\n" +
        "          loop \$L7\n" +
        "            get_local \$l7\n" +
        "            get_local \$p2\n" +
        "            f64.load\n" +
        "            get_local \$l1\n" +
        "            f64.load\n" +
        "            f64.mul\n" +
        "            f64.add\n" +
        "            set_local \$l7\n" +
        "            get_local \$p2\n" +
        "            i32.const 8\n" +
        "            i32.add\n" +
        "            set_local \$p2\n" +
        "            get_local \$l1\n" +
        "            i32.const -8\n" +
        "            i32.add\n" +
        "            set_local \$l1\n" +
        "            get_local \$l6\n" +
        "            i32.const -1\n" +
        "            i32.add\n" +
        "            tee_local \$l6\n" +
        "            br_if \$L7\n" +
        "          end\n" +
        "          get_local \$l0\n" +
        "          get_local \$l5\n" +
        "          i32.const 3\n" +
        "          i32.shl\n" +
        "          i32.add\n" +
        "          get_local \$l7\n" +
        "          f64.store\n" +
        "          get_local \$l9\n" +
        "          i32.const 8\n" +
        "          i32.add\n" +
        "          set_local \$l9\n" +
        "          get_local \$l5\n" +
        "          get_local \$l4\n" +
        "          i32.lt_s\n" +
        "          set_local \$p2\n" +
        "          get_local \$l5\n" +
        "          i32.const 1\n" +
        "          i32.add\n" +
        "          set_local \$l5\n" +
        "          get_local \$p2\n" +
        "          br_if \$L6\n" +
        "          br \$B4\n" +
        "        end\n" +
        "      end\n" +
        "      get_local \$l0\n" +
        "      i32.const 0\n" +
        "      get_local \$l4\n" +
        "      i32.const 0\n" +
        "      get_local \$l4\n" +
        "      i32.const 0\n" +
        "      i32.gt_s\n" +
        "      select\n" +
        "      i32.const 3\n" +
        "      i32.shl\n" +
        "      i32.const 8\n" +
        "      i32.add\n" +
        "      call \$memset\n" +
        "      drop\n" +
        "    end\n" +
        "    i32.const 23\n" +
        "    get_local \$l8\n" +
        "    i32.sub\n" +
        "    set_local \$l10\n" +
        "    i32.const 24\n" +
        "    get_local \$l8\n" +
        "    i32.sub\n" +
        "    set_local \$l11\n" +
        "    get_local \$l0\n" +
        "    i32.const 480\n" +
        "    i32.add\n" +
        "    get_local \$l4\n" +
        "    i32.const 2\n" +
        "    i32.shl\n" +
        "    i32.add\n" +
        "    i32.const -4\n" +
        "    i32.add\n" +
        "    set_local \$l12\n" +
        "    get_local \$l0\n" +
        "    i32.const 480\n" +
        "    i32.add\n" +
        "    i32.const -4\n" +
        "    i32.add\n" +
        "    set_local \$l13\n" +
        "    get_local \$l0\n" +
        "    i32.const -8\n" +
        "    i32.add\n" +
        "    set_local \$l14\n" +
        "    get_local \$l0\n" +
        "    i32.const 8\n" +
        "    i32.or\n" +
        "    set_local \$l15\n" +
        "    get_local \$l4\n" +
        "    set_local \$l1\n" +
        "    loop \$L8 (result i32)\n" +
        "      get_local \$l0\n" +
        "      get_local \$l1\n" +
        "      i32.const 3\n" +
        "      i32.shl\n" +
        "      tee_local \$l16\n" +
        "      i32.add\n" +
        "      f64.load\n" +
        "      set_local \$l7\n" +
        "      block \$B9\n" +
        "        get_local \$l1\n" +
        "        i32.const 1\n" +
        "        i32.lt_s\n" +
        "        tee_local \$l17\n" +
        "        br_if \$B9\n" +
        "        get_local \$l14\n" +
        "        get_local \$l16\n" +
        "        i32.add\n" +
        "        set_local \$p2\n" +
        "        get_local \$l0\n" +
        "        i32.const 480\n" +
        "        i32.add\n" +
        "        set_local \$l6\n" +
        "        get_local \$l1\n" +
        "        set_local \$l5\n" +
        "        loop \$L10\n" +
        "          block \$B11\n" +
        "            block \$B12\n" +
        "              get_local \$l7\n" +
        "              f64.const 0x1p-24 (;=5.96046e-08;)\n" +
        "              f64.mul\n" +
        "              tee_local \$l18\n" +
        "              f64.abs\n" +
        "              f64.const 0x1p+31 (;=2.14748e+09;)\n" +
        "              f64.lt\n" +
        "              br_if \$B12\n" +
        "              i32.const -2147483648\n" +
        "              set_local \$l9\n" +
        "              br \$B11\n" +
        "            end\n" +
        "            get_local \$l18\n" +
        "            i32.trunc_s/f64\n" +
        "            set_local \$l9\n" +
        "          end\n" +
        "          block \$B13\n" +
        "            block \$B14\n" +
        "              get_local \$l7\n" +
        "              get_local \$l9\n" +
        "              f64.convert_s/i32\n" +
        "              tee_local \$l18\n" +
        "              f64.const -0x1p+24 (;=-1.67772e+07;)\n" +
        "              f64.mul\n" +
        "              f64.add\n" +
        "              tee_local \$l7\n" +
        "              f64.abs\n" +
        "              f64.const 0x1p+31 (;=2.14748e+09;)\n" +
        "              f64.lt\n" +
        "              br_if \$B14\n" +
        "              i32.const -2147483648\n" +
        "              set_local \$l9\n" +
        "              br \$B13\n" +
        "            end\n" +
        "            get_local \$l7\n" +
        "            i32.trunc_s/f64\n" +
        "            set_local \$l9\n" +
        "          end\n" +
        "          get_local \$l6\n" +
        "          get_local \$l9\n" +
        "          i32.store\n" +
        "          get_local \$l6\n" +
        "          i32.const 4\n" +
        "          i32.add\n" +
        "          set_local \$l6\n" +
        "          get_local \$p2\n" +
        "          f64.load\n" +
        "          get_local \$l18\n" +
        "          f64.add\n" +
        "          set_local \$l7\n" +
        "          get_local \$p2\n" +
        "          i32.const -8\n" +
        "          i32.add\n" +
        "          set_local \$p2\n" +
        "          get_local \$l5\n" +
        "          i32.const -1\n" +
        "          i32.add\n" +
        "          tee_local \$l5\n" +
        "          br_if \$L10\n" +
        "        end\n" +
        "      end\n" +
        "      block \$B15\n" +
        "        block \$B16\n" +
        "          block \$B17\n" +
        "            block \$B18\n" +
        "              block \$B19\n" +
        "                block \$B20\n" +
        "                  block \$B21\n" +
        "                    block \$B22\n" +
        "                      block \$B23\n" +
        "                        block \$B24\n" +
        "                          block \$B25\n" +
        "                            block \$B26\n" +
        "                              block \$B27\n" +
        "                                block \$B28\n" +
        "                                  block \$B29\n" +
        "                                    block \$B30\n" +
        "                                      get_local \$l7\n" +
        "                                      get_local \$l8\n" +
        "                                      call \$scalbn\n" +
        "                                      tee_local \$l7\n" +
        "                                      get_local \$l7\n" +
        "                                      f64.const 0x1p-3 (;=0.125;)\n" +
        "                                      f64.mul\n" +
        "                                      f64.floor\n" +
        "                                      f64.const -0x1p+3 (;=-8;)\n" +
        "                                      f64.mul\n" +
        "                                      f64.add\n" +
        "                                      tee_local \$l7\n" +
        "                                      f64.abs\n" +
        "                                      f64.const 0x1p+31 (;=2.14748e+09;)\n" +
        "                                      f64.lt\n" +
        "                                      br_if \$B30\n" +
        "                                      i32.const -2147483648\n" +
        "                                      set_local \$l19\n" +
        "                                      get_local \$l7\n" +
        "                                      i32.const -2147483648\n" +
        "                                      f64.convert_s/i32\n" +
        "                                      f64.sub\n" +
        "                                      set_local \$l7\n" +
        "                                      get_local \$l8\n" +
        "                                      i32.const 1\n" +
        "                                      i32.lt_s\n" +
        "                                      tee_local \$l20\n" +
        "                                      i32.eqz\n" +
        "                                      br_if \$B29\n" +
        "                                      br \$B28\n" +
        "                                    end\n" +
        "                                    get_local \$l7\n" +
        "                                    get_local \$l7\n" +
        "                                    i32.trunc_s/f64\n" +
        "                                    tee_local \$l19\n" +
        "                                    f64.convert_s/i32\n" +
        "                                    f64.sub\n" +
        "                                    set_local \$l7\n" +
        "                                    get_local \$l8\n" +
        "                                    i32.const 1\n" +
        "                                    i32.lt_s\n" +
        "                                    tee_local \$l20\n" +
        "                                    br_if \$B28\n" +
        "                                  end\n" +
        "                                  get_local \$l0\n" +
        "                                  i32.const 480\n" +
        "                                  i32.add\n" +
        "                                  get_local \$l1\n" +
        "                                  i32.const 2\n" +
        "                                  i32.shl\n" +
        "                                  i32.add\n" +
        "                                  i32.const -4\n" +
        "                                  i32.add\n" +
        "                                  tee_local \$p2\n" +
        "                                  get_local \$p2\n" +
        "                                  i32.load\n" +
        "                                  tee_local \$p2\n" +
        "                                  get_local \$p2\n" +
        "                                  get_local \$l11\n" +
        "                                  i32.shr_s\n" +
        "                                  tee_local \$p2\n" +
        "                                  get_local \$l11\n" +
        "                                  i32.shl\n" +
        "                                  i32.sub\n" +
        "                                  tee_local \$l6\n" +
        "                                  i32.store\n" +
        "                                  get_local \$p2\n" +
        "                                  get_local \$l19\n" +
        "                                  i32.add\n" +
        "                                  set_local \$l19\n" +
        "                                  get_local \$l6\n" +
        "                                  get_local \$l10\n" +
        "                                  i32.shr_s\n" +
        "                                  tee_local \$l21\n" +
        "                                  i32.const 1\n" +
        "                                  i32.lt_s\n" +
        "                                  br_if \$B26\n" +
        "                                  br \$B27\n" +
        "                                end\n" +
        "                                block \$B31\n" +
        "                                  get_local \$l8\n" +
        "                                  i32.eqz\n" +
        "                                  br_if \$B31\n" +
        "                                  i32.const 2\n" +
        "                                  set_local \$l21\n" +
        "                                  get_local \$l7\n" +
        "                                  f64.const 0x1p-1 (;=0.5;)\n" +
        "                                  f64.ge\n" +
        "                                  i32.const 1\n" +
        "                                  i32.xor\n" +
        "                                  i32.eqz\n" +
        "                                  br_if \$B27\n" +
        "                                  i32.const 0\n" +
        "                                  set_local \$l21\n" +
        "                                  get_local \$l7\n" +
        "                                  f64.const 0x0p+0 (;=0;)\n" +
        "                                  f64.eq\n" +
        "                                  br_if \$B25\n" +
        "                                  br \$B24\n" +
        "                                end\n" +
        "                                get_local \$l0\n" +
        "                                i32.const 480\n" +
        "                                i32.add\n" +
        "                                get_local \$l1\n" +
        "                                i32.const 2\n" +
        "                                i32.shl\n" +
        "                                i32.add\n" +
        "                                i32.const -4\n" +
        "                                i32.add\n" +
        "                                i32.load\n" +
        "                                i32.const 23\n" +
        "                                i32.shr_s\n" +
        "                                tee_local \$l21\n" +
        "                                i32.const 1\n" +
        "                                i32.lt_s\n" +
        "                                br_if \$B26\n" +
        "                              end\n" +
        "                              block \$B32\n" +
        "                                block \$B33\n" +
        "                                  get_local \$l17\n" +
        "                                  br_if \$B33\n" +
        "                                  i32.const 0\n" +
        "                                  set_local \$l17\n" +
        "                                  get_local \$l0\n" +
        "                                  i32.const 480\n" +
        "                                  i32.add\n" +
        "                                  set_local \$p2\n" +
        "                                  get_local \$l1\n" +
        "                                  set_local \$l9\n" +
        "                                  loop \$L34\n" +
        "                                    get_local \$p2\n" +
        "                                    i32.load\n" +
        "                                    set_local \$l6\n" +
        "                                    i32.const 16777215\n" +
        "                                    set_local \$l5\n" +
        "                                    block \$B35\n" +
        "                                      block \$B36\n" +
        "                                        get_local \$l17\n" +
        "                                        br_if \$B36\n" +
        "                                        get_local \$l6\n" +
        "                                        i32.eqz\n" +
        "                                        br_if \$B35\n" +
        "                                        i32.const 1\n" +
        "                                        set_local \$l17\n" +
        "                                        i32.const 16777216\n" +
        "                                        set_local \$l5\n" +
        "                                      end\n" +
        "                                      get_local \$p2\n" +
        "                                      get_local \$l5\n" +
        "                                      get_local \$l6\n" +
        "                                      i32.sub\n" +
        "                                      i32.store\n" +
        "                                      get_local \$p2\n" +
        "                                      i32.const 4\n" +
        "                                      i32.add\n" +
        "                                      set_local \$p2\n" +
        "                                      get_local \$l9\n" +
        "                                      i32.const -1\n" +
        "                                      i32.add\n" +
        "                                      tee_local \$l9\n" +
        "                                      br_if \$L34\n" +
        "                                      br \$B32\n" +
        "                                    end\n" +
        "                                    i32.const 0\n" +
        "                                    set_local \$l17\n" +
        "                                    get_local \$p2\n" +
        "                                    i32.const 4\n" +
        "                                    i32.add\n" +
        "                                    set_local \$p2\n" +
        "                                    get_local \$l9\n" +
        "                                    i32.const -1\n" +
        "                                    i32.add\n" +
        "                                    tee_local \$l9\n" +
        "                                    br_if \$L34\n" +
        "                                    br \$B32\n" +
        "                                  end\n" +
        "                                end\n" +
        "                                i32.const 0\n" +
        "                                set_local \$l17\n" +
        "                              end\n" +
        "                              block \$B37\n" +
        "                                block \$B38\n" +
        "                                  block \$B39\n" +
        "                                    get_local \$l20\n" +
        "                                    br_if \$B39\n" +
        "                                    get_local \$l8\n" +
        "                                    i32.const 2\n" +
        "                                    i32.eq\n" +
        "                                    br_if \$B38\n" +
        "                                    get_local \$l8\n" +
        "                                    i32.const 1\n" +
        "                                    i32.ne\n" +
        "                                    br_if \$B39\n" +
        "                                    get_local \$l0\n" +
        "                                    i32.const 480\n" +
        "                                    i32.add\n" +
        "                                    get_local \$l1\n" +
        "                                    i32.const 2\n" +
        "                                    i32.shl\n" +
        "                                    i32.add\n" +
        "                                    i32.const -4\n" +
        "                                    i32.add\n" +
        "                                    tee_local \$p2\n" +
        "                                    get_local \$p2\n" +
        "                                    i32.load\n" +
        "                                    i32.const 8388607\n" +
        "                                    i32.and\n" +
        "                                    i32.store\n" +
        "                                  end\n" +
        "                                  get_local \$l19\n" +
        "                                  i32.const 1\n" +
        "                                  i32.add\n" +
        "                                  set_local \$l19\n" +
        "                                  get_local \$l21\n" +
        "                                  i32.const 2\n" +
        "                                  i32.ne\n" +
        "                                  br_if \$B26\n" +
        "                                  br \$B37\n" +
        "                                end\n" +
        "                                get_local \$l0\n" +
        "                                i32.const 480\n" +
        "                                i32.add\n" +
        "                                get_local \$l1\n" +
        "                                i32.const 2\n" +
        "                                i32.shl\n" +
        "                                i32.add\n" +
        "                                i32.const -4\n" +
        "                                i32.add\n" +
        "                                tee_local \$p2\n" +
        "                                get_local \$p2\n" +
        "                                i32.load\n" +
        "                                i32.const 4194303\n" +
        "                                i32.and\n" +
        "                                i32.store\n" +
        "                                get_local \$l19\n" +
        "                                i32.const 1\n" +
        "                                i32.add\n" +
        "                                set_local \$l19\n" +
        "                                get_local \$l21\n" +
        "                                i32.const 2\n" +
        "                                i32.ne\n" +
        "                                br_if \$B26\n" +
        "                              end\n" +
        "                              f64.const 0x1p+0 (;=1;)\n" +
        "                              get_local \$l7\n" +
        "                              f64.sub\n" +
        "                              set_local \$l7\n" +
        "                              i32.const 2\n" +
        "                              set_local \$l21\n" +
        "                              get_local \$l17\n" +
        "                              i32.eqz\n" +
        "                              br_if \$B26\n" +
        "                              get_local \$l7\n" +
        "                              f64.const 0x1p+0 (;=1;)\n" +
        "                              get_local \$l8\n" +
        "                              call \$scalbn\n" +
        "                              f64.sub\n" +
        "                              tee_local \$l7\n" +
        "                              f64.const 0x0p+0 (;=0;)\n" +
        "                              f64.eq\n" +
        "                              br_if \$B25\n" +
        "                              br \$B24\n" +
        "                            end\n" +
        "                            get_local \$l7\n" +
        "                            f64.const 0x0p+0 (;=0;)\n" +
        "                            f64.ne\n" +
        "                            br_if \$B24\n" +
        "                          end\n" +
        "                          block \$B40\n" +
        "                            get_local \$l1\n" +
        "                            get_local \$l4\n" +
        "                            i32.le_s\n" +
        "                            br_if \$B40\n" +
        "                            get_local \$l13\n" +
        "                            get_local \$l1\n" +
        "                            i32.const 2\n" +
        "                            i32.shl\n" +
        "                            i32.add\n" +
        "                            set_local \$p2\n" +
        "                            i32.const 0\n" +
        "                            set_local \$l6\n" +
        "                            get_local \$l1\n" +
        "                            set_local \$l5\n" +
        "                            loop \$L41\n" +
        "                              get_local \$p2\n" +
        "                              i32.load\n" +
        "                              get_local \$l6\n" +
        "                              i32.or\n" +
        "                              set_local \$l6\n" +
        "                              get_local \$p2\n" +
        "                              i32.const -4\n" +
        "                              i32.add\n" +
        "                              set_local \$p2\n" +
        "                              get_local \$l5\n" +
        "                              i32.const -1\n" +
        "                              i32.add\n" +
        "                              tee_local \$l5\n" +
        "                              get_local \$l4\n" +
        "                              i32.gt_s\n" +
        "                              br_if \$L41\n" +
        "                            end\n" +
        "                            get_local \$l6\n" +
        "                            br_if \$B23\n" +
        "                          end\n" +
        "                          get_local \$l12\n" +
        "                          set_local \$p2\n" +
        "                          get_local \$l1\n" +
        "                          set_local \$l9\n" +
        "                          loop \$L42\n" +
        "                            get_local \$l9\n" +
        "                            i32.const 1\n" +
        "                            i32.add\n" +
        "                            set_local \$l9\n" +
        "                            get_local \$p2\n" +
        "                            i32.load\n" +
        "                            set_local \$l6\n" +
        "                            get_local \$p2\n" +
        "                            i32.const -4\n" +
        "                            i32.add\n" +
        "                            set_local \$p2\n" +
        "                            get_local \$l6\n" +
        "                            i32.eqz\n" +
        "                            br_if \$L42\n" +
        "                          end\n" +
        "                          get_local \$l1\n" +
        "                          i32.const 1\n" +
        "                          i32.add\n" +
        "                          set_local \$p2\n" +
        "                          block \$B43\n" +
        "                            get_local \$p3\n" +
        "                            i32.const 1\n" +
        "                            i32.lt_s\n" +
        "                            br_if \$B43\n" +
        "                            get_local \$l0\n" +
        "                            i32.const 320\n" +
        "                            i32.add\n" +
        "                            get_local \$p3\n" +
        "                            get_local \$l1\n" +
        "                            i32.add\n" +
        "                            i32.const 3\n" +
        "                            i32.shl\n" +
        "                            i32.add\n" +
        "                            set_local \$l17\n" +
        "                            loop \$L44\n" +
        "                              get_local \$l0\n" +
        "                              i32.const 320\n" +
        "                              i32.add\n" +
        "                              get_local \$l1\n" +
        "                              get_local \$p3\n" +
        "                              i32.add\n" +
        "                              i32.const 3\n" +
        "                              i32.shl\n" +
        "                              i32.add\n" +
        "                              get_local \$p2\n" +
        "                              tee_local \$l5\n" +
        "                              get_local \$l2\n" +
        "                              i32.add\n" +
        "                              i32.const 2\n" +
        "                              i32.shl\n" +
        "                              i32.const 1104\n" +
        "                              i32.add\n" +
        "                              i32.load\n" +
        "                              f64.convert_s/i32\n" +
        "                              f64.store\n" +
        "                              f64.const 0x0p+0 (;=0;)\n" +
        "                              set_local \$l7\n" +
        "                              get_local \$p0\n" +
        "                              set_local \$p2\n" +
        "                              get_local \$l17\n" +
        "                              set_local \$l1\n" +
        "                              get_local \$p3\n" +
        "                              set_local \$l6\n" +
        "                              loop \$L45\n" +
        "                                get_local \$l7\n" +
        "                                get_local \$p2\n" +
        "                                f64.load\n" +
        "                                get_local \$l1\n" +
        "                                f64.load\n" +
        "                                f64.mul\n" +
        "                                f64.add\n" +
        "                                set_local \$l7\n" +
        "                                get_local \$p2\n" +
        "                                i32.const 8\n" +
        "                                i32.add\n" +
        "                                set_local \$p2\n" +
        "                                get_local \$l1\n" +
        "                                i32.const -8\n" +
        "                                i32.add\n" +
        "                                set_local \$l1\n" +
        "                                get_local \$l6\n" +
        "                                i32.const -1\n" +
        "                                i32.add\n" +
        "                                tee_local \$l6\n" +
        "                                br_if \$L45\n" +
        "                              end\n" +
        "                              get_local \$l0\n" +
        "                              get_local \$l5\n" +
        "                              i32.const 3\n" +
        "                              i32.shl\n" +
        "                              i32.add\n" +
        "                              get_local \$l7\n" +
        "                              f64.store\n" +
        "                              get_local \$l17\n" +
        "                              i32.const 8\n" +
        "                              i32.add\n" +
        "                              set_local \$l17\n" +
        "                              get_local \$l5\n" +
        "                              i32.const 1\n" +
        "                              i32.add\n" +
        "                              set_local \$p2\n" +
        "                              get_local \$l5\n" +
        "                              set_local \$l1\n" +
        "                              get_local \$l5\n" +
        "                              get_local \$l9\n" +
        "                              i32.lt_s\n" +
        "                              br_if \$L44\n" +
        "                              br \$B15\n" +
        "                            end\n" +
        "                          end\n" +
        "                          get_local \$l15\n" +
        "                          get_local \$l16\n" +
        "                          i32.add\n" +
        "                          i32.const 0\n" +
        "                          get_local \$l9\n" +
        "                          get_local \$p2\n" +
        "                          get_local \$l9\n" +
        "                          get_local \$p2\n" +
        "                          i32.gt_s\n" +
        "                          select\n" +
        "                          get_local \$l1\n" +
        "                          i32.sub\n" +
        "                          i32.const 3\n" +
        "                          i32.shl\n" +
        "                          call \$memset\n" +
        "                          drop\n" +
        "                          get_local \$l2\n" +
        "                          get_local \$l1\n" +
        "                          i32.add\n" +
        "                          i32.const 2\n" +
        "                          i32.shl\n" +
        "                          i32.const 1108\n" +
        "                          i32.add\n" +
        "                          set_local \$p2\n" +
        "                          get_local \$l0\n" +
        "                          i32.const 320\n" +
        "                          i32.add\n" +
        "                          get_local \$p3\n" +
        "                          get_local \$l1\n" +
        "                          i32.add\n" +
        "                          i32.const 3\n" +
        "                          i32.shl\n" +
        "                          i32.add\n" +
        "                          set_local \$l6\n" +
        "                          loop \$L46\n" +
        "                            get_local \$l6\n" +
        "                            get_local \$p2\n" +
        "                            i32.load\n" +
        "                            f64.convert_s/i32\n" +
        "                            f64.store\n" +
        "                            get_local \$p2\n" +
        "                            i32.const 4\n" +
        "                            i32.add\n" +
        "                            set_local \$p2\n" +
        "                            get_local \$l6\n" +
        "                            i32.const 8\n" +
        "                            i32.add\n" +
        "                            set_local \$l6\n" +
        "                            get_local \$l1\n" +
        "                            i32.const 1\n" +
        "                            i32.add\n" +
        "                            tee_local \$l1\n" +
        "                            get_local \$l9\n" +
        "                            i32.lt_s\n" +
        "                            br_if \$L46\n" +
        "                          end\n" +
        "                          get_local \$l9\n" +
        "                          set_local \$l1\n" +
        "                          br \$L8\n" +
        "                        end\n" +
        "                        block \$B47\n" +
        "                          get_local \$l7\n" +
        "                          i32.const 0\n" +
        "                          get_local \$l8\n" +
        "                          i32.sub\n" +
        "                          call \$scalbn\n" +
        "                          tee_local \$l7\n" +
        "                          f64.const 0x1p+24 (;=1.67772e+07;)\n" +
        "                          f64.ge\n" +
        "                          i32.const 1\n" +
        "                          i32.xor\n" +
        "                          i32.eqz\n" +
        "                          br_if \$B47\n" +
        "                          get_local \$l7\n" +
        "                          f64.abs\n" +
        "                          f64.const 0x1p+31 (;=2.14748e+09;)\n" +
        "                          f64.lt\n" +
        "                          br_if \$B22\n" +
        "                          i32.const -2147483648\n" +
        "                          set_local \$p2\n" +
        "                          br \$B21\n" +
        "                        end\n" +
        "                        get_local \$l1\n" +
        "                        i32.const 2\n" +
        "                        i32.shl\n" +
        "                        set_local \$l6\n" +
        "                        get_local \$l7\n" +
        "                        f64.const 0x1p-24 (;=5.96046e-08;)\n" +
        "                        f64.mul\n" +
        "                        tee_local \$l18\n" +
        "                        f64.abs\n" +
        "                        f64.const 0x1p+31 (;=2.14748e+09;)\n" +
        "                        f64.lt\n" +
        "                        br_if \$B20\n" +
        "                        i32.const -2147483648\n" +
        "                        set_local \$p2\n" +
        "                        br \$B19\n" +
        "                      end\n" +
        "                      get_local \$l0\n" +
        "                      i32.const 480\n" +
        "                      i32.add\n" +
        "                      get_local \$l1\n" +
        "                      i32.const 2\n" +
        "                      i32.shl\n" +
        "                      i32.add\n" +
        "                      i32.const -4\n" +
        "                      i32.add\n" +
        "                      set_local \$p2\n" +
        "                      get_local \$l8\n" +
        "                      set_local \$l3\n" +
        "                      loop \$L48\n" +
        "                        get_local \$l1\n" +
        "                        i32.const -1\n" +
        "                        i32.add\n" +
        "                        set_local \$l1\n" +
        "                        get_local \$l3\n" +
        "                        i32.const -24\n" +
        "                        i32.add\n" +
        "                        set_local \$l3\n" +
        "                        get_local \$p2\n" +
        "                        i32.load\n" +
        "                        set_local \$l6\n" +
        "                        get_local \$p2\n" +
        "                        i32.const -4\n" +
        "                        i32.add\n" +
        "                        set_local \$p2\n" +
        "                        get_local \$l6\n" +
        "                        i32.eqz\n" +
        "                        br_if \$L48\n" +
        "                      end\n" +
        "                      i32.const 0\n" +
        "                      set_local \$l9\n" +
        "                      get_local \$l1\n" +
        "                      i32.const 0\n" +
        "                      i32.ge_s\n" +
        "                      br_if \$B17\n" +
        "                      br \$B16\n" +
        "                    end\n" +
        "                    get_local \$l7\n" +
        "                    i32.trunc_s/f64\n" +
        "                    set_local \$p2\n" +
        "                  end\n" +
        "                  get_local \$l8\n" +
        "                  set_local \$l3\n" +
        "                  br \$B18\n" +
        "                end\n" +
        "                get_local \$l18\n" +
        "                i32.trunc_s/f64\n" +
        "                set_local \$p2\n" +
        "              end\n" +
        "              get_local \$l0\n" +
        "              i32.const 480\n" +
        "              i32.add\n" +
        "              get_local \$l6\n" +
        "              i32.add\n" +
        "              set_local \$l6\n" +
        "              block \$B49\n" +
        "                block \$B50\n" +
        "                  get_local \$l7\n" +
        "                  get_local \$p2\n" +
        "                  f64.convert_s/i32\n" +
        "                  f64.const -0x1p+24 (;=-1.67772e+07;)\n" +
        "                  f64.mul\n" +
        "                  f64.add\n" +
        "                  tee_local \$l7\n" +
        "                  f64.abs\n" +
        "                  f64.const 0x1p+31 (;=2.14748e+09;)\n" +
        "                  f64.lt\n" +
        "                  br_if \$B50\n" +
        "                  i32.const -2147483648\n" +
        "                  set_local \$l5\n" +
        "                  br \$B49\n" +
        "                end\n" +
        "                get_local \$l7\n" +
        "                i32.trunc_s/f64\n" +
        "                set_local \$l5\n" +
        "              end\n" +
        "              get_local \$l6\n" +
        "              get_local \$l5\n" +
        "              i32.store\n" +
        "              get_local \$l1\n" +
        "              i32.const 1\n" +
        "              i32.add\n" +
        "              set_local \$l1\n" +
        "            end\n" +
        "            get_local \$l0\n" +
        "            i32.const 480\n" +
        "            i32.add\n" +
        "            get_local \$l1\n" +
        "            i32.const 2\n" +
        "            i32.shl\n" +
        "            i32.add\n" +
        "            get_local \$p2\n" +
        "            i32.store\n" +
        "            i32.const 0\n" +
        "            set_local \$l9\n" +
        "            get_local \$l1\n" +
        "            i32.const 0\n" +
        "            i32.lt_s\n" +
        "            br_if \$B16\n" +
        "          end\n" +
        "          get_local \$l1\n" +
        "          i32.const 1\n" +
        "          i32.add\n" +
        "          set_local \$l5\n" +
        "          f64.const 0x1p+0 (;=1;)\n" +
        "          get_local \$l3\n" +
        "          call \$scalbn\n" +
        "          set_local \$l7\n" +
        "          get_local \$l0\n" +
        "          i32.const 480\n" +
        "          i32.add\n" +
        "          get_local \$l1\n" +
        "          i32.const 2\n" +
        "          i32.shl\n" +
        "          i32.add\n" +
        "          set_local \$p2\n" +
        "          get_local \$l0\n" +
        "          get_local \$l1\n" +
        "          i32.const 3\n" +
        "          i32.shl\n" +
        "          i32.add\n" +
        "          set_local \$l6\n" +
        "          loop \$L51\n" +
        "            get_local \$l6\n" +
        "            get_local \$l7\n" +
        "            get_local \$p2\n" +
        "            i32.load\n" +
        "            f64.convert_s/i32\n" +
        "            f64.mul\n" +
        "            f64.store\n" +
        "            get_local \$p2\n" +
        "            i32.const -4\n" +
        "            i32.add\n" +
        "            set_local \$p2\n" +
        "            get_local \$l6\n" +
        "            i32.const -8\n" +
        "            i32.add\n" +
        "            set_local \$l6\n" +
        "            get_local \$l7\n" +
        "            f64.const 0x1p-24 (;=5.96046e-08;)\n" +
        "            f64.mul\n" +
        "            set_local \$l7\n" +
        "            get_local \$l5\n" +
        "            i32.const -1\n" +
        "            i32.add\n" +
        "            tee_local \$l5\n" +
        "            get_local \$l9\n" +
        "            i32.gt_s\n" +
        "            br_if \$L51\n" +
        "          end\n" +
        "          get_local \$l1\n" +
        "          i32.const 0\n" +
        "          i32.lt_s\n" +
        "          br_if \$B16\n" +
        "          get_local \$l0\n" +
        "          get_local \$l1\n" +
        "          i32.const 3\n" +
        "          i32.shl\n" +
        "          i32.add\n" +
        "          set_local \$l9\n" +
        "          get_local \$l1\n" +
        "          set_local \$p2\n" +
        "          loop \$L52\n" +
        "            get_local \$l1\n" +
        "            get_local \$p2\n" +
        "            tee_local \$p3\n" +
        "            i32.sub\n" +
        "            set_local \$l17\n" +
        "            f64.const 0x0p+0 (;=0;)\n" +
        "            set_local \$l7\n" +
        "            i32.const 0\n" +
        "            set_local \$p2\n" +
        "            i32.const 0\n" +
        "            set_local \$l6\n" +
        "            block \$B53\n" +
        "              loop \$L54\n" +
        "                get_local \$l7\n" +
        "                get_local \$p2\n" +
        "                i32.const 3872\n" +
        "                i32.add\n" +
        "                f64.load\n" +
        "                get_local \$l9\n" +
        "                get_local \$p2\n" +
        "                i32.add\n" +
        "                f64.load\n" +
        "                f64.mul\n" +
        "                f64.add\n" +
        "                set_local \$l7\n" +
        "                get_local \$l6\n" +
        "                get_local \$l4\n" +
        "                i32.ge_s\n" +
        "                br_if \$B53\n" +
        "                get_local \$p2\n" +
        "                i32.const 8\n" +
        "                i32.add\n" +
        "                set_local \$p2\n" +
        "                get_local \$l6\n" +
        "                get_local \$l17\n" +
        "                i32.lt_u\n" +
        "                set_local \$l5\n" +
        "                get_local \$l6\n" +
        "                i32.const 1\n" +
        "                i32.add\n" +
        "                set_local \$l6\n" +
        "                get_local \$l5\n" +
        "                br_if \$L54\n" +
        "              end\n" +
        "            end\n" +
        "            get_local \$l0\n" +
        "            i32.const 160\n" +
        "            i32.add\n" +
        "            get_local \$l17\n" +
        "            i32.const 3\n" +
        "            i32.shl\n" +
        "            i32.add\n" +
        "            get_local \$l7\n" +
        "            f64.store\n" +
        "            get_local \$l9\n" +
        "            i32.const -8\n" +
        "            i32.add\n" +
        "            set_local \$l9\n" +
        "            get_local \$p3\n" +
        "            i32.const -1\n" +
        "            i32.add\n" +
        "            set_local \$p2\n" +
        "            get_local \$p3\n" +
        "            i32.const 0\n" +
        "            i32.gt_s\n" +
        "            br_if \$L52\n" +
        "          end\n" +
        "        end\n" +
        "        block \$B55\n" +
        "          block \$B56\n" +
        "            block \$B57\n" +
        "              block \$B58\n" +
        "                block \$B59\n" +
        "                  block \$B60\n" +
        "                    block \$B61\n" +
        "                      block \$B62\n" +
        "                        get_local \$p4\n" +
        "                        i32.const -1\n" +
        "                        i32.add\n" +
        "                        i32.const 2\n" +
        "                        i32.lt_u\n" +
        "                        br_if \$B62\n" +
        "                        get_local \$p4\n" +
        "                        i32.eqz\n" +
        "                        br_if \$B61\n" +
        "                        get_local \$p4\n" +
        "                        i32.const 3\n" +
        "                        i32.ne\n" +
        "                        br_if \$B55\n" +
        "                        f64.const 0x0p+0 (;=0;)\n" +
        "                        set_local \$l22\n" +
        "                        block \$B63\n" +
        "                          get_local \$l1\n" +
        "                          i32.const 1\n" +
        "                          i32.lt_s\n" +
        "                          br_if \$B63\n" +
        "                          get_local \$l0\n" +
        "                          i32.const 160\n" +
        "                          i32.add\n" +
        "                          get_local \$l1\n" +
        "                          i32.const 3\n" +
        "                          i32.shl\n" +
        "                          i32.add\n" +
        "                          tee_local \$l6\n" +
        "                          i32.const -8\n" +
        "                          i32.add\n" +
        "                          set_local \$p2\n" +
        "                          get_local \$l6\n" +
        "                          f64.load\n" +
        "                          set_local \$l7\n" +
        "                          get_local \$l1\n" +
        "                          set_local \$l6\n" +
        "                          loop \$L64\n" +
        "                            get_local \$p2\n" +
        "                            get_local \$p2\n" +
        "                            f64.load\n" +
        "                            tee_local \$l23\n" +
        "                            get_local \$l7\n" +
        "                            f64.add\n" +
        "                            tee_local \$l18\n" +
        "                            f64.store\n" +
        "                            get_local \$p2\n" +
        "                            i32.const 8\n" +
        "                            i32.add\n" +
        "                            get_local \$l7\n" +
        "                            get_local \$l23\n" +
        "                            get_local \$l18\n" +
        "                            f64.sub\n" +
        "                            f64.add\n" +
        "                            f64.store\n" +
        "                            get_local \$p2\n" +
        "                            i32.const -8\n" +
        "                            i32.add\n" +
        "                            set_local \$p2\n" +
        "                            get_local \$l18\n" +
        "                            set_local \$l7\n" +
        "                            get_local \$l6\n" +
        "                            i32.const -1\n" +
        "                            i32.add\n" +
        "                            tee_local \$l6\n" +
        "                            i32.const 0\n" +
        "                            i32.gt_s\n" +
        "                            br_if \$L64\n" +
        "                          end\n" +
        "                          get_local \$l1\n" +
        "                          i32.const 2\n" +
        "                          i32.lt_s\n" +
        "                          br_if \$B63\n" +
        "                          get_local \$l0\n" +
        "                          i32.const 160\n" +
        "                          i32.add\n" +
        "                          get_local \$l1\n" +
        "                          i32.const 3\n" +
        "                          i32.shl\n" +
        "                          i32.add\n" +
        "                          tee_local \$l6\n" +
        "                          i32.const -8\n" +
        "                          i32.add\n" +
        "                          set_local \$p2\n" +
        "                          get_local \$l6\n" +
        "                          f64.load\n" +
        "                          set_local \$l7\n" +
        "                          get_local \$l1\n" +
        "                          set_local \$l6\n" +
        "                          loop \$L65\n" +
        "                            get_local \$p2\n" +
        "                            get_local \$p2\n" +
        "                            f64.load\n" +
        "                            tee_local \$l23\n" +
        "                            get_local \$l7\n" +
        "                            f64.add\n" +
        "                            tee_local \$l18\n" +
        "                            f64.store\n" +
        "                            get_local \$p2\n" +
        "                            i32.const 8\n" +
        "                            i32.add\n" +
        "                            get_local \$l7\n" +
        "                            get_local \$l23\n" +
        "                            get_local \$l18\n" +
        "                            f64.sub\n" +
        "                            f64.add\n" +
        "                            f64.store\n" +
        "                            get_local \$p2\n" +
        "                            i32.const -8\n" +
        "                            i32.add\n" +
        "                            set_local \$p2\n" +
        "                            get_local \$l18\n" +
        "                            set_local \$l7\n" +
        "                            get_local \$l6\n" +
        "                            i32.const -1\n" +
        "                            i32.add\n" +
        "                            tee_local \$l6\n" +
        "                            i32.const 1\n" +
        "                            i32.gt_s\n" +
        "                            br_if \$L65\n" +
        "                          end\n" +
        "                          get_local \$l1\n" +
        "                          i32.const 2\n" +
        "                          i32.lt_s\n" +
        "                          br_if \$B63\n" +
        "                          get_local \$l0\n" +
        "                          i32.const 160\n" +
        "                          i32.add\n" +
        "                          get_local \$l1\n" +
        "                          i32.const 3\n" +
        "                          i32.shl\n" +
        "                          i32.add\n" +
        "                          set_local \$p2\n" +
        "                          f64.const 0x0p+0 (;=0;)\n" +
        "                          set_local \$l22\n" +
        "                          loop \$L66\n" +
        "                            get_local \$l22\n" +
        "                            get_local \$p2\n" +
        "                            f64.load\n" +
        "                            f64.add\n" +
        "                            set_local \$l22\n" +
        "                            get_local \$p2\n" +
        "                            i32.const -8\n" +
        "                            i32.add\n" +
        "                            set_local \$p2\n" +
        "                            get_local \$l1\n" +
        "                            i32.const -1\n" +
        "                            i32.add\n" +
        "                            tee_local \$l1\n" +
        "                            i32.const 1\n" +
        "                            i32.gt_s\n" +
        "                            br_if \$L66\n" +
        "                          end\n" +
        "                        end\n" +
        "                        get_local \$l0\n" +
        "                        f64.load offset=160\n" +
        "                        set_local \$l7\n" +
        "                        get_local \$l21\n" +
        "                        i32.eqz\n" +
        "                        br_if \$B58\n" +
        "                        get_local \$p1\n" +
        "                        get_local \$l7\n" +
        "                        f64.neg\n" +
        "                        f64.store\n" +
        "                        get_local \$p1\n" +
        "                        get_local \$l0\n" +
        "                        f64.load offset=168\n" +
        "                        f64.neg\n" +
        "                        f64.store offset=8\n" +
        "                        get_local \$p1\n" +
        "                        get_local \$l22\n" +
        "                        f64.neg\n" +
        "                        f64.store offset=16\n" +
        "                        br \$B55\n" +
        "                      end\n" +
        "                      get_local \$l1\n" +
        "                      i32.const 0\n" +
        "                      i32.lt_s\n" +
        "                      br_if \$B60\n" +
        "                      get_local \$l1\n" +
        "                      i32.const 1\n" +
        "                      i32.add\n" +
        "                      set_local \$l6\n" +
        "                      get_local \$l0\n" +
        "                      i32.const 160\n" +
        "                      i32.add\n" +
        "                      get_local \$l1\n" +
        "                      i32.const 3\n" +
        "                      i32.shl\n" +
        "                      i32.add\n" +
        "                      set_local \$p2\n" +
        "                      f64.const 0x0p+0 (;=0;)\n" +
        "                      set_local \$l7\n" +
        "                      loop \$L67\n" +
        "                        get_local \$l7\n" +
        "                        get_local \$p2\n" +
        "                        f64.load\n" +
        "                        f64.add\n" +
        "                        set_local \$l7\n" +
        "                        get_local \$p2\n" +
        "                        i32.const -8\n" +
        "                        i32.add\n" +
        "                        set_local \$p2\n" +
        "                        get_local \$l6\n" +
        "                        i32.const -1\n" +
        "                        i32.add\n" +
        "                        tee_local \$l6\n" +
        "                        i32.const 0\n" +
        "                        i32.gt_s\n" +
        "                        br_if \$L67\n" +
        "                        br \$B59\n" +
        "                      end\n" +
        "                    end\n" +
        "                    get_local \$l1\n" +
        "                    i32.const 0\n" +
        "                    i32.lt_s\n" +
        "                    br_if \$B57\n" +
        "                    get_local \$l1\n" +
        "                    i32.const 1\n" +
        "                    i32.add\n" +
        "                    set_local \$l6\n" +
        "                    get_local \$l0\n" +
        "                    i32.const 160\n" +
        "                    i32.add\n" +
        "                    get_local \$l1\n" +
        "                    i32.const 3\n" +
        "                    i32.shl\n" +
        "                    i32.add\n" +
        "                    set_local \$p2\n" +
        "                    f64.const 0x0p+0 (;=0;)\n" +
        "                    set_local \$l7\n" +
        "                    loop \$L68\n" +
        "                      get_local \$l7\n" +
        "                      get_local \$p2\n" +
        "                      f64.load\n" +
        "                      f64.add\n" +
        "                      set_local \$l7\n" +
        "                      get_local \$p2\n" +
        "                      i32.const -8\n" +
        "                      i32.add\n" +
        "                      set_local \$p2\n" +
        "                      get_local \$l6\n" +
        "                      i32.const -1\n" +
        "                      i32.add\n" +
        "                      tee_local \$l6\n" +
        "                      i32.const 0\n" +
        "                      i32.gt_s\n" +
        "                      br_if \$L68\n" +
        "                      br \$B56\n" +
        "                    end\n" +
        "                  end\n" +
        "                  f64.const 0x0p+0 (;=0;)\n" +
        "                  set_local \$l7\n" +
        "                end\n" +
        "                get_local \$p1\n" +
        "                get_local \$l7\n" +
        "                f64.neg\n" +
        "                get_local \$l7\n" +
        "                get_local \$l21\n" +
        "                select\n" +
        "                f64.store\n" +
        "                get_local \$l0\n" +
        "                f64.load offset=160\n" +
        "                get_local \$l7\n" +
        "                f64.sub\n" +
        "                set_local \$l7\n" +
        "                block \$B69\n" +
        "                  get_local \$l1\n" +
        "                  i32.const 1\n" +
        "                  i32.lt_s\n" +
        "                  br_if \$B69\n" +
        "                  get_local \$l0\n" +
        "                  i32.const 160\n" +
        "                  i32.add\n" +
        "                  i32.const 8\n" +
        "                  i32.or\n" +
        "                  set_local \$p2\n" +
        "                  loop \$L70\n" +
        "                    get_local \$l7\n" +
        "                    get_local \$p2\n" +
        "                    f64.load\n" +
        "                    f64.add\n" +
        "                    set_local \$l7\n" +
        "                    get_local \$p2\n" +
        "                    i32.const 8\n" +
        "                    i32.add\n" +
        "                    set_local \$p2\n" +
        "                    get_local \$l1\n" +
        "                    i32.const -1\n" +
        "                    i32.add\n" +
        "                    tee_local \$l1\n" +
        "                    br_if \$L70\n" +
        "                  end\n" +
        "                end\n" +
        "                get_local \$p1\n" +
        "                get_local \$l7\n" +
        "                f64.neg\n" +
        "                get_local \$l7\n" +
        "                get_local \$l21\n" +
        "                select\n" +
        "                f64.store offset=8\n" +
        "                br \$B55\n" +
        "              end\n" +
        "              get_local \$p1\n" +
        "              get_local \$l7\n" +
        "              f64.store\n" +
        "              get_local \$p1\n" +
        "              get_local \$l0\n" +
        "              i64.load offset=168\n" +
        "              i64.store offset=8\n" +
        "              get_local \$p1\n" +
        "              get_local \$l22\n" +
        "              f64.store offset=16\n" +
        "              br \$B55\n" +
        "            end\n" +
        "            f64.const 0x0p+0 (;=0;)\n" +
        "            set_local \$l7\n" +
        "          end\n" +
        "          get_local \$p1\n" +
        "          get_local \$l7\n" +
        "          f64.neg\n" +
        "          get_local \$l7\n" +
        "          get_local \$l21\n" +
        "          select\n" +
        "          f64.store\n" +
        "        end\n" +
        "        get_local \$l0\n" +
        "        i32.const 560\n" +
        "        i32.add\n" +
        "        set_global \$g0\n" +
        "        get_local \$l19\n" +
        "        i32.const 7\n" +
        "        i32.and\n" +
        "        return\n" +
        "      end\n" +
        "      get_local \$l9\n" +
        "      set_local \$l1\n" +
        "      br \$L8\n" +
        "    end)\n" +
        "  (func \$__rem_pio2 (type \$t4) (param \$p0 f64) (param \$p1 i32) (result i32)\n" +
        "    (local \$l0 i32) (local \$l1 i64) (local \$l2 i32) (local \$l3 i32) (local \$l4 i32) (local \$l5 f64) (local \$l6 f64) (local \$l7 f64) (local \$l8 i32) (local \$l9 f64)\n" +
        "    get_global \$g0\n" +
        "    i32.const 48\n" +
        "    i32.sub\n" +
        "    tee_local \$l0\n" +
        "    set_global \$g0\n" +
        "    get_local \$p0\n" +
        "    i64.reinterpret/f64\n" +
        "    tee_local \$l1\n" +
        "    i64.const 63\n" +
        "    i64.shr_u\n" +
        "    i32.wrap/i64\n" +
        "    set_local \$l2\n" +
        "    block \$B0\n" +
        "      block \$B1\n" +
        "        block \$B2\n" +
        "          block \$B3\n" +
        "            block \$B4\n" +
        "              block \$B5\n" +
        "                block \$B6\n" +
        "                  block \$B7\n" +
        "                    block \$B8\n" +
        "                      block \$B9\n" +
        "                        block \$B10\n" +
        "                          block \$B11\n" +
        "                            block \$B12\n" +
        "                              block \$B13\n" +
        "                                get_local \$l1\n" +
        "                                i64.const 32\n" +
        "                                i64.shr_u\n" +
        "                                i32.wrap/i64\n" +
        "                                tee_local \$l3\n" +
        "                                i32.const 2147483647\n" +
        "                                i32.and\n" +
        "                                tee_local \$l4\n" +
        "                                i32.const 1074752122\n" +
        "                                i32.gt_u\n" +
        "                                br_if \$B13\n" +
        "                                get_local \$l3\n" +
        "                                i32.const 1048575\n" +
        "                                i32.and\n" +
        "                                i32.const 598523\n" +
        "                                i32.eq\n" +
        "                                br_if \$B10\n" +
        "                                get_local \$l4\n" +
        "                                i32.const 1073928572\n" +
        "                                i32.gt_u\n" +
        "                                br_if \$B12\n" +
        "                                get_local \$l2\n" +
        "                                i32.eqz\n" +
        "                                br_if \$B7\n" +
        "                                get_local \$p1\n" +
        "                                get_local \$p0\n" +
        "                                f64.const 0x1.921fb544p+0 (;=1.5708;)\n" +
        "                                f64.add\n" +
        "                                tee_local \$p0\n" +
        "                                f64.const 0x1.0b4611a626331p-34 (;=6.0771e-11;)\n" +
        "                                f64.add\n" +
        "                                tee_local \$l5\n" +
        "                                f64.store\n" +
        "                                get_local \$p1\n" +
        "                                get_local \$p0\n" +
        "                                get_local \$l5\n" +
        "                                f64.sub\n" +
        "                                f64.const 0x1.0b4611a626331p-34 (;=6.0771e-11;)\n" +
        "                                f64.add\n" +
        "                                f64.store offset=8\n" +
        "                                get_local \$l0\n" +
        "                                i32.const 48\n" +
        "                                i32.add\n" +
        "                                set_global \$g0\n" +
        "                                i32.const -1\n" +
        "                                return\n" +
        "                              end\n" +
        "                              block \$B14\n" +
        "                                get_local \$l4\n" +
        "                                i32.const 1075594811\n" +
        "                                i32.gt_u\n" +
        "                                br_if \$B14\n" +
        "                                get_local \$l4\n" +
        "                                i32.const 1075183036\n" +
        "                                i32.gt_u\n" +
        "                                br_if \$B11\n" +
        "                                get_local \$l4\n" +
        "                                i32.const 1074977148\n" +
        "                                i32.eq\n" +
        "                                br_if \$B10\n" +
        "                                get_local \$l2\n" +
        "                                i32.eqz\n" +
        "                                br_if \$B3\n" +
        "                                get_local \$p1\n" +
        "                                get_local \$p0\n" +
        "                                f64.const 0x1.2d97c7f3p+2 (;=4.71239;)\n" +
        "                                f64.add\n" +
        "                                tee_local \$p0\n" +
        "                                f64.const 0x1.90e91a79394cap-33 (;=1.82313e-10;)\n" +
        "                                f64.add\n" +
        "                                tee_local \$l5\n" +
        "                                f64.store\n" +
        "                                get_local \$p1\n" +
        "                                get_local \$p0\n" +
        "                                get_local \$l5\n" +
        "                                f64.sub\n" +
        "                                f64.const 0x1.90e91a79394cap-33 (;=1.82313e-10;)\n" +
        "                                f64.add\n" +
        "                                f64.store offset=8\n" +
        "                                get_local \$l0\n" +
        "                                i32.const 48\n" +
        "                                i32.add\n" +
        "                                set_global \$g0\n" +
        "                                i32.const -3\n" +
        "                                return\n" +
        "                              end\n" +
        "                              get_local \$l4\n" +
        "                              i32.const 1094263290\n" +
        "                              i32.le_u\n" +
        "                              br_if \$B10\n" +
        "                              get_local \$l4\n" +
        "                              i32.const 2146435072\n" +
        "                              i32.lt_u\n" +
        "                              br_if \$B9\n" +
        "                              get_local \$p1\n" +
        "                              get_local \$p0\n" +
        "                              get_local \$p0\n" +
        "                              f64.sub\n" +
        "                              tee_local \$p0\n" +
        "                              f64.store\n" +
        "                              get_local \$p1\n" +
        "                              get_local \$p0\n" +
        "                              f64.store offset=8\n" +
        "                              get_local \$l0\n" +
        "                              i32.const 48\n" +
        "                              i32.add\n" +
        "                              set_global \$g0\n" +
        "                              i32.const 0\n" +
        "                              return\n" +
        "                            end\n" +
        "                            get_local \$l2\n" +
        "                            i32.eqz\n" +
        "                            br_if \$B6\n" +
        "                            get_local \$p1\n" +
        "                            get_local \$p0\n" +
        "                            f64.const 0x1.921fb544p+1 (;=3.14159;)\n" +
        "                            f64.add\n" +
        "                            tee_local \$p0\n" +
        "                            f64.const 0x1.0b4611a626331p-33 (;=1.21542e-10;)\n" +
        "                            f64.add\n" +
        "                            tee_local \$l5\n" +
        "                            f64.store\n" +
        "                            get_local \$p1\n" +
        "                            get_local \$p0\n" +
        "                            get_local \$l5\n" +
        "                            f64.sub\n" +
        "                            f64.const 0x1.0b4611a626331p-33 (;=1.21542e-10;)\n" +
        "                            f64.add\n" +
        "                            f64.store offset=8\n" +
        "                            get_local \$l0\n" +
        "                            i32.const 48\n" +
        "                            i32.add\n" +
        "                            set_global \$g0\n" +
        "                            i32.const -2\n" +
        "                            return\n" +
        "                          end\n" +
        "                          get_local \$l4\n" +
        "                          i32.const 1075388923\n" +
        "                          i32.ne\n" +
        "                          br_if \$B8\n" +
        "                        end\n" +
        "                        get_local \$p1\n" +
        "                        get_local \$p0\n" +
        "                        get_local \$p0\n" +
        "                        f64.const 0x1.45f306dc9c883p-1 (;=0.63662;)\n" +
        "                        f64.mul\n" +
        "                        f64.const 0x1.8p+52 (;=6.7554e+15;)\n" +
        "                        f64.add\n" +
        "                        f64.const -0x1.8p+52 (;=-6.7554e+15;)\n" +
        "                        f64.add\n" +
        "                        tee_local \$l5\n" +
        "                        f64.const -0x1.921fb544p+0 (;=-1.5708;)\n" +
        "                        f64.mul\n" +
        "                        f64.add\n" +
        "                        tee_local \$l6\n" +
        "                        get_local \$l5\n" +
        "                        f64.const 0x1.0b4611a626331p-34 (;=6.0771e-11;)\n" +
        "                        f64.mul\n" +
        "                        tee_local \$l7\n" +
        "                        f64.sub\n" +
        "                        tee_local \$p0\n" +
        "                        f64.store\n" +
        "                        get_local \$l4\n" +
        "                        i32.const 20\n" +
        "                        i32.shr_u\n" +
        "                        tee_local \$l8\n" +
        "                        get_local \$p0\n" +
        "                        i64.reinterpret/f64\n" +
        "                        i64.const 52\n" +
        "                        i64.shr_u\n" +
        "                        i32.wrap/i64\n" +
        "                        i32.const 2047\n" +
        "                        i32.and\n" +
        "                        i32.sub\n" +
        "                        i32.const 17\n" +
        "                        i32.lt_s\n" +
        "                        set_local \$l3\n" +
        "                        block \$B15\n" +
        "                          block \$B16\n" +
        "                            block \$B17\n" +
        "                              get_local \$l5\n" +
        "                              f64.abs\n" +
        "                              f64.const 0x1p+31 (;=2.14748e+09;)\n" +
        "                              f64.lt\n" +
        "                              br_if \$B17\n" +
        "                              i32.const -2147483648\n" +
        "                              set_local \$l4\n" +
        "                              get_local \$l3\n" +
        "                              i32.eqz\n" +
        "                              br_if \$B16\n" +
        "                              br \$B15\n" +
        "                            end\n" +
        "                            get_local \$l5\n" +
        "                            i32.trunc_s/f64\n" +
        "                            set_local \$l4\n" +
        "                            get_local \$l3\n" +
        "                            br_if \$B15\n" +
        "                          end\n" +
        "                          get_local \$p1\n" +
        "                          get_local \$l6\n" +
        "                          get_local \$l5\n" +
        "                          f64.const 0x1.0b4611a6p-34 (;=6.0771e-11;)\n" +
        "                          f64.mul\n" +
        "                          tee_local \$p0\n" +
        "                          f64.sub\n" +
        "                          tee_local \$l9\n" +
        "                          get_local \$l5\n" +
        "                          f64.const 0x1.3198a2e037073p-69 (;=2.02227e-21;)\n" +
        "                          f64.mul\n" +
        "                          get_local \$l6\n" +
        "                          get_local \$l9\n" +
        "                          f64.sub\n" +
        "                          get_local \$p0\n" +
        "                          f64.sub\n" +
        "                          f64.sub\n" +
        "                          tee_local \$l7\n" +
        "                          f64.sub\n" +
        "                          tee_local \$p0\n" +
        "                          f64.store\n" +
        "                          block \$B18\n" +
        "                            get_local \$l8\n" +
        "                            get_local \$p0\n" +
        "                            i64.reinterpret/f64\n" +
        "                            i64.const 52\n" +
        "                            i64.shr_u\n" +
        "                            i32.wrap/i64\n" +
        "                            i32.const 2047\n" +
        "                            i32.and\n" +
        "                            i32.sub\n" +
        "                            i32.const 50\n" +
        "                            i32.lt_s\n" +
        "                            br_if \$B18\n" +
        "                            get_local \$p1\n" +
        "                            get_local \$l9\n" +
        "                            get_local \$l5\n" +
        "                            f64.const 0x1.3198a2ep-69 (;=2.02227e-21;)\n" +
        "                            f64.mul\n" +
        "                            tee_local \$p0\n" +
        "                            f64.sub\n" +
        "                            tee_local \$l6\n" +
        "                            get_local \$l5\n" +
        "                            f64.const 0x1.b839a252049c1p-104 (;=8.47843e-32;)\n" +
        "                            f64.mul\n" +
        "                            get_local \$l9\n" +
        "                            get_local \$l6\n" +
        "                            f64.sub\n" +
        "                            get_local \$p0\n" +
        "                            f64.sub\n" +
        "                            f64.sub\n" +
        "                            tee_local \$l7\n" +
        "                            f64.sub\n" +
        "                            tee_local \$p0\n" +
        "                            f64.store\n" +
        "                            br \$B15\n" +
        "                          end\n" +
        "                          get_local \$l9\n" +
        "                          set_local \$l6\n" +
        "                        end\n" +
        "                        get_local \$p1\n" +
        "                        get_local \$l6\n" +
        "                        get_local \$p0\n" +
        "                        f64.sub\n" +
        "                        get_local \$l7\n" +
        "                        f64.sub\n" +
        "                        f64.store offset=8\n" +
        "                        get_local \$l0\n" +
        "                        i32.const 48\n" +
        "                        i32.add\n" +
        "                        set_global \$g0\n" +
        "                        get_local \$l4\n" +
        "                        return\n" +
        "                      end\n" +
        "                      get_local \$l1\n" +
        "                      i64.const 4503599627370495\n" +
        "                      i64.and\n" +
        "                      i64.const 4710765210229538816\n" +
        "                      i64.or\n" +
        "                      f64.reinterpret/i64\n" +
        "                      tee_local \$p0\n" +
        "                      f64.abs\n" +
        "                      f64.const 0x1p+31 (;=2.14748e+09;)\n" +
        "                      f64.lt\n" +
        "                      br_if \$B5\n" +
        "                      i32.const -2147483648\n" +
        "                      set_local \$l3\n" +
        "                      br \$B4\n" +
        "                    end\n" +
        "                    get_local \$l2\n" +
        "                    i32.eqz\n" +
        "                    br_if \$B2\n" +
        "                    get_local \$p1\n" +
        "                    get_local \$p0\n" +
        "                    f64.const 0x1.921fb544p+2 (;=6.28319;)\n" +
        "                    f64.add\n" +
        "                    tee_local \$p0\n" +
        "                    f64.const 0x1.0b4611a626331p-32 (;=2.43084e-10;)\n" +
        "                    f64.add\n" +
        "                    tee_local \$l5\n" +
        "                    f64.store\n" +
        "                    get_local \$p1\n" +
        "                    get_local \$p0\n" +
        "                    get_local \$l5\n" +
        "                    f64.sub\n" +
        "                    f64.const 0x1.0b4611a626331p-32 (;=2.43084e-10;)\n" +
        "                    f64.add\n" +
        "                    f64.store offset=8\n" +
        "                    get_local \$l0\n" +
        "                    i32.const 48\n" +
        "                    i32.add\n" +
        "                    set_global \$g0\n" +
        "                    i32.const -4\n" +
        "                    return\n" +
        "                  end\n" +
        "                  get_local \$p1\n" +
        "                  get_local \$p0\n" +
        "                  f64.const -0x1.921fb544p+0 (;=-1.5708;)\n" +
        "                  f64.add\n" +
        "                  tee_local \$p0\n" +
        "                  f64.const -0x1.0b4611a626331p-34 (;=-6.0771e-11;)\n" +
        "                  f64.add\n" +
        "                  tee_local \$l5\n" +
        "                  f64.store\n" +
        "                  get_local \$p1\n" +
        "                  get_local \$p0\n" +
        "                  get_local \$l5\n" +
        "                  f64.sub\n" +
        "                  f64.const -0x1.0b4611a626331p-34 (;=-6.0771e-11;)\n" +
        "                  f64.add\n" +
        "                  f64.store offset=8\n" +
        "                  get_local \$l0\n" +
        "                  i32.const 48\n" +
        "                  i32.add\n" +
        "                  set_global \$g0\n" +
        "                  i32.const 1\n" +
        "                  return\n" +
        "                end\n" +
        "                get_local \$p1\n" +
        "                get_local \$p0\n" +
        "                f64.const -0x1.921fb544p+1 (;=-3.14159;)\n" +
        "                f64.add\n" +
        "                tee_local \$p0\n" +
        "                f64.const -0x1.0b4611a626331p-33 (;=-1.21542e-10;)\n" +
        "                f64.add\n" +
        "                tee_local \$l5\n" +
        "                f64.store\n" +
        "                get_local \$p1\n" +
        "                get_local \$p0\n" +
        "                get_local \$l5\n" +
        "                f64.sub\n" +
        "                f64.const -0x1.0b4611a626331p-33 (;=-1.21542e-10;)\n" +
        "                f64.add\n" +
        "                f64.store offset=8\n" +
        "                get_local \$l0\n" +
        "                i32.const 48\n" +
        "                i32.add\n" +
        "                set_global \$g0\n" +
        "                i32.const 2\n" +
        "                return\n" +
        "              end\n" +
        "              get_local \$p0\n" +
        "              i32.trunc_s/f64\n" +
        "              set_local \$l3\n" +
        "            end\n" +
        "            get_local \$l0\n" +
        "            get_local \$l3\n" +
        "            f64.convert_s/i32\n" +
        "            tee_local \$l5\n" +
        "            f64.store offset=16\n" +
        "            block \$B19\n" +
        "              block \$B20\n" +
        "                get_local \$p0\n" +
        "                get_local \$l5\n" +
        "                f64.sub\n" +
        "                f64.const 0x1p+24 (;=1.67772e+07;)\n" +
        "                f64.mul\n" +
        "                tee_local \$p0\n" +
        "                f64.abs\n" +
        "                f64.const 0x1p+31 (;=2.14748e+09;)\n" +
        "                f64.lt\n" +
        "                br_if \$B20\n" +
        "                i32.const -2147483648\n" +
        "                set_local \$l3\n" +
        "                br \$B19\n" +
        "              end\n" +
        "              get_local \$p0\n" +
        "              i32.trunc_s/f64\n" +
        "              set_local \$l3\n" +
        "            end\n" +
        "            get_local \$l0\n" +
        "            get_local \$l3\n" +
        "            f64.convert_s/i32\n" +
        "            tee_local \$l5\n" +
        "            f64.store offset=24\n" +
        "            get_local \$l0\n" +
        "            get_local \$p0\n" +
        "            get_local \$l5\n" +
        "            f64.sub\n" +
        "            f64.const 0x1p+24 (;=1.67772e+07;)\n" +
        "            f64.mul\n" +
        "            tee_local \$p0\n" +
        "            f64.store offset=32\n" +
        "            get_local \$p0\n" +
        "            f64.const 0x0p+0 (;=0;)\n" +
        "            f64.ne\n" +
        "            br_if \$B1\n" +
        "            get_local \$l0\n" +
        "            i32.const 16\n" +
        "            i32.add\n" +
        "            i32.const 8\n" +
        "            i32.or\n" +
        "            set_local \$l3\n" +
        "            i32.const 2\n" +
        "            set_local \$l8\n" +
        "            loop \$L21\n" +
        "              get_local \$l8\n" +
        "              i32.const -1\n" +
        "              i32.add\n" +
        "              set_local \$l8\n" +
        "              get_local \$l3\n" +
        "              f64.load\n" +
        "              set_local \$p0\n" +
        "              get_local \$l3\n" +
        "              i32.const -8\n" +
        "              i32.add\n" +
        "              set_local \$l3\n" +
        "              get_local \$p0\n" +
        "              f64.const 0x0p+0 (;=0;)\n" +
        "              f64.eq\n" +
        "              br_if \$L21\n" +
        "              br \$B0\n" +
        "            end\n" +
        "          end\n" +
        "          get_local \$p1\n" +
        "          get_local \$p0\n" +
        "          f64.const -0x1.2d97c7f3p+2 (;=-4.71239;)\n" +
        "          f64.add\n" +
        "          tee_local \$p0\n" +
        "          f64.const -0x1.90e91a79394cap-33 (;=-1.82313e-10;)\n" +
        "          f64.add\n" +
        "          tee_local \$l5\n" +
        "          f64.store\n" +
        "          get_local \$p1\n" +
        "          get_local \$p0\n" +
        "          get_local \$l5\n" +
        "          f64.sub\n" +
        "          f64.const -0x1.90e91a79394cap-33 (;=-1.82313e-10;)\n" +
        "          f64.add\n" +
        "          f64.store offset=8\n" +
        "          get_local \$l0\n" +
        "          i32.const 48\n" +
        "          i32.add\n" +
        "          set_global \$g0\n" +
        "          i32.const 3\n" +
        "          return\n" +
        "        end\n" +
        "        get_local \$p1\n" +
        "        get_local \$p0\n" +
        "        f64.const -0x1.921fb544p+2 (;=-6.28319;)\n" +
        "        f64.add\n" +
        "        tee_local \$p0\n" +
        "        f64.const -0x1.0b4611a626331p-32 (;=-2.43084e-10;)\n" +
        "        f64.add\n" +
        "        tee_local \$l5\n" +
        "        f64.store\n" +
        "        get_local \$p1\n" +
        "        get_local \$p0\n" +
        "        get_local \$l5\n" +
        "        f64.sub\n" +
        "        f64.const -0x1.0b4611a626331p-32 (;=-2.43084e-10;)\n" +
        "        f64.add\n" +
        "        f64.store offset=8\n" +
        "        get_local \$l0\n" +
        "        i32.const 48\n" +
        "        i32.add\n" +
        "        set_global \$g0\n" +
        "        i32.const 4\n" +
        "        return\n" +
        "      end\n" +
        "      i32.const 2\n" +
        "      set_local \$l8\n" +
        "    end\n" +
        "    get_local \$l0\n" +
        "    i32.const 16\n" +
        "    i32.add\n" +
        "    get_local \$l0\n" +
        "    get_local \$l4\n" +
        "    i32.const 20\n" +
        "    i32.shr_u\n" +
        "    i32.const -1046\n" +
        "    i32.add\n" +
        "    get_local \$l8\n" +
        "    i32.const 1\n" +
        "    i32.add\n" +
        "    i32.const 1\n" +
        "    call \$__rem_pio2_large\n" +
        "    set_local \$l3\n" +
        "    get_local \$l0\n" +
        "    f64.load\n" +
        "    set_local \$p0\n" +
        "    block \$B22\n" +
        "      get_local \$l2\n" +
        "      i32.eqz\n" +
        "      br_if \$B22\n" +
        "      get_local \$p1\n" +
        "      get_local \$p0\n" +
        "      f64.neg\n" +
        "      f64.store\n" +
        "      get_local \$p1\n" +
        "      get_local \$l0\n" +
        "      f64.load offset=8\n" +
        "      f64.neg\n" +
        "      f64.store offset=8\n" +
        "      get_local \$l0\n" +
        "      i32.const 48\n" +
        "      i32.add\n" +
        "      set_global \$g0\n" +
        "      i32.const 0\n" +
        "      get_local \$l3\n" +
        "      i32.sub\n" +
        "      return\n" +
        "    end\n" +
        "    get_local \$p1\n" +
        "    get_local \$p0\n" +
        "    f64.store\n" +
        "    get_local \$p1\n" +
        "    get_local \$l0\n" +
        "    i64.load offset=8\n" +
        "    i64.store offset=8\n" +
        "    get_local \$l0\n" +
        "    i32.const 48\n" +
        "    i32.add\n" +
        "    set_global \$g0\n" +
        "    get_local \$l3)\n" +
        "  (func \$__sin (type \$t5) (param \$p0 f64) (param \$p1 f64) (param \$p2 i32) (result f64)\n" +
        "    (local \$l0 f64) (local \$l1 f64) (local \$l2 f64)\n" +
        "    get_local \$p0\n" +
        "    get_local \$p0\n" +
        "    f64.mul\n" +
        "    tee_local \$l0\n" +
        "    get_local \$l0\n" +
        "    get_local \$l0\n" +
        "    f64.mul\n" +
        "    f64.mul\n" +
        "    get_local \$l0\n" +
        "    f64.const 0x1.5d93a5acfd57cp-33 (;=1.58969e-10;)\n" +
        "    f64.mul\n" +
        "    f64.const -0x1.ae5e68a2b9cebp-26 (;=-2.50508e-08;)\n" +
        "    f64.add\n" +
        "    f64.mul\n" +
        "    get_local \$l0\n" +
        "    get_local \$l0\n" +
        "    f64.const 0x1.71de357b1fe7dp-19 (;=2.75573e-06;)\n" +
        "    f64.mul\n" +
        "    f64.const -0x1.a01a019c161d5p-13 (;=-0.000198413;)\n" +
        "    f64.add\n" +
        "    f64.mul\n" +
        "    f64.const 0x1.111111110f8a6p-7 (;=0.00833333;)\n" +
        "    f64.add\n" +
        "    f64.add\n" +
        "    set_local \$l1\n" +
        "    get_local \$l0\n" +
        "    get_local \$p0\n" +
        "    f64.mul\n" +
        "    set_local \$l2\n" +
        "    block \$B0\n" +
        "      get_local \$p2\n" +
        "      i32.eqz\n" +
        "      br_if \$B0\n" +
        "      get_local \$p0\n" +
        "      get_local \$l2\n" +
        "      f64.const 0x1.5555555555549p-3 (;=0.166667;)\n" +
        "      f64.mul\n" +
        "      get_local \$l0\n" +
        "      get_local \$p1\n" +
        "      f64.const 0x1p-1 (;=0.5;)\n" +
        "      f64.mul\n" +
        "      get_local \$l2\n" +
        "      get_local \$l1\n" +
        "      f64.mul\n" +
        "      f64.sub\n" +
        "      f64.mul\n" +
        "      get_local \$p1\n" +
        "      f64.sub\n" +
        "      f64.add\n" +
        "      f64.sub\n" +
        "      return\n" +
        "    end\n" +
        "    get_local \$l2\n" +
        "    get_local \$l0\n" +
        "    get_local \$l1\n" +
        "    f64.mul\n" +
        "    f64.const -0x1.5555555555549p-3 (;=-0.166667;)\n" +
        "    f64.add\n" +
        "    f64.mul\n" +
        "    get_local \$p0\n" +
        "    f64.add)\n" +
        "  (func \$cos (type \$t1) (param \$p0 f64) (result f64)\n" +
        "    (local \$l0 i32) (local \$l1 i32) (local \$l2 f64)\n" +
        "    get_global \$g0\n" +
        "    i32.const 16\n" +
        "    i32.sub\n" +
        "    tee_local \$l0\n" +
        "    set_global \$g0\n" +
        "    block \$B0\n" +
        "      block \$B1\n" +
        "        block \$B2\n" +
        "          get_local \$p0\n" +
        "          i64.reinterpret/f64\n" +
        "          i64.const 32\n" +
        "          i64.shr_u\n" +
        "          i32.wrap/i64\n" +
        "          i32.const 2147483647\n" +
        "          i32.and\n" +
        "          tee_local \$l1\n" +
        "          i32.const 1072243195\n" +
        "          i32.gt_u\n" +
        "          br_if \$B2\n" +
        "          get_local \$l1\n" +
        "          i32.const 1044816029\n" +
        "          i32.gt_u\n" +
        "          br_if \$B1\n" +
        "          get_local \$l0\n" +
        "          get_local \$p0\n" +
        "          f64.const 0x1p+120 (;=1.32923e+36;)\n" +
        "          f64.add\n" +
        "          f64.store\n" +
        "          get_local \$l0\n" +
        "          i32.const 16\n" +
        "          i32.add\n" +
        "          set_global \$g0\n" +
        "          f64.const 0x1p+0 (;=1;)\n" +
        "          return\n" +
        "        end\n" +
        "        get_local \$l1\n" +
        "        i32.const 2146435072\n" +
        "        i32.lt_u\n" +
        "        br_if \$B0\n" +
        "        get_local \$l0\n" +
        "        i32.const 16\n" +
        "        i32.add\n" +
        "        set_global \$g0\n" +
        "        get_local \$p0\n" +
        "        get_local \$p0\n" +
        "        f64.sub\n" +
        "        return\n" +
        "      end\n" +
        "      get_local \$p0\n" +
        "      f64.const 0x0p+0 (;=0;)\n" +
        "      call \$__cos\n" +
        "      set_local \$p0\n" +
        "      get_local \$l0\n" +
        "      i32.const 16\n" +
        "      i32.add\n" +
        "      set_global \$g0\n" +
        "      get_local \$p0\n" +
        "      return\n" +
        "    end\n" +
        "    get_local \$p0\n" +
        "    get_local \$l0\n" +
        "    call \$__rem_pio2\n" +
        "    set_local \$l1\n" +
        "    get_local \$l0\n" +
        "    f64.load offset=8\n" +
        "    set_local \$p0\n" +
        "    get_local \$l0\n" +
        "    f64.load\n" +
        "    set_local \$l2\n" +
        "    block \$B3\n" +
        "      block \$B4\n" +
        "        block \$B5\n" +
        "          get_local \$l1\n" +
        "          i32.const 3\n" +
        "          i32.and\n" +
        "          tee_local \$l1\n" +
        "          i32.const 2\n" +
        "          i32.eq\n" +
        "          br_if \$B5\n" +
        "          get_local \$l1\n" +
        "          i32.const 1\n" +
        "          i32.eq\n" +
        "          br_if \$B4\n" +
        "          get_local \$l1\n" +
        "          br_if \$B3\n" +
        "          get_local \$l2\n" +
        "          get_local \$p0\n" +
        "          call \$__cos\n" +
        "          set_local \$p0\n" +
        "          get_local \$l0\n" +
        "          i32.const 16\n" +
        "          i32.add\n" +
        "          set_global \$g0\n" +
        "          get_local \$p0\n" +
        "          return\n" +
        "        end\n" +
        "        get_local \$l2\n" +
        "        get_local \$p0\n" +
        "        call \$__cos\n" +
        "        set_local \$p0\n" +
        "        get_local \$l0\n" +
        "        i32.const 16\n" +
        "        i32.add\n" +
        "        set_global \$g0\n" +
        "        get_local \$p0\n" +
        "        f64.neg\n" +
        "        return\n" +
        "      end\n" +
        "      get_local \$l2\n" +
        "      get_local \$p0\n" +
        "      i32.const 1\n" +
        "      call \$__sin\n" +
        "      set_local \$p0\n" +
        "      get_local \$l0\n" +
        "      i32.const 16\n" +
        "      i32.add\n" +
        "      set_global \$g0\n" +
        "      get_local \$p0\n" +
        "      f64.neg\n" +
        "      return\n" +
        "    end\n" +
        "    get_local \$l2\n" +
        "    get_local \$p0\n" +
        "    i32.const 1\n" +
        "    call \$__sin\n" +
        "    set_local \$p0\n" +
        "    get_local \$l0\n" +
        "    i32.const 16\n" +
        "    i32.add\n" +
        "    set_global \$g0\n" +
        "    get_local \$p0)\n" +
        "  (func \$__expo2 (type \$t1) (param \$p0 f64) (result f64)\n" +
        "    get_local \$p0\n" +
        "    f64.const -0x1.62066151add8bp+10 (;=-1416.1;)\n" +
        "    f64.add\n" +
        "    call \$exp\n" +
        "    f64.const 0x1p+1021 (;=2.24712e+307;)\n" +
        "    f64.mul\n" +
        "    f64.const 0x1p+1021 (;=2.24712e+307;)\n" +
        "    f64.mul)\n" +
        "  (func \$cosh (type \$t1) (param \$p0 f64) (result f64)\n" +
        "    (local \$l0 i32) (local \$l1 i64) (local \$l2 i32)\n" +
        "    get_global \$g0\n" +
        "    i32.const 16\n" +
        "    i32.sub\n" +
        "    tee_local \$l0\n" +
        "    set_global \$g0\n" +
        "    get_local \$p0\n" +
        "    i64.reinterpret/f64\n" +
        "    i64.const 9223372036854775807\n" +
        "    i64.and\n" +
        "    tee_local \$l1\n" +
        "    f64.reinterpret/i64\n" +
        "    set_local \$p0\n" +
        "    block \$B0\n" +
        "      block \$B1\n" +
        "        block \$B2\n" +
        "          get_local \$l1\n" +
        "          i64.const 32\n" +
        "          i64.shr_u\n" +
        "          i32.wrap/i64\n" +
        "          tee_local \$l2\n" +
        "          i32.const 1072049729\n" +
        "          i32.gt_u\n" +
        "          br_if \$B2\n" +
        "          get_local \$l2\n" +
        "          i32.const 1045430271\n" +
        "          i32.gt_u\n" +
        "          br_if \$B1\n" +
        "          get_local \$l0\n" +
        "          get_local \$p0\n" +
        "          f64.const 0x1p+120 (;=1.32923e+36;)\n" +
        "          f64.add\n" +
        "          f64.store offset=8\n" +
        "          get_local \$l0\n" +
        "          i32.const 16\n" +
        "          i32.add\n" +
        "          set_global \$g0\n" +
        "          f64.const 0x1p+0 (;=1;)\n" +
        "          return\n" +
        "        end\n" +
        "        get_local \$l2\n" +
        "        i32.const 1082535489\n" +
        "        i32.gt_u\n" +
        "        br_if \$B0\n" +
        "        get_local \$p0\n" +
        "        call \$exp\n" +
        "        set_local \$p0\n" +
        "        get_local \$l0\n" +
        "        i32.const 16\n" +
        "        i32.add\n" +
        "        set_global \$g0\n" +
        "        get_local \$p0\n" +
        "        f64.const 0x1p+0 (;=1;)\n" +
        "        get_local \$p0\n" +
        "        f64.div\n" +
        "        f64.add\n" +
        "        f64.const 0x1p-1 (;=0.5;)\n" +
        "        f64.mul\n" +
        "        return\n" +
        "      end\n" +
        "      get_local \$l0\n" +
        "      i32.const 16\n" +
        "      i32.add\n" +
        "      set_global \$g0\n" +
        "      get_local \$p0\n" +
        "      call \$expm1\n" +
        "      tee_local \$p0\n" +
        "      get_local \$p0\n" +
        "      f64.mul\n" +
        "      get_local \$p0\n" +
        "      f64.const 0x1p+0 (;=1;)\n" +
        "      f64.add\n" +
        "      tee_local \$p0\n" +
        "      get_local \$p0\n" +
        "      f64.add\n" +
        "      f64.div\n" +
        "      f64.const 0x1p+0 (;=1;)\n" +
        "      f64.add\n" +
        "      return\n" +
        "    end\n" +
        "    get_local \$p0\n" +
        "    call \$__expo2\n" +
        "    set_local \$p0\n" +
        "    get_local \$l0\n" +
        "    i32.const 16\n" +
        "    i32.add\n" +
        "    set_global \$g0\n" +
        "    get_local \$p0)\n" +
        "  (func \$exp (type \$t1) (param \$p0 f64) (result f64)\n" +
        "    (local \$l0 i32) (local \$l1 i64) (local \$l2 i32) (local \$l3 i32) (local \$l4 f64) (local \$l5 f64) (local \$l6 f64)\n" +
        "    get_global \$g0\n" +
        "    i32.const 16\n" +
        "    i32.sub\n" +
        "    tee_local \$l0\n" +
        "    set_global \$g0\n" +
        "    get_local \$p0\n" +
        "    i64.reinterpret/f64\n" +
        "    tee_local \$l1\n" +
        "    i64.const 63\n" +
        "    i64.shr_u\n" +
        "    i32.wrap/i64\n" +
        "    set_local \$l2\n" +
        "    block \$B0\n" +
        "      block \$B1\n" +
        "        block \$B2\n" +
        "          block \$B3\n" +
        "            block \$B4\n" +
        "              block \$B5\n" +
        "                block \$B6\n" +
        "                  block \$B7\n" +
        "                    block \$B8\n" +
        "                      block \$B9\n" +
        "                        get_local \$l1\n" +
        "                        i64.const 32\n" +
        "                        i64.shr_u\n" +
        "                        i32.wrap/i64\n" +
        "                        i32.const 2147483647\n" +
        "                        i32.and\n" +
        "                        tee_local \$l3\n" +
        "                        i32.const 1082532651\n" +
        "                        i32.lt_u\n" +
        "                        br_if \$B9\n" +
        "                        get_local \$l1\n" +
        "                        i64.const 9223372036854775807\n" +
        "                        i64.and\n" +
        "                        i64.const 9218868437227405312\n" +
        "                        i64.le_u\n" +
        "                        br_if \$B8\n" +
        "                        get_local \$l0\n" +
        "                        i32.const 16\n" +
        "                        i32.add\n" +
        "                        set_global \$g0\n" +
        "                        get_local \$p0\n" +
        "                        return\n" +
        "                      end\n" +
        "                      get_local \$l3\n" +
        "                      i32.const 1071001155\n" +
        "                      i32.lt_u\n" +
        "                      br_if \$B7\n" +
        "                      get_local \$l3\n" +
        "                      i32.const 1072734898\n" +
        "                      i32.ge_u\n" +
        "                      br_if \$B5\n" +
        "                      get_local \$l2\n" +
        "                      i32.const 1\n" +
        "                      i32.xor\n" +
        "                      get_local \$l2\n" +
        "                      i32.sub\n" +
        "                      set_local \$l3\n" +
        "                      br \$B2\n" +
        "                    end\n" +
        "                    get_local \$p0\n" +
        "                    f64.const 0x1.62e42fefa39efp+9 (;=709.783;)\n" +
        "                    f64.gt\n" +
        "                    i32.const 1\n" +
        "                    i32.xor\n" +
        "                    br_if \$B6\n" +
        "                    get_local \$l0\n" +
        "                    i32.const 16\n" +
        "                    i32.add\n" +
        "                    set_global \$g0\n" +
        "                    get_local \$p0\n" +
        "                    f64.const 0x1p+1023 (;=8.98847e+307;)\n" +
        "                    f64.mul\n" +
        "                    return\n" +
        "                  end\n" +
        "                  get_local \$l3\n" +
        "                  i32.const 1043333120\n" +
        "                  i32.le_u\n" +
        "                  br_if \$B4\n" +
        "                  i32.const 0\n" +
        "                  set_local \$l3\n" +
        "                  f64.const 0x0p+0 (;=0;)\n" +
        "                  set_local \$l4\n" +
        "                  get_local \$p0\n" +
        "                  set_local \$l5\n" +
        "                  br \$B1\n" +
        "                end\n" +
        "                get_local \$p0\n" +
        "                f64.const -0x1.6232bdd7abcd2p+9 (;=-708.396;)\n" +
        "                f64.lt\n" +
        "                i32.const 1\n" +
        "                i32.xor\n" +
        "                br_if \$B5\n" +
        "                get_local \$l0\n" +
        "                f64.const -0x1p-149 (;=-1.4013e-45;)\n" +
        "                get_local \$p0\n" +
        "                f64.div\n" +
        "                f32.demote/f64\n" +
        "                f32.store offset=12\n" +
        "                f64.const 0x0p+0 (;=0;)\n" +
        "                set_local \$l6\n" +
        "                get_local \$p0\n" +
        "                f64.const -0x1.74910d52d3051p+9 (;=-745.133;)\n" +
        "                f64.lt\n" +
        "                br_if \$B0\n" +
        "              end\n" +
        "              get_local \$p0\n" +
        "              f64.const 0x1.71547652b82fep+0 (;=1.4427;)\n" +
        "              f64.mul\n" +
        "              get_local \$l2\n" +
        "              i32.const 3\n" +
        "              i32.shl\n" +
        "              i32.const 3936\n" +
        "              i32.add\n" +
        "              f64.load\n" +
        "              f64.add\n" +
        "              tee_local \$l6\n" +
        "              f64.abs\n" +
        "              f64.const 0x1p+31 (;=2.14748e+09;)\n" +
        "              f64.lt\n" +
        "              br_if \$B3\n" +
        "              i32.const -2147483648\n" +
        "              set_local \$l3\n" +
        "              br \$B2\n" +
        "            end\n" +
        "            get_local \$l0\n" +
        "            get_local \$p0\n" +
        "            f64.const 0x1p+1023 (;=8.98847e+307;)\n" +
        "            f64.add\n" +
        "            f64.store\n" +
        "            get_local \$l0\n" +
        "            i32.const 16\n" +
        "            i32.add\n" +
        "            set_global \$g0\n" +
        "            get_local \$p0\n" +
        "            f64.const 0x1p+0 (;=1;)\n" +
        "            f64.add\n" +
        "            return\n" +
        "          end\n" +
        "          get_local \$l6\n" +
        "          i32.trunc_s/f64\n" +
        "          set_local \$l3\n" +
        "        end\n" +
        "        get_local \$p0\n" +
        "        get_local \$l3\n" +
        "        f64.convert_s/i32\n" +
        "        tee_local \$l6\n" +
        "        f64.const -0x1.62e42feep-1 (;=-0.693147;)\n" +
        "        f64.mul\n" +
        "        f64.add\n" +
        "        tee_local \$p0\n" +
        "        get_local \$l6\n" +
        "        f64.const 0x1.a39ef35793c76p-33 (;=1.90821e-10;)\n" +
        "        f64.mul\n" +
        "        tee_local \$l4\n" +
        "        f64.sub\n" +
        "        set_local \$l5\n" +
        "      end\n" +
        "      get_local \$p0\n" +
        "      get_local \$l5\n" +
        "      get_local \$l5\n" +
        "      get_local \$l5\n" +
        "      get_local \$l5\n" +
        "      f64.mul\n" +
        "      tee_local \$l6\n" +
        "      get_local \$l6\n" +
        "      get_local \$l6\n" +
        "      get_local \$l6\n" +
        "      get_local \$l6\n" +
        "      f64.const 0x1.6376972bea4dp-25 (;=4.13814e-08;)\n" +
        "      f64.mul\n" +
        "      f64.const -0x1.bbd41c5d26bf1p-20 (;=-1.65339e-06;)\n" +
        "      f64.add\n" +
        "      f64.mul\n" +
        "      f64.const 0x1.1566aaf25de2cp-14 (;=6.61376e-05;)\n" +
        "      f64.add\n" +
        "      f64.mul\n" +
        "      f64.const -0x1.6c16c16bebd93p-9 (;=-0.00277778;)\n" +
        "      f64.add\n" +
        "      f64.mul\n" +
        "      f64.const 0x1.555555555553ep-3 (;=0.166667;)\n" +
        "      f64.add\n" +
        "      f64.mul\n" +
        "      f64.sub\n" +
        "      tee_local \$l6\n" +
        "      f64.mul\n" +
        "      f64.const 0x1p+1 (;=2;)\n" +
        "      get_local \$l6\n" +
        "      f64.sub\n" +
        "      f64.div\n" +
        "      get_local \$l4\n" +
        "      f64.sub\n" +
        "      f64.add\n" +
        "      f64.const 0x1p+0 (;=1;)\n" +
        "      f64.add\n" +
        "      set_local \$l6\n" +
        "      get_local \$l3\n" +
        "      i32.eqz\n" +
        "      br_if \$B0\n" +
        "      get_local \$l6\n" +
        "      get_local \$l3\n" +
        "      call \$scalbn\n" +
        "      set_local \$l6\n" +
        "    end\n" +
        "    get_local \$l0\n" +
        "    i32.const 16\n" +
        "    i32.add\n" +
        "    set_global \$g0\n" +
        "    get_local \$l6)\n" +
        "  (func \$expm1 (type \$t1) (param \$p0 f64) (result f64)\n" +
        "    (local \$l0 i32) (local \$l1 i64) (local \$l2 i32) (local \$l3 i32) (local \$l4 f64) (local \$l5 f64) (local \$l6 f64) (local \$l7 f64)\n" +
        "    get_global \$g0\n" +
        "    i32.const 16\n" +
        "    i32.sub\n" +
        "    set_local \$l0\n" +
        "    get_local \$p0\n" +
        "    i64.reinterpret/f64\n" +
        "    tee_local \$l1\n" +
        "    i64.const 63\n" +
        "    i64.shr_u\n" +
        "    i32.wrap/i64\n" +
        "    set_local \$l2\n" +
        "    block \$B0\n" +
        "      block \$B1\n" +
        "        block \$B2\n" +
        "          block \$B3\n" +
        "            block \$B4\n" +
        "              block \$B5\n" +
        "                block \$B6\n" +
        "                  block \$B7\n" +
        "                    block \$B8\n" +
        "                      block \$B9\n" +
        "                        block \$B10\n" +
        "                          block \$B11\n" +
        "                            block \$B12\n" +
        "                              get_local \$l1\n" +
        "                              i64.const 32\n" +
        "                              i64.shr_u\n" +
        "                              i32.wrap/i64\n" +
        "                              i32.const 2147483647\n" +
        "                              i32.and\n" +
        "                              tee_local \$l3\n" +
        "                              i32.const 1078159482\n" +
        "                              i32.lt_u\n" +
        "                              br_if \$B12\n" +
        "                              get_local \$l1\n" +
        "                              i64.const 9223372036854775807\n" +
        "                              i64.and\n" +
        "                              i64.const 9218868437227405312\n" +
        "                              i64.le_u\n" +
        "                              br_if \$B11\n" +
        "                              get_local \$p0\n" +
        "                              return\n" +
        "                            end\n" +
        "                            get_local \$l3\n" +
        "                            i32.const 1071001155\n" +
        "                            i32.lt_u\n" +
        "                            br_if \$B10\n" +
        "                            get_local \$l3\n" +
        "                            i32.const 1072734897\n" +
        "                            i32.gt_u\n" +
        "                            br_if \$B9\n" +
        "                            get_local \$l2\n" +
        "                            i32.eqz\n" +
        "                            br_if \$B5\n" +
        "                            get_local \$p0\n" +
        "                            f64.const 0x1.62e42feep-1 (;=0.693147;)\n" +
        "                            f64.add\n" +
        "                            set_local \$l4\n" +
        "                            i32.const -1\n" +
        "                            set_local \$l3\n" +
        "                            f64.const -0x1.a39ef35793c76p-33 (;=-1.90821e-10;)\n" +
        "                            set_local \$l5\n" +
        "                            br \$B1\n" +
        "                          end\n" +
        "                          get_local \$l2\n" +
        "                          i32.eqz\n" +
        "                          br_if \$B7\n" +
        "                          f64.const -0x1p+0 (;=-1;)\n" +
        "                          return\n" +
        "                        end\n" +
        "                        get_local \$l3\n" +
        "                        i32.const 1016070143\n" +
        "                        i32.gt_u\n" +
        "                        br_if \$B8\n" +
        "                        get_local \$l3\n" +
        "                        i32.const 1048575\n" +
        "                        i32.gt_u\n" +
        "                        br_if \$B6\n" +
        "                        get_local \$l0\n" +
        "                        get_local \$p0\n" +
        "                        f32.demote/f64\n" +
        "                        f32.store offset=12\n" +
        "                        get_local \$p0\n" +
        "                        return\n" +
        "                      end\n" +
        "                      get_local \$p0\n" +
        "                      f64.const 0x1.71547652b82fep+0 (;=1.4427;)\n" +
        "                      f64.mul\n" +
        "                      set_local \$l5\n" +
        "                      f64.const -0x1p-1 (;=-0.5;)\n" +
        "                      set_local \$l4\n" +
        "                      get_local \$l2\n" +
        "                      br_if \$B2\n" +
        "                      br \$B3\n" +
        "                    end\n" +
        "                    i32.const 0\n" +
        "                    set_local \$l3\n" +
        "                    br \$B0\n" +
        "                  end\n" +
        "                  get_local \$p0\n" +
        "                  f64.const 0x1.62e42fefa39efp+9 (;=709.783;)\n" +
        "                  f64.gt\n" +
        "                  i32.eqz\n" +
        "                  br_if \$B4\n" +
        "                  get_local \$p0\n" +
        "                  f64.const 0x1p+1023 (;=8.98847e+307;)\n" +
        "                  f64.mul\n" +
        "                  return\n" +
        "                end\n" +
        "                get_local \$p0\n" +
        "                return\n" +
        "              end\n" +
        "              get_local \$p0\n" +
        "              f64.const -0x1.62e42feep-1 (;=-0.693147;)\n" +
        "              f64.add\n" +
        "              set_local \$l4\n" +
        "              i32.const 1\n" +
        "              set_local \$l3\n" +
        "              f64.const 0x1.a39ef35793c76p-33 (;=1.90821e-10;)\n" +
        "              set_local \$l5\n" +
        "              br \$B1\n" +
        "            end\n" +
        "            get_local \$p0\n" +
        "            f64.const 0x1.71547652b82fep+0 (;=1.4427;)\n" +
        "            f64.mul\n" +
        "            set_local \$l5\n" +
        "          end\n" +
        "          f64.const 0x1p-1 (;=0.5;)\n" +
        "          set_local \$l4\n" +
        "        end\n" +
        "        block \$B13\n" +
        "          block \$B14\n" +
        "            get_local \$l5\n" +
        "            get_local \$l4\n" +
        "            f64.add\n" +
        "            tee_local \$l4\n" +
        "            f64.abs\n" +
        "            f64.const 0x1p+31 (;=2.14748e+09;)\n" +
        "            f64.lt\n" +
        "            br_if \$B14\n" +
        "            i32.const -2147483648\n" +
        "            set_local \$l3\n" +
        "            br \$B13\n" +
        "          end\n" +
        "          get_local \$l4\n" +
        "          i32.trunc_s/f64\n" +
        "          set_local \$l3\n" +
        "        end\n" +
        "        get_local \$l3\n" +
        "        f64.convert_s/i32\n" +
        "        tee_local \$l4\n" +
        "        f64.const 0x1.a39ef35793c76p-33 (;=1.90821e-10;)\n" +
        "        f64.mul\n" +
        "        set_local \$l5\n" +
        "        get_local \$p0\n" +
        "        get_local \$l4\n" +
        "        f64.const -0x1.62e42feep-1 (;=-0.693147;)\n" +
        "        f64.mul\n" +
        "        f64.add\n" +
        "        set_local \$l4\n" +
        "      end\n" +
        "      get_local \$l4\n" +
        "      get_local \$l4\n" +
        "      get_local \$l5\n" +
        "      f64.sub\n" +
        "      tee_local \$p0\n" +
        "      f64.sub\n" +
        "      get_local \$l5\n" +
        "      f64.sub\n" +
        "      set_local \$l5\n" +
        "    end\n" +
        "    get_local \$p0\n" +
        "    get_local \$p0\n" +
        "    f64.const 0x1p-1 (;=0.5;)\n" +
        "    f64.mul\n" +
        "    tee_local \$l6\n" +
        "    f64.mul\n" +
        "    tee_local \$l4\n" +
        "    get_local \$l4\n" +
        "    get_local \$l4\n" +
        "    get_local \$l4\n" +
        "    get_local \$l4\n" +
        "    get_local \$l4\n" +
        "    f64.const -0x1.afdb76e09c32dp-23 (;=-2.01099e-07;)\n" +
        "    f64.mul\n" +
        "    f64.const 0x1.0cfca86e65239p-18 (;=4.00822e-06;)\n" +
        "    f64.add\n" +
        "    f64.mul\n" +
        "    f64.const -0x1.4ce199eaadbb7p-14 (;=-7.93651e-05;)\n" +
        "    f64.add\n" +
        "    f64.mul\n" +
        "    f64.const 0x1.a01a019fe5585p-10 (;=0.0015873;)\n" +
        "    f64.add\n" +
        "    f64.mul\n" +
        "    f64.const -0x1.11111111110f4p-5 (;=-0.0333333;)\n" +
        "    f64.add\n" +
        "    f64.mul\n" +
        "    f64.const 0x1p+0 (;=1;)\n" +
        "    f64.add\n" +
        "    tee_local \$l7\n" +
        "    f64.const 0x1.8p+1 (;=3;)\n" +
        "    get_local \$l6\n" +
        "    get_local \$l7\n" +
        "    f64.mul\n" +
        "    f64.sub\n" +
        "    tee_local \$l6\n" +
        "    f64.sub\n" +
        "    f64.const 0x1.8p+2 (;=6;)\n" +
        "    get_local \$p0\n" +
        "    get_local \$l6\n" +
        "    f64.mul\n" +
        "    f64.sub\n" +
        "    f64.div\n" +
        "    f64.mul\n" +
        "    set_local \$l6\n" +
        "    block \$B15\n" +
        "      block \$B16\n" +
        "        block \$B17\n" +
        "          block \$B18\n" +
        "            block \$B19\n" +
        "              get_local \$l3\n" +
        "              i32.eqz\n" +
        "              br_if \$B19\n" +
        "              get_local \$p0\n" +
        "              get_local \$l6\n" +
        "              get_local \$l5\n" +
        "              f64.sub\n" +
        "              f64.mul\n" +
        "              get_local \$l5\n" +
        "              f64.sub\n" +
        "              get_local \$l4\n" +
        "              f64.sub\n" +
        "              set_local \$l4\n" +
        "              get_local \$l3\n" +
        "              i32.const 1\n" +
        "              i32.eq\n" +
        "              br_if \$B18\n" +
        "              get_local \$l3\n" +
        "              i32.const -1\n" +
        "              i32.ne\n" +
        "              br_if \$B17\n" +
        "              get_local \$p0\n" +
        "              get_local \$l4\n" +
        "              f64.sub\n" +
        "              f64.const 0x1p-1 (;=0.5;)\n" +
        "              f64.mul\n" +
        "              f64.const -0x1p-1 (;=-0.5;)\n" +
        "              f64.add\n" +
        "              return\n" +
        "            end\n" +
        "            get_local \$p0\n" +
        "            get_local \$p0\n" +
        "            get_local \$l6\n" +
        "            f64.mul\n" +
        "            get_local \$l4\n" +
        "            f64.sub\n" +
        "            f64.sub\n" +
        "            return\n" +
        "          end\n" +
        "          get_local \$p0\n" +
        "          f64.const -0x1p-2 (;=-0.25;)\n" +
        "          f64.lt\n" +
        "          i32.const 1\n" +
        "          i32.xor\n" +
        "          br_if \$B16\n" +
        "          get_local \$l4\n" +
        "          get_local \$p0\n" +
        "          f64.const 0x1p-1 (;=0.5;)\n" +
        "          f64.add\n" +
        "          f64.sub\n" +
        "          f64.const -0x1p+1 (;=-2;)\n" +
        "          f64.mul\n" +
        "          return\n" +
        "        end\n" +
        "        get_local \$l3\n" +
        "        i32.const 1023\n" +
        "        i32.add\n" +
        "        i64.extend_u/i32\n" +
        "        i64.const 52\n" +
        "        i64.shl\n" +
        "        f64.reinterpret/i64\n" +
        "        set_local \$l5\n" +
        "        get_local \$l3\n" +
        "        i32.const 57\n" +
        "        i32.lt_u\n" +
        "        br_if \$B15\n" +
        "        get_local \$p0\n" +
        "        get_local \$l4\n" +
        "        f64.sub\n" +
        "        f64.const 0x1p+0 (;=1;)\n" +
        "        f64.add\n" +
        "        tee_local \$p0\n" +
        "        get_local \$p0\n" +
        "        f64.add\n" +
        "        f64.const 0x1p+1023 (;=8.98847e+307;)\n" +
        "        f64.mul\n" +
        "        get_local \$p0\n" +
        "        get_local \$l5\n" +
        "        f64.mul\n" +
        "        get_local \$l3\n" +
        "        i32.const 1024\n" +
        "        i32.eq\n" +
        "        select\n" +
        "        f64.const -0x1p+0 (;=-1;)\n" +
        "        f64.add\n" +
        "        return\n" +
        "      end\n" +
        "      get_local \$p0\n" +
        "      get_local \$l4\n" +
        "      f64.sub\n" +
        "      tee_local \$p0\n" +
        "      get_local \$p0\n" +
        "      f64.add\n" +
        "      f64.const 0x1p+0 (;=1;)\n" +
        "      f64.add\n" +
        "      return\n" +
        "    end\n" +
        "    f64.const 0x1p+0 (;=1;)\n" +
        "    i32.const 1023\n" +
        "    get_local \$l3\n" +
        "    i32.sub\n" +
        "    i64.extend_u/i32\n" +
        "    i64.const 52\n" +
        "    i64.shl\n" +
        "    f64.reinterpret/i64\n" +
        "    tee_local \$l6\n" +
        "    f64.sub\n" +
        "    get_local \$p0\n" +
        "    get_local \$l4\n" +
        "    get_local \$l6\n" +
        "    f64.add\n" +
        "    f64.sub\n" +
        "    get_local \$l3\n" +
        "    i32.const 20\n" +
        "    i32.lt_s\n" +
        "    tee_local \$l3\n" +
        "    select\n" +
        "    get_local \$p0\n" +
        "    get_local \$l4\n" +
        "    f64.sub\n" +
        "    f64.const 0x1p+0 (;=1;)\n" +
        "    get_local \$l3\n" +
        "    select\n" +
        "    f64.add\n" +
        "    get_local \$l5\n" +
        "    f64.mul)\n" +
        "  (func \$log (type \$t1) (param \$p0 f64) (result f64)\n" +
        "    (local \$l0 i64) (local \$l1 i32) (local \$l2 i32) (local \$l3 i32) (local \$l4 f64) (local \$l5 f64)\n" +
        "    block \$B0\n" +
        "      block \$B1\n" +
        "        block \$B2\n" +
        "          block \$B3\n" +
        "            block \$B4\n" +
        "              get_local \$p0\n" +
        "              i64.reinterpret/f64\n" +
        "              tee_local \$l0\n" +
        "              i64.const 0\n" +
        "              i64.lt_s\n" +
        "              br_if \$B4\n" +
        "              get_local \$l0\n" +
        "              i64.const 32\n" +
        "              i64.shr_u\n" +
        "              i32.wrap/i64\n" +
        "              tee_local \$l1\n" +
        "              i32.const 1048575\n" +
        "              i32.le_u\n" +
        "              br_if \$B4\n" +
        "              get_local \$l1\n" +
        "              i32.const 2146435071\n" +
        "              i32.gt_u\n" +
        "              br_if \$B1\n" +
        "              i32.const 1072693248\n" +
        "              set_local \$l2\n" +
        "              i32.const -1023\n" +
        "              set_local \$l3\n" +
        "              get_local \$l1\n" +
        "              i32.const 1072693248\n" +
        "              i32.ne\n" +
        "              br_if \$B3\n" +
        "              get_local \$l0\n" +
        "              i32.wrap/i64\n" +
        "              br_if \$B2\n" +
        "              f64.const 0x0p+0 (;=0;)\n" +
        "              return\n" +
        "            end\n" +
        "            block \$B5\n" +
        "              get_local \$l0\n" +
        "              i64.const 9223372036854775807\n" +
        "              i64.and\n" +
        "              i64.const 0\n" +
        "              i64.eq\n" +
        "              br_if \$B5\n" +
        "              get_local \$l0\n" +
        "              i64.const -1\n" +
        "              i64.le_s\n" +
        "              br_if \$B0\n" +
        "              get_local \$p0\n" +
        "              f64.const 0x1p+54 (;=1.80144e+16;)\n" +
        "              f64.mul\n" +
        "              i64.reinterpret/f64\n" +
        "              tee_local \$l0\n" +
        "              i64.const 32\n" +
        "              i64.shr_u\n" +
        "              i32.wrap/i64\n" +
        "              set_local \$l2\n" +
        "              i32.const -1077\n" +
        "              set_local \$l3\n" +
        "              br \$B2\n" +
        "            end\n" +
        "            f64.const -0x1p+0 (;=-1;)\n" +
        "            get_local \$p0\n" +
        "            get_local \$p0\n" +
        "            f64.mul\n" +
        "            f64.div\n" +
        "            return\n" +
        "          end\n" +
        "          get_local \$l1\n" +
        "          set_local \$l2\n" +
        "        end\n" +
        "        get_local \$l3\n" +
        "        get_local \$l2\n" +
        "        i32.const 614242\n" +
        "        i32.add\n" +
        "        tee_local \$l1\n" +
        "        i32.const 20\n" +
        "        i32.shr_u\n" +
        "        i32.add\n" +
        "        f64.convert_s/i32\n" +
        "        tee_local \$l4\n" +
        "        f64.const 0x1.62e42feep-1 (;=0.693147;)\n" +
        "        f64.mul\n" +
        "        get_local \$l1\n" +
        "        i32.const 1048575\n" +
        "        i32.and\n" +
        "        i32.const 1072079006\n" +
        "        i32.add\n" +
        "        i64.extend_u/i32\n" +
        "        i64.const 32\n" +
        "        i64.shl\n" +
        "        get_local \$l0\n" +
        "        i64.const 4294967295\n" +
        "        i64.and\n" +
        "        i64.or\n" +
        "        f64.reinterpret/i64\n" +
        "        f64.const -0x1p+0 (;=-1;)\n" +
        "        f64.add\n" +
        "        tee_local \$p0\n" +
        "        get_local \$l4\n" +
        "        f64.const 0x1.a39ef35793c76p-33 (;=1.90821e-10;)\n" +
        "        f64.mul\n" +
        "        get_local \$p0\n" +
        "        get_local \$p0\n" +
        "        f64.const 0x1p+1 (;=2;)\n" +
        "        f64.add\n" +
        "        f64.div\n" +
        "        tee_local \$l4\n" +
        "        get_local \$p0\n" +
        "        get_local \$p0\n" +
        "        f64.const 0x1p-1 (;=0.5;)\n" +
        "        f64.mul\n" +
        "        f64.mul\n" +
        "        tee_local \$l5\n" +
        "        get_local \$l4\n" +
        "        get_local \$l4\n" +
        "        f64.mul\n" +
        "        tee_local \$l4\n" +
        "        get_local \$l4\n" +
        "        f64.mul\n" +
        "        tee_local \$p0\n" +
        "        get_local \$p0\n" +
        "        get_local \$p0\n" +
        "        f64.const 0x1.39a09d078c69fp-3 (;=0.153138;)\n" +
        "        f64.mul\n" +
        "        f64.const 0x1.c71c51d8e78afp-3 (;=0.222222;)\n" +
        "        f64.add\n" +
        "        f64.mul\n" +
        "        f64.const 0x1.999999997fa04p-2 (;=0.4;)\n" +
        "        f64.add\n" +
        "        f64.mul\n" +
        "        get_local \$l4\n" +
        "        get_local \$p0\n" +
        "        get_local \$p0\n" +
        "        get_local \$p0\n" +
        "        f64.const 0x1.2f112df3e5244p-3 (;=0.147982;)\n" +
        "        f64.mul\n" +
        "        f64.const 0x1.7466496cb03dep-3 (;=0.181836;)\n" +
        "        f64.add\n" +
        "        f64.mul\n" +
        "        f64.const 0x1.2492494229359p-2 (;=0.285714;)\n" +
        "        f64.add\n" +
        "        f64.mul\n" +
        "        f64.const 0x1.5555555555593p-1 (;=0.666667;)\n" +
        "        f64.add\n" +
        "        f64.mul\n" +
        "        f64.add\n" +
        "        f64.add\n" +
        "        f64.mul\n" +
        "        f64.add\n" +
        "        get_local \$l5\n" +
        "        f64.sub\n" +
        "        f64.add\n" +
        "        f64.add\n" +
        "        set_local \$p0\n" +
        "      end\n" +
        "      get_local \$p0\n" +
        "      return\n" +
        "    end\n" +
        "    get_local \$p0\n" +
        "    get_local \$p0\n" +
        "    f64.sub\n" +
        "    f64.const 0x0p+0 (;=0;)\n" +
        "    f64.div)\n" +
        "  (func \$log1p (type \$t1) (param \$p0 f64) (result f64)\n" +
        "    (local \$l0 i32) (local \$l1 i64) (local \$l2 i32) (local \$l3 f64) (local \$l4 f64) (local \$l5 f64)\n" +
        "    get_global \$g0\n" +
        "    i32.const 16\n" +
        "    i32.sub\n" +
        "    set_local \$l0\n" +
        "    get_local \$p0\n" +
        "    i64.reinterpret/f64\n" +
        "    tee_local \$l1\n" +
        "    i64.const 32\n" +
        "    i64.shr_u\n" +
        "    i32.wrap/i64\n" +
        "    set_local \$l2\n" +
        "    block \$B0\n" +
        "      block \$B1\n" +
        "        block \$B2\n" +
        "          block \$B3\n" +
        "            block \$B4\n" +
        "              get_local \$l1\n" +
        "              i64.const 0\n" +
        "              i64.lt_s\n" +
        "              br_if \$B4\n" +
        "              get_local \$l2\n" +
        "              i32.const 1071284857\n" +
        "              i32.le_u\n" +
        "              br_if \$B4\n" +
        "              get_local \$l2\n" +
        "              i32.const 2146435071\n" +
        "              i32.le_u\n" +
        "              br_if \$B3\n" +
        "              get_local \$p0\n" +
        "              return\n" +
        "            end\n" +
        "            block \$B5\n" +
        "              get_local \$l2\n" +
        "              i32.const -1074790400\n" +
        "              i32.lt_u\n" +
        "              br_if \$B5\n" +
        "              get_local \$p0\n" +
        "              f64.const -0x1p+0 (;=-1;)\n" +
        "              f64.ne\n" +
        "              br_if \$B1\n" +
        "              f64.const -inf (;=-inf;)\n" +
        "              return\n" +
        "            end\n" +
        "            block \$B6\n" +
        "              get_local \$l2\n" +
        "              i32.const 1\n" +
        "              i32.shl\n" +
        "              i32.const 2034237439\n" +
        "              i32.gt_u\n" +
        "              br_if \$B6\n" +
        "              get_local \$l2\n" +
        "              i32.const 2146435072\n" +
        "              i32.and\n" +
        "              i32.eqz\n" +
        "              br_if \$B0\n" +
        "              get_local \$p0\n" +
        "              return\n" +
        "            end\n" +
        "            f64.const 0x0p+0 (;=0;)\n" +
        "            set_local \$l3\n" +
        "            get_local \$l2\n" +
        "            i32.const -1076707643\n" +
        "            i32.ge_u\n" +
        "            br_if \$B3\n" +
        "            f64.const 0x0p+0 (;=0;)\n" +
        "            set_local \$l4\n" +
        "            br \$B2\n" +
        "          end\n" +
        "          f64.const 0x0p+0 (;=0;)\n" +
        "          set_local \$l3\n" +
        "          block \$B7\n" +
        "            get_local \$p0\n" +
        "            f64.const 0x1p+0 (;=1;)\n" +
        "            f64.add\n" +
        "            tee_local \$l4\n" +
        "            i64.reinterpret/f64\n" +
        "            tee_local \$l1\n" +
        "            i64.const 32\n" +
        "            i64.shr_u\n" +
        "            i32.wrap/i64\n" +
        "            i32.const 614242\n" +
        "            i32.add\n" +
        "            tee_local \$l0\n" +
        "            i32.const 20\n" +
        "            i32.shr_u\n" +
        "            i32.const -1023\n" +
        "            i32.add\n" +
        "            tee_local \$l2\n" +
        "            i32.const 53\n" +
        "            i32.gt_s\n" +
        "            br_if \$B7\n" +
        "            get_local \$p0\n" +
        "            get_local \$l4\n" +
        "            f64.sub\n" +
        "            f64.const 0x1p+0 (;=1;)\n" +
        "            f64.add\n" +
        "            get_local \$p0\n" +
        "            get_local \$l4\n" +
        "            f64.const -0x1p+0 (;=-1;)\n" +
        "            f64.add\n" +
        "            f64.sub\n" +
        "            get_local \$l2\n" +
        "            i32.const 1\n" +
        "            i32.gt_s\n" +
        "            select\n" +
        "            get_local \$l4\n" +
        "            f64.div\n" +
        "            set_local \$l3\n" +
        "          end\n" +
        "          get_local \$l0\n" +
        "          i32.const 1048575\n" +
        "          i32.and\n" +
        "          i32.const 1072079006\n" +
        "          i32.add\n" +
        "          i64.extend_u/i32\n" +
        "          i64.const 32\n" +
        "          i64.shl\n" +
        "          get_local \$l1\n" +
        "          i64.const 4294967295\n" +
        "          i64.and\n" +
        "          i64.or\n" +
        "          f64.reinterpret/i64\n" +
        "          f64.const -0x1p+0 (;=-1;)\n" +
        "          f64.add\n" +
        "          set_local \$p0\n" +
        "          get_local \$l2\n" +
        "          f64.convert_s/i32\n" +
        "          set_local \$l4\n" +
        "        end\n" +
        "        get_local \$l4\n" +
        "        f64.const 0x1.62e42feep-1 (;=0.693147;)\n" +
        "        f64.mul\n" +
        "        get_local \$p0\n" +
        "        get_local \$l3\n" +
        "        get_local \$l4\n" +
        "        f64.const 0x1.a39ef35793c76p-33 (;=1.90821e-10;)\n" +
        "        f64.mul\n" +
        "        f64.add\n" +
        "        get_local \$p0\n" +
        "        get_local \$p0\n" +
        "        f64.const 0x1p+1 (;=2;)\n" +
        "        f64.add\n" +
        "        f64.div\n" +
        "        tee_local \$l4\n" +
        "        get_local \$p0\n" +
        "        get_local \$p0\n" +
        "        f64.const 0x1p-1 (;=0.5;)\n" +
        "        f64.mul\n" +
        "        f64.mul\n" +
        "        tee_local \$l5\n" +
        "        get_local \$l4\n" +
        "        get_local \$l4\n" +
        "        f64.mul\n" +
        "        tee_local \$l3\n" +
        "        get_local \$l3\n" +
        "        f64.mul\n" +
        "        tee_local \$l4\n" +
        "        get_local \$l4\n" +
        "        get_local \$l4\n" +
        "        f64.const 0x1.39a09d078c69fp-3 (;=0.153138;)\n" +
        "        f64.mul\n" +
        "        f64.const 0x1.c71c51d8e78afp-3 (;=0.222222;)\n" +
        "        f64.add\n" +
        "        f64.mul\n" +
        "        f64.const 0x1.999999997fa04p-2 (;=0.4;)\n" +
        "        f64.add\n" +
        "        f64.mul\n" +
        "        get_local \$l3\n" +
        "        get_local \$l4\n" +
        "        get_local \$l4\n" +
        "        get_local \$l4\n" +
        "        f64.const 0x1.2f112df3e5244p-3 (;=0.147982;)\n" +
        "        f64.mul\n" +
        "        f64.const 0x1.7466496cb03dep-3 (;=0.181836;)\n" +
        "        f64.add\n" +
        "        f64.mul\n" +
        "        f64.const 0x1.2492494229359p-2 (;=0.285714;)\n" +
        "        f64.add\n" +
        "        f64.mul\n" +
        "        f64.const 0x1.5555555555593p-1 (;=0.666667;)\n" +
        "        f64.add\n" +
        "        f64.mul\n" +
        "        f64.add\n" +
        "        f64.add\n" +
        "        f64.mul\n" +
        "        f64.add\n" +
        "        get_local \$l5\n" +
        "        f64.sub\n" +
        "        f64.add\n" +
        "        f64.add\n" +
        "        return\n" +
        "      end\n" +
        "      get_local \$p0\n" +
        "      get_local \$p0\n" +
        "      f64.sub\n" +
        "      f64.const 0x0p+0 (;=0;)\n" +
        "      f64.div\n" +
        "      return\n" +
        "    end\n" +
        "    get_local \$l0\n" +
        "    get_local \$p0\n" +
        "    f32.demote/f64\n" +
        "    f32.store offset=12\n" +
        "    get_local \$p0)\n" +
        "  (func \$scalbn (type \$t6) (param \$p0 f64) (param \$p1 i32) (result f64)\n" +
        "    (local \$l0 i32)\n" +
        "    block \$B0\n" +
        "      block \$B1\n" +
        "        block \$B2\n" +
        "          block \$B3\n" +
        "            get_local \$p1\n" +
        "            i32.const 1024\n" +
        "            i32.lt_s\n" +
        "            br_if \$B3\n" +
        "            get_local \$p0\n" +
        "            f64.const 0x1p+1023 (;=8.98847e+307;)\n" +
        "            f64.mul\n" +
        "            set_local \$p0\n" +
        "            get_local \$p1\n" +
        "            i32.const -1023\n" +
        "            i32.add\n" +
        "            tee_local \$l0\n" +
        "            i32.const 1024\n" +
        "            i32.lt_s\n" +
        "            br_if \$B2\n" +
        "            get_local \$p1\n" +
        "            i32.const -2046\n" +
        "            i32.add\n" +
        "            tee_local \$p1\n" +
        "            i32.const 1023\n" +
        "            get_local \$p1\n" +
        "            i32.const 1023\n" +
        "            i32.lt_s\n" +
        "            select\n" +
        "            set_local \$p1\n" +
        "            get_local \$p0\n" +
        "            f64.const 0x1p+1023 (;=8.98847e+307;)\n" +
        "            f64.mul\n" +
        "            set_local \$p0\n" +
        "            br \$B0\n" +
        "          end\n" +
        "          get_local \$p1\n" +
        "          i32.const -1023\n" +
        "          i32.gt_s\n" +
        "          br_if \$B0\n" +
        "          get_local \$p0\n" +
        "          f64.const 0x1p-969 (;=2.00417e-292;)\n" +
        "          f64.mul\n" +
        "          set_local \$p0\n" +
        "          get_local \$p1\n" +
        "          i32.const 969\n" +
        "          i32.add\n" +
        "          tee_local \$l0\n" +
        "          i32.const -1023\n" +
        "          i32.gt_s\n" +
        "          br_if \$B1\n" +
        "          get_local \$p1\n" +
        "          i32.const 1938\n" +
        "          i32.add\n" +
        "          tee_local \$p1\n" +
        "          i32.const -1022\n" +
        "          get_local \$p1\n" +
        "          i32.const -1022\n" +
        "          i32.gt_s\n" +
        "          select\n" +
        "          set_local \$p1\n" +
        "          get_local \$p0\n" +
        "          f64.const 0x1p-969 (;=2.00417e-292;)\n" +
        "          f64.mul\n" +
        "          set_local \$p0\n" +
        "          br \$B0\n" +
        "        end\n" +
        "        get_local \$l0\n" +
        "        set_local \$p1\n" +
        "        br \$B0\n" +
        "      end\n" +
        "      get_local \$l0\n" +
        "      set_local \$p1\n" +
        "    end\n" +
        "    get_local \$p0\n" +
        "    get_local \$p1\n" +
        "    i32.const 1023\n" +
        "    i32.add\n" +
        "    i64.extend_u/i32\n" +
        "    i64.const 52\n" +
        "    i64.shl\n" +
        "    f64.reinterpret/i64\n" +
        "    f64.mul)\n" +
        "  (func \$sin (type \$t1) (param \$p0 f64) (result f64)\n" +
        "    (local \$l0 i32) (local \$l1 i32) (local \$l2 f64)\n" +
        "    get_global \$g0\n" +
        "    i32.const 16\n" +
        "    i32.sub\n" +
        "    tee_local \$l0\n" +
        "    set_global \$g0\n" +
        "    block \$B0\n" +
        "      block \$B1\n" +
        "        block \$B2\n" +
        "          get_local \$p0\n" +
        "          i64.reinterpret/f64\n" +
        "          i64.const 32\n" +
        "          i64.shr_u\n" +
        "          i32.wrap/i64\n" +
        "          i32.const 2147483647\n" +
        "          i32.and\n" +
        "          tee_local \$l1\n" +
        "          i32.const 1072243195\n" +
        "          i32.gt_u\n" +
        "          br_if \$B2\n" +
        "          get_local \$l1\n" +
        "          i32.const 1045430271\n" +
        "          i32.gt_u\n" +
        "          br_if \$B1\n" +
        "          get_local \$l0\n" +
        "          get_local \$p0\n" +
        "          f64.const 0x1p-120 (;=7.52316e-37;)\n" +
        "          f64.mul\n" +
        "          get_local \$p0\n" +
        "          f64.const 0x1p+120 (;=1.32923e+36;)\n" +
        "          f64.add\n" +
        "          get_local \$l1\n" +
        "          i32.const 1048576\n" +
        "          i32.lt_u\n" +
        "          select\n" +
        "          f64.store\n" +
        "          get_local \$l0\n" +
        "          i32.const 16\n" +
        "          i32.add\n" +
        "          set_global \$g0\n" +
        "          get_local \$p0\n" +
        "          return\n" +
        "        end\n" +
        "        get_local \$l1\n" +
        "        i32.const 2146435072\n" +
        "        i32.lt_u\n" +
        "        br_if \$B0\n" +
        "        get_local \$l0\n" +
        "        i32.const 16\n" +
        "        i32.add\n" +
        "        set_global \$g0\n" +
        "        get_local \$p0\n" +
        "        get_local \$p0\n" +
        "        f64.sub\n" +
        "        return\n" +
        "      end\n" +
        "      get_local \$p0\n" +
        "      f64.const 0x0p+0 (;=0;)\n" +
        "      i32.const 0\n" +
        "      call \$__sin\n" +
        "      set_local \$p0\n" +
        "      get_local \$l0\n" +
        "      i32.const 16\n" +
        "      i32.add\n" +
        "      set_global \$g0\n" +
        "      get_local \$p0\n" +
        "      return\n" +
        "    end\n" +
        "    get_local \$p0\n" +
        "    get_local \$l0\n" +
        "    call \$__rem_pio2\n" +
        "    set_local \$l1\n" +
        "    get_local \$l0\n" +
        "    f64.load offset=8\n" +
        "    set_local \$p0\n" +
        "    get_local \$l0\n" +
        "    f64.load\n" +
        "    set_local \$l2\n" +
        "    block \$B3\n" +
        "      block \$B4\n" +
        "        block \$B5\n" +
        "          get_local \$l1\n" +
        "          i32.const 3\n" +
        "          i32.and\n" +
        "          tee_local \$l1\n" +
        "          i32.const 2\n" +
        "          i32.eq\n" +
        "          br_if \$B5\n" +
        "          get_local \$l1\n" +
        "          i32.const 1\n" +
        "          i32.eq\n" +
        "          br_if \$B4\n" +
        "          get_local \$l1\n" +
        "          br_if \$B3\n" +
        "          get_local \$l2\n" +
        "          get_local \$p0\n" +
        "          i32.const 1\n" +
        "          call \$__sin\n" +
        "          set_local \$p0\n" +
        "          get_local \$l0\n" +
        "          i32.const 16\n" +
        "          i32.add\n" +
        "          set_global \$g0\n" +
        "          get_local \$p0\n" +
        "          return\n" +
        "        end\n" +
        "        get_local \$l2\n" +
        "        get_local \$p0\n" +
        "        i32.const 1\n" +
        "        call \$__sin\n" +
        "        set_local \$p0\n" +
        "        get_local \$l0\n" +
        "        i32.const 16\n" +
        "        i32.add\n" +
        "        set_global \$g0\n" +
        "        get_local \$p0\n" +
        "        f64.neg\n" +
        "        return\n" +
        "      end\n" +
        "      get_local \$l2\n" +
        "      get_local \$p0\n" +
        "      call \$__cos\n" +
        "      set_local \$p0\n" +
        "      get_local \$l0\n" +
        "      i32.const 16\n" +
        "      i32.add\n" +
        "      set_global \$g0\n" +
        "      get_local \$p0\n" +
        "      return\n" +
        "    end\n" +
        "    get_local \$l2\n" +
        "    get_local \$p0\n" +
        "    call \$__cos\n" +
        "    set_local \$p0\n" +
        "    get_local \$l0\n" +
        "    i32.const 16\n" +
        "    i32.add\n" +
        "    set_global \$g0\n" +
        "    get_local \$p0\n" +
        "    f64.neg)\n" +
        "  (func \$sinh (type \$t1) (param \$p0 f64) (result f64)\n" +
        "    (local \$l0 i64) (local \$l1 f64) (local \$l2 f64) (local \$l3 i32)\n" +
        "    f64.const -0x1p-1 (;=-0.5;)\n" +
        "    f64.const 0x1p-1 (;=0.5;)\n" +
        "    get_local \$p0\n" +
        "    i64.reinterpret/f64\n" +
        "    tee_local \$l0\n" +
        "    i64.const 0\n" +
        "    i64.lt_s\n" +
        "    select\n" +
        "    set_local \$l1\n" +
        "    get_local \$l0\n" +
        "    i64.const 9223372036854775807\n" +
        "    i64.and\n" +
        "    tee_local \$l0\n" +
        "    f64.reinterpret/i64\n" +
        "    set_local \$l2\n" +
        "    block \$B0\n" +
        "      block \$B1\n" +
        "        block \$B2\n" +
        "          get_local \$l0\n" +
        "          i64.const 32\n" +
        "          i64.shr_u\n" +
        "          i32.wrap/i64\n" +
        "          tee_local \$l3\n" +
        "          i32.const 1082535489\n" +
        "          i32.gt_u\n" +
        "          br_if \$B2\n" +
        "          get_local \$l2\n" +
        "          call \$expm1\n" +
        "          set_local \$l2\n" +
        "          get_local \$l3\n" +
        "          i32.const 1072693247\n" +
        "          i32.gt_u\n" +
        "          br_if \$B0\n" +
        "          get_local \$l3\n" +
        "          i32.const 1045430272\n" +
        "          i32.lt_u\n" +
        "          br_if \$B1\n" +
        "          get_local \$l1\n" +
        "          get_local \$l2\n" +
        "          get_local \$l2\n" +
        "          f64.add\n" +
        "          get_local \$l2\n" +
        "          get_local \$l2\n" +
        "          f64.mul\n" +
        "          get_local \$l2\n" +
        "          f64.const 0x1p+0 (;=1;)\n" +
        "          f64.add\n" +
        "          f64.div\n" +
        "          f64.sub\n" +
        "          f64.mul\n" +
        "          return\n" +
        "        end\n" +
        "        get_local \$l1\n" +
        "        get_local \$l1\n" +
        "        f64.add\n" +
        "        get_local \$l2\n" +
        "        call \$__expo2\n" +
        "        f64.mul\n" +
        "        set_local \$p0\n" +
        "      end\n" +
        "      get_local \$p0\n" +
        "      return\n" +
        "    end\n" +
        "    get_local \$l1\n" +
        "    get_local \$l2\n" +
        "    get_local \$l2\n" +
        "    get_local \$l2\n" +
        "    f64.const 0x1p+0 (;=1;)\n" +
        "    f64.add\n" +
        "    f64.div\n" +
        "    f64.add\n" +
        "    f64.mul)\n" +
        "  (func \$__tan (type \$t5) (param \$p0 f64) (param \$p1 f64) (param \$p2 i32) (result f64)\n" +
        "    (local \$l0 i64) (local \$l1 i32) (local \$l2 i32) (local \$l3 f64) (local \$l4 f64) (local \$l5 f64)\n" +
        "    block \$B0\n" +
        "      block \$B1\n" +
        "        get_local \$p0\n" +
        "        i64.reinterpret/f64\n" +
        "        tee_local \$l0\n" +
        "        i64.const 9223372002495037440\n" +
        "        i64.and\n" +
        "        i64.const 4604249089280835585\n" +
        "        i64.lt_u\n" +
        "        tee_local \$l1\n" +
        "        br_if \$B1\n" +
        "        f64.const 0x1.921fb54442d18p-1 (;=0.785398;)\n" +
        "        get_local \$p0\n" +
        "        f64.neg\n" +
        "        get_local \$p0\n" +
        "        get_local \$l0\n" +
        "        i64.const 63\n" +
        "        i64.shr_u\n" +
        "        i32.wrap/i64\n" +
        "        tee_local \$l2\n" +
        "        select\n" +
        "        f64.sub\n" +
        "        f64.const 0x1.1a62633145c07p-55 (;=3.06162e-17;)\n" +
        "        get_local \$p1\n" +
        "        f64.neg\n" +
        "        get_local \$p1\n" +
        "        get_local \$l2\n" +
        "        select\n" +
        "        f64.sub\n" +
        "        f64.add\n" +
        "        set_local \$p0\n" +
        "        f64.const 0x0p+0 (;=0;)\n" +
        "        set_local \$p1\n" +
        "        br \$B0\n" +
        "      end\n" +
        "    end\n" +
        "    get_local \$p0\n" +
        "    get_local \$p0\n" +
        "    get_local \$p0\n" +
        "    get_local \$p0\n" +
        "    f64.mul\n" +
        "    tee_local \$l3\n" +
        "    f64.mul\n" +
        "    tee_local \$l4\n" +
        "    f64.const 0x1.5555555555563p-2 (;=0.333333;)\n" +
        "    f64.mul\n" +
        "    get_local \$p1\n" +
        "    get_local \$l3\n" +
        "    get_local \$p1\n" +
        "    get_local \$l4\n" +
        "    get_local \$l3\n" +
        "    get_local \$l3\n" +
        "    f64.mul\n" +
        "    tee_local \$l5\n" +
        "    get_local \$l5\n" +
        "    get_local \$l5\n" +
        "    get_local \$l5\n" +
        "    get_local \$l5\n" +
        "    f64.const -0x1.375cbdb605373p-16 (;=-1.85586e-05;)\n" +
        "    f64.mul\n" +
        "    f64.const 0x1.47e88a03792a6p-14 (;=7.81794e-05;)\n" +
        "    f64.add\n" +
        "    f64.mul\n" +
        "    f64.const 0x1.344d8f2f26501p-11 (;=0.000588041;)\n" +
        "    f64.add\n" +
        "    f64.mul\n" +
        "    f64.const 0x1.d6d22c9560328p-9 (;=0.00359208;)\n" +
        "    f64.add\n" +
        "    f64.mul\n" +
        "    f64.const 0x1.664f48406d637p-6 (;=0.0218695;)\n" +
        "    f64.add\n" +
        "    f64.mul\n" +
        "    f64.const 0x1.111111110fe7ap-3 (;=0.133333;)\n" +
        "    f64.add\n" +
        "    get_local \$l3\n" +
        "    get_local \$l5\n" +
        "    get_local \$l5\n" +
        "    get_local \$l5\n" +
        "    get_local \$l5\n" +
        "    get_local \$l5\n" +
        "    f64.const 0x1.b2a7074bf7ad4p-16 (;=2.59073e-05;)\n" +
        "    f64.mul\n" +
        "    f64.const 0x1.2b80f32f0a7e9p-14 (;=7.14072e-05;)\n" +
        "    f64.add\n" +
        "    f64.mul\n" +
        "    f64.const 0x1.026f71a8d1068p-12 (;=0.000246463;)\n" +
        "    f64.add\n" +
        "    f64.mul\n" +
        "    f64.const 0x1.7dbc8fee08315p-10 (;=0.00145621;)\n" +
        "    f64.add\n" +
        "    f64.mul\n" +
        "    f64.const 0x1.226e3e96e8493p-7 (;=0.00886324;)\n" +
        "    f64.add\n" +
        "    f64.mul\n" +
        "    f64.const 0x1.ba1ba1bb341fep-5 (;=0.0539683;)\n" +
        "    f64.add\n" +
        "    f64.mul\n" +
        "    f64.add\n" +
        "    f64.mul\n" +
        "    f64.add\n" +
        "    f64.mul\n" +
        "    f64.add\n" +
        "    f64.add\n" +
        "    tee_local \$l3\n" +
        "    f64.add\n" +
        "    set_local \$l5\n" +
        "    block \$B2\n" +
        "      get_local \$l1\n" +
        "      br_if \$B2\n" +
        "      i32.const 1\n" +
        "      get_local \$p2\n" +
        "      i32.const 1\n" +
        "      i32.shl\n" +
        "      i32.sub\n" +
        "      f64.convert_s/i32\n" +
        "      tee_local \$p1\n" +
        "      get_local \$p0\n" +
        "      get_local \$l3\n" +
        "      get_local \$l5\n" +
        "      get_local \$l5\n" +
        "      f64.mul\n" +
        "      get_local \$l5\n" +
        "      get_local \$p1\n" +
        "      f64.add\n" +
        "      f64.div\n" +
        "      f64.sub\n" +
        "      f64.add\n" +
        "      tee_local \$l5\n" +
        "      get_local \$l5\n" +
        "      f64.add\n" +
        "      f64.sub\n" +
        "      tee_local \$l5\n" +
        "      f64.neg\n" +
        "      get_local \$l5\n" +
        "      get_local \$l2\n" +
        "      select\n" +
        "      return\n" +
        "    end\n" +
        "    block \$B3\n" +
        "      get_local \$p2\n" +
        "      i32.eqz\n" +
        "      br_if \$B3\n" +
        "      f64.const -0x1p+0 (;=-1;)\n" +
        "      get_local \$l5\n" +
        "      f64.div\n" +
        "      tee_local \$p1\n" +
        "      get_local \$l5\n" +
        "      i64.reinterpret/f64\n" +
        "      i64.const -4294967296\n" +
        "      i64.and\n" +
        "      f64.reinterpret/i64\n" +
        "      tee_local \$l4\n" +
        "      get_local \$p1\n" +
        "      i64.reinterpret/f64\n" +
        "      i64.const -4294967296\n" +
        "      i64.and\n" +
        "      f64.reinterpret/i64\n" +
        "      tee_local \$l5\n" +
        "      f64.mul\n" +
        "      f64.const 0x1p+0 (;=1;)\n" +
        "      f64.add\n" +
        "      get_local \$l3\n" +
        "      get_local \$l4\n" +
        "      get_local \$p0\n" +
        "      f64.sub\n" +
        "      f64.sub\n" +
        "      get_local \$l5\n" +
        "      f64.mul\n" +
        "      f64.add\n" +
        "      f64.mul\n" +
        "      get_local \$l5\n" +
        "      f64.add\n" +
        "      set_local \$l5\n" +
        "    end\n" +
        "    get_local \$l5)\n" +
        "  (func \$tan (type \$t1) (param \$p0 f64) (result f64)\n" +
        "    (local \$l0 i32) (local \$l1 i32)\n" +
        "    get_global \$g0\n" +
        "    i32.const 16\n" +
        "    i32.sub\n" +
        "    tee_local \$l0\n" +
        "    set_global \$g0\n" +
        "    block \$B0\n" +
        "      block \$B1\n" +
        "        block \$B2\n" +
        "          get_local \$p0\n" +
        "          i64.reinterpret/f64\n" +
        "          i64.const 32\n" +
        "          i64.shr_u\n" +
        "          i32.wrap/i64\n" +
        "          i32.const 2147483647\n" +
        "          i32.and\n" +
        "          tee_local \$l1\n" +
        "          i32.const 1072243195\n" +
        "          i32.gt_u\n" +
        "          br_if \$B2\n" +
        "          get_local \$l1\n" +
        "          i32.const 1044381695\n" +
        "          i32.gt_u\n" +
        "          br_if \$B1\n" +
        "          get_local \$l0\n" +
        "          get_local \$p0\n" +
        "          f64.const 0x1p-120 (;=7.52316e-37;)\n" +
        "          f64.mul\n" +
        "          get_local \$p0\n" +
        "          f64.const 0x1p+120 (;=1.32923e+36;)\n" +
        "          f64.add\n" +
        "          get_local \$l1\n" +
        "          i32.const 1048576\n" +
        "          i32.lt_u\n" +
        "          select\n" +
        "          f64.store\n" +
        "          get_local \$l0\n" +
        "          i32.const 16\n" +
        "          i32.add\n" +
        "          set_global \$g0\n" +
        "          get_local \$p0\n" +
        "          return\n" +
        "        end\n" +
        "        get_local \$l1\n" +
        "        i32.const 2146435072\n" +
        "        i32.lt_u\n" +
        "        br_if \$B0\n" +
        "        get_local \$l0\n" +
        "        i32.const 16\n" +
        "        i32.add\n" +
        "        set_global \$g0\n" +
        "        get_local \$p0\n" +
        "        get_local \$p0\n" +
        "        f64.sub\n" +
        "        return\n" +
        "      end\n" +
        "      get_local \$p0\n" +
        "      f64.const 0x0p+0 (;=0;)\n" +
        "      i32.const 0\n" +
        "      call \$__tan\n" +
        "      set_local \$p0\n" +
        "      get_local \$l0\n" +
        "      i32.const 16\n" +
        "      i32.add\n" +
        "      set_global \$g0\n" +
        "      get_local \$p0\n" +
        "      return\n" +
        "    end\n" +
        "    get_local \$p0\n" +
        "    get_local \$l0\n" +
        "    call \$__rem_pio2\n" +
        "    set_local \$l1\n" +
        "    get_local \$l0\n" +
        "    f64.load\n" +
        "    get_local \$l0\n" +
        "    f64.load offset=8\n" +
        "    get_local \$l1\n" +
        "    i32.const 1\n" +
        "    i32.and\n" +
        "    call \$__tan\n" +
        "    set_local \$p0\n" +
        "    get_local \$l0\n" +
        "    i32.const 16\n" +
        "    i32.add\n" +
        "    set_global \$g0\n" +
        "    get_local \$p0)\n" +
        "  (func \$tanh (type \$t1) (param \$p0 f64) (result f64)\n" +
        "    (local \$l0 i32) (local \$l1 i64) (local \$l2 i64) (local \$l3 i32)\n" +
        "    get_global \$g0\n" +
        "    i32.const 16\n" +
        "    i32.sub\n" +
        "    tee_local \$l0\n" +
        "    set_global \$g0\n" +
        "    get_local \$p0\n" +
        "    i64.reinterpret/f64\n" +
        "    tee_local \$l1\n" +
        "    i64.const 9223372036854775807\n" +
        "    i64.and\n" +
        "    tee_local \$l2\n" +
        "    f64.reinterpret/i64\n" +
        "    set_local \$p0\n" +
        "    block \$B0\n" +
        "      block \$B1\n" +
        "        block \$B2\n" +
        "          block \$B3\n" +
        "            get_local \$l2\n" +
        "            i64.const 32\n" +
        "            i64.shr_u\n" +
        "            i32.wrap/i64\n" +
        "            tee_local \$l3\n" +
        "            i32.const 1071748075\n" +
        "            i32.lt_u\n" +
        "            br_if \$B3\n" +
        "            get_local \$l3\n" +
        "            i32.const 1077149697\n" +
        "            i32.lt_u\n" +
        "            br_if \$B2\n" +
        "            f64.const -0x0p+0 (;=-0;)\n" +
        "            get_local \$p0\n" +
        "            f64.div\n" +
        "            f64.const 0x1p+0 (;=1;)\n" +
        "            f64.add\n" +
        "            set_local \$p0\n" +
        "            br \$B0\n" +
        "          end\n" +
        "          get_local \$l3\n" +
        "          i32.const 1070618799\n" +
        "          i32.lt_u\n" +
        "          br_if \$B1\n" +
        "          get_local \$p0\n" +
        "          get_local \$p0\n" +
        "          f64.add\n" +
        "          call \$expm1\n" +
        "          tee_local \$p0\n" +
        "          get_local \$p0\n" +
        "          f64.const 0x1p+1 (;=2;)\n" +
        "          f64.add\n" +
        "          f64.div\n" +
        "          set_local \$p0\n" +
        "          br \$B0\n" +
        "        end\n" +
        "        f64.const 0x1p+0 (;=1;)\n" +
        "        f64.const 0x1p+1 (;=2;)\n" +
        "        get_local \$p0\n" +
        "        get_local \$p0\n" +
        "        f64.add\n" +
        "        call \$expm1\n" +
        "        f64.const 0x1p+1 (;=2;)\n" +
        "        f64.add\n" +
        "        f64.div\n" +
        "        f64.sub\n" +
        "        set_local \$p0\n" +
        "        br \$B0\n" +
        "      end\n" +
        "      block \$B4\n" +
        "        get_local \$l3\n" +
        "        i32.const 1048576\n" +
        "        i32.lt_u\n" +
        "        br_if \$B4\n" +
        "        get_local \$p0\n" +
        "        f64.const -0x1p+1 (;=-2;)\n" +
        "        f64.mul\n" +
        "        call \$expm1\n" +
        "        tee_local \$p0\n" +
        "        f64.neg\n" +
        "        get_local \$p0\n" +
        "        f64.const 0x1p+1 (;=2;)\n" +
        "        f64.add\n" +
        "        f64.div\n" +
        "        set_local \$p0\n" +
        "        br \$B0\n" +
        "      end\n" +
        "      get_local \$l0\n" +
        "      get_local \$p0\n" +
        "      f32.demote/f64\n" +
        "      f32.store offset=12\n" +
        "    end\n" +
        "    get_local \$l0\n" +
        "    i32.const 16\n" +
        "    i32.add\n" +
        "    set_global \$g0\n" +
        "    get_local \$p0\n" +
        "    f64.neg\n" +
        "    get_local \$p0\n" +
        "    get_local \$l1\n" +
        "    i64.const 0\n" +
        "    i64.lt_s\n" +
        "    select)\n" +
        "  (func \$memset (type \$t7) (param \$p0 i32) (param \$p1 i32) (param \$p2 i32) (result i32)\n" +
        "    (local \$l0 i32) (local \$l1 i32) (local \$l2 i32) (local \$l3 i64)\n" +
        "    block \$B0\n" +
        "      get_local \$p2\n" +
        "      i32.eqz\n" +
        "      br_if \$B0\n" +
        "      get_local \$p0\n" +
        "      get_local \$p1\n" +
        "      i32.store8\n" +
        "      get_local \$p0\n" +
        "      get_local \$p2\n" +
        "      i32.add\n" +
        "      tee_local \$l0\n" +
        "      i32.const -1\n" +
        "      i32.add\n" +
        "      get_local \$p1\n" +
        "      i32.store8\n" +
        "      get_local \$p2\n" +
        "      i32.const 3\n" +
        "      i32.lt_u\n" +
        "      br_if \$B0\n" +
        "      get_local \$p0\n" +
        "      get_local \$p1\n" +
        "      i32.store8 offset=2\n" +
        "      get_local \$p0\n" +
        "      get_local \$p1\n" +
        "      i32.store8 offset=1\n" +
        "      get_local \$l0\n" +
        "      i32.const -3\n" +
        "      i32.add\n" +
        "      get_local \$p1\n" +
        "      i32.store8\n" +
        "      get_local \$l0\n" +
        "      i32.const -2\n" +
        "      i32.add\n" +
        "      get_local \$p1\n" +
        "      i32.store8\n" +
        "      get_local \$p2\n" +
        "      i32.const 7\n" +
        "      i32.lt_u\n" +
        "      br_if \$B0\n" +
        "      get_local \$p0\n" +
        "      get_local \$p1\n" +
        "      i32.store8 offset=3\n" +
        "      get_local \$l0\n" +
        "      i32.const -4\n" +
        "      i32.add\n" +
        "      get_local \$p1\n" +
        "      i32.store8\n" +
        "      get_local \$p2\n" +
        "      i32.const 9\n" +
        "      i32.lt_u\n" +
        "      br_if \$B0\n" +
        "      get_local \$p0\n" +
        "      i32.const 0\n" +
        "      get_local \$p0\n" +
        "      i32.sub\n" +
        "      i32.const 3\n" +
        "      i32.and\n" +
        "      tee_local \$l1\n" +
        "      i32.add\n" +
        "      tee_local \$l0\n" +
        "      get_local \$p1\n" +
        "      i32.const 255\n" +
        "      i32.and\n" +
        "      i32.const 16843009\n" +
        "      i32.mul\n" +
        "      tee_local \$p1\n" +
        "      i32.store\n" +
        "      get_local \$l0\n" +
        "      get_local \$p2\n" +
        "      get_local \$l1\n" +
        "      i32.sub\n" +
        "      i32.const -4\n" +
        "      i32.and\n" +
        "      tee_local \$l1\n" +
        "      i32.add\n" +
        "      tee_local \$p2\n" +
        "      i32.const -4\n" +
        "      i32.add\n" +
        "      get_local \$p1\n" +
        "      i32.store\n" +
        "      get_local \$l1\n" +
        "      i32.const 9\n" +
        "      i32.lt_u\n" +
        "      br_if \$B0\n" +
        "      get_local \$l0\n" +
        "      get_local \$p1\n" +
        "      i32.store offset=8\n" +
        "      get_local \$l0\n" +
        "      get_local \$p1\n" +
        "      i32.store offset=4\n" +
        "      get_local \$p2\n" +
        "      i32.const -8\n" +
        "      i32.add\n" +
        "      get_local \$p1\n" +
        "      i32.store\n" +
        "      get_local \$p2\n" +
        "      i32.const -12\n" +
        "      i32.add\n" +
        "      get_local \$p1\n" +
        "      i32.store\n" +
        "      get_local \$l1\n" +
        "      i32.const 25\n" +
        "      i32.lt_u\n" +
        "      br_if \$B0\n" +
        "      get_local \$l0\n" +
        "      get_local \$p1\n" +
        "      i32.store offset=16\n" +
        "      get_local \$l0\n" +
        "      get_local \$p1\n" +
        "      i32.store offset=12\n" +
        "      get_local \$l0\n" +
        "      get_local \$p1\n" +
        "      i32.store offset=20\n" +
        "      get_local \$l0\n" +
        "      get_local \$p1\n" +
        "      i32.store offset=24\n" +
        "      get_local \$p2\n" +
        "      i32.const -24\n" +
        "      i32.add\n" +
        "      get_local \$p1\n" +
        "      i32.store\n" +
        "      get_local \$p2\n" +
        "      i32.const -28\n" +
        "      i32.add\n" +
        "      get_local \$p1\n" +
        "      i32.store\n" +
        "      get_local \$p2\n" +
        "      i32.const -20\n" +
        "      i32.add\n" +
        "      get_local \$p1\n" +
        "      i32.store\n" +
        "      get_local \$p2\n" +
        "      i32.const -16\n" +
        "      i32.add\n" +
        "      get_local \$p1\n" +
        "      i32.store\n" +
        "      get_local \$l1\n" +
        "      get_local \$l0\n" +
        "      i32.const 4\n" +
        "      i32.and\n" +
        "      i32.const 24\n" +
        "      i32.or\n" +
        "      tee_local \$l2\n" +
        "      i32.sub\n" +
        "      tee_local \$p2\n" +
        "      i32.const 32\n" +
        "      i32.lt_u\n" +
        "      br_if \$B0\n" +
        "      get_local \$p1\n" +
        "      i64.extend_u/i32\n" +
        "      tee_local \$l3\n" +
        "      i64.const 32\n" +
        "      i64.shl\n" +
        "      get_local \$l3\n" +
        "      i64.or\n" +
        "      set_local \$l3\n" +
        "      get_local \$l0\n" +
        "      get_local \$l2\n" +
        "      i32.add\n" +
        "      set_local \$p1\n" +
        "      loop \$L1\n" +
        "        get_local \$p1\n" +
        "        get_local \$l3\n" +
        "        i64.store\n" +
        "        get_local \$p1\n" +
        "        i32.const 8\n" +
        "        i32.add\n" +
        "        get_local \$l3\n" +
        "        i64.store\n" +
        "        get_local \$p1\n" +
        "        i32.const 16\n" +
        "        i32.add\n" +
        "        get_local \$l3\n" +
        "        i64.store\n" +
        "        get_local \$p1\n" +
        "        i32.const 24\n" +
        "        i32.add\n" +
        "        get_local \$l3\n" +
        "        i64.store\n" +
        "        get_local \$p1\n" +
        "        i32.const 32\n" +
        "        i32.add\n" +
        "        set_local \$p1\n" +
        "        get_local \$p2\n" +
        "        i32.const -32\n" +
        "        i32.add\n" +
        "        tee_local \$p2\n" +
        "        i32.const 31\n" +
        "        i32.gt_u\n" +
        "        br_if \$L1\n" +
        "      end\n" +
        "    end\n" +
        "    get_local \$p0)\n" +
        "  (table \$T0 1 1 anyfunc)\n" +
        "  (memory \$memory (export \"memory\") 2)\n" +
        "  (global \$g0 (mut i32) (i32.const 69488))\n" +
        "  (global \$__heap_base (export \"__heap_base\") i32 (i32.const 69488))\n" +
        "  (global \$__data_end (export \"__data_end\") i32 (i32.const 3952))\n" +
        "  (data (i32.const 1024) \"O\\bba\\05g\\ac\\dd?\\18-DT\\fb!\\e9?\\9b\\f6\\81\\d2\\0bs\\ef?\\18-DT\\fb!\\f9?\\e2e/\\22\\7f+z<\\07\\5c\\143&\\a6\\81<\\bd\\cb\\f0z\\88\\07p<\\07\\5c\\143&\\a6\\91<\\03\\00\\00\\00\\04\\00\\00\\00\\04\\00\\00\\00\\06\\00\\00\\00\\83\\f9\\a2\\00DNn\\00\\fc)\\15\\00\\d1W'\\00\\dd4\\f5\\00b\\db\\c0\\00<\\99\\95\\00A\\90C\\00cQ\\fe\\00\\bb\\de\\ab\\00\\b7a\\c5\\00:n\\$\\00\\d2MB\\00I\\06\\e0\\00\\09\\ea.\\00\\1c\\92\\d1\\00\\eb\\1d\\fe\\00)\\b1\\1c\\00\\e8>\\a7\\00\\f55\\82\\00D\\bb.\\00\\9c\\e9\\84\\00\\b4&p\\00A~_\\00\\d6\\919\\00S\\839\\00\\9c\\f49\\00\\8b_\\84\\00(\\f9\\bd\\00\\f8\\1f;\\00\\de\\ff\\97\\00\\0f\\98\\05\\00\\11/\\ef\\00\\0aZ\\8b\\00m\\1fm\\00\\cf~6\\00\\09\\cb'\\00FO\\b7\\00\\9ef?\\00-\\ea_\\00\\ba'u\\00\\e5\\eb\\c7\\00={\\f1\\00\\f79\\07\\00\\92R\\8a\\00\\fbk\\ea\\00\\1f\\b1_\\00\\08]\\8d\\000\\03V\\00{\\fcF\\00\\f0\\abk\\00 \\bc\\cf\\006\\f4\\9a\\00\\e3\\a9\\1d\\00^a\\91\\00\\08\\1b\\e6\\00\\85\\99e\\00\\a0\\14_\\00\\8d@h\\00\\80\\d8\\ff\\00'sM\\00\\06\\061\\00\\caV\\15\\00\\c9\\a8s\\00{\\e2`\\00k\\8c\\c0\\00\\19\\c4G\\00\\cdg\\c3\\00\\09\\e8\\dc\\00Y\\83*\\00\\8bv\\c4\\00\\a6\\1c\\96\\00D\\af\\dd\\00\\19W\\d1\\00\\a5>\\05\\00\\05\\07\\ff\\003~?\\00\\c22\\e8\\00\\98O\\de\\00\\bb}2\\00&=\\c3\\00\\1ek\\ef\\00\\9f\\f8^\\005\\1f:\\00\\7f\\f2\\ca\\00\\f1\\87\\1d\\00|\\90!\\00j\\$|\\00\\d5n\\fa\\000-w\\00\\15;C\\00\\b5\\14\\c6\\00\\c3\\19\\9d\\00\\ad\\c4\\c2\\00,MA\\00\\0c\\00]\\00\\86}F\\00\\e3q-\\00\\9b\\c6\\9a\\003b\\00\\00\\b4\\d2|\\00\\b4\\a7\\97\\007U\\d5\\00\\d7>\\f6\\00\\a3\\10\\18\\00Mv\\fc\\00d\\9d*\\00p\\d7\\ab\\00c|\\f8\\00z\\b0W\\00\\17\\15\\e7\\00\\c0IV\\00;\\d6\\d9\\00\\a7\\848\\00\\$#\\cb\\00\\d6\\8aw\\00ZT#\\00\\00\\1f\\b9\\00\\f1\\0a\\1b\\00\\19\\ce\\df\\00\\9f1\\ff\\00f\\1ej\\00\\99Wa\\00\\ac\\fbG\\00~\\7f\\d8\\00\\22e\\b7\\002\\e8\\89\\00\\e6\\bf`\\00\\ef\\c4\\cd\\00l6\\09\\00]?\\d4\\00\\16\\de\\d7\\00X;\\de\\00\\de\\9b\\92\\00\\d2\\22(\\00(\\86\\e8\\00\\e2XM\\00\\c6\\ca2\\00\\08\\e3\\16\\00\\e0}\\cb\\00\\17\\c0P\\00\\f3\\1d\\a7\\00\\18\\e0[\\00.\\134\\00\\83\\12b\\00\\83H\\01\\00\\f5\\8e[\\00\\ad\\b0\\7f\\00\\1e\\e9\\f2\\00HJC\\00\\10g\\d3\\00\\aa\\dd\\d8\\00\\ae_B\\00ja\\ce\\00\\0a(\\a4\\00\\d3\\99\\b4\\00\\06\\a6\\f2\\00\\5cw\\7f\\00\\a3\\c2\\83\\00a<\\88\\00\\8asx\\00\\af\\8cZ\\00o\\d7\\bd\\00-\\a6c\\00\\f4\\bf\\cb\\00\\8d\\81\\ef\\00&\\c1g\\00U\\caE\\00\\ca\\d96\\00(\\a8\\d2\\00\\c2a\\8d\\00\\12\\c9w\\00\\04&\\14\\00\\12F\\9b\\00\\c4Y\\c4\\00\\c8\\c5D\\00M\\b2\\91\\00\\00\\17\\f3\\00\\d4C\\ad\\00)I\\e5\\00\\fd\\d5\\10\\00\\00\\be\\fc\\00\\1e\\94\\cc\\00p\\ce\\ee\\00\\13>\\f5\\00\\ec\\f1\\80\\00\\b3\\e7\\c3\\00\\c7\\f8(\\00\\93\\05\\94\\00\\c1q>\\00.\\09\\b3\\00\\0bE\\f3\\00\\88\\12\\9c\\00\\ab {\\00.\\b5\\9f\\00G\\92\\c2\\00{2/\\00\\0cUm\\00r\\a7\\90\\00k\\e7\\1f\\001\\cb\\96\\00y\\16J\\00Ay\\e2\\00\\f4\\df\\89\\00\\e8\\94\\97\\00\\e2\\e6\\84\\00\\991\\97\\00\\88\\edk\\00__6\\00\\bb\\fd\\0e\\00H\\9a\\b4\\00g\\a4l\\00qrB\\00\\8d]2\\00\\9f\\15\\b8\\00\\bc\\e5\\09\\00\\8d1%\\00\\f7t9\\000\\05\\1c\\00\\0d\\0c\\01\\00K\\08h\\00,\\eeX\\00G\\aa\\90\\00t\\e7\\02\\00\\bd\\d6\\$\\00\\f7}\\a6\\00nHr\\00\\9f\\16\\ef\\00\\8e\\94\\a6\\00\\b4\\91\\f6\\00\\d1SQ\\00\\cf\\0a\\f2\\00 \\983\\00\\f5K~\\00\\b2ch\\00\\dd>_\\00@]\\03\\00\\85\\89\\7f\\00UR)\\007d\\c0\\00m\\d8\\10\\002H2\\00[Lu\\00Nq\\d4\\00ETn\\00\\0b\\09\\c1\\00*\\f5i\\00\\14f\\d5\\00'\\07\\9d\\00]\\04P\\00\\b4;\\db\\00\\eav\\c5\\00\\87\\f9\\17\\00Ik}\\00\\1d'\\ba\\00\\96i)\\00\\c6\\cc\\ac\\00\\ad\\14T\\00\\90\\e2j\\00\\88\\d9\\89\\00,rP\\00\\04\\a4\\be\\00w\\07\\94\\00\\f30p\\00\\00\\fc'\\00\\eaq\\a8\\00f\\c2I\\00d\\e0=\\00\\97\\dd\\83\\00\\a3?\\97\\00C\\94\\fd\\00\\0d\\86\\8c\\001A\\de\\00\\929\\9d\\00\\ddp\\8c\\00\\17\\b7\\e7\\00\\08\\df;\\00\\157+\\00\\5c\\80\\a0\\00Z\\80\\93\\00\\10\\11\\92\\00\\0f\\e8\\d8\\00l\\80\\af\\00\\db\\ffK\\008\\90\\0f\\00Y\\18v\\00b\\a5\\15\\00a\\cb\\bb\\00\\c7\\89\\b9\\00\\10@\\bd\\00\\d2\\f2\\04\\00Iu'\\00\\eb\\b6\\f6\\00\\db\\22\\bb\\00\\0a\\14\\aa\\00\\89&/\\00d\\83v\\00\\09;3\\00\\0e\\94\\1a\\00Q:\\aa\\00\\1d\\a3\\c2\\00\\af\\ed\\ae\\00\\5c&\\12\\00m\\c2M\\00-z\\9c\\00\\c0V\\97\\00\\03?\\83\\00\\09\\f0\\f6\\00+@\\8c\\00m1\\99\\009\\b4\\07\\00\\0c \\15\\00\\d8\\c3[\\00\\f5\\92\\c4\\00\\c6\\adK\\00N\\ca\\a5\\00\\a77\\cd\\00\\e6\\a96\\00\\ab\\92\\94\\00\\ddBh\\00\\19c\\de\\00v\\8c\\ef\\00h\\8bR\\00\\fc\\db7\\00\\ae\\a1\\ab\\00\\df\\151\\00\\00\\ae\\a1\\00\\0c\\fb\\da\\00dMf\\00\\ed\\05\\b7\\00)e0\\00WV\\bf\\00G\\ff:\\00j\\f9\\b9\\00u\\be\\f3\\00(\\93\\df\\00\\ab\\800\\00f\\8c\\f6\\00\\04\\cb\\15\\00\\fa\\22\\06\\00\\d9\\e4\\1d\\00=\\b3\\a4\\00W\\1b\\8f\\006\\cd\\09\\00NB\\e9\\00\\13\\be\\a4\\003#\\b5\\00\\f0\\aa\\1a\\00Oe\\a8\\00\\d2\\c1\\a5\\00\\0b?\\0f\\00[x\\cd\\00#\\f9v\\00{\\8b\\04\\00\\89\\17r\\00\\c6\\a6S\\00on\\e2\\00\\ef\\eb\\00\\00\\9bJX\\00\\c4\\da\\b7\\00\\aaf\\ba\\00v\\cf\\cf\\00\\d1\\02\\1d\\00\\b1\\f1-\\00\\8c\\99\\c1\\00\\c3\\adw\\00\\86H\\da\\00\\f7]\\a0\\00\\c6\\80\\f4\\00\\ac\\f0/\\00\\dd\\ec\\9a\\00?\\5c\\bc\\00\\d0\\dem\\00\\90\\c7\\1f\\00*\\db\\b6\\00\\a3%:\\00\\00\\af\\9a\\00\\adS\\93\\00\\b6W\\04\\00)-\\b4\\00K\\80~\\00\\da\\07\\a7\\00v\\aa\\0e\\00{Y\\a1\\00\\16\\12*\\00\\dc\\b7-\\00\\fa\\e5\\fd\\00\\89\\db\\fe\\00\\89\\be\\fd\\00\\e4vl\\00\\06\\a9\\fc\\00>\\80p\\00\\85n\\15\\00\\fd\\87\\ff\\00(>\\07\\00ag3\\00*\\18\\86\\00M\\bd\\ea\\00\\b3\\e7\\af\\00\\8fmn\\00\\95g9\\001\\bf[\\00\\84\\d7H\\000\\df\\16\\00\\c7-C\\00%a5\\00\\c9p\\ce\\000\\cb\\b8\\00\\bfl\\fd\\00\\a4\\00\\a2\\00\\05l\\e4\\00Z\\dd\\a0\\00!oG\\00b\\12\\d2\\00\\b9\\5c\\84\\00paI\\00kV\\e0\\00\\99R\\01\\00PU7\\00\\1e\\d5\\b7\\003\\f1\\c4\\00\\13n_\\00]0\\e4\\00\\85.\\a9\\00\\1d\\b2\\c3\\00\\a126\\00\\08\\b7\\a4\\00\\ea\\b1\\d4\\00\\16\\f7!\\00\\8fi\\e4\\00'\\ffw\\00\\0c\\03\\80\\00\\8d@-\\00O\\cd\\a0\\00 \\a5\\99\\00\\b3\\a2\\d3\\00/]\\0a\\00\\b4\\f9B\\00\\11\\da\\cb\\00}\\be\\d0\\00\\9b\\db\\c1\\00\\ab\\17\\bd\\00\\ca\\a2\\81\\00\\08j\\5c\\00.U\\17\\00'\\00U\\00\\7f\\14\\f0\\00\\e1\\07\\86\\00\\14\\0bd\\00\\96A\\8d\\00\\87\\be\\de\\00\\da\\fd*\\00k%\\b6\\00{\\894\\00\\05\\f3\\fe\\00\\b9\\bf\\9e\\00hjO\\00J*\\a8\\00O\\c4Z\\00-\\f8\\bc\\00\\d7Z\\98\\00\\f4\\c7\\95\\00\\0dM\\8d\\00 :\\a6\\00\\a4W_\\00\\14?\\b1\\00\\808\\95\\00\\cc \\01\\00q\\dd\\86\\00\\c9\\de\\b6\\00\\bf`\\f5\\00Me\\11\\00\\01\\07k\\00\\8c\\b0\\ac\\00\\b2\\c0\\d0\\00QUH\\00\\1e\\fb\\0e\\00\\95r\\c3\\00\\a3\\06;\\00\\c0@5\\00\\06\\dc{\\00\\e0E\\cc\\00N)\\fa\\00\\d6\\ca\\c8\\00\\e8\\f3A\\00|d\\de\\00\\9bd\\d8\\00\\d9\\be1\\00\\a4\\97\\c3\\00wX\\d4\\00i\\e3\\c5\\00\\f0\\da\\13\\00\\ba:<\\00F\\18F\\00Uu_\\00\\d2\\bd\\f5\\00n\\92\\c6\\00\\ac.]\\00\\0eD\\ed\\00\\1c>B\\00a\\c4\\87\\00)\\fd\\e9\\00\\e7\\d6\\f3\\00\\22|\\ca\\00o\\915\\00\\08\\e0\\c5\\00\\ff\\d7\\8d\\00nj\\e2\\00\\b0\\fd\\c6\\00\\93\\08\\c1\\00|]t\\00k\\ad\\b2\\00\\cdn\\9d\\00>r{\\00\\c6\\11j\\00\\f7\\cf\\a9\\00)s\\df\\00\\b5\\c9\\ba\\00\\b7\\00Q\\00\\e2\\b2\\0d\\00t\\ba\\$\\00\\e5}`\\00t\\d8\\8a\\00\\0d\\15,\\00\\81\\18\\0c\\00~f\\94\\00\\01)\\16\\00\\9fzv\\00\\fd\\fd\\be\\00VE\\ef\\00\\d9~6\\00\\ec\\d9\\13\\00\\8b\\ba\\b9\\00\\c4\\97\\fc\\001\\a8'\\00\\f1n\\c3\\00\\94\\c56\\00\\d8\\a8V\\00\\b4\\a8\\b5\\00\\cf\\cc\\0e\\00\\12\\89-\\00oW4\\00,V\\89\\00\\99\\ce\\e3\\00\\d6 \\b9\\00k^\\aa\\00>*\\9c\\00\\11_\\cc\\00\\fd\\0bJ\\00\\e1\\f4\\fb\\00\\8e;m\\00\\e2\\86,\\00\\e9\\d4\\84\\00\\fc\\b4\\a9\\00\\ef\\ee\\d1\\00.5\\c9\\00/9a\\008!D\\00\\1b\\d9\\c8\\00\\81\\fc\\0a\\00\\fbJj\\00/\\1c\\d8\\00S\\b4\\84\\00N\\99\\8c\\00T\\22\\cc\\00*U\\dc\\00\\c0\\c6\\d6\\00\\0b\\19\\96\\00\\1ap\\b8\\00i\\95d\\00&Z`\\00?R\\ee\\00\\7f\\11\\0f\\00\\f4\\b5\\11\\00\\fc\\cb\\f5\\004\\bc-\\004\\bc\\ee\\00\\e8]\\cc\\00\\dd^`\\00g\\8e\\9b\\00\\923\\ef\\00\\c9\\17\\b8\\00aX\\9b\\00\\e1W\\bc\\00Q\\83\\c6\\00\\d8>\\10\\00\\ddqH\\00-\\1c\\dd\\00\\af\\18\\a1\\00!,F\\00Y\\f3\\d7\\00\\d9z\\98\\00\\9eT\\c0\\00O\\86\\fa\\00V\\06\\fc\\00\\e5y\\ae\\00\\89\\226\\008\\ad\\22\\00g\\93\\dc\\00U\\e8\\aa\\00\\82&8\\00\\ca\\e7\\9b\\00Q\\0d\\a4\\00\\993\\b1\\00\\a9\\d7\\0e\\00i\\05H\\00e\\b2\\f0\\00\\7f\\88\\a7\\00\\88L\\97\\00\\f9\\d16\\00!\\92\\b3\\00{\\82J\\00\\98\\cf!\\00@\\9f\\dc\\00\\dcGU\\00\\e1t:\\00g\\ebB\\00\\fe\\9d\\df\\00^\\d4_\\00{g\\a4\\00\\ba\\acz\\00U\\f6\\a2\\00+\\88#\\00A\\baU\\00Yn\\08\\00!*\\86\\009G\\83\\00\\89\\e3\\e6\\00\\e5\\9e\\d4\\00I\\fb@\\00\\ffV\\e9\\00\\1c\\0f\\ca\\00\\c5Y\\8a\\00\\94\\fa+\\00\\d3\\c1\\c5\\00\\0f\\c5\\cf\\00\\dbZ\\ae\\00G\\c5\\86\\00\\85Cb\\00!\\86;\\00,y\\94\\00\\10a\\87\\00*L{\\00\\80,\\1a\\00C\\bf\\12\\00\\88&\\90\\00x<\\89\\00\\a8\\c4\\e4\\00\\e5\\db{\\00\\c4:\\c2\\00&\\f4\\ea\\00\\f7g\\8a\\00\\0d\\92\\bf\\00e\\a3+\\00=\\93\\b1\\00\\bd|\\0b\\00\\a4Q\\dc\\00'\\ddc\\00i\\e1\\dd\\00\\9a\\94\\19\\00\\a8)\\95\\00h\\ce(\\00\\09\\ed\\b4\\00D\\9f \\00N\\98\\ca\\00p\\82c\\00~|#\\00\\0f\\b92\\00\\a7\\f5\\8e\\00\\14V\\e7\\00!\\f1\\08\\00\\b5\\9d*\\00o~M\\00\\a5\\19Q\\00\\b5\\f9\\ab\\00\\82\\df\\d6\\00\\96\\dda\\00\\166\\02\\00\\c4:\\9f\\00\\83\\a2\\a1\\00r\\edm\\009\\8dz\\00\\82\\b8\\a9\\00k2\\5c\\00F'[\\00\\004\\ed\\00\\d2\\00w\\00\\fc\\f4U\\00\\01YM\\00\\e0q\\80\\00\\00\\00\\00\\00\\00\\00\\00\\00\\00\\00\\00@\\fb!\\f9?\\00\\00\\00\\00-Dt>\\00\\00\\00\\80\\98F\\f8<\\00\\00\\00`Q\\ccx;\\00\\00\\00\\80\\83\\1b\\f09\\00\\00\\00@ %z8\\00\\00\\00\\80\\22\\82\\e36\\00\\00\\00\\00\\1d\\f3i5\\00\\00\\00\\00\\00\\00\\e0?\\00\\00\\00\\00\\00\\00\\e0\\bf\"))\n   "
