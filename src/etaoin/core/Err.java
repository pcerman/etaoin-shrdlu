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

public class Err extends Func {

    public Err() {
        super("ERR");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {
        if (Lst.hasNMoreElms(args, 3))
            requireN(0, 2);

        Value exp = Lst.nth(args, 0);
        Value tag = Lst.nth(args, 1);

        if (exp == null) {
            exp = Constant.Nil;
            throw new LispException.Throw(new Pair(Num.Int._one, exp), in.ERRSET);
        }
        else if (Utils.isNil(tag)) {
            exp = in.eval(exp, env);
            throw new LispException.Throw(new Pair(Num.Int.zero, exp), in.ERRSET);
        } else {
            throw new LispException.Throw(new Pair(Num.Int.one, exp), in.ERRSET);
        }
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.FSUBR;
    }
}
