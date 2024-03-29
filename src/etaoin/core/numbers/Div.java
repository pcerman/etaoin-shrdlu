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

public class Div extends Func {

    public Div() {
        super("QUOTIENT");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        if (!Lst.hasNMoreElms(args, 1))
            requireNMore(1);

        Num n = Lst.fold(new Lst.FoldFun<Num>() {
            int idx = 0;

            @Override
            public Num apply(Num init, Value v) throws LispException {
                idx++;
                if (!(v instanceof Num))
                    expected("number", idx);

                Num n = (Num) v;

                if (init == null)
                    init = n.create();
                else if (init instanceof Num.Int) {
                    if (v instanceof Num.Real)
                        init = new Num.Real(init.getReal() / n.getReal());
                    else
                        init.div(n);
                } else
                    init.div(n);

                return init;
            }
        }, null, args);

        if (Lst.hasSingleElm(args)) {
            if (n.getType() == Value.Type.INTEGER) {
                n = new Num.Int(1 / n.getInt());
            } else if (n.getType() == Value.Type.FLOAT) {
                double val = n.getReal();
                if (val != 1.0 && val != -1.0) {
                    ((Num.Real) n).setValue(1.0 / val);
                }
            }
        }

        return n;
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.LSUBR;
    }
}
