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
import etaoin.core.Utils;
import etaoin.data.*;

public class LessP extends Func {

    public LessP() {
        super("LESSP");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        if (!Lst.hasNMoreElms(args, 2))
            requireNMore(2);

        Boolean grt = Lst.fold(new Lst.FoldFun<Boolean>() {
            Num last = null;
            int idx = 0;

            @Override
            public Boolean apply(Boolean init, Value v) throws LispException {
                idx++;
                if (!(v instanceof Num))
                    expected("number", idx);

                if (init) {
                    Num n = (Num) v;

                    if (last == null)
                        last = n;
                    else if (last instanceof Num.Real || v instanceof Num.Real)
                        init = last.getReal() < n.getReal();
                    else
                        init = last.getInt() < n.getInt();

                    last = n;
                }

                return init;
            }
        }, true, args);

        return in.getBool(grt);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.LSUBR;
    }
}
