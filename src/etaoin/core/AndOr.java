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

public class AndOr extends Func {

    public static enum FType { AND, OR }

    private final FType ftype;

    public AndOr(FType ftype) {
        super(ftype.name());
        this.ftype = ftype;
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        boolean isAnd = ftype == FType.AND;

        Value val = in.getBool(isAnd);

        for (int idx = 1; args != null; idx++) {
            Value arg = args.getCar();
            args = Lst.safeCdr(args);

            val = in.eval(arg, env);
            if (Utils.isNil(val) == isAnd)
                break;
        }

        return Utils.checkNull(val);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.FSUBR;
    }
}
