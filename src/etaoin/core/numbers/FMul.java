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

public class FMul extends Func {

    public FMul() {
        super("*");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        return Lst.fold(new Lst.FoldFun<Num.Int>() {
            int idx = 0;

            @Override
            public Num.Int apply(Num.Int init, Value v) throws LispException {
                idx++;
                if (!(v instanceof Num.Int))
                    expected("fixnum", idx);

                Num.Int n = (Num.Int) v;

                init.mul(n);

                return init;
            }
        }, new Num.Int(1), args);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.LSUBR;
    }
}
