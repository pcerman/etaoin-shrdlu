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
import etaoin.core.Utils;
import etaoin.data.*;

public class CompSymStr extends Func {

    public static enum FType { SAMEPNAMEP, ALPHALESSP }

    private final FType ftype;

    public CompSymStr(FType ftype) {
        super(ftype.name());
        this.ftype = ftype;
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        if (!Lst.hasTwoElms(args))
            requireN(2);

        String sn1;
        String sn2;

        Str str = Utils.getStrArg(args, 0);
        if (str != null) {
            sn1 = str.getValue();
        } else {
            Symbol symb = Utils.getSymbArg(args, 0);
            if (symb == null)
                expected("symbol/string", 1);
            sn1 = symb.getName();
        }

        str = Utils.getStrArg(args, 1);
        if (str != null) {
            sn2 = str.getValue();
        } else {
            Symbol symb = Utils.getSymbArg(args, 1);
            if (symb == null)
                expected("symbol/string", 2);
            sn2 = symb.getName();
        }

        boolean compared = switch (ftype) {
            case SAMEPNAMEP -> sn1.compareTo(sn2) == 0;
            case ALPHALESSP -> sn1.compareTo(sn2) < 0;
        };

        return in.getBool(compared);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
