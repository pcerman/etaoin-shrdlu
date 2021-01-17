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

public class Get extends Func {

    public Get() {
        super("GET");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        if (!Lst.hasTwoElms(args))
            requireN(2);

        if (Utils.isNil(args.getCar()))
            return Constant.Nil;

        Symbol symb = Utils.getSymbArg(args, 0);
        Pair plist = Utils.getPairArg(args, 0);
        if (symb == null && plist == null)
            expected("symbol/plist", 1);

        Symbol prop = Utils.getSymbArg(args, 1);
        if (prop == null)
            return Constant.Nil;

        Value val;
        if (symb != null) {
            val = symb.getProperty(prop);
        } else {
            Pair p = Lst.getProperty(prop, plist);
            val = Lst.cadr(p);
        }

        return Utils.checkNull(val);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
