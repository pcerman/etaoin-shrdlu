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

public class Min extends Func {

    public Min() {
        super("MIN");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        if (!Lst.hasNMoreElms(args, 1))
            requireNMore(1);

        return Lst.fold(new Lst.FoldFun<Num>() {
            int idx = 0;

            @Override
            public Num apply(Num init, Value v) throws LispException {
                idx++;
                if (!(v instanceof Num))
                    expected("number", idx);

                Num n = (Num) v;

                if (init == null) {
                    init = n;
                } else if (init instanceof Num.Real || n instanceof Num.Real) {
                    if (n.getReal() < init.getReal())
                        init = n;
                } else {
                    if (n.getInt() < init.getInt())
                        init = n;
                }

                return init;
            }
        }, null, args);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.LSUBR;
    }
}
