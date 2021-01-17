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

package etaoin.core;

import etaoin.Environment;
import etaoin.Interpreter;
import etaoin.LispException;
import etaoin.Ref;
import etaoin.data.*;

public class ProgV extends Func {

    public ProgV() {
        super("PROGV");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {
        if (!Lst.hasNMoreElms(args, 3))
            requireNMore(3);

        //------------------------------------------
        Value v1 = in.eval(Lst.nth(args, 0), env);
        Pair vars = Utils.getPair(v1);
        if (vars == null && !Utils.isNil(v1))
            expected("list of variables", 1);

        Ref<Value> t = new Ref<>(null);
        var jvars = Lst.toJavaList(vars, t);
        if (t.val != null)
            expected("proper list of variables", 1);

        //------------------------------------------
        Value v2 = in.eval(Lst.nth(args, 1), env);
        Pair vals = Utils.getPair(v2);
        if (vals == null && !Utils.isNil(v2))
            expected("list of values", 2);

        var jvals = Lst.toJavaList(vals, t);
        if (t.val != null)
            expected("proper list of values", 2);
        //------------------------------------------

        env = new Environment(env);

        int cnt = Math.min(jvars.size(), jvals.size());
        for (int i=0; i < cnt; i++) {
            Symbol s = Utils.getSymb(jvars.get(i));
            if (s == null)
                expected("symbol", i+ 1);
            env.setValue(s, jvals.get(i));
        }

        cnt = jvars.size();
        for (int i= jvals.size(); i < cnt; i++) {
            Symbol s = Utils.getSymb(jvars.get(i));
            if (s == null)
                expected("symbol", i+ 1);
            env.setValue(s, Constant.Nil);
        }

        Value val = null;

        for (Pair body = Lst.safeCdr(args.getCdr());
             body != null;
             body = Lst.safeCdr(body)) {
            val = in.eval(body.getCar(), env);
        }

        return Utils.checkNull(val);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.FSUBR;
    }
}
