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
import java.util.List;

public class DeFun extends Func {

    public DeFun() {
        super("DEFUN");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {
        //if (env.getOuter() != null)
        //    error("it can be used only in top environment");

        if (!Lst.hasNMoreElms(args, 3))
            requireNMore(3);

        Symbol fname = Utils.getSymbArg(args, 0);
        if (fname == null)
            expected("symbol", 1);

        Value v2 = Lst.nth(args, 1);
        Symbol ftype = Utils.getSymb(v2);

        Pair pars = null;
        Pair body = null;

        if (ftype != null) {
            if (in.isDefunType(fname)) {
                Symbol type = fname;
                fname = ftype;
                ftype = type;
            }

            if (in.isDefunType(ftype)) {
                pars = getPars(Lst.nth(args, 2));
                body = Lst.nthPair(args, 3);
            } else if (in.symbolToFunctionType(fname) != null) {
                error("unexpected function name: '%s'", fname.getName());
            } else if (in.symbolToFunctionType(fname) != null) {
                error("unexpected parameter name: '%s'", ftype.getName());
            } else {
                pars = Lst.create(ftype);
                body = Lst.nthPair(args, 2);
                ftype = in.LEXPR;
            }
        } else {
            pars = getPars(v2);
            body = Lst.nthPair(args, 2);
            ftype = in.EXPR;
        }

        if (ftype == in.FEXPR) {
            if (!Lst.hasSingleElm(pars))
                error("FEXPR functions requires single argument");
        }

        FunctionType type = in.symbolToFunctionType(ftype);
        Func func = getFunction(fname, type, pars, body);
        fname.setProperty(ftype, func);

        return fname;
    }

    private Pair getPars(Value v) throws LispException {
        if (Utils.isNil(v)) {
            return null;
        }

        Pair p = Utils.getPair(v);
        if (p == null)
            expected("list of symbols", 2);

        if (!Lst.isProperList(p))
            expected("proper list of symbols", 2);

        return p;
    }

    private Func getFunction(Symbol fname, FunctionType ftype, Pair fpars, Pair fbody) throws LispException {

        //------------------------------------------
        Ref<Value> t = new Ref<>(null);
        List<Symbol> jvars = Lst.toJavaList((Value v) -> Utils.getSymb(v), fpars, t);
        if (t.val != null)
            expected("proper list of parameters", 1);

        for (int i=0; i < jvars.size(); i++) {
            if (jvars.get(i) == null)
                expected("symbol for parameter", i+1);
        }
        //------------------------------------------

        List<Value> body = Lst.toJavaList(fbody);

        return new Expr(fname.getName(), ftype, jvars, body);
    }

    @Override
    public FunctionType getFunctionType() {
        return FunctionType.FEXPR;
    }
}
