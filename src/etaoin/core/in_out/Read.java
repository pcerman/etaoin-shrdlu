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
import etaoin.Reader;
import etaoin.core.Utils;
import etaoin.data.*;

public class Read extends Func {

    public static enum FType { READ, READCH, READLINE }

    private final FType ftype;

    public Read(FType ftype) {
        super(ftype.name());
        this.ftype = ftype;
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

        Value v = switch (ftype) {
            case READ -> {
                if (port != null) {
                    yield port.read(in, env);
                }

                String ln = Etaoin.terminal.readline();
                if (ln != null)
                    try {
                        Reader rdr = new Reader(ln);
                        yield in.read(rdr, env);
                    }
                    catch (Exception ex) {}

                yield null;
            }

            case READCH -> {
                int ch = port != null ?  port.read_char() : Etaoin.terminal.read();
                yield ch > 0 ? in.createSymbol(String.format("%c", (char)ch)) : null;
            }

            case READLINE -> {
                String ln = port != null ?  port.read_line() : Etaoin.terminal.readline();
                yield ln != null ? new Str(ln) : null;
            }
        };

        return v == null ? Utils.checkNull(eof) : v;
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
