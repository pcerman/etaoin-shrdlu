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

package etaoin.core.list;

import etaoin.Environment;
import etaoin.Interpreter;
import etaoin.LispException;
import etaoin.core.Utils;
import etaoin.data.*;

public class Remove extends Func {

    public static enum FType { REMOVE, REMQ }

    private final FType ftype;

    public Remove(FType ftype) {
        super(ftype.name());
        this.ftype = ftype;
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        if (!Lst.hasNMoreElms(args, 2) || Lst.hasNMoreElms(args, 4))
            requireN(2, 3);

        Value elm = args.getCar();

        Value v2 = Lst.nth(args, 1);
        if (Utils.isNil(v2))
            return Constant.Nil;

        Pair lst = Utils.getPair(v2);
        if (lst == null)
            expected("list", 2);

        int cnt = 1;

        Num.Int n = Utils.getIntArg(args, 2);
        if (n != null && n.getValue() < Integer.MAX_VALUE)
            cnt = (int) Math.max(n.getValue(), 0);

        Pair res;

        if (ftype == FType.REMOVE)
            res = Lst.remove(lst, cnt, (v) -> Value.equal(v, elm));
        else
            res = Lst.remove(lst, cnt, (v) -> Value.eq(v, elm));

        return Utils.checkNull(res);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
