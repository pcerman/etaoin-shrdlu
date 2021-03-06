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

public class CXr extends Func {

    public CXr(String name) {
        super(name);
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        if (!Lst.hasSingleElm(args))
            requireN(1);

        Value v = args.getCar();

        for (int i = name.length() - 2; i > 0; i--) {
            char ch = name.charAt(i);
            if (ch != 'A' && ch != 'D')
                break;

            Pair p = Utils.getPair(v);
            if (p == null) {
                if (Utils.isNil(v) || in.Status.cxr)
                    return Constant.Nil;

                expected("pair");
            }

            v = (ch == 'A') ? p.getCar() : p.getCdr();
        }

        return Utils.checkNull(v);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
