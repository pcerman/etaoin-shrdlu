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

package etaoin.core.mapping;

import etaoin.Environment;
import etaoin.Interpreter;
import etaoin.LispException;
import etaoin.core.Utils;
import etaoin.data.*;
import java.util.ArrayList;
import java.util.List;

public class MapCar extends Func {

    public static enum FType { MAPCAR, MAPCAN, MAPC, MAPLIST, MAPCON, MAP }

    private final FType ftype;

    public MapCar(FType ftype) {
        super(ftype.name());
        this.ftype = ftype;
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {

        if (!Lst.hasNMoreElms(args, 2))
            requireNMore(2);

        Func fn = in.getFunction(args.getCar(), env);
        if (fn == null)
            expected("function", 1);

        List<Pair> par = Lst.toJavaList((Value v) -> v instanceof Pair ? (Pair) v : null, Lst.safeCdr(args));
        int cnt = par.size();

        boolean argIsCar = switch (ftype) {
            case MAPCAR, MAPCAN, MAPC -> true;
            default -> false;
        };

        boolean collect = switch (ftype) {
            case MAPCAR, MAPCAN, MAPLIST, MAPCON -> true;
            default -> false;
        };

        List<Value> res = new ArrayList<>();
        List<Value> arg = new ArrayList<>(cnt);
        for (int i=0; i < cnt; i++)
            arg.add(null);

        for (;;) {
            boolean valid = true;
            for (int i = 0; i < cnt; i++) {
                Pair p = par.get(i);
                if (p == null) {
                    valid = false;
                    break;
                }
                arg.set(i, argIsCar ? p.getCar() : p);
            }
            if (!valid)
                break;

            Value exp = fn.applyFn(in, env, Lst.toList(arg));
            if (collect)
                res.add(exp);

            for (int i = 0; i < cnt; i++) {
                arg.set(i, null);
                par.set(i, Lst.safeCdr(par.get(i)));
            }
        }

        Value val = switch (ftype) {
            case MAPCAR, MAPLIST -> Lst.toList(res);
            case MAPCAN, MAPCON -> {
                Pair lst = null;
                for (int i = res.size() - 1; i >= 0; i--) {
                    Value v = res.get(i);
                    if (v == Constant.Nil || v == null)
                        continue;
                    Pair p = Utils.getPair(v);
                    if (p == null)
                        expected("list");
                    lst = Lst.nconc(p, lst);
                }
                yield lst;
            }
            default -> Lst.nth(args, 1);
        };

        return Utils.checkNull(val);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
