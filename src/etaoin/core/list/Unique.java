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
import etaoin.Ref;
import etaoin.core.Utils;
import etaoin.data.*;

public class Unique extends Func {

    public static enum FType { UNIQUE, UNIQ }

    private final FType ftype;

    public Unique(FType ftype) {
        super(ftype.name());
        this.ftype = ftype;
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        if (!Lst.hasSingleElm(args))
            requireN(1);

        Value v1 = args.getCar();
        if (Utils.isNil(v1))
            return Constant.Nil;

        Pair lst = Utils.getPair(v1);
        if (lst == null)
            expected("list", 1);

        Ref<Value> lastCdr = new Ref<>(null);

        if (ftype == FType.UNIQUE)
            lst = Lst.unique(lst, lastCdr, (x, y) -> Value.equal(x, y));
        else
            lst = Lst.unique(lst, lastCdr, (x, y) -> Value.eq(x, y));

        if (lastCdr.val != null)
            expected("proper list");

        return Utils.checkNull(lst);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
