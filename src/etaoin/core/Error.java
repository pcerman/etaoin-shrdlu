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

package etaoin.core;

import etaoin.Environment;
import etaoin.Interpreter;
import etaoin.LispException;
import etaoin.data.*;

public class Error extends Func {

    public Error() {
        super("ERROR");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {
        if (Lst.hasNMoreElms(args, 2))
            requireN(0, 1);

        Value exp = Lst.nth(args, 0);

        if (exp == null) {
            exp = Constant.Nil;
            throw new LispException.Throw(new Pair(Num.Int._one, exp), in.ERRSET);
        } else {
            String msg = exp == null ? "null" : exp.toString(false);
            throw new LispException(msg);
        }
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
