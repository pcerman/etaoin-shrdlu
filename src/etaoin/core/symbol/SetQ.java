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

public class SetQ extends Func {

    public SetQ() {
        super("SETQ");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        int len = Lst.length(args);
        if (len % 2 != 0 || len < 2)
            error("even number of arguments is required");

        Value v2 = null;

        for (int idx = 1; args != null; idx += 2) {
            Symbol symb = Utils.getSymbArg(args, 0);
            if (symb == null) {
                expected("symbol", idx);
            }
            if (symb.isReadonly())
                error("symbol '%s' is protected", symb.getName());

            v2 = in.eval(Lst.nth(args, 1), env);

            var symbEnv = env.getOwnerOf(symb);
            if (symbEnv != null)
                symbEnv.setValue(symb, v2);
            else
                symb.setValue(v2);

            args = Lst.safeCdr(args.getCdr());
        }

        return Utils.checkNull(v2);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.FSUBR;
    }
}
