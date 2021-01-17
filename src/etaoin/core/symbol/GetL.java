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

package etaoin.core.symbol;

import etaoin.Environment;
import etaoin.Interpreter;
import etaoin.LispException;
import etaoin.core.Utils;
import etaoin.data.*;

public class GetL extends Func {

    public GetL() {
        super("GETL");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        if (!Lst.hasTwoElms(args))
            requireN(2);

        Symbol symb = Utils.getSymbArg(args, 0);
        if (symb == null)
            expected("symbol", 1);

        Value v = Lst.nth(args, 1);
        if (Utils.isNil(v))
            return Constant.Nil;

        Pair props = Utils.getPair(v);
        if (props == null)
            expected("list", 2);

        Pair plist = symb.getPlist();

        while (props != null) {
            Symbol psym = Utils.getSymbArg(props, 0);
            if (psym != null) {
                Pair plst = Lst.getProperty(psym, plist);
                if (plst != null)
                    return plst;
            }

            v = props.getCdr();
            props = Utils.getPair(v);
            if (props == null && v != null)
                expected("proper list", 2);
        }

        return Constant.Nil;
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
