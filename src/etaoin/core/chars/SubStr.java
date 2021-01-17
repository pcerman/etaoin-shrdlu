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

public class SubStr extends Func {

    public SubStr() {
        super("SUBSTR");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        if (!Lst.hasTwoElms(args) && !Lst.hasNElms(args, 3))
            requireN(2, 3);

        int cnt = Lst.length(args);
        int len = 0;

        String st = null;
        long n1 = 0;
        long n2;

        if (cnt >= 2) {
            Str str = Utils.getStrArg(args, 0);
            if (str == null)
                expected("string", 1);
            st = str.getValue();
            len = st.length();

            Num.Int num = Utils.getIntArg(args, 1);
            if (num == null)
                expected("fixnum", 2);
            n1 = num.getValue();

            if (n1 < 1 || n1 > len)
                error("index is out of range");

            n1--;
        }

        if (cnt == 3) {
            Num.Int num = Utils.getIntArg(args, 2);
            if (num == null)
                expected("fixnum", 3);
            n2 = num.getValue();

            if (n2 < 0)
                error("negative length");
            else if (n1 + n2 > len)
                error("length is to big");

            n2 += n1;
        } else
            n2 = len;

        if (n1 == n2)
            return new Str("");

        st = st.substring((int)n1, (int)n2);

        return new Str(st);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
