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

public class Boole extends Func {

    public Boole() {
        super("BOOLE");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        if (!Lst.hasNElms(args, 3))
            requireN(3);

        Num.Int op = Utils.getIntArg(args, 0);
        if (op == null || op.getValue() < 0 || op.getValue() > 15)
            expected("fixnum from range <0, 15>", 1);

        Num.Int n1 = Utils.getIntArg(args, 1);
        if (n1 == null)
            expected("fixnum", 2);

        Num.Int n2 = Utils.getIntArg(args, 2);
        if (n2 == null)
            expected("fixnum", 3);

        long x = n1.getValue();
        long y = n2.getValue();

        // if op is in binary form abcd then boolean operation is:
        //
        //          y
        //      | 0   1 |
        // -----+---+---|
        //    0 | a | c |
        // x    +---+---|
        //    1 | b | d |
        //

        long n = switch ((int)op.getValue()) {
            case 0 -> 0;
            case 1 -> x & y;
            case 2 -> (~x) & y;
            case 3 -> y;
            case 4 -> x & (~y);
            case 5 -> x;
            case 6 -> (x ^ (~y)) | ((~x) & y);
            case 7 -> x | y;
            case 8 -> ~(x | y);
            case 9 -> (x & y) | ((~x) & (~y));
            case 10 -> ~x;
            case 11 -> ~(x) | y;
            case 12 -> ~y;
            case 13 -> x | (~y);
            case 14 -> ~(x & y);
            default -> -1;
        };

        return new Num.Int(n);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
