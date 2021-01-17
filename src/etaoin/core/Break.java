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

package etaoin.core;

import etaoin.Environment;
import etaoin.Etaoin;
import etaoin.Interpreter;
import etaoin.LispException;
import etaoin.Printer;
import etaoin.Reader;
import etaoin.data.*;

public class Break extends Func {

    private static int level = 0;

    public Break() {
        super("BREAK");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {
        if (Lst.hasNMoreElms(args, 4))
            requireN(0, 3);

        Value tag = Lst.nth(args, 0);
        Value pre = Lst.nth(args, 1);
        Value ret = Lst.nth(args, 2);

        env = new Environment(env);

        env.setValue(in.BRKARGS, Utils.checkNull(args));
        env.setValue(in.BRKLVL, new Num.Int(++level));

        env.setValue(in.PLUS1, in.PLUS1);
        env.setValue(in.PLUS2, in.PLUS2);
        env.setValue(in.PLUS3, in.PLUS3);

        env.setValue(in.STAR1, in.STAR1);
        env.setValue(in.STAR2, in.STAR2);
        env.setValue(in.STAR3, in.STAR3);

        if (pre != null && Utils.isNil(in.eval(pre, env))) {
            return ret == null ? Constant.Nil
                   : Utils.checkNull(in.eval(ret, env));
        }

        String prompt = String.format("break %d> ", level);

        if (Etaoin.terminal.getOutPosition() != 0)
            Etaoin.terminal.println();

        if (!Utils.isNil(tag))
            Etaoin.terminal.printf("; bkpt %s\n", tag.toString(false));

        int exp_cnt = 0;

        loop: for (;;) {
            String line = Etaoin.terminal.readline(prompt);

            if (line == null || "".equals(line)) {
                continue;
            }

            boolean next_ret = false;

            switch (line.charAt(0)) {
                case 0x04, 0x1a -> {
                    // Ctrl-D, Ctrl-Z  =>  return NIL
                    ret = null;
                    break loop;
                }

                case 0x01, 0x18 -> {
                    // Ctrl-A, Ctrl-X  =>  return EXP or (ARG 3)
                    line = line.substring(1);
                    if (line.isBlank()) {
                        if (ret != null)
                            ret = in.eval(ret, env);
                        break loop;
                    } else {
                        next_ret = true;
                    }
                }

                case 0x07, 0x13 -> {
                    // Ctrl-G, Ctrl-S  =>  top REPL
                    --level;
                    throw new LispException.Throw(in.BRKSTOP, in.BRKSTOP);
                }

                case 0x11 -> {
                    // Ctrl-Q  =>  (QUIT)
                    --level;
                    throw new LispException.Quit(Num.Int.one.create());
                }
            }

            String ln = line.trim();
            if (ln.compareTo("$P") == 0 || ln.compareTo("$p") == 0) {
                if (ret != null)
                    ret = in.eval(ret, env);
                break;
            }

            try {
                Reader rdr = new Reader(line);

                Value last_v = null;

                for (Value v = in.read(rdr, env); v != null; v = in.read(rdr, env)) {
                    in.setCharPosition(env);

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

                    if (!next_ret)
                        Printer.print(last_v, in.getOutBase(env));
                }

                if (next_ret) {
                    ret = last_v;
                    break;
                }
            }
            catch (LispException.Quit ex) {
                --level;
                throw ex;
            }
            catch (LispException.Return ex) {
                ret = ex.getValue();
                break;
            }
            catch (LispException.Throw ex) {
                if (ex.getTag() == in.BRKSTOP && ex.getValue() == in.BRKSTOP) {
                    throw ex;
                }
                Etaoin.terminal.println("\nERROR: " + ex.getMessage());
            }
            catch (Exception ex) {
                Etaoin.terminal.println("\nERROR: " + ex.getMessage());
            }
        }

        --level;

        return Utils.checkNull(ret);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.FSUBR;
    }
}
