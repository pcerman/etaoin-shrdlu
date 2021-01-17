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

public class FDiv extends Func {

    public FDiv() {
        super("/");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        Num.Int n = Lst.fold(new Lst.FoldFun<Num.Int>() {
            int idx = 0;

            @Override
            public Num.Int apply(Num.Int init, Value v) throws LispException {
                idx++;
                if (!(v instanceof Num.Int))
                    expected("fixnum", idx);

                Num.Int n = (Num.Int) v;

                if (init == null)
                    init = n.create();
                else
                    init.div(n);

                return init;
            }
        }, null, args);

        if (n == null)
            return Num.Int.one;

        if (Lst.hasSingleElm(args)) {
            if (n.getInt() > 1 || n.getInt() < -1)
                n.setValue(0);
            else
                n.setValue(1 / n.getValue());
        }

        return n;
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.LSUBR;
    }
}
