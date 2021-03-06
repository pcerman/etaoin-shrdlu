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
import etaoin.Interpreter;
import etaoin.LispException;
import etaoin.core.Utils;
import etaoin.data.*;

public class Close extends Func {

    public Close() {
        super("CLOSE");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {
        if (!Lst.hasSingleElm(args))
            requireN(1);

        Port p = Utils.getPortArg(args, 0);
        if (p == null)
            expected("port");

        return in.getBool(p.close());
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
