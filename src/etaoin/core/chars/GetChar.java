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

public class GetChar extends Func {

    public static enum FType { GETCHAR, GETCHARN }

    private final FType ftype;

    public GetChar(FType ftype) {
        super(ftype.name());
        this.ftype = ftype;
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        if (!Lst.hasTwoElms(args))
            requireN(2);

        String sn;

        Value v = Lst.nth(args, 0);
        if (v == Constant.Nil)
            sn = Constant.Nil.getName();
        else {
            Str str = Utils.getStr(v);
            if (str != null) {
                sn = str.getValue();
            } else {
                Symbol symb = Utils.getSymb(v);
                if (symb == null)
                    expected("symbol", 1);
                sn = symb.getName();
            }
        }

        Num.Int idx = Utils.getIntArg(args, 1);
        if (idx == null)
            expected("fixnum", 2);

        if (idx.getValue() <= 0 || idx.getValue() > sn.length())
            return Constant.Nil;

        return ftype == FType.GETCHARN
               ? new Num.Int((int) sn.charAt((int) idx.getValue() - 1))
               : in.getSymbol(String.format("%c", sn.charAt((int) idx.getValue() - 1)));
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
