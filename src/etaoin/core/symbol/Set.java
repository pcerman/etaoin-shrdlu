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

package etaoin.core.symbol;

import etaoin.Environment;
import etaoin.Interpreter;
import etaoin.LispException;
import etaoin.core.Utils;
import etaoin.data.*;

public class Set extends Func {

    public Set() {
        super("SET");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        if (!Lst.hasTwoElms(args))
            requireN(2);

        Symbol symb = Utils.getSymbArg(args, 0);
        if (symb == null)
            expected("symbol", 1);
        if (symb.isReadonly())
            error("symbol '%s' is protected", symb.getName());

        Value v2 = Lst.nth(args, 1);

        var symbEnv = env.getOwnerOf(symb);
        if (symbEnv != null)
            symbEnv.setValue(symb, v2);
        else
            symb.setValue(v2);

        return v2;
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
