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

import etaoin.Interpreter;
import etaoin.Lexer;
import etaoin.Reader;
import etaoin.Ref;
import java.util.Objects;

public class Symbol extends Value implements Comparable {

    private final String name;
    private Value value;
    private Pair plist;
    private final boolean readonly;

    public Symbol(String name, Interpreter.IId iid) {
        Objects.requireNonNull(iid);
        this.name = name;
        this.plist = null;
        this.readonly = false;
    }

    public Symbol(String name, boolean readonly, Interpreter.IId iid) {
        Objects.requireNonNull(iid);
        this.name = name;
        this.plist = null;
        this.readonly = readonly;
    }

    public String getName() {
        return name;
    }

    public boolean isReadonly() {
        return readonly;
    }

    @Override
    public Type getType() {
        return Type.SYMBOL;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public Pair getPlist() {
        return plist;
    }

    public Value getProperty(Symbol symb) {
        return Lst.cadr(Lst.getProperty(symb, plist));
    }

    public void setProperty(Symbol symb, Value val) {
        plist = new Pair(symb, new Pair(val, Lst.remProperty(symb, plist)));
    }

    public Value remProperty(Symbol symb) {
        Ref<Pair> oldProp = new Ref<>(null);
        plist = Lst.remProperty(symb, plist, oldProp);
        return Lst.cadr(oldProp.val);
    }

    @Override
    protected void pr_str(StringBuilder builder, boolean readably) {
        if (readably) {
            if (Reader.isNumberLiteral(name, 10)
                || Reader.isDecimalLiteral(name)
                || Reader.isFloatingLiteral(name)) {
                builder.append(Lexer.ESCAPE_CHAR);
            }

            int len = name.length();
            for (int i = 0; i < len; i++) {
                Character ch = name.charAt(i);
                if (Lexer.isEscapeChar(ch))
                    builder.append(Lexer.ESCAPE_CHAR);
                builder.append(ch);
            }
        } else
            builder.append(name);
    }

    @Override
    public int compareTo(Object o) {
        if (this == o)
            return 0;

        if (!(o instanceof Symbol))
            return 1;

        Symbol s = (Symbol) o;

        return Integer.compare(hashCode(), s.hashCode());
    }
}
