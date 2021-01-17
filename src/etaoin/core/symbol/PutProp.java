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

public class PutProp extends Func {

    public PutProp() {
        super("PUTPROP");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        if (!Lst.hasNElms(args, 3))
            requireN(3);

        Symbol symb = Utils.getSymbArg(args, 0);
        Pair plist = Utils.getPairArg(args, 0);
        if (symb == null && plist == null)
            expected("symbol", 1);

        Value v = Lst.nth(args, 1);

        Symbol prop = Utils.getSymbArg(args, 2);
        if (prop == null)
            expected("symbol", 3);

        if (prop == in.PNAME)
            error("PNAME is protected property");

        if (Utils.isNil(v)) {
            if (symb != null)
                symb.remProperty(prop);
            else
                v = Lst.remProperty(prop, plist);
        } else {
            if (symb != null)
                symb.setProperty(prop, v);
            else
                v = new Pair(prop, new Pair(v, Lst.remProperty(prop, plist)));
        }

        return v;
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
