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

import etaoin.core.Utils;
import etaoin.data.*;

public class LispException extends Exception {

    public static class Go extends LispException {

        private final Value label;

        public Go(Value label) {
            super("GO");
            this.label = label;
        }

        public Value getLabel() {
            return label;
        }

        @Override
        public String getMessage() {
            if (label == null)
                return "GO - null";

            return String.format("GO - %s", label.toString(true));
        }
    }

    public static class Return extends LispException {

        private final Value value;

        public Return(Value value) {
            super("RETURN");

            this.value = value;
        }

        public Value getValue() {
            return value;
        }

        @Override
        public String getMessage() {
            String msg = value == null ? "null" : value.toString(true);

            return String.format("RETURN - %s", msg);
        }
    }

    public static class Throw extends LispException {

        private final Value value;
        private final Symbol tag;

        public Throw(Value value, Symbol tag) {
            super("THROW");

            this.value = value;
            this.tag = tag;
        }

        public Value getValue() {
            return value;
        }

        public Symbol getTag() {
            return tag;
        }

        @Override
        public String getMessage() {
            Pair pair = Utils.getPair(value);
            Value val = pair == null ? value : pair.getCdr();

            String msg = val == null ? "null" : val.toString(true);

            if (tag == null)
                return String.format("THROW - %s", msg);
            else
                return String.format("THROW <tag: %s> - %s", tag.getName(), msg);
        }
    }

    public static class Quit extends LispException {

        private final Num.Int value;

        public Quit(Num.Int value) {
            super("QUIT");

            this.value = value;
        }

        public Num.Int getValue() {
            return value;
        }

        @Override
        public String getMessage() {
            if (value == null || value.getValue() == 0)
                return "QUIT";

            return String.format("QUIT - %s", value.toString(true));
        }
    }

    protected String message;
    protected int[] position;

    public LispException(String message) {
        this.message = message;
        this.position = null;
    }

    public LispException(String message, int[] position) {
        this.message = message;
        this.position = position;
    }

    public int[] getPosition() {
        return position;
    }

    @Override
    public String getMessage() {
        if (position != null && position.length >= 2) {
            return String.format("%s at [%d, %d]", message, position[0], position[1]);
        }

        return message;
    }
}
