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

public class Print extends Func {

    public static enum FType { PRINT, PRIN1, PRINC };

    private final FType ftype;

    public Print(FType ftype) {
        super(ftype.name());
        this.ftype = ftype;
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

        Value v = Utils.checkNull(args.getCar());

        int err = 0;
        switch (ftype) {
            case PRINT -> {
                if (port != null) {
                    err = port.printf("\n%s ", v.toString(true));
                } else {
                    Etaoin.terminal.printf("\n%s ", v.toString(true));
                    in.setCharPosition(env);
                }
            }
            case PRIN1 -> {
                if (port != null) {
                    err = port.print(v.toString(true));
                } else {
                    Etaoin.terminal.print(v.toString(true));
                    in.setCharPosition(env);
                }
            }
            case PRINC -> {
                if (port != null) {
                    err = port.print(v.toString(false));
                } else {
                    Etaoin.terminal.print(v.toString(false));
                    in.setCharPosition(env);
                }
            }
        }

        if (err < 0)
            error("cannot write to port");

        return v;
    }

    @Override
    public FunctionType getFunctionType() {
        return FunctionType.SUBR;
    }
}
