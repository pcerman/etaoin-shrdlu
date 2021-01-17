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

public class Member extends Func {

    public static enum FType { MEMBER, MEMQ }

    private final FType ftype;

    public Member(FType ftype) {
        super(ftype.name());
        this.ftype = ftype;
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        if (!Lst.hasTwoElms(args))
            requireN(2);

        Value elm = args.getCar();

        Value v2 = Lst.nth(args, 1);
        if (Utils.isNil(v2))
            return Constant.Nil;

        Pair lst = Utils.getPair(v2);
        if (lst == null)
            expected("list", 2);

        Pair res;
        Ref<Value> t = new Ref<>(null);

        if (ftype == FType.MEMBER)
            res = Lst.find(lst, (v) -> Value.equal(v, elm), t);
        else
            res = Lst.find(lst, (v) -> Value.eq(v, elm), t);

        if (t.val != null)
            expected("proper list");

        return Utils.checkNull(res);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
