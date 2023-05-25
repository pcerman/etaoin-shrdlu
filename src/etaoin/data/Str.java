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

public class Str extends Value {

    private final String value;

    public Str(String str) {
        this.value = str;
    }

    public String getValue() {
        return value;
    }

    @Override
    public Type getType() {
        return Type.STRING;
    }

    @Override
    protected void pr_str(StringBuilder builder, boolean readably) {
        if (readably) {
            builder.append(Value.STR_MARKER);

            int len = value.length();
            for (int i = 0; i < len; i++) {
                char ch = value.charAt(i);
                if (ch < ' ') {
                    switch (ch) {
                        case '\n': builder.append("\\n"); break;
                        case '\r': builder.append("\\r"); break;
                        case '\f': builder.append("\\f"); break;
                        case '\b': builder.append("\\b"); break;
                        case '\t': builder.append("\\t"); break;
                        default:   builder.append(String.format("\\x%02x", (int)ch)); break;
                    }
                } else {
                    switch (ch) {
                        case Value.STR_MARKER:
                        case '\\': builder.append(String.format("\\%c", ch)); break;
                        default: builder.append(ch);
                    }
                }
            }

            builder.append(Value.STR_MARKER);
        } else
            builder.append(value);
    }

    public static boolean equals(Str s1, Str s2) {
        if (s1 == s2)
            return true;

        if (s1 == null || s2 == null)
            return false;

        if (s1.value == s2.value)
            return true;

        return s1.value.equals(s2.value);
    }

    public static boolean eq(Str s1, Str s2) {
        if (s1 == s2)
            return true;

        if (s1 == null || s2 == null)
            return false;

        return s1.value == s2.value;
    }
}
