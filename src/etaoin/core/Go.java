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

public class Go extends Func {

    public Go() {
        super("GO");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {
        if (!Lst.hasSingleElm(args))
            requireN(1);

        Value v = args.getCar();
        if (Utils.getInt(v) != null || Utils.getSymb(v) != null)
            throw new LispException.Go(v);

        v = in.eval(v, env);
        if (Utils.getInt(v) != null || Utils.getSymb(v) != null)
            throw new LispException.Go(v);

        expected("symbol or fixnum");
        return null;
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.FSUBR;
    }
}
