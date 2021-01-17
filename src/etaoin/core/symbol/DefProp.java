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

public class DefProp extends Func {

    public DefProp() {
        super("DEFPROP");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        if (!Lst.hasNElms(args, 3))
            requireN(3);

        Symbol symb = Utils.getSymbArg(args, 0);
        if (symb == null)
            expected("symbol", 1);

        Value val = Lst.nth(args, 1);

        Symbol prop = Utils.getSymbArg(args, 2);
        if (prop == null)
            expected("symbol", 3);

        if (prop == in.PNAME)
            error("PNAME is protected property");

        if (Utils.isNil(val))
            symb.remProperty(prop);
        else
            symb.setProperty(prop, val);

        return val;
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.FSUBR;
    }
}
