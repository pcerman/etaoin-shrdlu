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

public class Quit extends Func {

    public static enum FType { QUIT, EXIT, BYE }

    private final FType ftype;

    public Quit(FType ftype) {
        super(ftype.name());
        this.ftype = ftype;
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        Num.Int n = Num.Int.zero;

        if (ftype == FType.EXIT) {
            if (Lst.hasSingleElm(args)) {
                n = Utils.getIntArg(args, 0);
                if (n == null)
                    expected("fixnum");
            } else if (args != null) {
                requireN(0, 1);
            }
        } else if (args != null) {
            requireN(0);
        }

        throw new LispException.Quit(n);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.FSUBR;
    }
}
