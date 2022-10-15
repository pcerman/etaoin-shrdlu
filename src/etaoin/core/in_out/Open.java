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

package etaoin.core.in_out;

import etaoin.Environment;
import etaoin.Interpreter;
import etaoin.LispException;
import etaoin.Ref;
import etaoin.core.Utils;
import etaoin.data.*;
import java.io.File;

public class Open extends Func {

    public static enum FType { OPENI, OPENO, OPENA };

    private final FType ftype;

    public Open(FType ftype) {
        super(ftype.name());
        this.ftype = ftype;
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {
        if (!Lst.hasSingleElm(args))
            requireN(1);

        Ref<Value> t = new Ref<>(null);

        String fn = getFilename(args.getCar(), t);
        if (fn == null)
            expected("filename");
        if (t.val != null)
            expected("proper list");

        Port p = switch (ftype) {
            case OPENI -> {
                File file = new File(fn);
                if (!file.canRead())
                    error("unable to read file: %s", fn);

                Port port = Port.openInp(file);
                if (port == null)
                    error("unable to open file: %s", fn);

                yield port;
            }

            case OPENO -> {
                File file = new File(fn);
                if (!file.canWrite() && file.exists())
                    error("unable write to file: %s", fn);

                Port port = Port.openOut(file, false);
                if (port == null)
                    error("unable to open file: %s", fn);

                yield port;
            }

            case OPENA ->  {
                File file = new File(fn);
                if (!file.canWrite())
                    error("unable write to file: %s", fn);

                Port port = Port.openOut(file, true);
                if (port == null)
                    error("unable to open file: %s", fn);

                yield port;
            }
        };

        return p;
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }

    public static String getFilename(Value v) {
        return getFilename(v, null);
    }

    public static String getFilename(Value v, Ref<Value> t) {
        Symbol symb = Utils.getSymb(v);
        if (symb != null)
            return symb.getName();

        Str str = Utils.getStr(v);
        if (str != null)
            return str.getValue();

        Pair p = Utils.getPair(v);
        if (p != null) {
            try {
                StringBuilder sb = Lst.fold((StringBuilder init, Value val) -> {
                    if (Utils.isNil(val))
                        return init;

                    if (!init.isEmpty())
                        init.append('/');

                    Symbol sym = Utils.getSymb(val);
                    if (sym != null) {
                        init.append(sym.getName());
                        return init;
                    }
                    Str str1 = Utils.getStr(val);
                    if (str1 != null) {
                        init.append(str1.getValue());
                        return init;
                    }
                    throw new LispException(null);
                }, new StringBuilder(), p, t);

                String fn = sb.toString();
                while (fn.contains("//"))
                    fn = fn.replace("//", "/");

                return fn;
            }
            catch (LispException ex) { }
        }

        return null;
    }
}
