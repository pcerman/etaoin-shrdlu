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

public class Listify extends Func {

    public Listify() {
        super("LISTIFY");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        if (!Lst.hasSingleElm(args))
            requireN(1);

        Value va = env.getValue(in.TOPARGS);
        if (va == null)
            error("no arguments found");

        Pair topArgs = Utils.getPair(va);

        Num.Int n = Utils.getIntArg(args, 0);
        if (n == null)
            expected("fixnum", 1);

        long cnt = n.getValue();
        int len = Lst.length(topArgs);

        if (Math.abs(cnt) > len) {
            error("count %d is out of range", Math.abs(cnt));
        }

        Pair lst = null;
        if (cnt > 0)
            lst = Lst.take(topArgs, (int) cnt);
        else if (cnt < 0)
            lst = Lst.take(Lst.nthPair(topArgs, (int) (len + cnt)), (int) -cnt);

        return Utils.checkNull(lst);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
