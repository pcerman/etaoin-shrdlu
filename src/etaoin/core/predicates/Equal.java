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

package etaoin.core.predicates;

import etaoin.Environment;
import etaoin.Interpreter;
import etaoin.LispException;
import etaoin.data.*;

public class Equal extends Func {

    public static enum FType { EQUAL, EQ }

    private final FType ftype;

    public Equal(FType ftype) {
        super(ftype.name());
        this.ftype = ftype;
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        if (!Lst.hasTwoElms(args))
            requireN(2);

        Value v1 = args.getCar();
        Value v2 = Lst.nth(args, 1);

        boolean eql = ftype == FType.EQUAL
                      ? Value.equal(v1, v2)
                      : Value.eq(v1, v2);

        return in.getBool(eql);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
