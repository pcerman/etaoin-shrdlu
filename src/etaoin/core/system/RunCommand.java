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

package etaoin.core.system;

import etaoin.Environment;
import etaoin.Interpreter;
import etaoin.LispException;
import etaoin.core.Utils;
import etaoin.data.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 *
 * @author peter
 */
public class RunCommand extends Func {

    public RunCommand() {
        super("RUN-COMMAND");
    }

    @Override
    protected Value apply(Interpreter in, Environment env, Pair args) throws LispException {
        if (!Lst.hasNMoreElms(args, 1))
            requireNMore(1);

        List<String> as = Lst.toJavaList((Value v) -> v.toString(false), args);

        ProcessBuilder pb = new ProcessBuilder(as);

        Value v = null;

        try {
            Process process = pb.start();

            int exitVal = process.waitFor();

            var out = process.getInputStream();
            var err = process.getErrorStream();

            v = Lst.create(
                out == null ? Constant.Nil : new Port(Port.PortType.INP, new InputStreamReader(out)),
                err == null ? Constant.Nil : new Port(Port.PortType.INP, new InputStreamReader(err)),
                new Num.Int(exitVal)
            );

        } catch (IOException | InterruptedException e) {
            error(e.getMessage());
        }

        return Utils.checkNull(v);
    }

    @Override
    public FunctionType getFunctionType() {
        return Func.FunctionType.LSUBR;
    }
}
