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

public class Atan extends Func {

    public Atan() {
        super("ATAN");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        if (!Lst.hasSingleElm(args) && !Lst.hasTwoElms(args))
            requireN(1, 2);

        Num n1 = Utils.getNumArg(args, 0);
        if (n1 == null)
            expected("number", 1);

        if (Lst.length(args) > 1) {
            Num n2 = Utils.getNumArg(args, 1);
            if (n2 == null)
                expected("number", 2);

            n1 = new Num.Real(Math.atan2(n1.getReal(), n2.getReal()));
        } else
            n1 = new Num.Real(Math.atan(n1.getReal()));

        return n1;
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
