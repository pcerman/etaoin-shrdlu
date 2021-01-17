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

public class Random extends Func {

    private static final long A1 = 40014;
    private static final long M1 = 2147483563;
    private static final long A2 = 40692;
    private static final long M2 = 2147483399;

    private static final long MM = M1 - 1;

    private long s1 = 1;
    private long s2 = 1;

    public Random() {
        super("RANDOM");
    }

    private void reset() {
        s1 = 1;
        s2 = 1;
    }

    private void init(long n1, long n2) {
        s1 = (Math.abs(n1) + 1) % M1;
        s2 = (Math.abs(n2) + 1) % M2;
    }

    private long random() {
        s1 = (A1 * s1) % M1;
        s2 = (A2 * s2) % M2;

        long s = s1 - s2;
        if (s < 0)
            s += MM;

        return s;
    }

    private long random(long n) {
        return n * random() / MM;
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        if (Lst.hasNMoreElms(args, 3))
            requireN(0, 2);

        long rnd = switch (Lst.length(args)) {
            case 1 -> {
                Value v = args.getCar();
                if (v == Constant.Nil) {
                    reset();
                    yield random();
                }

                if (v == in.T)
                    yield MM - 1;

                Num.Int n1 = Utils.getIntArg(args, 0);
                if (n1 == null || n1.getValue() < 2)
                    expected("fixnum > 1");
                yield random(n1.getValue());
            }

            case 2 -> {
                Num.Int n1 = Utils.getIntArg(args, 0);
                if (n1 == null)
                    expected("fixnum");

                Num.Int n2 = Utils.getIntArg(args, 1);
                if (n2 == null)
                    expected("fixnum");

                init(n1.getValue(), n2.getValue());
                yield random();
            }

            default -> random();
        };

        return new Num.Int(rnd);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
