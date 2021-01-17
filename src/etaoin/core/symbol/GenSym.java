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

public class GenSym extends Func {

    private char prefix = 'G';
    private int index = 0;

    public GenSym() {
        super("GENSYM");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        if (args != null && !Lst.hasSingleElm(args))
            requireN(0, 1);

        if (args != null) {
            Symbol symb = Utils.getSymbArg(args, 0);
            if (symb != null)
                prefix = symb.getName().charAt(0);
            else {
                Num.Int n = Utils.getIntArg(args, 0);
                if (n != null)
                    index = (int)n.getValue();
                else
                    expected("symbol or fixnum");
            }
        }

        String sn = String.format("%c%04d", prefix, index++);

        return in.createSymbol(sn);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
