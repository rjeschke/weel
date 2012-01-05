/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.rjeschke.weel;

/**
 * JVM bytecode opcodes.
 * 
 * @author René Jeschke <rene_jeschke@yahoo.de>
 */
final class JvmOp
{
    /**
     * Constructor.
     */
    private JvmOp()
    {
        /* */
    }

    public final static int NOP = 0;
    public final static int ACONST_NULL = 1;
    public final static int ICONST_M1 = 2;
    public final static int ICONST_0 = 3;
    public final static int ICONST_1 = 4;
    public final static int ICONST_2 = 5;
    public final static int ICONST_3 = 6;
    public final static int ICONST_4 = 7;
    public final static int ICONST_5 = 8;
    public final static int LCONST_0 = 9;
    public final static int LCONST_1 = 10;
    public final static int FCONST_0 = 11;
    public final static int FCONST_1 = 12;
    public final static int FCONST_2 = 13;
    public final static int DCONST_0 = 14;
    public final static int DCONST_1 = 15;
    public final static int BIPUSH = 16;
    public final static int SIPUSH = 17;
    public final static int LDC = 18;
    public final static int LDC_W = 19;
    public final static int LDC2_W = 20;
    public final static int ILOAD = 21;
    public final static int LLOAD = 22;
    public final static int FLOAD = 23;
    public final static int DLOAD = 24;
    public final static int ALOAD = 25;
    public final static int ILOAD_0 = 26;
    public final static int ILOAD_1 = 27;
    public final static int ILOAD_2 = 28;
    public final static int ILOAD_3 = 29;
    public final static int LLOAD_0 = 30;
    public final static int LLOAD_1 = 31;
    public final static int LLOAD_2 = 32;
    public final static int LLOAD_3 = 33;
    public final static int FLOAD_0 = 34;
    public final static int FLOAD_1 = 35;
    public final static int FLOAD_2 = 36;
    public final static int FLOAD_3 = 37;
    public final static int DLOAD_0 = 38;
    public final static int DLOAD_1 = 39;
    public final static int DLOAD_2 = 40;
    public final static int DLOAD_3 = 41;
    public final static int ALOAD_0 = 42;
    public final static int ALOAD_1 = 43;
    public final static int ALOAD_2 = 44;
    public final static int ALOAD_3 = 45;
    public final static int IALOAD = 46;
    public final static int LALOAD = 47;
    public final static int FALOAD = 48;
    public final static int DALOAD = 49;
    public final static int AALOAD = 50;
    public final static int BALOAD = 51;
    public final static int CALOAD = 52;
    public final static int SALOAD = 53;
    public final static int ISTORE = 54;
    public final static int LSTORE = 55;
    public final static int FSTORE = 56;
    public final static int DSTORE = 57;
    public final static int ASTORE = 58;
    public final static int ISTORE_0 = 59;
    public final static int ISTORE_1 = 60;
    public final static int ISTORE_2 = 61;
    public final static int ISTORE_3 = 62;
    public final static int LSTORE_0 = 63;
    public final static int LSTORE_1 = 64;
    public final static int LSTORE_2 = 65;
    public final static int LSTORE_3 = 66;
    public final static int FSTORE_0 = 67;
    public final static int FSTORE_1 = 68;
    public final static int FSTORE_2 = 69;
    public final static int FSTORE_3 = 70;
    public final static int DSTORE_0 = 71;
    public final static int DSTORE_1 = 72;
    public final static int DSTORE_2 = 73;
    public final static int DSTORE_3 = 74;
    public final static int ASTORE_0 = 75;
    public final static int ASTORE_1 = 76;
    public final static int ASTORE_2 = 77;
    public final static int ASTORE_3 = 78;
    public final static int IASTORE = 79;
    public final static int LASTORE = 80;
    public final static int FASTORE = 81;
    public final static int DASTORE = 82;
    public final static int AASTORE = 83;
    public final static int BASTORE = 84;
    public final static int CASTORE = 85;
    public final static int SASTORE = 86;
    public final static int POP = 87;
    public final static int POP2 = 88;
    public final static int DUP = 89;
    public final static int DUP_X1 = 90;
    public final static int DUP_X2 = 91;
    public final static int DUP2 = 92;
    public final static int DUP2_X1 = 93;
    public final static int DUP2_X2 = 94;
    public final static int SWAP = 95;
    public final static int IADD = 96;
    public final static int LADD = 97;
    public final static int FADD = 98;
    public final static int DADD = 99;
    public final static int ISUB = 100;
    public final static int LSUB = 101;
    public final static int FSUB = 102;
    public final static int DSUB = 103;
    public final static int IMUL = 104;
    public final static int LMUL = 105;
    public final static int FMUL = 106;
    public final static int DMUL = 107;
    public final static int IDIV = 108;
    public final static int LDIV = 109;
    public final static int FDIV = 110;
    public final static int DDIV = 111;
    public final static int IREM = 112;
    public final static int LREM = 113;
    public final static int FREM = 114;
    public final static int DREM = 115;
    public final static int LNEG = 117;
    public final static int FNEG = 118;
    public final static int DNEG = 119;
    public final static int ISHL = 120;
    public final static int LSHL = 121;
    public final static int ISHR = 122;
    public final static int LSHR = 123;
    public final static int IUSHR = 124;
    public final static int LUSHR = 125;
    public final static int IAND = 126;
    public final static int LAND = 127;
    public final static int IOR = 128;
    public final static int LOR = 129;
    public final static int IXOR = 130;
    public final static int LXOR = 131;
    public final static int IINC = 132;
    public final static int I2L = 133;
    public final static int I2F = 134;
    public final static int I2D = 135;
    public final static int L2I = 136;
    public final static int L2F = 137;
    public final static int L2D = 138;
    public final static int F2I = 139;
    public final static int F2L = 140;
    public final static int F2D = 141;
    public final static int D2I = 142;
    public final static int D2L = 143;
    public final static int D2F = 144;
    public final static int I2B = 145;
    public final static int I2C = 146;
    public final static int I2S = 147;
    public final static int LCMP = 148;
    public final static int FCMPL = 149;
    public final static int FCMPG = 150;
    public final static int DCMPL = 151;
    public final static int DCMPG = 152;
    public final static int IFEQ = 153;
    public final static int IFNE = 154;
    public final static int IFLT = 155;
    public final static int IFGE = 156;
    public final static int IFGT = 157;
    public final static int IFLE = 158;
    public final static int IF_ICMPEQ = 159;
    public final static int IF_ICMPNE = 160;
    public final static int IF_ICMPLT = 161;
    public final static int IF_ICMPGE = 162;
    public final static int IF_ICMPGT = 163;
    public final static int IF_ICMPLE = 164;
    public final static int IF_ACMPEQ = 165;
    public final static int IF_ACMPNE = 166;
    public final static int GOTO = 167;
    public final static int JSR = 168;
    public final static int RET = 169;
    public final static int TABLESWITCH = 170;
    public final static int LOOKUPSWITCH = 171;
    public final static int IRETURN = 172;
    public final static int LRETURN = 173;
    public final static int FRETURN = 174;
    public final static int DRETURN = 175;
    public final static int ARETURN = 176;
    public final static int RETURN = 177;
    public final static int GETSTATIC = 178;
    public final static int PUTSTATIC = 179;
    public final static int GETFIELD = 180;
    public final static int PUTFIELD = 181;
    public final static int INVOKEVIRTUAL = 182;
    public final static int INVOKESPECIAL = 183;
    public final static int INVOKESTATIC = 184;
    public final static int INVOKEINTERFACE = 185;
    public final static int XXXUNUSEDXXX1 = 186;
    public final static int NEW = 187;
    public final static int NEWARRAY = 188;
    public final static int ANEWARRAY = 189;
    public final static int ARRAYLENGTH = 190;
    public final static int ATHROW = 191;
    public final static int CHECKCAST = 192;
    public final static int INSTANCEOF = 193;
    public final static int MONITORENTER = 194;
    public final static int MONITOREXIT = 195;
    public final static int WIDE = 196;
    public final static int MULTIANEWARRAY = 197;
    public final static int IFNULL = 198;
    public final static int IFNONNULL = 199;
    public final static int GOTO_W = 200;
    public final static int JSR_W = 201;
    public final static int BREAKPOINT = 202;
    public final static int IMPDEP1 = 254;
    public final static int IMPDEP2 = 255;
}
