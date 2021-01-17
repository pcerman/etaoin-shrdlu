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
import etaoin.Reader;
import etaoin.Ref;
import etaoin.core.Utils;
import etaoin.data.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class Load extends Func {

    public Load() {
        super("LOAD");
    }

    @Override
    public Value apply(Interpreter in, Environment env, Pair args) throws LispException {
        if (!Lst.hasSingleElm(args))
            requireN(1);
        Ref<Value> t = new Ref<>(null);
        String fn = Open.getFilename(args.getCar(), t);
        if (fn == null)
            expected("filename");
        if (t.val != null)
            expected("proper list");
        File file = new File(fn);
        if (file.isDirectory() || !file.canRead())
            error("unable to read file: %s", fn);

        Value eval = null;
        int[] position = null;

        FileInputStream stream = null;
        try {
            stream = new FileInputStream(file);
            Reader rdr = new Reader(new InputStreamReader(stream));

            Value val = in.read(rdr, env);
            position = rdr.getPosition();

            while (val != null) {
                eval = in.eval(val, env);
                val = in.read(rdr, env);
                position = rdr.getPosition();
            }
        }
        catch (FileNotFoundException ex) {
            error("unable load file: '%s'", fn);
        }
        catch (Exception ex) {
            if (position != null)
                error("loading error '%s' at [%d, %d]: %s", fn, position[0], position[1], ex.getMessage());
            else
                error("loading error '%s': %s", fn, ex.getMessage());
            if (ex instanceof LispException)
                throw (LispException)ex;
            else
                error(ex.getMessage());
        }
        finally {
            try {
                stream.close();
            }
            catch (IOException ex) { }
        }

        return Utils.checkNull(eval);
    }

    @Override
    public Func.FunctionType getFunctionType() {
        return Func.FunctionType.SUBR;
    }
}
