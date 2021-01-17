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

public class Expt extends Func {

    public Expt() {
        super("EXPT");
    }

    private static long expt(long x, long y) {
        if (y == 1)
            return x;

        if (y % 2 == 0) {
            long z = expt(x, y / 2);
            return z * z;
        } else {
            long z = expt(x, y / 2);
            return x * z * z;
        }
    }

    private static double expt(double x, long y) {
        if (y == 1)
            return x;

        if (y % 2 == 0) {
            double z = expt(x, y / 2);
            return z * z;
        } else {
            double z = expt(x, y / 2);
            return x * z * z;
        }
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        if (!Lst.hasTwoElms(args))
            requireN(2);

        Num n1 = Utils.getNumArg(args, 0);
        if (n1 == null)
            expected("number", 1);

        Num.Int n2 = Utils.getIntArg(args, 1);
        if (n2 == null)
            expected("fixnum", 2);

        long y = n2.getInt();
        if (y == 0)
            return Num.Int.one;

        if (y < 0) {
            double x = expt(n1.getReal(), -y);
            n1 = new Num.Real(1.0 / x);
        }
        else if (n1 instanceof Num.Int) {
            long x = expt(n1.getInt(), y);
            n1 = new Num.Int(x);
        } else {
            double x = expt(n1.getReal(), y);
            n1 = new Num.Real(x);
        }

        return n1;
    }

    @Override
    public FunctionType getFunctionType() {
        return FunctionType.SUBR;
    }
}
