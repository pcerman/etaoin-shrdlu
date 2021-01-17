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
import etaoin.Interpreter;
import etaoin.LispException;
import etaoin.data.*;
import java.util.ArrayList;
import java.util.List;

public class BreakInfo extends Func {

    public static enum FType { BRK_LEVEL, BRK_TAG, BRK_FORM, BRK_DEPTH,
                               BRK_ENV, BRK_GET, BRK_SET, BRK_PUT,
                               BRK_VARS, BRK_VALS, BACKTRACE }

    private final FType ftype;

    public BreakInfo(FType ftype) {
        super(ftype.name().replace('_', '-'));
        this.ftype = ftype;
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        Value ret = switch (ftype) {

            case BRK_LEVEL -> {
                if (Lst.hasNMoreElms(args, 2))
                    requireN(0, 1);

                Environment brk_env = find_brk_env(in, env, get_depth_arg(args));
                yield (brk_env == null) ? null
                      : Utils.getInt(brk_env.getValue(in.BRKLVL));
            }

            case BRK_TAG -> {
                if (Lst.hasNMoreElms(args, 2))
                    requireN(0, 1);

                Environment brk_env = find_brk_env(in, env, get_depth_arg(args));
                yield (brk_env == null) ? null
                      : Lst.nth(Utils.getPair(brk_env.getValue(in.BRKARGS)), 0);
            }

            case BRK_FORM -> {
                if (Lst.hasNMoreElms(args, 2))
                    requireN(0, 1);

                Environment brk_env = find_brk_env(in, env, get_depth_arg(args));
                yield (brk_env == null) ? null
                      : Lst.nth(Utils.getPair(brk_env.getValue(in.BRKARGS)), 2);
            }

            case BRK_DEPTH -> {
                if (args != null)
                    requireN(0);

                Environment brk_env = get_nth_env(in, env, 0);
                int cnt = 0;
                for (; brk_env != null; cnt++)
                    brk_env = brk_env.getOuter();

                yield new Num.Int(cnt);
            }

            case BRK_ENV -> {
                if (Lst.hasNMoreElms(args, 2))
                    requireN(0, 1);

                Environment brk_env = get_nth_env(in, env, get_depth_arg(args));

                if (brk_env != null) {
                    var map = brk_env.getBindings();
                    if (map != null) {
                        List<Value> jlst = new ArrayList<>();

                        map.entrySet().forEach(
                                (ent) -> {
                                    jlst.add(Lst.cons(ent.getKey(), ent.getValue()));
                                });

                        yield Lst.toList(jlst);
                    }
                }
                yield null;
            }

            case BRK_GET -> {
                if (!Lst.hasTwoElms(args) && !Lst.hasNMoreElms(args, 3))
                    requireN(2, 3);

                Environment brk_env = get_nth_env(in, env, get_depth_arg(args));

                Symbol sym = Utils.getSymbArg(args, 1);
                if (sym == null)
                    expected("symbol", 1);

                yield (brk_env == null) ? Lst.nth(args, 2)
                      : brk_env.getValue(sym);
            }

            case BRK_SET -> {
                if (!Lst.hasNMoreElms(args, 3))
                    requireN(3);

                Environment brk_env = get_nth_env(in, env, get_depth_arg(args));

                Symbol sym = Utils.getSymbArg(args, 1);
                if (sym == null)
                    expected("symbol", 1);

                if (brk_env != null)
                    brk_env = brk_env.getOwnerOf(sym);

                if (brk_env == null)
                    error("symbol not found");

                Value val = Lst.nth(args, 2);
                brk_env.setValue(sym, val);
                yield val;
            }

            case BRK_PUT -> {
                if (!Lst.hasNMoreElms(args, 3))
                    requireN(3);

                Environment brk_env = get_nth_env(in, env, get_depth_arg(args));

                Symbol sym = Utils.getSymbArg(args, 1);
                if (sym == null)
                    expected("symbol", 1);

                if (brk_env == null)
                    error("environment not found");

                Value val = Lst.nth(args, 2);
                brk_env.setValue(sym, val);
                yield val;
            }

            case BRK_VARS -> {
                if (!Lst.hasSingleElm(args))
                    requireN(1);

                Symbol sym = Utils.getSymbArg(args, 0);
                if (sym == null)
                    expected("symbol", 1);

                List<Value> envs = new ArrayList<>();

                Environment brk_env = get_nth_env(in, env, 0);

                for (int i = 0; brk_env != null; i++) {
                    if (brk_env.isOwnerOf(sym))
                        envs.add(new Num.Int(i));
                    brk_env = brk_env.getOuter();
                }

                yield Lst.toList(envs);
            }

            case BRK_VALS -> {
                if (!Lst.hasSingleElm(args))
                    requireN(1);

                Symbol sym = Utils.getSymbArg(args, 0);
                if (sym == null)
                    expected("symbol", 1);

                List<Value> envs = new ArrayList<>();

                Environment brk_env = get_nth_env(in, env, 0);

                for (int i = 0; brk_env != null; i++) {
                    if (brk_env.isOwnerOf(sym))
                        envs.add(Lst.cons(new Num.Int(i), brk_env.getValue(sym)));
                    brk_env = brk_env.getOuter();
                }

                yield Lst.toList(envs);
            }

            case BACKTRACE -> {
                if (Lst.hasNMoreElms(args, 2))
                    requireN(0, 1);

                Environment brk_env = get_nth_env(in, env, 0);

                Value val = null;

                if (args != null) {

                    int depth = get_depth_arg(args);

                    for (int i = 0; brk_env != null; i++) {
                        if (brk_env.isOwnerOf(in.CUREXP)) {
                            if (depth-- <= 0) {
                                val = Lst.cons(brk_env.getValue(in.CUREXP), new Num.Int(i));
                                break;
                            }
                        }
                        brk_env = brk_env.getOuter();
                    }

                } else {

                    List<Value> jlst = new ArrayList<>();

                    for (int i = 0; brk_env != null; i++) {
                        if (brk_env.isOwnerOf(in.CUREXP))
                            jlst.add(brk_env.getValue(in.CUREXP));
                        brk_env = brk_env.getOuter();
                    }

                    val = Lst.toList(jlst);

                }

                yield val;
            }
        };

        return Utils.checkNull(ret);
    }

    private int get_depth_arg(Pair args) throws LispException {

        if (!Lst.hasNMoreElms(args, 1))
            return 0;

        Num.Int num = Utils.getIntArg(args, 0);
        if (num == null || num.getValue() < 0)
            expected("non negative number");

        return (int)Math.min(num.getValue(), Integer.MAX_VALUE);
    }

    private Environment get_nth_env(Interpreter in, Environment env, int depth)
            throws LispException {

        env = env.getOwnerOf(in.BRKARGS);
        if (env == null)
            error("break environment is not found");

        for (int i=0; env != null && i <= depth; i++) {
            env = env.getOuter();
        }

        return env;
    }

    private Environment find_brk_env(Interpreter in, Environment env, int depth)
            throws LispException {

        env = env.getOwnerOf(in.BRKARGS);
        if (env == null)
            error("break environment is not found");

        for (; env != null && depth > 0; depth--) {
            env = env.getOuter();
            if (env != null)
                env = env.getOwnerOf(in.BRKARGS);
        }

        return env;
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
