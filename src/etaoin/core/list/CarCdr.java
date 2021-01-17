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

package etaoin.core.list;

import etaoin.Environment;
import etaoin.Interpreter;
import etaoin.LispException;
import etaoin.core.Utils;
import etaoin.data.*;

public class CarCdr extends Func {

    public static enum FType { CAR, CDR }

    private final FType ftype;

    public CarCdr(FType ftype) {
        super(ftype.name());
        this.ftype = ftype;
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        if (!Lst.hasSingleElm(args))
            requireN(1);

        Value v = args.getCar();
        Pair p = Utils.getPair(v);
        if (p == null) {
            if (Utils.isNil(v) || in.Status.cxr)
                return Constant.Nil;

            expected("pair");
        }

        return Utils.checkNull(ftype == FType.CAR ? p.getCar() : p.getCdr());
    }

    @Override
    public FunctionType getFunctionType() {
        return FunctionType.SUBR;
    }
}
