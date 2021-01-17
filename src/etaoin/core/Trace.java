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

public class Trace extends Func {

    public static enum FType { TRACE, UNTRACE }

    private final FType ftype;

    public Trace(FType ftype) {
        super(ftype.name());
        this.ftype = ftype;
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        if (ftype == FType.UNTRACE && args == null) {

            in.Traced.forEach((fn) -> {
                fn.setTraced(false);
            });

            in.Traced.clear();

            return Constant.Nil;
        }

        Lst.fold((idx, v) -> {
            Func fn = in.getFunction(v, env);
            if (fn == null)
                expected("function", idx + 1);

            if (fn.getFunctionType() == FunctionType.MACRO) {
                error("unable to trace macro");
            }

            if (ftype == FType.TRACE) {
                if (!fn.isTraced())
                    fn.setTraced(true);

                if (!in.Traced.contains(fn)) {
                    in.Traced.add(fn);
                }
            } else {
                if (fn.isTraced())
                    fn.setTraced(false);

                if (in.Traced.contains(fn)) {
                    in.Traced.remove(fn);
                }
            }

            return idx + 1;
        }, 0, args);

        return args;
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.FSUBR;
    }
}
