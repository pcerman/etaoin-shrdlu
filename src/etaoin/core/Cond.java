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

package etaoin.core;

import etaoin.Environment;
import etaoin.Interpreter;
import etaoin.LispException;
import etaoin.data.*;

public class Cond extends Func {

    public Cond() {
        super("COND");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        Value val = null;

        for (int idx = 1; args != null; idx++) {
            Pair clause = Utils.getPairArg(args, 0);
            if (clause == null)
                expected("clause list", idx);

            args = Lst.safeCdr(args);

            val = in.eval(clause.getCar(), env);
            if (!Utils.isNil(val)) {
                for (clause = Lst.safeCdr(clause);
                     clause != null;
                     clause = Lst.safeCdr(clause)) {
                    val = in.eval(clause.getCar(), env);
                }
                break;
            }
        }

        return Utils.checkNull(val);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.FSUBR;
    }
}
