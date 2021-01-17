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

package etaoin.data;

import etaoin.Environment;
import etaoin.Interpreter;
import etaoin.LispException;
import etaoin.core.Utils;
import java.util.List;

public class Expr extends Func {

    private final FunctionType ftype;
    private final List<Symbol> args;
    private final List<Value> body;

    public Expr(String name, FunctionType ftype, List<Symbol> args, List<Value> body) {
        super(name);

        this.ftype = ftype;
        this.args = args;
        this.body = body;
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {
        int cnt = Lst.length(args);

        switch (ftype) {
            case EXPR -> {
                if (this.args.size() != cnt)
                    requireN(this.args.size());
            }

            case MACRO, FEXPR -> {
                if (this.args.size() != 1)
                    requireN(1);
            }
        }

        env = new Environment(env);
        env.setValue(in.CUREXP, Lst.cons(this, args));

        switch (ftype) {
            case EXPR -> {
                env.setValue(in.TOPARGS, Constant.Nil);
                for (int i = 0; i < cnt; i++) {
                    env.setValue(this.args.get(i), args.getCar());
                    args = Lst.safeCdr(args);
                }
            }

            case MACRO, FEXPR -> {
                env.setValue(in.TOPARGS, Constant.Nil);
                env.setValue(this.args.get(0), Utils.checkNull(args));
            }

            case LEXPR -> {
                env.setValue(in.TOPARGS, Utils.checkNull(args));
                env.setValue(this.args.get(0), new Num.Int(cnt));
            }
        }

        Value val = null;
        try {
            for (int i=0; i < body.size(); i++) {
                val = in.eval(body.get(i), env);
            }
        }
        catch (LispException.Return ex) {
            val = ex.getValue();
        }

        return Utils.checkNull(val);
    }

    @Override
    public FunctionType getFunctionType() {
        return ftype;
    }
}
