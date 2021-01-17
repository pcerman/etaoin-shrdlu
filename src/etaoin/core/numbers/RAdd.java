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

public class RAdd extends Func {

    public RAdd() {
        super("+$");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        return Lst.fold(new Lst.FoldFun<Num.Real>() {
            int idx = 0;

            @Override
            public Num.Real apply(Num.Real init, Value v) throws LispException {
                idx++;
                if (!(v instanceof Num.Real))
                    expected("flonum", idx);

                Num.Real n = (Num.Real) v;

                init.add(n);

                return init;
            }
        }, new Num.Real(0), args);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.LSUBR;
    }
}
