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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Etaoin {

    public static final Terminal terminal;

    private static final long START_NANO_TIME;

    static {
        terminal = new Terminal();
        START_NANO_TIME = System.nanoTime();
    }

    public static long getNanoTime() {
        return System.nanoTime() - START_NANO_TIME;
    }

    private void loadInit(Interpreter in) {

        Environment env = in.TopEnv;
        InputStream stream = null;

        in.setTraceLevel(0);

        try {
            stream = getClass().getResourceAsStream("/init.lisp");
            Reader rdr = new Reader(new InputStreamReader(stream));

            for (Value val = in.read(rdr, env); val != null; val = in.read(rdr, env)) {
                in.eval(val, env);
            }
        }
        catch (Exception ex) { }
        finally {
            try {
                if (stream != null)
                    stream.close();
            }
            catch (IOException ex) { }
        }
    }

    public static void main(String[] args) throws Exception {

        terminal.println("etaoin 0.1");

        Interpreter in = new Interpreter();

        var etaoin = new Etaoin();
        etaoin.loadInit(in);

        Environment env = in.TopEnv;

        env.setValue(in.PLUS1, in.PLUS1);
        env.setValue(in.PLUS2, in.PLUS2);
        env.setValue(in.PLUS3, in.PLUS3);

        env.setValue(in.STAR1, in.STAR1);
        env.setValue(in.STAR2, in.STAR2);
        env.setValue(in.STAR3, in.STAR3);

        File file = new File(".etaoinrc");
        if (file.exists()) {
            in.setTraceLevel(0);

            Reader loader = new Reader("(load `.etaoinrc`)");
            try {
                in.eval(in.read(loader, env), env);
            }
            catch (Exception ex) {
                terminal.println("\nERROR: " + ex.getMessage());
            }
        }

        int exp_cnt = 0;
        int blank_cnt = 0;
        int required_blank = 5;

        for (;;) {
            String line = terminal.readline("user> ");

            if (line == null || "".equals(line)) {
                if (++blank_cnt >= required_blank) {
                    terminal.println("\nBye");
                    break;
                }
                continue;
            } else {
                if (line.charAt(0) == 0x04
                        || line.charAt(0) == 0x11
                        || line.charAt(0) == 0x1a) {
                    // Ctrl-D, Ctrl-Q, Ctrl-Z
                    terminal.println("\nBye");
                    break;
                }

                blank_cnt = 0;
            }

            try {
                Reader rdr = new Reader(line);

                in.setTraceLevel(0);
                for (Value v = in.read(rdr, env); v != null; v = in.read(rdr, env)) {
                    in.setCharPosition(env);

                    Value last_v;

                    if (v == in.PLUS1 || v == in.PLUS2 || v == in.PLUS3
                        || v == in.STAR1 || v == in.STAR2 || v == in.STAR3)
                        last_v = env.getValue(Utils.getSymb(v));
                    else {
                        last_v = Utils.checkNull(in.eval(v, env));

                        exp_cnt++;

                        if (exp_cnt > 2) {
                            env.setValue(in.PLUS3, env.getValue(in.PLUS2));
                            env.setValue(in.STAR3, env.getValue(in.STAR2));
                        }

                        if (exp_cnt > 1) {
                            env.setValue(in.PLUS2, env.getValue(in.PLUS1));
                            env.setValue(in.STAR2, env.getValue(in.STAR1));
                        }

                        env.setValue(in.PLUS1, v);
                        env.setValue(in.STAR1, last_v);
                    }

                    Printer.print(last_v, in.getOutBase(env));

                    in.setTraceLevel(0);
                }
            }
            catch (LispException.Quit ex) {
                terminal.println(ex.getMessage());
                break;
            }
            catch (Exception ex) {
                blank_cnt = 0;
                terminal.println("\nERROR: " + ex.getMessage());
            }
        }
    }
}
