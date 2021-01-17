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

public class FSqrt extends Func {

    public FSqrt() {
        super("ISQRT");
    }

    public long isqrt(long num) throws LispException {
        if (num < 0)
            error("negative number");

        if (num < 9) {
            if (num == 0)
                return 0;
            if (num < 4)
                return 1;
            return 2;
        }

        long one = 1;
        for (long sha = 4; sha <= num && sha > 0; sha *= 4)
            one = sha;

        long res = 0;
        while (one > 0) {
            if (num >= one + res) {
                num -= one + res;
                res  = (res / 2) + one;
                one /= 4;
            } else {
                res /= 2;
                one /= 4;
            }
        }
        return res;
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        if (!Lst.hasSingleElm(args))
            requireN(1);

        Num.Int n = Utils.getIntArg(args, 0);
        if (n == null)
            expected("fixnum");

        n = new Num.Int(isqrt(n.getInt()));

        return n;
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
