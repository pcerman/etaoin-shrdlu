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
import etaoin.Reader;
import etaoin.data.*;
import java.io.File;
import java.util.ArrayList;

public class Status extends Func {

    public static class LispState {

        public boolean cxr = false;

        public long tty_w1 = -1;
        public long tty_w2 = -1;

        public boolean break_on_error = false;

        public Value pagepause = null;
        public Value toplevel = null;

        public Func read_hook = null;
    }

    public static enum FType { STATUS, SSTATUS }

    private final FType ftype;

    public Status(FType ftype) {
        super(ftype.name());
        this.ftype = ftype;
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {
        if (!Lst.hasNMoreElms(args, 1))
            requireNMore(1);

        if (ftype == FType.SSTATUS) {

            Symbol s = Utils.getSymbArg(args, 0);
            if (in.hasSymbol(s)) {
                switch (s.getName()) {
                    case "CXR" -> {
                        if (!Lst.hasTwoElms(args))
                            requireN(2);

                        in.Status.cxr = !Utils.isNil(in.eval(Lst.nth(args, 1), env));
                    }

                    case "READ-HOOK" -> {
                        if (!Lst.hasTwoElms(args))
                            requireN(2);

                        Value v = Lst.nth(args, 1);
                        if (Utils.isNil(v)) {
                            in.Status.read_hook = null;
                        } else {
                            Func fn = in.getFunction(v, env);
                            if (fn == null) {
                                v = in.eval(v, env);

                                if (!Utils.isNil(v)) {
                                    fn = in.getFunction(v, env);
                                    if (fn == null)
                                        expected("function");
                                }
                            }
                            in.Status.read_hook = fn;
                        }
                    }

                    case "PAGEPAUSE" -> {
                        if (!Lst.hasTwoElms(args))
                            requireN(2);
                        in.Status.pagepause = Lst.nth(args, 1);
                    }

                    case "TTY" -> {
                        if (!Lst.hasNElms(args, 3))
                            requireN(3);

                        Value v1 = in.eval(Lst.nth(args, 1), env);
                        Num.Int w1 = Utils.getInt(v1);
                        if (w1 == null)
                            expected("fixnum", 2);

                        Value v2 = in.eval(Lst.nth(args, 2), env);
                        Num.Int w2 = Utils.getInt(v2);
                        if (w2 == null)
                            expected("fixnum", 3);

                        in.Status.tty_w1 = w1.getValue();
                        in.Status.tty_w2 = w2.getValue();
                    }

                    case "MACRO" -> {
                        if (!Lst.hasNElms(args, 3))
                            requireN(3);
                        return Constant.Nil;
                    }

                    case "TOPLEVEL" -> {
                        if (!Lst.hasTwoElms(args))
                            requireN(2);
                        in.Status.toplevel = in.eval(Lst.nth(args, 1), env);
                    }

                    case "BREAK-ON-ERROR" -> {
                        if (!Lst.hasTwoElms(args))
                            requireN(2);
                        Value v = in.eval(Lst.nth(args, 1), env);
                        in.Status.break_on_error = v != Constant.Nil;
                    }

                    default -> {
                        error("unknown status selector: %s", Value.toString(args.getCar()));
                    }
                }
            }
        }

        // (ftype == FType.STATUS)

        Symbol s = Utils.getSymbArg(args, 0);
        if (in.hasSymbol(s)) {
            switch (s.getName()) {
                case "UREAD" -> {
                    Value v = env.getValue(s);
                    if (v == null)
                        v = s.getValue();
                    return Utils.checkNull(v);
                }

                case "GCTIME" -> {
                    return new Num.Int(0);
                }

                case "LISPVERSION" -> {
                    return in.getSymbol(etaoin.Etaoin.VERSION);
                }

                case "CXR" -> {
                    return in.Status.cxr ? in.T : Constant.Nil;
                }

                case "READ-HOOK" -> {
                    return in.Status.read_hook != null ? in.Status.read_hook : Constant.Nil;
                }

                case "TRACE" -> {
                    return Utils.checkNull(Lst.toList(new ArrayList<>(in.Traced)));
                }

                case "PAGEPAUSE" -> {
                    return Utils.checkNull(in.Status.pagepause);
                }

                case "TTY" -> {
                    return Lst.create(new Num.Int(in.Status.tty_w1),
                                      new Num.Int(in.Status.tty_w2));
                }

                case "MACRO" -> {
                    return Constant.Nil;
                }

                case "TOPLEVEL" -> {
                    return in.Status.toplevel;
                }

                case "BREAK-ON-ERROR" -> {
                    return in.getBool(in.Status.break_on_error);
                }

                case "RC-FILE" -> {
                    return in.RcFile != null ? new Str(in.RcFile) : Constant.Nil;
                }

                case "RC-PATH" -> {
                    if (in.RcFile == null)
                        return Constant.Nil;
                    var path = (new File(in.RcFile)).getParent();
                    return path != null ? new Str(path) : Constant.Nil;
                }

                case "SYS-PROP" -> {
                    if (!Lst.hasTwoElms(args))
                        requireN(2);

                    Str key = Utils.getStr(in.eval(Lst.nth(args, 1), env));
                    if (key == null)
                        expected("string", 1);

                    Value val = null;

                    try {
                        val = getValue(System.getProperty(key.getValue()));
                    } catch (Exception e) { }

                    return Utils.checkNull(val);
                }

                case "SYS-PROPS" -> {
                    Value val = null;

                    var props = System.getProperties();
                    if (props != null) {
                        var ps = new ArrayList<Value>();

                        props.forEach((k, v) -> {
                            Value k_ = getValue(k);
                            if (k_ != null)
                                ps.add(new Pair(k_, Utils.checkNull(getValue(v))));
                        });

                        val = Lst.toList(ps);
                    }

                    return Utils.checkNull(val);
                }

                case "FUNCTION" -> {
                    if (!Lst.hasTwoElms(args))
                        requireN(2);
                    Value v = in.eval(Lst.nth(args, 1), env);
                    Func fn = Utils.getFunc(v);
                    if (fn == null)
                        fn = in.getFunction(v, env);
                    if (fn == null)
                        expected("function", 2);

                    var type = fn.getFunctionType();
                    var stype = in.functionTypeToSymbol(type);

                    if (fn instanceof Expr) {
                        Expr expr = (Expr) fn;
                        Pair pars = Lst.toList(expr.getArgs());
                        Pair body = Lst.toList(expr.getBody());

                        return (type == FunctionType.LEXPR)
                                ? Lst.cons(stype, Lst.nconc(pars, body))
                                : Lst.cons(stype, pars, body);
                    }

                    return Lst.create(stype, new Str(fn.toString(true)));
                }
            }
        }

        error("unknown status selector: %s", Value.toString(args.getCar()));

        return Constant.Nil;
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.FSUBR;
    }

    private static Value getValue(Object obj) {
        if (obj == null)
            return null;

        if (obj instanceof String) {
            String val = (String)obj;

            try {
                if (Reader.isNumberLiteral(val, 10))
                    return new Num.Int(Long.parseLong(val));
                else if (Reader.isFloatingLiteral(val))
                    return new Num.Real(Double.parseDouble(val));
            } catch (Exception e) { }

            return new Str(val);
        }
        if (obj instanceof Integer)
            return new Num.Int((Integer)obj);
        if (obj instanceof Long)
            return new Num.Int((Long)obj);
        if (obj instanceof Float)
            return new Num.Real((Float)obj);
        if (obj instanceof Double)
            return new Num.Real((Double)obj);

        return new Str(obj.toString());
    }
}
