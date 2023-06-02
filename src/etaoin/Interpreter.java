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

package etaoin;

import etaoin.core.Status;
import etaoin.core.Utils;
import etaoin.data.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class Interpreter {
    public static final class IId { private IId() {} }
    private static final IId iid = new IId();

    private final long startMilliTime;
    private final Symbol[] funcProperties;
    private final HashMap<String, Symbol> symbols;
    private int traceLevel = 0;

    public final String[] Arguments;
    public String RcFile;

    public final List<Func> Traced;
    public final List<Func> Before;
    public final List<Func> After;
    public final etaoin.core.Status.LispState Status;
    public final Environment TopEnv;

    // interned symbols
    public final Symbol PNAME;

    public final Symbol T;
    public final Symbol QUOTE;
    public final Symbol B_QUOTE;
    public final Symbol UNQUOTE;
    public final Symbol SPLICE;

    public final Symbol LAMBDA;

    public final Symbol SYMBOL;
    public final Symbol FIXNUM;
    public final Symbol FLONUM;
    public final Symbol STRING;
    public final Symbol LIST;
    public final Symbol RANDOM;
    public final Symbol BASE;

    public final Symbol EXPR;
    public final Symbol FEXPR;
    public final Symbol LEXPR;
    public final Symbol FSUBR;
    public final Symbol LSUBR;
    public final Symbol SUBR;
    public final Symbol MACRO;

    public final Symbol CHARPOS;
    public final Symbol CHRCT;

    public final Symbol PLUS1;
    public final Symbol PLUS2;
    public final Symbol PLUS3;
    public final Symbol STAR1;
    public final Symbol STAR2;
    public final Symbol STAR3;

    // uninterned symbols
    public final Symbol LAMBDA_FEXPR;
    public final Symbol LAMBDA_MACRO;

    public final Symbol TOPARGS;
    public final Symbol ERRSET;

    public final Symbol BRKSTOP;
    public final Symbol BRKARGS;
    public final Symbol BRKLVL;

    public final Symbol CUREXP;

    public Interpreter(String[] args) {

        Arguments = args;

        startMilliTime = System.currentTimeMillis();

        symbols = new HashMap<>();

        // --------------------------------
        // interned symbols
        //
        PNAME = new Symbol("PNAME", iid);
        PNAME.setProperty(PNAME, PNAME);
        symbols.put(PNAME.getName(), PNAME);

        T = getSymbol("T", true);
        T.setValue(T);

        QUOTE   = getSymbol("QUOTE", true);
        B_QUOTE = getSymbol("BACKQUOTE", true);
        UNQUOTE = getSymbol("UNQUOTE", true);
        SPLICE  = getSymbol("SPLICE-UNQUOTE", true);
        LAMBDA  = getSymbol("LAMBDA");

        SYMBOL = getSymbol("SYMBOL");
        FIXNUM = getSymbol("FIXNUM");
        FLONUM = getSymbol("FLONUM");
        STRING = getSymbol("STRING");
        LIST   = getSymbol("LIST");
        RANDOM = getSymbol("RANDOM");
        BASE   = getSymbol("BASE");

        EXPR  = getSymbol("EXPR");
        FEXPR = getSymbol("FEXPR");
        LEXPR = getSymbol("LEXPR");
        FSUBR = getSymbol("FSUBR");
        LSUBR = getSymbol("LSUBR");
        SUBR  = getSymbol("SUBR");
        MACRO = getSymbol("MACRO");

        CHARPOS = getSymbol("CHARPOS");
        CHRCT   = getSymbol("CHRCT");

        PLUS1 = getSymbol("+");
        PLUS2 = getSymbol("++");
        PLUS3 = getSymbol("+++");
        STAR1 = getSymbol("*");
        STAR2 = getSymbol("**");
        STAR3 = getSymbol("***");

        // --------------------------------
        // uninterned symbols
        //
        LAMBDA_FEXPR = createSymbol("LAMBDA-FEXPR", true);
        LAMBDA_MACRO = createSymbol("LAMBDA-MACRO", true);

        TOPARGS = createSymbol("TOPARGS");
        ERRSET  = createSymbol("ERRSET");

        BRKSTOP = createSymbol("BRKSTOP");
        BRKARGS = createSymbol("BRKARGS");
        BRKLVL  = createSymbol("BRKLVL");

        CUREXP  = createSymbol("CUREXP");

        // --------------------------------

        funcProperties = new Symbol[] {
            EXPR, FEXPR, LEXPR, MACRO, SUBR, FSUBR, LSUBR
        };

        Traced = new ArrayList<>();
        Before = new ArrayList<>();
        After  = new ArrayList<>();
        Status = new Status.LispState();
        TopEnv = new Environment();

        init();
    }

    private void init() {
        CHARPOS.setValue(new Num.Int(0));
        CHRCT.setValue(new Num.Int(Terminal.WIDTH));

        Utils.initCoreFunc(this);
        Utils.initMappFunc(this);
        Utils.initCharsFunc(this);
        Utils.initListFunc(this);
        Utils.initNumFunc(this);
        Utils.initPredFunc(this);
        Utils.initSymbFunc(this);
        Utils.initInOutFunc(this);
        Utils.initSysFunc(this);
    }

    public long getCurrentMilliTime() {
        return System.currentTimeMillis() - startMilliTime;
    }

    public Symbol addFunction(Func func) {
        return addFunction(func, null);
    }

    public Symbol addFunction(Func func, Symbol symb) {
        if (symb == null)
            symb = getSymbol(func.getName());

        symb.setProperty(functionTypeToSymbol(func.getFunctionType()), func);

        return symb;
    }

    public boolean hasSymbol(Symbol symb) {
        if (symb == null)
            return false;

        return symbols.get(symb.getName()) == symb;
    }

    public Symbol addSymbol(Symbol symb) {
        return symbols.computeIfAbsent(symb.getName(), n -> symb);
    }

    public Symbol getSymbol(String name) {
        return symbols.computeIfAbsent(name, n -> createSymbol(name));
    }

    public Symbol getSymbol(String name, boolean readonly) {
        return symbols.computeIfAbsent(name, n -> createSymbol(name, readonly));
    }

    public Symbol getSymbol(String name, Value val) {
        Symbol symb = getSymbol(name);
        symb.setValue(val);

        return symb;
    }

    public boolean remSymbol(Symbol symb) {
        if (symb.isReadonly())
            return false;

        boolean found = false;

        for (Symbol v : symbols.values()) {
            if (v == symb) {
                found = true;
                break;
            }
        }

        if (found)
            symbols.remove(symb.getName());

        return found;
    }

    public List<Value> getObArray() {
        List<Value> obarray = new ArrayList<>(symbols.values());
        return obarray;
    }

    public Symbol createSymbol(String name) {
        Symbol symb = new Symbol(name, iid);
        symb.setProperty(PNAME, symb);
        return symb;
    }

    public Symbol createSymbol(String name, boolean readonly) {
        Symbol symb = new Symbol(name, readonly, iid);
        symb.setProperty(PNAME, symb);
        return symb;
    }

    private int getBase(Environment env, Symbol sym) {
        Value bval = env.getValue(sym);
        if (bval == null)
            bval = sym.getValue();

        if (bval != null) {
            Num.Int num = Utils.getInt(bval);
            if (num != null) {
                long base = num.getValue();

                if (base >= Character.MIN_RADIX && base <= Character.MAX_RADIX)
                    return (int) base;
            }
        }
        return 0;
    }

    public int getOutBase(Environment env) {
        int base = getBase(env, BASE);
        return base > 0 ? base : Value.NUM_BASE;
    }

    public Value read(Reader rdr, Environment env) throws Exception {
        Value val = rdr.readForm(this);
        if (val == null)
            return val;

        if (Status.read_hook != null) {
            val = Status.read_hook.applyFn(this, env, Lst.create(val));
        }

        return val;
    }

    public Value eval(Value val, Environment env) throws LispException {
        if (val instanceof Pair) {
            while (val instanceof Pair) {
                Pair p = (Pair) val;

                if (p.getCar() == QUOTE) {
                    if (!Lst.hasTwoElms(p))
                        error("QUOTE - expected one arg");
                    return Lst.nth(p, 1);
                }

                if (p.getCar() == B_QUOTE) {
                    if (!Lst.hasTwoElms(p))
                        error("BACKQUOTE - expected one arg");
                    return backquote(Lst.nth(p, 1), env);
                }

                Func fn = getFunction(p.getCar(), env);
                if (fn == null) {
                    if (Status.break_on_error)
                        callBreak(env, String.format("Invalid function '%s'", Value.toString(p.getCar())));
                    error("Invalid function '%s'", Value.toString(p.getCar()));
                }

                if (!Lst.isProperList(p))
                    error("function '%s': requires proper list of args", fn.getName());

                switch (fn.getFunctionType()) {
                    case MACRO:
                        val = fn.applyFn(this, env, p);
                        break;
                    case EXPR, LEXPR, SUBR, LSUBR:
                        return fn.applyFn(this, env, (Pair)eval_ast(p.getCdr(), env));
                    case FEXPR, FSUBR:
                        return fn.applyFn(this, env, (Pair) p.getCdr());
                }
            }
        }

        if (val instanceof Symbol) {
            return evalSymbol((Symbol)val, env);
        }

        return val;
    }

    public void setTraceLevel(int traceLevel) {
        this.traceLevel = traceLevel;
    }

    public void printTracedInp(Func fn, Pair args) {
        if (Etaoin.terminal.getOutPosition() > 0)
            Etaoin.terminal.println();
        Etaoin.terminal.printf("*** %2d: ", traceLevel + 1);
        for (int i=0; i < traceLevel; i++)
            Etaoin.terminal.printf("  ", traceLevel);
        Etaoin.terminal.printf("%s >>", fn.getName());

        try {
            Lst.fold((va, v) -> { Etaoin.terminal.printf(" %s", Value.toString(v)); return va; }, Constant.Nil, args);
        }
        catch (LispException ex) {}
        Etaoin.terminal.println();

        traceLevel++;
    }

    public void printTracedOut(Func fn, Value arg) {
        traceLevel--;

        if (Etaoin.terminal.getOutPosition() > 0)
            Etaoin.terminal.println();
        Etaoin.terminal.printf("*** %2d: ", traceLevel + 1);
        for (int i=0; i < traceLevel; i++)
            Etaoin.terminal.printf("  ", traceLevel);
        Etaoin.terminal.printf("%s == %s", fn.getName(), Value.toString(arg));
        Etaoin.terminal.println();
    }

    public Value expand_once(Environment env, Value val) throws LispException {
        if (val instanceof Pair) {
            Pair p = (Pair) val;
            var fn = getMacroFunction(p.getCar(), env);
            if (fn != null && fn.getFunctionType() == Func.FunctionType.MACRO) {
                val = fn.applyFn(this, env, p);
            }
        }

        return val;
    }

    public Value expand_all(Environment env, Value val) throws LispException {
        if (!(val instanceof Pair)) {
            return val;
        }

        Value car = ((Pair) val).getCar();
        if (car == QUOTE || car == B_QUOTE ||
            car == UNQUOTE || car == SPLICE) {
            return val;
        }

        for (;;)
        {
            Value v = expand_once(env, val);
            if (v == val) {
                if (val instanceof Pair) {
                    Pair p = (Pair) val;

                    Pair list = Lst.create(p.getCar());
                    Pair last = list;

                    val = p.getCdr();

                    while (val instanceof Pair) {
                        p = (Pair) val;
                        val = p.getCdr();

                        v = expand_all(env, p.getCar());

                        p = Lst.create(v);
                        last.setCdr(p);
                        last = p;
                    }

                    if (val != null) {
                        last.setCdr(expand_all(env, val));
                    }

                    val = list;
                }
                break;
            }

            val = v;
        }

        return val;
    }

    private Value backquote(Value val, Environment env) throws LispException {
        if (val instanceof Pair) {
            Pair p = (Pair) val;

            if (p.getCar() == UNQUOTE) {
                if (!Lst.hasTwoElms(p))
                    error("UNQUOTE - expected one arg");
                return eval(Lst.nth(p, 1), env);
            }
            if (p.getCar() == SPLICE) {
                error("SPLICE-UNQUOTE - invalid context within backquote");
            } else {
                Pair list = null;
                Pair last = null;

                while (val instanceof Pair) {
                    p = (Pair) val;
                    val = p.getCdr();

                    Value v = p.getCar();

                    if (v instanceof Pair) {
                        Pair fst = (Pair) v;

                        if (fst.getCar() == UNQUOTE) {
                            if (!Lst.hasTwoElms(fst)) {
                                error("UNQUOTE - expected one arg");
                            }
                            v = eval(Lst.nth(fst, 1), env);
                        } else if (fst.getCar() == SPLICE) {
                            if (!Lst.hasTwoElms(fst)) {
                                error("SPLICE-UNQUOTE - expected one arg");
                            }
                            v = eval(Lst.nth(fst, 1), env);
                            if (v instanceof Pair) {
                                var lcdr = new Ref<Value>(null);
                                v = Lst.copy((Pair) v, lcdr);

                                if (list == null) {
                                    list = (Pair) v;
                                    last = Lst.lastPair(list);
                                } else {
                                    last.setCdr(v);
                                    last = Lst.lastPair(last);
                                }
                                last.setCdr(null);
                                v = lcdr.val;
                            }

                        } else {
                            v = backquote(fst, env);
                        }
                    } else if (last != null && (v == UNQUOTE || v == SPLICE)) {
                        last.setCdr(backquote(p, env));
                        val = null;
                        break;
                    }

                    if (v != null) {
                        if (list == null) {
                            list = Lst.create(v);
                            last = list;
                        } else {
                            p = last;
                            last = Lst.create(v);
                            p.setCdr(last);
                        }
                    }
                }

                if (val != null)
                    last.setCdr(backquote(val, env));

                return list;
            }
        }
        return val;
    }

    private Value eval_ast(Value val, Environment env) throws LispException {
        if (val instanceof Pair) {
            Pair list = null;
            Pair last = null;

            while (val instanceof Pair) {
                Pair p = (Pair) val;
                val = p.getCdr();

                Value v = eval(p.getCar(), env);
                if (list == null) {
                    list = Lst.create(v);
                    last = list;
                } else {
                    p = last;
                    last = Lst.create(v);
                    p.setCdr(last);
                }
            }

            if (val != null)
                last.setCdr(eval(val, env));

            return list;
        }

        if (val instanceof Symbol) {
            return evalSymbol((Symbol)val, env);
        }

        return val;
    }

    private Value evalSymbol(Symbol sym, Environment env) throws LispException {
        Value val = null;

        if (env != null)
            val = env.getValue(sym);
        if (val == null)
            val = sym.getValue();

        if (val == null) {
            if (Status.break_on_error)
                callBreak(env, String.format("Symbol '%s' not found!", sym.getName()));
            error("Symbol '%s' not found!", sym.getName());
        }

        return val;
    }

    public Func getMacroFunction(Value val, Environment env) throws LispException {
        return getFunction(val, env, true);
    }

    public Func getFunction(Value val, Environment env) throws LispException {
        return getFunction(val, env, false);
    }

    private Func getFunction(Value val, Environment env, boolean dont_eval) throws LispException {
        List<Value> vals = null;

        boolean is_evaluated = false;

        while (val != null) {

            if (val instanceof Symbol) {

                Symbol symb = (Symbol)val;
                Value func = getFunctionProperty(symb);

                if (func instanceof Func)
                    return (Func) func;

                if (!dont_eval && func instanceof Pair) {
                    Pair lmd = (Pair) func;
                    return (Func) eval(lmd, env);
                }

                if (vals == null)
                    vals = new ArrayList<>();
                else if (vals.contains(val))
                    break;
                vals.add(val);

                if (func instanceof Symbol)
                    val = func;
                else {
                    val = env.getValue(symb);
                    if (val == null)
                        val = symb.getValue();
                }

            } else if (!dont_eval && val instanceof Pair) {

                Pair lmd = (Pair) val;
                if (lmd.getCar() == LAMBDA)
                    return (Func) eval(lmd, env);

                if (is_evaluated)
                    break;

                val = eval(lmd, env);
                is_evaluated = true;

            } else
                break;
        }

        if (val instanceof Func)
            return (Func) val;

        return null;
    }

    public boolean isDefunType(Symbol symb) {
        return symb == EXPR || symb == FEXPR || symb == MACRO;
    }

    public Value getBool(boolean b) {
        return b ? T : Constant.Nil;
    }

    public Func.FunctionType symbolToFunctionType(Symbol symb) {
        if (symb == EXPR)
            return Func.FunctionType.EXPR;
        if (symb == FEXPR)
            return Func.FunctionType.FEXPR;
        if (symb == LEXPR)
            return Func.FunctionType.LEXPR;
        if (symb == FSUBR)
            return Func.FunctionType.FSUBR;
        if (symb == LSUBR)
            return Func.FunctionType.LSUBR;
        if (symb == SUBR)
            return Func.FunctionType.SUBR;
        if (symb == MACRO)
            return Func.FunctionType.MACRO;

        return null;
    }

    public Symbol functionTypeToSymbol(Func.FunctionType ftype) {
        return switch (ftype) {
            case EXPR  -> EXPR;
            case FEXPR -> FEXPR;
            case LEXPR -> LEXPR;
            case FSUBR -> FSUBR;
            case LSUBR -> LSUBR;
            case SUBR  -> SUBR;
            case MACRO -> MACRO;
        };
    }

    private Value getFunctionProperty(Symbol symb) {
        for (var prop : funcProperties) {
            var func = symb.getProperty(prop);
            if (func == null)
                continue;

            if (func instanceof Func)
                return func;

            if (func instanceof Symbol)
                return func;

            if (func instanceof Pair) {
                Pair lmd = (Pair) func;
                if (lmd.getCar() == LAMBDA) {
                    if (prop == EXPR)
                        return lmd;
                    if (prop == FEXPR)
                        return new Pair(LAMBDA_FEXPR, lmd.getCdr());
                    if (prop == MACRO)
                        return new Pair(LAMBDA_MACRO, lmd.getCdr());

                    return null;
                }
            }
        }
        return null;
    }

    public void setCharPosition(Environment env) {
        int charPos = Etaoin.terminal.getOutPosition();

        CHARPOS.setValue(new Num.Int(charPos));
        CHRCT.setValue(new Num.Int(Math.max(Terminal.WIDTH - charPos, 0)));
    }

    public void callBreak(Environment env, String msg) throws LispException {
        Symbol sym = getSymbol("BREAK");
        if (sym != null) {
            for (Symbol prop : funcProperties) {
                Func fn = Utils.getFunc(sym.getProperty(prop));

                if (fn != null) {
                    fn.applyFn(this, env, Lst.create(new Str(msg)));
                    break;
                }
            }
        }
    }

    public static void error(String message) throws LispException {
        throw new LispException(message);
    }

    public static void error(String format, Object... args) throws LispException {
        throw new LispException(String.format(format, args));
    }

    public static void error(String message, int[] position) throws LispException {
        throw new LispException(message, position);
    }
}
