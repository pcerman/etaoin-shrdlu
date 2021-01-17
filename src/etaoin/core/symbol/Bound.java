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

package etaoin.core.symbol;

import etaoin.Environment;
import etaoin.Interpreter;
import etaoin.LispException;
import etaoin.core.Utils;
import etaoin.data.*;

public class Bound extends Func {

    public static enum FType { BOUNDP, BOUND, DEFINEDP }

    private final FType ftype;

    public Bound(FType ftype) {
        super(ftype.name());
        this.ftype = ftype;
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        Value val = switch (ftype) {

            case DEFINEDP -> {
                if (!Lst.hasSingleElm(args))
                    requireN(1);

                Symbol symb = Utils.getSymbArg(args, 0);
                if (symb == null)
                    expected("symbol", 1);

                Value v = env.getValue(symb);
                if (v == null)
                    v = symb.getValue();

                yield v != null ? Lst.create(v) : Constant.Nil;
            }

            case BOUNDP -> {
                if (!Lst.hasSingleElm(args))
                    requireN(1);

                Symbol symb = Utils.getSymbArg(args, 0);
                if (symb == null)
                    expected("symbol", 1);

                Value v = symb.getValue();

                yield v != null ? Lst.create(v) : Constant.Nil;
            }

            case BOUND -> {
                if (!Lst.hasTwoElms(args))
                    requireN(2);

                Symbol symb = Utils.getSymbArg(args, 0);
                if (symb == null)
                    expected("symbol", 1);

                Value v = Utils.checkNull(Lst.nth(args, 1));
                symb.setValue(v);

                yield v;
            }
        };

        return Utils.checkNull(val);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
