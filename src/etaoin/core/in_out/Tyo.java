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

public class Tyo extends Func {

    public Tyo() {
        super("TYO");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {
        if (!Lst.hasSingleElm(args) && !Lst.hasTwoElms(args))
            requireN(1, 2);

        Port port = null;
        if (Lst.length(args) == 2 && !Utils.isNil(Lst.nth(args, 1))) {
            port = Utils.getPortArg(args, 1);
            if (port == null || port.getPortType() == Port.PortType.INP)
                expected("output port");
        }

        Num.Int n = Utils.getIntArg(args, 0);
        if (n == null)
            expected("fixnum");

        if (port != null) {
            if (port.printf("%c", (char)n.getValue()) < 0)
                error("cannot write to port");
        } else {
            Etaoin.terminal.print((char)n.getValue());
            in.setCharPosition(env);
        }

        return n;
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
