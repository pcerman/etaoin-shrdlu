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

public class Pair extends Value {

    private Value car;
    private Value cdr;

    public Pair(Value car, Value cdr) {
        this.car = car;
        this.cdr = cdr;
    }

    public Value getCar() {
        return car;
    }

    public Value getCdr() {
        return cdr;
    }

    public void setCar(Value car) {
        this.car = car;
    }

    public void setCdr(Value cdr) {
        this.cdr = cdr;
    }

    @Override
    public Type getType() {
        return Type.PAIR;
    }

    @Override
    protected void pr_str(StringBuilder builder, boolean readably) {
        builder.append('(');

        boolean first = false;
        for (Value v = this; v != null;) {
            if (v instanceof Pair) {
                if (first)
                    builder.append(' ');
                else
                    first = true;
                Pair p = (Pair) v;
                Value.pr_str(p.getCar(), builder, readably);
                v = p.getCdr();
            } else {
                builder.append(" . ");
                Value.pr_str(v, builder, readably);
                break;
            }
        }
        builder.append(')');
    }
}
