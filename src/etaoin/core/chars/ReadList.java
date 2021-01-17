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
import etaoin.Reader;
import etaoin.core.Utils;
import etaoin.data.*;

public class ReadList extends Func {

    public ReadList() {
        super("READLIST");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        if (!Lst.hasSingleElm(args))
            requireN(1);

        Pair arg = Utils.getPairArg(args, 0);
        if (arg == null)
            expected("list");

        StringBuilder sb = Lst.fold(new Lst.FoldFun<StringBuilder>() {
            int idx = 0;

            @Override
            public StringBuilder apply(StringBuilder init, Value v) throws LispException {
                idx++;

                int ch = Utils.getChar(v);
                if (ch < 0)
                    expected("char or fixnum", idx);

                init.append((char)ch);

                return init;
            }
        }, new StringBuilder(), arg);

        Value v;
        try {
            Reader rdr = new Reader(sb.toString());
            v = in.read(rdr, env);
        }
        catch (Exception ex) {
            v = null;
        }

        return Utils.checkNull(v);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
