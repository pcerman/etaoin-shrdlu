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

public class Comment extends Func {

    public Comment() {
        super("COMMENT");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        return in.getSymbol("COMMENT");
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.FSUBR;
    }
}
