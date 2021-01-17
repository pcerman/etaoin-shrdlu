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

public class Tyi extends Func {

    public Tyi() {
        super("TYI");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {
        if (Lst.hasNMoreElms(args, 3))
            requireN(0, 2);

        int cnt = Lst.length(args);

        Port port = null;
        Value eof = null;

        if (cnt >= 1) {
            eof = Lst.nth(args, 0);
            port = Utils.getPort(eof);
            if (port != null)
                eof = null;

            if (cnt >= 2) {
                Value v2 = Lst.nth(args, 1);
                Port p2 = Utils.getPort(v2);

                if (port != null && p2 != null)
                    expected("only one port");

                if (port == null && p2 == null)
                    expected("input port");

                if (p2 != null)
                    port = p2;
                else
                    eof = v2;
            }
        }

        if (port != null && port.getPortType() != Port.PortType.INP)
            expected("input port");

        int ch = (port != null) ? port.read_char(): Etaoin.terminal.read();

        return ch > 0 ? new Num.Int(ch) : Utils.checkNull(eof);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
