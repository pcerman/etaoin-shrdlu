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

public class Eval extends Func {

    public static enum FType { EVAL, EVAL_BEFORE, EVAL_AFTER }

    private final FType ftype;

    public Eval(FType ftype) {
        super(ftype.name().replace('_', '-'));
        this.ftype = ftype;
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        Value ret = switch (ftype) {
            case EVAL -> {
                if (!Lst.hasSingleElm(args))
                    requireN(1);

                yield in.eval(args.getCar(), env);
            }

            case EVAL_BEFORE, EVAL_AFTER -> {

                Value v1 = Lst.nth(args, 0);
                if (v1 == null || v1 == Constant.Nil) {

                    Pair fns;

                    if (ftype == FType.EVAL_BEFORE) {
                        in.Before.forEach(fn -> fn.setBefore(null));
                        fns = Lst.toList(in.Before);
                        in.Before.clear();
                    } else {
                        in.After.forEach(fn -> fn.setAfter(null));
                        fns = Lst.toList(in.After);
                        in.After.clear();
                    }

                    yield fns;
                }

                if (v1 == in.T) {
                    if (ftype == FType.EVAL_BEFORE)
                        yield Lst.toList(in.Before);
                    else
                        yield Lst.toList(in.After);
                }

                if (Lst.hasNMoreElms(args, 3))
                    requireN(1, 2);

                Value v2 = Lst.nth(args, 1);

                Func fn1 = in.getFunction(v1, env);
                if (fn1 == null)
                    expected("function", 1);

                Func fn2 = in.getFunction(v2, env);
                if (v2 != null && fn2 == null)
                    expected("function", 2);

                if (ftype == FType.EVAL_BEFORE) {

                    if (fn2 == null) {
                        if (in.Before.contains(fn1))
                            in.Before.remove(fn1);
                    } else {
                        if (!in.Before.contains(fn1))
                            in.Before.add(fn1);
                    }
                    fn1.setBefore(fn2);

                } else {

                    if (fn2 == null) {
                        if (in.After.contains(fn1))
                            in.After.remove(fn1);
                    } else {
                        if (!in.After.contains(fn1))
                            in.After.add(fn1);
                    }
                    fn1.setAfter(fn2);

                }

                yield v1;
            }
        };

        return Utils.checkNull(ret);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
