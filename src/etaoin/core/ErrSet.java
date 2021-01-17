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
import static etaoin.Etaoin.terminal;
import etaoin.Interpreter;
import etaoin.LispException;
import etaoin.data.*;

public class ErrSet extends Func {

    public ErrSet() {
        super("ERRSET");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {
        if (!Lst.hasSingleElm(args) && !Lst.hasTwoElms(args))
            requireN(1, 2);

        Value exp = Lst.nth(args, 0);
        Value tag = Lst.nth(args, 1);

        Value val = null;

        try {
            val = in.eval(exp, env);
            val = Lst.create(val);
        }
        catch (LispException.Throw ex) {
            if (ex.getTag() == in.ERRSET) {
                val = ex.getValue();
                Pair p = Utils.getPair(val);
                if (p != null) {
                    if (p.getCar() == Num.Int.one)
                        val = in.eval(p.getCdr(), env);
                    else
                        val = p.getCdr();
                }
            } else
                throw ex;
        }
        catch (LispException.Go
                | LispException.Return
                | LispException.Quit ex) {
            throw ex;
        }
        catch (LispException ex) {
            val = null;

            if (tag == null) {
                terminal.println("\nERROR: " + ex.getMessage());
                in.setCharPosition(env);
            } else if (tag != Constant.Nil) {
                in.eval(tag, env);
            }
        }
        catch (Exception ex) {
            val = null;

            if (tag == null) {
                terminal.println("\nERROR: " + ex.getMessage());
                in.setCharPosition(env);
            } else if (tag != Constant.Nil) {
                in.eval(tag, env);
            }
        }

        return Utils.checkNull(val);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.FSUBR;
    }
}
