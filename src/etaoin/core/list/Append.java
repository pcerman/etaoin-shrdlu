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
import etaoin.Ref;
import etaoin.core.Utils;
import etaoin.data.*;

public class Append extends Func {

    public Append() {
        super("APPEND");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        Pair list = null;
        Pair last = null;
        var lcdr = new Ref<Value>(null);

        Pair p = args;
        for (int idx = 1; p != null; idx++) {
            Value v = p.getCar();
            p = Lst.safeCdr(p);

            if (Utils.isNil(v))
                continue;

            Pair pa = Utils.getPair(v);
            if (pa == null)
                expected("list", idx);

            if (p != null) {
                pa = Lst.copy(pa, lcdr);
                if (lcdr.val != null)
                    expected("proper list", idx);
            }

            if (list == null) {
                list = pa;
                last = Lst.lastPair(pa);
            } else {
                last.setCdr(pa);
                last = Lst.lastPair(pa);
            }
        }

        return Utils.checkNull(list);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
