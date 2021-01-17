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

package etaoin.core.chars;

import etaoin.Environment;
import etaoin.Interpreter;
import etaoin.LispException;
import etaoin.core.Utils;
import etaoin.data.*;

public class Explode extends Func {

    public static enum FType { EXPLODE, EXPLODEC, EXPLODEN };

    private final FType ftype;

    public Explode(FType ftype) {
        super(ftype.name());
        this.ftype = ftype;
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        if (!Lst.hasSingleElm(args))
            requireN(1);

        Value val = args.getCar();
        String str = Value.toString(val, ftype == FType.EXPLODE, in.getOutBase(env));

        Pair list = null;
        Pair last = null;
        for (int i=0; i < str.length(); i++) {
            Value ch;
            if (ftype == FType.EXPLODEN)
                ch = new Num.Int(str.charAt(i));
            else {
                String nm = String.format("%c", str.charAt(i));
                ch = in.getSymbol(nm);
            }

            if (list == null) {
                list = new Pair(ch, null);
                last = list;
            } else {
                Pair p = last;
                last = new Pair(ch, null);
                p.setCdr(last);
            }
        }

        return Utils.checkNull(list);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
