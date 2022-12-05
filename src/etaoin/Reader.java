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

package etaoin;

import etaoin.core.Utils;
import etaoin.data.*;
import java.util.regex.Pattern;

public class Reader {

    private static final Pattern RADIX_LIT_RE = Pattern.compile(
            "^#[0-9]+[Rr][+-]?[0-9A-Za-z]+$"
    );

    private static final Pattern OCTAL_LIT_RE = Pattern.compile(
            "^[+-]?0[0-7]*$"
    );

    private static final Pattern DECIMAL_LIT_RE = Pattern.compile(
            "^[+-]?[0-9]+[.]$"
    );

    private static final Pattern FLOAT_LIT_RE = Pattern.compile(
            "^[+-]?([0-9]*[.][0-9]+([Ee][+-]?[0-9]+)?|[0-9]+[.]?[Ee][+-]?[0-9]+)$"
    );

    private static final String ESCAPE;
    private static final String UNESCAPE;

    private final Lexer lexer;
    private int[] position;
    private int base;

    static {
        ESCAPE = new String(new char[]{Lexer.ESCAPE_CHAR});
        UNESCAPE = new String(new char[]{Lexer.ESCAPE_CHAR, Lexer.ESCAPE_CHAR});
    }

    public Reader(java.io.Reader reader) {
        lexer = new Lexer(reader);
        base = 8;
    }

    public Reader(String input) {
        lexer = new Lexer(input);
        base = 8;
    }

    public int[] getPosition() {
        return position;
    }

    public int getBase() {
        return base;
    }

    public void setBase(int base) {
        this.base = base;
    }

    public void error(String message) throws LispException {
        Interpreter.error(message, lexer.getTokenPosition());
    }

    public Value readForm(Interpreter in) throws Exception {
        String token = lexer.nextToken();
        if (token == null || token.isEmpty())
            return null;

        position = lexer.getTokenPosition();

        switch (token) {
            case "NIL":
                return Constant.Nil;
            case "T":
                return in.T;
            case "'":
                Value val = readForm(in);
                if (val == null)
                    error("quote value is expected");
                return Lst.create(in.QUOTE, val);
            case "`":
                val = readForm(in);
                if (val == null)
                    error("backquote value is expected");
                return Lst.create(in.B_QUOTE, val);
            case ",":
                val = readForm(in);
                if (val == null)
                    error("unquote value is expected");
                return Lst.create(in.UNQUOTE, val);
            case ",@":
                val = readForm(in);
                if (val == null)
                    error("splice-quote value is expected");
                return Lst.create(in.SPLICE, val);
            case "(":
                return readList(in);
            case ")", ".":
                error("unexpected token: " + token);
            default:
                Ref<Value> rval = new Ref<>(null);

                if (isStringToken(token, rval))
                    return rval.val;
                if (isRadixLiteral(token, rval))
                    return rval.val;
                if (isOctalLiteral(token))
                    return new Num.Int(token, 8);
                if (isDecimalLiteral(token))
                    return new Num.Int(token);
                if (isNumberLiteral(token, base))
                    return new Num.Int(token, base);
                if (isFloatingLiteral(token))
                    return new Num.Real(token);
                return in.getSymbol(unescape(token));
        }
    }

    public int read_char() {
        if (lexer != null)
            return lexer.next();
        return -1;
    }

    public int peek_char() {
        if (lexer != null)
            return lexer.peek();
        return -1;
    }

    public String read_line() {
        if (lexer != null)
            return lexer.read_line();
        return null;
    }

    public void close() {
        lexer.close();
    }

    private Value readList(Interpreter in) throws Exception {
        Pair list = null;
        Pair pair = null;

        for (;;) {
            String token = lexer.peekToken();
            if (token == null)
                error("unbalanced list");

            if (")".equals(token)) {
                lexer.nextToken();
                break;
            }

            if (".".equals(token)) {
                lexer.nextToken();
                if (pair == null)
                    error("unexpected '.'");

                Value value = readForm(in);
                if (!")".equals(lexer.peekToken()))
                    error("')' is expected");
                lexer.nextToken();

                pair.setCdr(value);
                break;
            }

            if (list == null) {
                list = new Pair(readForm(in), null);
                pair = list;
            }
            else {
                Pair p = pair;
                pair = new Pair(readForm(in), null);
                p.setCdr(pair);
            }
        }
        return Utils.checkNull(list);
    }

    public static boolean isNumberLiteral(String token, int read_base) {
        if (read_base < Character.MIN_RADIX || read_base > Character.MAX_RADIX)
            return false;

        if (token == null || token.isEmpty())
            return false;

        int idx = 0;

        if ("+-".indexOf(token.charAt(idx), 0) >= 0)
            idx++;

        int cnt = token.length();
        if (idx == cnt)
            return false;

        if (token.charAt(idx) == '0')
            return false;

        int digit;

        for (; idx < cnt; idx++) {
            char ch = token.charAt(idx);
            if ('0' <= ch && ch <= '9')
                digit = (int)(ch - '0');
            else if ('A' <= ch && ch <= 'Z')
                digit = 10 + (int)(ch - 'A');
            else if ('a' <= ch && ch <= 'z')
                digit = 10 + (int)(ch - 'a');
            else
                return false;

            if (digit >= read_base)
                return false;
        }

        return true;
    }

    public static boolean isRadixLiteral(String token, Ref<Value> rval) {
        if (!RADIX_LIT_RE.matcher(token).matches())
            return false;

        int ridx = token.indexOf('R');
        if (ridx < 0)
            ridx = token.indexOf('r');

        int radix = Integer.parseInt(token.substring(1, ridx));
        token = token.substring(ridx + 1);

        if (!isNumberLiteral(token, radix))
            return false;

        rval.val = new Num.Int(token, radix);

        return true;
    }

    public static boolean isOctalLiteral(String token) {
        return OCTAL_LIT_RE.matcher(token).matches();
    }

    public static boolean isDecimalLiteral(String token) {
        return DECIMAL_LIT_RE.matcher(token).matches();
    }

    public static boolean isFloatingLiteral(String token) {
        return FLOAT_LIT_RE.matcher(token).matches();
    }

    private boolean isStringToken(String token, Ref<Value> rval) {
        if (!Value.HAS_STRING)
            return false;

        int len = token.length();
        if (len < 2 || token.charAt(0) != Value.STR_MARKER
                    || token.charAt(len - 1) != Value.STR_MARKER)
            return false;

        rval.val = new Str(token.substring(1, len - 1));

        return true;
    }

    private String unescape(String token) {
        if (ESCAPE.equals(token))
            return token;

        token = token.replace(UNESCAPE, "\001");
        token = token.replace(ESCAPE, "");
        return token.replace("\001", ESCAPE);
    }
}
