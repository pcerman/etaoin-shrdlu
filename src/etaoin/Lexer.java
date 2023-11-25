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

import etaoin.data.Value;
import java.io.IOException;
import java.io.StringReader;

public class Lexer {

    public static final char ESCAPE_CHAR = '\\';

    private static final String NON_ATOM_CHAR;

    private java.io.Reader input_reader;
    private int charbuf = -1;

    private int row = 1;
    private int col = 0;

    private String token_buf;
    private int token_row = -1;
    private int token_col = -1;

    static {
        String nonAtomChar = "()';`,";

        if (Value.HAS_STRING) {
            NON_ATOM_CHAR = nonAtomChar + Value.STR_MARKER;
        } else {
            NON_ATOM_CHAR = nonAtomChar;
        }
    }

    public Lexer(java.io.Reader input_reader) {
        this.input_reader = input_reader;
    }

    public Lexer(String input) {
        input_reader = new StringReader(input);
    }

    public String peekToken() throws LispException {
        if (token_buf == null)
            token_buf = token();

        return token_buf;
    }

    public String nextToken() throws LispException {
        if (token_buf != null) {
            String token = token_buf;
            token_buf = null;

            return token;
        } else
            return token();
    }

    public int[] getPosition() {
        return new int[] { row, col };
    }

    public int[] getTokenPosition() {
        return new int[] { token_row, token_col };
    }

    public void close() {
        try {
            if (input_reader != null) {
                input_reader.close();
                input_reader = null;
            }
        }
        catch (IOException ex) {
            input_reader = null;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            close();
        }
        catch (Exception ex) {}
    }

    public int peek() {
        if (charbuf < 0)
            charbuf = next();

        return charbuf;
    }

    public int next() {
        int chr = charbuf;
        if (chr >= 0) {
            charbuf = -1;
            return chr;
        }

        try {
            chr = input_reader.read();
        }
        catch (IOException e) { }

        if (chr == '\r') {
            if (peek() != '\n') {
                row++;
                col = 0;
            }
        } else if (chr == '\n') {
            row++;
            col = 0;
        } else if (chr >= 0)
            col++;

        return chr;
    }

    public String read_line() {
        StringBuilder sb = new StringBuilder();

        int ch;
        for (ch = next(); ch >= 0 && ch != '\r' && ch != '\n'; ch = next()) {
            sb.append((char)ch);
        }

        if (ch < 0 && sb.isEmpty())
            return null;

        if (ch == '\r') {
            if (peek() == '\n')
                next();
        }

        return sb.toString();
    }

    private static boolean isWhitespace(int ch) {
        return ch <= ' ';
    }

    private void skipWhitespace() {
        while (isWhitespace(peek())) {
            if (next() < 0)
                break;
        }
    }

    private void skipToEol() {
        while (peek() >= 0) {
            switch (next()) {
                case '\r': if (peek() == '\n') next();
                case '\n': return;
            }
        }
    }

    private String token() throws LispException {
        StringBuilder token = new StringBuilder();
        boolean esc = false;

        skipWhitespace();
        token_row = row;
        token_col = col;

        for (;;) {
            int ch = peek();
            if (ch < 0)
                break;

            if (esc) {
                esc = false;
                token.append((char) ch);
                next();
                continue;
            }

            if (isWhitespace(ch))
                break;

            if (ch == ';') {
                if (token.isEmpty()) {
                    skipToEol();
                    skipWhitespace();
                    token_row = row;
                    token_col = col;
                    continue;
                }
                break;
            }

            if (ch == ',') {
                token.append((char) next());
                if (peek() == '@')
                    token.append((char) next());
                break;
            }

            if (ch == ESCAPE_CHAR) {
                esc = true;
                token.append((char) ch);
                next();
                continue;
            }

            if (Value.HAS_STRING && ch == Value.STR_MARKER) {
                if (token.isEmpty())
                {
                    token.append((char)next());
                    return readString(token);
                }
                break;
            }

            if (NON_ATOM_CHAR.indexOf(ch) >= 0) {
                if (token.isEmpty()) {
                    token.append((char) ch);
                    next();
                }
                break;
            }

            if (Character.isLowerCase(ch))
                ch = Character.toUpperCase(ch);

            token.append((char) ch);
            next();
        }

        return token.isEmpty() ? null : token.toString();
    }

