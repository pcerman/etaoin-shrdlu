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

public class Expand extends Func {

    public static enum FType { EXPAND, EXPAND_ALL }

    private final FType ftype;

    public Expand(FType ftype) {
        super(ftype.name().replace('_', '-'));
        this.ftype = ftype;
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        if (!Lst.hasSingleElm(args))
            requireN(1);

        Value val = Lst.nth(args, 0);

        if (ftype == FType.EXPAND)
            val = in.expand_once(env, val);
        else
            val = in.expand_all(env, val);

        return Utils.checkNull(val);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.FSUBR;
    }
}
