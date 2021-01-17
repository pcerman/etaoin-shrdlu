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

public class Throw extends Func {

    public Throw() {
        super("THROW");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {
        Value exp = null;
        Symbol tag = null;

        if (Lst.hasSingleElm(args)) {
            exp = args.getCar();
        } else if (Lst.hasTwoElms(args)) {
            exp = args.getCar();
            tag = Utils.getSymbArg(args, 1);
            if (tag == null)
                expected("symbol", 2);
        } else
            requireN(1, 2);

        Value val = in.eval(exp, env);

        throw new LispException.Throw(val, tag);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.FSUBR;
    }
}