    private String readString(StringBuilder token) throws LispException {
        boolean esc = false;

        for (;;) {
            int ch = peek();
            if (ch < 0)
                Interpreter.error("Unbalanced string", getPosition());

            if (esc) {
                switch (ch) {
                    case 'n':  next(); token.append('\n'); break;
                    case 'r':  next(); token.append('\r'); break;
                    case 'f':  next(); token.append('\f'); break;
                    case 'b':  next(); token.append('\b'); break;
                    case 't':  next(); token.append('\t'); break;
                    case '"':
                    case '\\': token.append((char)next()); break;

                    case 'o':
                    case '0':
                        next();
                        int ov = readEscOctal();
                        if (ov < 0) {
                            token.append(ch == '0' ? '\0' : (char) ch);
                        } else {
                            token.append((char) ov);
                        }
                        break;

                    case 'x':
                        next();
                        int xv = readEscHex();
                        if (xv < 0) {
                            token.append((char) ch);
                        } else {
                            token.append((char) xv);
                        }
                        break;

                    case 'u':
                    case 'U':
                        next();
                        int uv = readEscUnicode();
                        if (uv < 0) {
                            token.append((char) ch);
                        } else {
                            token.append((char) uv);
                        }
                        break;

                    default:
                        if ('1' <= ch && ch <= '9') {
                            int dv = readEscDecimal();
                            token.append((char) dv);
                        } else {
                            token.append((char) next());
                        }
                        break;
                }
                esc = false;
                continue;
            }

            if (ch == '\r' || ch == '\n')
                Interpreter.error("Unbalanced string", getPosition());

            if (ch == '\\') {
                next();
                esc = true;
                continue;
            }

            token.append((char)next());

            if (ch == Value.STR_MARKER)
                break;
        }

        return token.toString();
    }

    private int readEscOctal() {
        return readEscNumber(8, 255);
    }

    private int readEscDecimal() {
        return readEscNumber(10, 255);
    }

    private int readEscHex() {
        return readEscNumber(16, 255);
    }

    private int readEscUnicode() {
        return readEscNumber(16, 65535);
    }

    private int readEscNumber(int base, int max) {
        int val = 0;
        int idx = 0;

        int cnt = 0;
        for (int m = max; m > 0; m /= base) {
            cnt++;
        }

        for (idx = 0; idx < cnt; idx++) {
            int digit = switch (base) {
                case 8 -> getOctalDigit(peek());
                case 10 -> getDecimalDigit(peek());
                case 16 -> getHexadecimalDigit(peek());
                default -> -1;
            };
            if (digit < 0) {
                break;
            }
            int v = val * base + digit;
            if (v > max) {
                break;
            }
            val = v;
            next();
        }

        return idx == 0 ? -1 : val;
    }

    public static int getOctalDigit(int ch) {
        return '0' <= ch && ch <= '7' ? ch - '0' : -1;
    }

    public static int getDecimalDigit(int ch) {
        return '0' <= ch && ch <= '9' ? ch - '0' : -1;
    }

    public static int getHexadecimalDigit(int ch) {
        return '0' <= ch && ch <= '9' ? ch - '0'
             : 'A' <= ch && ch <= 'F' ? 10 + ch - 'A'
             : 'a' <= ch && ch <= 'f' ? 10 + ch - 'a'
             : -1;
    }

    public static boolean isEscapeChar(char ch) {
        if (Character.isAlphabetic(ch))
            return Character.isLowerCase(ch);

        return ch == ESCAPE_CHAR
                || isWhitespace(ch)
                || NON_ATOM_CHAR.indexOf(ch) >= 0;
    }
}
