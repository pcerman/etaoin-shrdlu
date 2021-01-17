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

public class Add extends Func {

    public Add() {
        super("PLUS");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        return Lst.fold(new Lst.FoldFun<Num>() {
            int idx = 0;

            @Override
            public Num apply(Num init, Value v) throws LispException {
                idx++;
                if (!(v instanceof Num))
                    expected("number", idx);

                Num n = (Num) v;

                if (init instanceof Num.Int) {
                    if (v instanceof Num.Real)
                        init = new Num.Real(init.getReal() + n.getReal());
                    else
                        init.add(n);
                } else
                    init.add(n);

                return init;
            }
        }, new Num.Int(0), args);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.LSUBR;
    }
}
