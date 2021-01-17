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

public class Assoc extends Func {

    public static enum FType { ASSOC, ASSQ, SASSOC, SASSQ }

    private final FType ftype;

    public Assoc(FType ftype) {
        super(ftype.name());
        this.ftype = ftype;
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        boolean useFn = switch (ftype) {
            case SASSOC, SASSQ -> true;
            default -> false;
        };

        if (!useFn && !Lst.hasTwoElms(args))
            requireN(2);

        if (useFn && !Lst.hasNElms(args, 3))
            requireN(3);

        Value elm = args.getCar();

        Value v2 = Lst.nth(args, 1);
        if (Utils.isNil(v2))
            return Constant.Nil;

        Pair lst = Utils.getPair(v2);
        if (lst == null)
            expected("list", 2);

        Func fn = null;
        if (useFn) {
            fn = in.getFunction(Lst.nth(args, 2), env);
            if (fn == null)
                expected("function", 3);
        }

        Pair res;
        Ref<Value> t = new Ref<>(null);

        boolean useEqual = switch (ftype) {
            case ASSOC, SASSOC -> true;
            default -> false;
        };

        if (useEqual)
            res = Lst.find(lst,
                           (v) -> {
                               Pair p = Utils.getPair(v);
                               return p != null && Value.equal(p.getCar(), elm);
                           },
                           t);
        else
            res = Lst.find(lst,
                           (v) -> {
                               Pair p = Utils.getPair(v);
                               return p != null && Value.eq(p.getCar(), elm);
                           },
                           t);

        if (t.val != null)
            expected("proper list");

        if (useFn && res == null)
            return fn.applyFn(in, env, null);

        return Utils.checkNull(res != null ? res.getCar() : null);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
