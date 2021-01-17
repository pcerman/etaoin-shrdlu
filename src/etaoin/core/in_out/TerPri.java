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
import etaoin.core.Utils;
import etaoin.data.*;

public class TerPri extends Func {

    public TerPri() {
        super("TERPRI");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {
        if (Lst.hasNMoreElms(args, 2))
            requireN(0, 1);

        Port port = null;
        if (Lst.length(args) == 1 && !Utils.isNil(Lst.nth(args, 0))) {
            port = Utils.getPortArg(args, 0);
            if (port == null || port.getPortType() == Port.PortType.INP)
                expected("output port");
        }

        if (port != null) {
            if (port.print("\n") < 0)
                error("cannot write to port");
        } else {
            Etaoin.terminal.println();
            in.setCharPosition(env);
        }
        return Constant.Nil;
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
