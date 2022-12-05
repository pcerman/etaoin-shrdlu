//   +---------------------------------------------------------------+
//   | etaoin-shrdlu: LISP interpreter for the SHRDLU project        |
//   |                                                               |
//   | Original source code is published in github resository:       |
//   | https://github.com/pcerman/etaoin-shrdlu.                     |
//   |                                                               |
//   | Copyright (c) 2021 Peter Cerman (https://github.com/pcerman)  |
//   |                                                               |
//   | This source code is released under Mozilla Public License 2.0 |
//   +---------------------------------------------------------------+

package etaoin.data;

import etaoin.core.Utils;

public abstract class Value {

    public static final boolean HAS_STRING = true;
    public static final char STR_MARKER = '|';

    public static final int NUM_BASE = 10;

    protected static int print_base = NUM_BASE;

    public static enum Type {
        CONSTANT, SYMBOL, STRING, INTEGER, FLOAT, PAIR, FUNC, PORT
    }

    public abstract Type getType();

    protected abstract void pr_str(StringBuilder builder, boolean readably);

    public static void pr_str(Value value, StringBuilder builder, boolean readably)
    {
        if (value == null)
            builder.append("null");
        else
            value.pr_str(builder, readably);
    }

    public String toString(boolean readably, int base) {
        int old_base = print_base;
        print_base = base;

        StringBuilder builder = new StringBuilder();
        this.pr_str(builder, readably);

        print_base = old_base;

        return builder.toString();
    }

    public String toString(boolean readably) {
        return toString(readably, 10);
    }

    public static String toString(Value value) {
        return toString(value, true, 10);
    }

    public static String toString(Value value, boolean readably) {
        return toString(value, readably, 10);
    }

    public static String toString(Value value, boolean readably, int base) {
        if (value == null)
            return "null";

        return value.toString(readably, base);
    }

    public static boolean equal(Value v1, Value v2) {
        if (v1 == v2)
            return true;

        if (v1 == null || v2 == null)
            return false;

        if (v1.getType() != v2.getType())
            return false;

        switch (v1.getType()) {
            case INTEGER -> {
                Num.Int n1 = (Num.Int) v1;
                Num.Int n2 = (Num.Int) v2;
                return n1.getValue() == n2.getValue();
            }

            case FLOAT -> {
                Num.Real n1 = (Num.Real) v1;
                Num.Real n2 = (Num.Real) v2;
                return n1.getValue() == n2.getValue();
            }

            case STRING -> {
                Str s1 = (Str) v1;
                Str s2 = (Str) v2;
                return Str.equals(s1, s2);
            }

            case PAIR -> {
                Pair p1 = (Pair) v1;
                Pair p2 = (Pair) v2;
                Value lcdr_1 = null;
                Value lcdr_2 = null;

                while (p1 != null && p2 != null) {
                    if (p1 == p2)
                        return true;

                    if (!equal(p1.getCar(), p2.getCar()))
                        return false;

                    lcdr_1 = p1.getCdr();
                    lcdr_2 = p2.getCdr();
                    p1 = Utils.getPair(lcdr_1);
                    p2 = Utils.getPair(lcdr_2);
                }

                if (p1 != p2)
                    return false;

                return equal(lcdr_1, lcdr_2);
            }
        }

        return false;
    }

    public static boolean eq(Value v1, Value v2) {
        if (v1 == v2)
            return true;

        if (v1 == null || v2 == null)
            return false;

        if (v1.getType() != v2.getType())
            return false;

        switch (v1.getType()) {
            case INTEGER -> {
            Num.Int n1 = (Num.Int) v1;
            Num.Int n2 = (Num.Int) v2;
            return n1.getValue() == n2.getValue();
            }

            case FLOAT -> {
                Num.Real n1 = (Num.Real) v1;
                Num.Real n2 = (Num.Real) v2;
                return n1.getValue() == n2.getValue();
            }

            case STRING -> {
                Str s1 = (Str) v1;
                Str s2 = (Str) v2;
                return Str.eq(s1, s2);
            }
        }

        return false;
    }
}
