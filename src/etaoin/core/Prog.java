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

public class Prog extends Func {

    public Prog() {
        super("PROG");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {
        if (!Lst.hasNMoreElms(args, 2))
            requireNMore(2);

        //------------------------------------------
        Value v1 = args.getCar();
        Pair vars = Utils.getPair(v1);
        if (vars == null && !Utils.isNil(v1))
            expected("list of variables", 1);

        Ref<Value> t = new Ref<>(null);
        var jvars = Lst.toJavaList(vars, t);
        if (t.val != null)
            expected("proper list of variables", 1);
        //------------------------------------------

        env = new Environment(env);

        int cnt = jvars.size();
        for (int i=0; i < cnt; i++) {
            Symbol s = Utils.getSymb(jvars.get(i));
            if (s == null)
                expected("symbol", i+ 1);
            env.setValue(s, Constant.Nil);
        }

        Value val = null;

        Pair body = Lst.safeCdr(args);

        for (Pair start = body; start != null;) {
            try {
                for (Pair exp = start; exp != null; exp = Lst.safeCdr(exp)) {
                    Value v = exp.getCar();

                    if (Utils.getSymb(v) == null && Utils.getInt(v) == null)
                        in.eval(exp.getCar(), env);
                }
                start = null;
            }
            catch (LispException.Go e) {
                start = Lst.find(body, e.getLabel());
                if (start == null)
                    error("GO - label %s not found", e.getLabel().toString(true));
            }
            catch (LispException.Return e) {
                start = null;
                val = e.getValue();
            }
        }

        return Utils.checkNull(val);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.FSUBR;
    }
}
