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

public class Do extends Func {

    public Do() {
        super("DO");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {
        if (!Lst.hasNMoreElms(args, 2))
            requireNMore(2);

        Value v = args.getCar();
        if (Utils.getSymb(v) != null)
            return old_do(in, env, args);

        if (Utils.getPair(v) != null || v == Constant.Nil)
            return new_do(in, env, args);

        error("wrong syntax");

        return null;
    }

    private Value old_do(Interpreter in, Environment env, Pair args) throws LispException {
        if (!Lst.hasNMoreElms(args, 4))
            requireNMore(4);

        Symbol symb = Utils.getSymbArg(args, 0);
        Value init = Lst.nth(args, 1);
        Value next = Lst.nth(args, 2);
        Value test = Lst.nth(args, 3);

        Pair body = Lst.nthPair(args, 4);

        Pair do_vars = Lst.create(Lst.create(symb, init, next));
        Pair do_test = Lst.create(test);
        Pair do_exp = Lst.create(do_vars, do_test);

        Lst.lastPair(do_exp).setCdr(body);

        return new_do(in, env, do_exp);
    }

    private Value new_do(Interpreter in, Environment env, Pair args) throws LispException {

        //------------------------------------------
        Value v1 = args.getCar();
        Pair vars = Utils.getPair(v1);

        Ref<Value> t = new Ref<>(null);
        var jvars = Lst.toJavaList(vars, t);
        if (t.val != null)
            expected("proper list of variables", 1);
        //------------------------------------------
        Value v2 = Lst.nth(args, 1);
        Pair test = Utils.getPair(v2);
        if (test == null || !Lst.isProperList(test))
            expected("proper list for test", 2);
        //------------------------------------------

        var jvals = new ArrayList<Value>();
        env = new Environment(env);

        //------------------------------------------
        // initialize variables
        int cnt = jvars.size();
        for (int i=0; i < cnt; i++) {
            Value v = in.eval(getInit(jvars.get(i)), env);
            jvals.add(v);
        }
        setVars(env, jvars, jvals);

        Pair body = Lst.safeCdr(args.getCdr());

        for (;;) {
            //------------------------------------------
            // evaluate test
            Value tr = in.eval(test.getCar(), env);
            if (tr != null && tr != Constant.Nil) {
                //------------------------------------------
                // evaluate result
                tr = null;
                for (Pair exp = Lst.safeCdr(test);
                     exp != null;
                     exp = Lst.safeCdr(exp)) {
                    tr = in.eval(exp.getCar(), env);
                }
                return Utils.checkNull(tr);
            }

            //------------------------------------------
            // eval body expressions
            for (Pair start = body; start != null; ) {
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
                    return Utils.checkNull(e.getValue());
                }
            }

            //------------------------------------------
            // update variables
            for (int i = 0; i < cnt; i++) {
                Value v = getNext(jvars.get(i));
                if (v != null)
                    jvals.set(i, in.eval(v, env));
                else
                    jvals.set(i, null);
            }
            setVars(env, jvars, jvals);
        }
    }

    private Symbol getSymbol(Value v) throws LispException {
        Pair sd = Utils.getPair(v);
        if (sd != null) {
            Symbol sym = Utils.getSymbArg(sd, 0);
            if (sym != null)
                return sym;
        }

        error("wrong syntax for symbol definition");
        return null;
    }

    private Value getInit(Value v) throws LispException {
        Pair sd = Utils.getPair(v);
        if (sd != null) {
            return Utils.checkNull(Lst.nth(sd, 1));
        }

        error("wrong syntax for symbol definition");
        return null;
    }

    private Value getNext(Value v) throws LispException {
        Pair sd = Utils.getPair(v);
        if (sd != null) {
            return Lst.nth(sd, 2);
        }

        error("wrong syntax for symbol definition");
        return null;
    }

    private void setVars(Environment env, List<Value> vars, List<Value> vals) throws LispException {
        int cnt = Math.min(vars.size(), vals.size());
        for (int i=0; i < cnt; i++) {
            Value v = vals.get(i);
            if (v != null) {
                Symbol s = getSymbol(vars.get(i));
                env.setValue(s, v);
            }
        }
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.FSUBR;
    }
}
