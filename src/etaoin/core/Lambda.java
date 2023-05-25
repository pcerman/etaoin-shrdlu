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
import java.util.ArrayList;
import java.util.List;

public class Lambda extends Func {

    private final Symbol type;

    public Lambda(Symbol type) {
        super(type.getName());
        this.type = type;
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {
        if (!Lst.hasNMoreElms(args, 2))
            requireNMore(2);

        //------------------------------------------
        Value v1 = Lst.nth(args, 0);

        Symbol s = Utils.getSymb(v1);
        if (s != null) {
            List<Value> body = Lst.toJavaList(Lst.safeCdr(args));
            List<Symbol> jvars = new ArrayList<>(1);
            jvars.add(s);

            for (int i = 0; i < body.size(); i++) {
                body.set(i, in.expand_all(env, body.get(i)));
            }
            return new Expr("LAMBDA", FunctionType.LEXPR, jvars, body);
        } else {
            Pair vars = Utils.getPair(v1);
            if (vars == null && !Utils.isNil(v1))
                expected("list of parameters", 1);

            //------------------------------------------
            Ref<Value> t = new Ref<>(null);
            List<Symbol> jvars = Lst.toJavaList((Value v) -> Utils.getSymb(v), vars, t);
            if (t.val != null)
                expected("proper list of parameters", 1);

            for (int i=0; i < jvars.size(); i++) {
                if (jvars.get(i) == null)
                    expected("symbol for parameter", i+1);
            }
            //------------------------------------------

            List<Value> body = Lst.toJavaList(Lst.safeCdr(args));

            FunctionType ftype = FunctionType.EXPR;

            if (type == in.LAMBDA)
                ftype = FunctionType.EXPR;
            else if (type == in.LAMBDA_FEXPR)
                ftype = FunctionType.FEXPR;
            else if (type == in.LAMBDA_MACRO)
                ftype = FunctionType.MACRO;

            if (ftype != FunctionType.MACRO) {
                for (int i = 0; i < body.size(); i++) {
                    body.set(i, in.expand_all(env, body.get(i)));
                }
            }
            return new Expr("LAMBDA", ftype, jvars, body);
        }
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.FSUBR;
    }
}
