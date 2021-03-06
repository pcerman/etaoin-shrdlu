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

public class FAdd1 extends Func {

    public FAdd1() {
        super("1+");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        if (!Lst.hasSingleElm(args))
            requireN(1);

        Num.Int n = Utils.getIntArg(args, 0);
        if (n == null)
            expected("fixnum");

        n = n.create();
        n.add(Num.Int.one);

        return n;
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
