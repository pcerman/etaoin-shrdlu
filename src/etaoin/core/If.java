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

public class If extends Func {

    public If() {
        super("IF");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        if (!Lst.hasNElms(args, 3))
            requireN(3);

        Value val = in.eval(Lst.nth(args, 0), env);
        if (Utils.isNil(val)) {
            val = in.eval(Lst.nth(args, 2), env);
        } else {
            val = in.eval(Lst.nth(args, 1), env);
        }

        return Utils.checkNull(val);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.FSUBR;
    }
}
