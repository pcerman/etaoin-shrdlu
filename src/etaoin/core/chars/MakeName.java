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

public class MakeName extends Func {

    public static enum FType { MAKNAM, IMPLODE, MAKSTR }

    private final FType ftype;

    public MakeName(FType ftype) {
        super(ftype.name());
        this.ftype = ftype;
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        if (!Lst.hasSingleElm(args))
            requireN(1);

        Pair arg = Utils.getPairArg(args, 0);
        if (arg == null) {
            if (ftype == FType.MAKSTR && Utils.isNil(args.getCar()))
                return new Str("");

            expected("list");
        }

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

        return switch(ftype) {
            case IMPLODE -> in.getSymbol(sb.toString());
            case MAKNAM -> in.createSymbol(sb.toString());
            case MAKSTR -> new Str(sb.toString());
        };
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
