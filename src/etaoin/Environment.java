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

import java.util.Map;
import java.util.TreeMap;
import etaoin.data.*;

public class Environment {

    private final Environment outer;
    private Map<Symbol, Value> bindings;

    public Environment() {
        this.outer = null;
        this.bindings = null;
    }

    public Environment(Environment outer) {
        this.outer = outer;
        this.bindings = null;
    }

    public Environment getOuter() {
        return outer;
    }

    public Map<Symbol, Value> getBindings() {
        return bindings;
    }

    public Value getValue(Symbol symbol) {
        Value val = bindings != null ? bindings.get(symbol) : null;
        return val == null && outer != null ? outer.getValue(symbol) : val;
    }

    public void setValue(Symbol symbol, Value value) {
        if (bindings == null)
            bindings = new TreeMap<>();

        bindings.put(symbol, value);
    }

    public Environment getOwnerOf(Symbol symbol) {
        if (bindings != null && bindings.containsKey(symbol))
            return this;
        return outer != null ? outer.getOwnerOf(symbol) : null;
    }

    public boolean isOwnerOf(Symbol symbol) {
        return bindings != null && bindings.containsKey(symbol);
    }
}
