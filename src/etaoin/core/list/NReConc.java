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

public class NReConc extends Func {

    public NReConc() {
        super("NRECONC");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        if (!Lst.hasTwoElms(args))
            requireN(2);

        Value v1 = args.getCar();
        Pair p1 = Utils.getPair(v1);
        if (p1 == null && !Utils.isNil(v1))
            expected("list", 1);

        Value v2 = Lst.nth(args, 1);
        Pair p2 = Utils.getPair(v2);
        if (p2 == null && !Utils.isNil(v2))
            expected("list", 2);

        if (p1 == null)
            return Utils.checkNull(p2);

        if (p1 != null) {
            var lv = new Ref<Value>(null);
            p1 = Lst.nreconc(p1, p2, lv);

            if (lv.val != null)
                expected("proper list", 1);
        }

        return Utils.checkNull(p1);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
