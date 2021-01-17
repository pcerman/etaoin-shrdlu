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

public abstract class Func extends Value {

    public static enum FunctionType {
        EXPR, FEXPR, LEXPR, FSUBR, LSUBR, SUBR, MACRO
    }

    protected final String name;
    private boolean traced = false;
    private Func before = null;
    private Func after = null;

    private Func() {
        name = "";
    }

    protected Func(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setTraced(boolean traced) {
        this.traced = traced;
    }

    public boolean isTraced() {
        return traced;
    }

    public Func getBefore() {
        return before;
    }

    public void setBefore(Func before) {
        this.before = before;
    }

    public Func getAfter() {
        return after;
    }

    public void setAfter(Func after) {
        this.after = after;
    }

    protected void requireN(int n) throws LispException {
        if (n <= 0)
            Interpreter.error("function %s: expects no args", name);
        else {
            String es = n <= 1 ? "" : "s";
            Interpreter.error("function %s: expects %d arg%s", name, n, es);
        }
    }

    protected void requireN(int n1, int n2) throws LispException {
        String es = n2 <= 1 ? "" : "s";
        if (n1 <= 0) {
            Interpreter.error("function %s: expects a maximum of %d arg%s", name, n2, es);
        } else if (n1 + 1 == n2) {
            Interpreter.error("function %s: expects either %d or %d arg%s", name, n1, n2, es);
        } else {
            Interpreter.error("function %s: expects from %d to %d arg%s", name, n1, n2, es);
        }
    }

    protected void requireNMore(int n) throws LispException {
        String es = n <= 1 ? "" : "s";
        Interpreter.error("function %s: expects at least %d arg%s", name, n, es);
    }

    protected void error(String msg) throws LispException {
        Interpreter.error("function %s: %s", name, msg);
    }

    protected void error(String format, Object... args) throws LispException {
        error(String.format(format, args));
    }

    protected void sameNumbers() throws LispException {
        Interpreter.error("function %s: %s", name, "numbers of the same type are expected");
    }

    protected void expected(String type) throws LispException {
        Interpreter.error("function %s: %s is expected", name, type);
    }

    protected void expected(String type, int argIdx) throws LispException {
        String nth = argIdx > 4 && argIdx <= 20 ? "th"
                     : switch (argIdx % 10) {
                        case 1  -> "fst";
                        case 2  -> "nd";
                        case 3  -> "rd";
                        default -> "th";
                    };

        Interpreter.error("function %s %d%s arg: %s is expected", name, argIdx, nth, type);
    }

    public abstract FunctionType getFunctionType();

    protected abstract Value apply(Interpreter in, Environment env, Pair args) throws LispException;

    public Value applyFn(Interpreter in, Environment env, Pair args) throws LispException {
        if (before != null) {
            Value val = before.apply(in, env, Lst.create(args));
            if (val == Constant.Nil)
                args = null;
            else
                args = Utils.getPair(val);
        }

        if (traced)
            in.printTracedInp(this, args);

        Value val = apply(in, env, args);

        if (traced)
            in.printTracedOut(this, val);

        if (after != null) {
            val = after.apply(in, env, Lst.create(val));
        }

        return val;
    }

    @Override
    public Type getType() {
        return Type.FUNC;
    }

    @Override
    protected void pr_str(StringBuilder builder, boolean readably) {
        String msg = String.format("<%s %s 0x%X>", getFunctionType().name(), getName(), this.hashCode());
        builder.append(msg);
    }
}
