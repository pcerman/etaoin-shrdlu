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

public class TypeP extends Func {

    public TypeP() {
        super("TYPEP");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        if (!Lst.hasSingleElm(args))
            requireN(1);

        Value v = args.getCar();
        Symbol t = switch (v.getType()) {
            case CONSTANT, SYMBOL -> in.SYMBOL;
            case INTEGER -> in.FIXNUM;
            case FLOAT -> in.FLONUM;
            case STRING -> in.STRING;
            case PAIR -> in.LIST;
            default -> in.RANDOM;
        };

        return t;
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
