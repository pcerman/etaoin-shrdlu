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

public class AppArgs extends Func {

    public AppArgs() {
        super("APP-ARGS");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        if (!Lst.isNull(args))
            requireN(0);

        Pair list = null;
        Pair last = null;

        String[] appArgs = in.Arguments;

        if (appArgs != null && appArgs.length > 0) {
            list = Lst.create(new Str(appArgs[0]));
            last = list;

            for (int i = 1; i < appArgs.length; i++) {
                String arg = appArgs[i];

                var nv = new etaoin.Ref<Value>(null);

                if (etaoin.Reader.isDecimalLiteral(arg))
                    nv.val = new Num.Int(arg);
                else if (etaoin.Reader.isOctalLiteral(arg))
                    nv.val = new Num.Int(arg, 8);
                else if (etaoin.Reader.isFloatingLiteral(arg))
                    nv.val = new Num.Real(arg);
                else if (!etaoin.Reader.isRadixLiteral(arg, nv))
                    nv.val = new Str(arg);

                Pair p = Lst.create(nv.val);
                last.setCdr(p);
                last = p;
            }
        }

        return Utils.checkNull(list);
    }

    @Override
    public FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
