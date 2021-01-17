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

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;

public class Terminal {

    public static final int WIDTH = 120;
    private static final char CONTROL = '~';

    private final Console console;
    private final BufferedReader buffrd;

    private String buffer;
    private int charpos;
    private int outPosition;

    public Terminal() {
        console = System.console();

        if (console != null)
            buffrd = null;
        else
            buffrd = new BufferedReader(new InputStreamReader(System.in));

        buffer = null;
        charpos = 0;
        outPosition = 0;
    }

    public int getOutPosition() {
        return outPosition;
    }

    public String readline() {
        return readline(null);
    }

    public String readline(String prompt) {

        if (buffer != null) {
            String str = buffer;
            buffer = null;
            if (charpos < str.length()) {
                if (charpos > 0)
                    str = str.substring(charpos);
                return str;
            }
        }

        String line = null;

        if (console != null) {
            if (prompt != null)
                line = console.readLine(prompt);
            else
                line = console.readLine();
        } else {
            if (prompt != null)
                System.out.print(prompt);

            try { line = buffrd.readLine(); }
            catch (IOException ex) { }
        }

        return checkControlChars(line);
    }

    public int read() {
        if (buffer == null) {
            buffer = readline();
            charpos = 0;
        }

        int ch = -1;
        int len = buffer.length();

        if (charpos < len) {
            ch = buffer.charAt(charpos++);
        } else if (charpos == len) {
            buffer = null;
            charpos = 0;
            ch = '\n';
        }

        return ch;
    }

    public int peek() {
        if (buffer == null) {
            buffer = readline();
            charpos = 0;
        }

        int ch = -1;
        int len = buffer.length();

        if (charpos < len) {
            ch = buffer.charAt(charpos);
        } else if (charpos == len) {
            ch = '\n';
        }

        return ch;
    }

    public void println() {
        if (console != null)
            console.format("\n");
        else
            System.out.println();
        outPosition = 0;
    }

    public void println(String str) {
        if (console != null)
            console.format("%s\n", str);
        else
            System.out.println(str);
        outPosition = 0;
    }

    public void print(char ch) {
        if (console != null)
            console.format("%c", ch);
        else
            System.out.print(ch);

        if (ch == '\r' || ch == '\n')
            outPosition = 0;
        else
            outPosition++;
    }

    public void print(String str) {
        if (console != null)
            console.format("%s", str);
        else
            System.out.print(str);

        int idxcr = str.lastIndexOf('\r');
        int idxlf = str.lastIndexOf('\n');
        int idx = Math.max(idxcr, idxlf);
        if (idx >= 0)
            outPosition = str.length() - idx;
        else
            outPosition += str.length();
    }

    public void printf(String fmt, Object ...args) {
        print(String.format(fmt, args));
    }

    private static String checkControlChars(String str) {
        if (str == null || str.isEmpty())
            return str;

        if (str.charAt(0) != CONTROL)
            return str;

        int len = str.length();
        if (len < 2)
            return str;

        char ch = str.charAt(1);
        if ('A' <= ch && ch <= '_' || 'a' <= ch && ch <= 'z') {
            String ctrl = String.valueOf((char)(ch & 0x1f));
            return len > 2 ? ctrl.concat(str.substring(2)) : ctrl;
        }

        return str;
    }
}
