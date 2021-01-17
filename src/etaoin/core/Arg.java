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

public class Arg extends Func {

    public Arg() {
        super("ARG");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        if (!Lst.hasSingleElm(args))
            requireN(1);

        Value va = env.getValue(in.TOPARGS);
        if (va == null)
            error("no arguments found");

        Pair topArgs = Utils.getPair(va);

        Value v1 = args.getCar();
        if (v1 == Constant.Nil) {
            return new Num.Int(Lst.length(topArgs));
        }

        Num.Int n = Utils.getIntArg(args, 0);
        if (n == null)
            expected("fixnum", 1);

        long idx = n.getValue();
        int cnt = Lst.length(topArgs);

        if (idx < 1 || idx > cnt) {
            if (cnt > 1)
                error("index %d is out of range: <1, %d>", idx, cnt);
            else
                error("index %d is out of range", idx);
        }

        return Lst.nth(topArgs, (int) idx - 1);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
