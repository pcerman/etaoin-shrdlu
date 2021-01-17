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

package etaoin.core.list;

import etaoin.Environment;
import etaoin.Interpreter;
import etaoin.LispException;
import etaoin.core.Utils;
import etaoin.data.*;

public class NConc extends Func {

    public NConc() {
        super("NCONC");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        Pair list = null;
        Pair last = null;

        for (int idx = 1; args != null; idx++) {
            Value v = args.getCar();
            args = Lst.safeCdr(args);

            if (Utils.isNil(v))
                continue;

            Pair pa = Utils.getPair(v);
            if (pa == null)
                expected("list", idx);

            Pair lp = Lst.lastPair(pa);
            if (lp.getCdr() != null && args != null)
                expected("proper list", idx);

            if (list == null) {
                list = pa;
                last = lp;
            } else {
                last.setCdr(pa);
                last = lp;
            }
        }

        return Utils.checkNull(list);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
