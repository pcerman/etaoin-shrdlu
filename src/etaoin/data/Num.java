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

public abstract class Num extends Value {

    public abstract long getInt();
    public abstract double getReal();

    public abstract Num create();

    public abstract boolean isZero();
    public abstract boolean isPositive();
    public abstract boolean isNegative();

    public abstract void neg();
    public abstract void add(Num num);
    public abstract void sub(Num num);
    public abstract void mul(Num num);
    public abstract void div(Num num);

    public static class Int extends Num {

        private long value;

        public Int(long value) {
            this.value = value;
        }

        public Int(String token) {
            this(token, 10);
        }

        public Int(String token, int base) {
            int last_idx = token.length() - 1;

            if (last_idx >= 0 && token.charAt(last_idx) == '.')
                value = Long.parseLong(token.substring(0, last_idx));
            else if (base == 10)
                value = Long.parseLong(token);
            else
                value = Long.parseLong(token, base);
        }

        public long getValue() {
            return value;
        }

        public void setValue(long value) {
            this.value = value;
        }

        public void Add(long val) {
            value += val;
        }

        @Override
        public long getInt() {
            return value;
        }

        @Override
        public double getReal() {
            return value;
        }

        @Override
        public Int create() {
            return new Int(value);
        }

        @Override
        public boolean isZero() {
            return value == 0;
        }

        @Override
        public boolean isPositive() {
            return value > 0;
        }

        @Override
        public boolean isNegative() {
            return value < 0;
        }

        @Override
        public void neg() {
            value = -value;
        }

        @Override
        public void add(Num num) {
            value += num.getInt();
        }

        @Override
        public void sub(Num num) {
            value -= num.getInt();
        }

        @Override
        public void mul(Num num) {
            value *= num.getInt();
        }

        @Override
        public void div(Num num) {
            value /= num.getInt();
        }

        @Override
        public Type getType() {
            return Type.INTEGER;
        }

        @Override
        protected void pr_str(StringBuilder builder, boolean readably) {
            int base = print_base;

            if (base < Character.MIN_RADIX || base > Character.MAX_RADIX)
                base = 8;

            if (base == 10)
                builder.append(value);
            else
                builder.append(Long.toString(value, base));
        }

        public static final Int zero = new Int(0);
        public static final Int  one = new Int(1);
        public static final Int _one = new Int(-1);
    }

    public static class Real extends Num {

        private double value;

        public Real(double value) {
            this.value = value;
        }

        public Real(String token) {
            value = Double.parseDouble(token);
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }

        @Override
        public Real create() {
            return new Real(value);
        }

        @Override
        public boolean isZero() {
            return value == 0;
        }

        @Override
        public boolean isPositive() {
            return value > 0;
        }

        @Override
        public boolean isNegative() {
            return value < 0;
        }

        @Override
        public void neg() {
            value = -value;
        }

        public void Add(double val) {
            value += val;
        }

        @Override
        public long getInt() {
            return (long)value;
        }

        @Override
        public double getReal() {
            return value;
        }

        @Override
        public void add(Num num) {
            value += num.getReal();
        }

        @Override
        public void sub(Num num) {
            value -= num.getReal();
        }

        @Override
        public void mul(Num num) {
            value *= num.getReal();
        }

        @Override
        public void div(Num num) {
            value /= num.getReal();
        }

        @Override
        public Type getType() {
            return Type.FLOAT;
        }

        @Override
        protected void pr_str(StringBuilder builder, boolean readably) {
            builder.append(value);
        }

        public static final Real zero = new Real(0);
        public static final Real  one = new Real(1);
        public static final Real _one = new Real(-1);
    }
}
