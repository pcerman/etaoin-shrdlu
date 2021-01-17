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

package etaoin.core.numbers;

import etaoin.Environment;
import etaoin.Interpreter;
import etaoin.LispException;
import etaoin.data.*;

public class RDiv extends Func {

    public RDiv() {
        super("/$");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        Num.Real n = Lst.fold(new Lst.FoldFun<Num.Real>() {
            int idx = 0;

            @Override
            public Num.Real apply(Num.Real init, Value v) throws LispException {
                idx++;
                if (!(v instanceof Num.Real))
                    expected("flonum", idx);

                Num.Real n = (Num.Real) v;

                if (init == null)
                    init = n.create();
                else
                    init.div(n);

                return init;
            }
        }, null, args);

        if (n == null)
            return Num.Real.one;

        if (Lst.hasSingleElm(args))
            n.setValue(1.0 / n.getValue());

        return n;
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.LSUBR;
    }
}
