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

public class Gcd extends Func {

    public Gcd() {
        super("GCD");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        if (!Lst.hasTwoElms(args))
            requireN(2);

        Num.Int n1 = Utils.getIntArg(args, 0);
        if (n1 == null)
            expected("fixnum", 1);

        Num.Int n2 = Utils.getIntArg(args, 1);
        if (n2 == null)
            expected("fixnum", 2);

        long x = Math.abs(n1.getInt());
        long y = Math.abs(n2.getInt());
        if (x < y) {
            long t = x;
            x = y;
            y = t;
        }

        while (y != 0) {
            long t = y;
            y = x % y;
            x = t;
        }

        return new Num.Int(x);
    }

    @Override
    public FunctionType getFunctionType() {
        return FunctionType.SUBR;
    }
}
