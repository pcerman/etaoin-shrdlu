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

package etaoin.core.numbers;

import etaoin.Environment;
import etaoin.Interpreter;
import etaoin.LispException;
import etaoin.core.Utils;
import etaoin.data.*;

public class NGreat extends Func {

    public NGreat() {
        super(">");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        if (!Lst.hasTwoElms(args))
            requireN(2);

        Num n1 = Utils.getNumArg(args, 0);
        if (n1 == null)
            expected("number", 1);

        Num n2 = Utils.getNumArg(args, 1);
        if (n2 == null)
            expected("number", 2);

        if (!n1.getClass().equals(n2.getClass()))
            sameNumbers();

        boolean eql;

        if (n1 instanceof Num.Int)
            eql = n1.getInt() > n2.getInt();
        else
            eql = n1.getReal() > n2.getReal();

        return in.getBool(eql);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
