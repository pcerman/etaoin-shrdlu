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

package etaoin.core.in_out;

import etaoin.Environment;
import etaoin.Etaoin;
import etaoin.Interpreter;
import etaoin.LispException;
import etaoin.data.*;

public class TyiPeek extends Func {

    public TyiPeek() {
        super("TYIPEEK");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {
        if (!Lst.isNull(args))
            requireN(0);

        int ch = Etaoin.terminal.peek();

        return new Num.Int(ch);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
