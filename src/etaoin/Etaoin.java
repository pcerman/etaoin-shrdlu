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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Etaoin {

    public static final String VERSION = "ETAOIN-0.2";
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

    private static final Pattern OPTION = Pattern.compile("^(--?[^-=][^=]*)");
    private static final Pattern OPTION_VAL = Pattern.compile("=(.*)$");

    private static final List<String> options = new ArrayList<String>();
    private static final List<String> data = new ArrayList<String>();

    private static String getOption(String arg) {
        if (arg == null)
            return null;

        var m = OPTION.matcher(arg);
        return m.find() ? m.group(1) : null;
    }

    private static int search(String[] arr, String elm) {
        for (int i=0; i < arr.length; i++) {
            if (elm.compareTo(arr[i]) == 0)
                return i;
        }
        return -1;
    }

    private static boolean parseArguments(String[] args, String[] allowedOptions) {
        for (int i=0; i < args.length; i++) {
            var arg = args[i];

            if ("--".compareTo(arg) == 0) {
                for (int j=i+1; j < args.length; j++) {
                    data.add(args[j]);
                }
                return true;
            }

            var opt = getOption(arg);
            if (opt != null && !opt.isBlank()) {
                if (search(allowedOptions, opt) < 0) {
                    terminal.println("unknown option: " + opt);
                    return false;
                }
                options.add(arg);
            } else if (arg.startsWith("-")) {
                terminal.println("wrong option: " + arg);
                return false;
            } else
                data.add(arg);
        }

        return true;
    }

    private static boolean hasOption(String option) {
        return options.stream().anyMatch(opt -> (option.compareTo(getOption(opt)) == 0));
    }

    private static boolean hasAnyOption(String... opts) {
        for (var opt : opts) {
            if (hasOption(opt))
                return true;
        }
        return false;
    }

    private static String getValue(String option) {
        var m = OPTION_VAL.matcher(option);
        return m.find() ? m.group(1) : "";
    }

    private static String optValue(String option) {
        var fopt = options.stream().filter(opt -> (option.compareTo(getOption(opt)) == 0)).findFirst();
        if (fopt.isPresent()) {
            return getValue(fopt.get());
        }
        return null;
    }

    private static void evalString(Interpreter in, Environment env, String line) throws Exception {
        if (line == null || line.isBlank())
            return;

        Reader rdr = new Reader(line);

        for (Value v = in.read(rdr, env); v != null; v = in.read(rdr, env)) {
            in.setTraceLevel(0);
            in.eval(v, env);
        }
    }

    public static void main(String[] args) throws Exception {

        if (!parseArguments(args, new String[] {"-h", "-help", "--help", "-init", "-e", "-f", "-q", "-v"}))
            return;

        if (hasAnyOption("-h", "-help", "--help")) {
            terminal.println("use: etaoin <option>* <-->? <arg>*\n");
            terminal.println("where <option> is:");
            terminal.println("      -h, -help, --help    print this help");
            terminal.println("      -init=<file>?        load initialization file (default: .etaoinrc)");
            terminal.println("      -e=<expr>            evaluate expression");
            terminal.println("      -f=<file>            load file");
            terminal.println("      -q                   be quiet");
            terminal.println("      -v                   print version and exit");
            return;
        }

        if (hasOption("-v")) {
            terminal.println(VERSION);
            return;
        }

        if (!hasOption("-q"))
            terminal.println(VERSION);

        Interpreter in = new Interpreter(data.toArray(String[]::new));

        var etaoin = new Etaoin();
        etaoin.loadInit(in);

        Environment env = in.TopEnv;

        env.setValue(in.PLUS1, in.PLUS1);
        env.setValue(in.PLUS2, in.PLUS2);
        env.setValue(in.PLUS3, in.PLUS3);

        env.setValue(in.STAR1, in.STAR1);
        env.setValue(in.STAR2, in.STAR2);
        env.setValue(in.STAR3, in.STAR3);

        var rc = optValue("-init");

        String rc_file = null;
        if (rc == null)
            rc_file = ".etaoinrc";
        else if (!rc.isBlank())
            rc_file = rc;

        try {
            if (rc_file != null && (new File(rc_file)).exists()) {
                String load_rc = String.format("(load %s%s%s)", Value.STR_MARKER, rc_file, Value.STR_MARKER);
                evalString(in, env, load_rc);
            }

            for (var opt : options) {
                switch (getOption(opt)) {
                    case "-e": {
                        String val = getValue(opt);
                        if (val != null && !val.isBlank())
                            evalString(in, env, val);
                        break;
                    }

                    case "-f": {
                        String val = getValue(opt);
                        if (val != null && !val.isBlank()) {
                            String exp = String.format("(load %s%s%s)", Value.STR_MARKER, val, Value.STR_MARKER);
                            evalString(in, env, exp);
                        }
                        break;
                    }
                }
            }
        }
        catch (LispException.Quit ex) {
            return;
        }
        catch (Exception ex) {
            terminal.println("\nERROR: " + ex.getMessage());
            return;
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
