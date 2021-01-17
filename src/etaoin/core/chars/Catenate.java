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

package etaoin.core.chars;

import etaoin.Environment;
import etaoin.Interpreter;
import etaoin.LispException;
import etaoin.core.Utils;
import etaoin.data.*;

public class Catenate extends Func {

    public Catenate() {
        super("CATENATE");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        StringBuilder sb = new StringBuilder();

        Pair p = args;
        for (int idx = 1; p != null; idx++) {
            Str str = Utils.getStrArg(p, 0);
            if (str == null)
                expected("string", idx);

            sb.append(str.getValue());
            p = Lst.safeCdr(p);
        }

        return new Str(sb.toString());
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
