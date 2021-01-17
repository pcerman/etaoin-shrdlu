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

import etaoin.LispException;
import etaoin.Ref;
import etaoin.core.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class Lst {

    public static Pair create(Value v) {
        return new Pair(v, null);
    }

    public static Pair create(Value v1, Value v2) {
        return new Pair(v1, new Pair(v2, null));
    }

    public static Pair create(Value... vals) {
        Pair list = null;
        Pair last = null;
        for (Value val : vals) {
            if (last == null) {
                last = new Pair(val, null);
                list = last;
            } else {
                Pair pt = last;
                last = new Pair(val, null);
                pt.setCdr(last);
            }
        }
        return list;
    }

    public static Pair cons(Value v1, Value v2, Value... vals) {
        int cnt = vals.length;
        if (cnt == 0) {
            return new Pair(v1, v2 == Constant.Nil ? null : v2);
        }

        Pair lst = vals[cnt - 1] == Constant.Nil ? null : new Pair(vals[cnt - 1], null);
        for (int i = cnt - 2; i >= 0; i--)
            lst = new Pair(vals[i], lst);

        return new Pair(v1, new Pair(v2, lst));
    }

    public static Pair append(Pair lst1, Pair lst2, Ref<Value> lastCdr) {
        Pair list = null;
        Pair last = null;
        Value lcdr = null;

        for (Pair p = lst1; p != null; ) {
            if (last == null) {
                last = new Pair(p.getCar(), null);
                list = last;
            } else {
                Pair pt = last;
                last = new Pair(p.getCar(), null);
                pt.setCdr(last);
            }
            lcdr = p.getCdr();
            p = Utils.getPair(lcdr);
        }
        if (last != null)
            last.setCdr(lst2);

        if (lastCdr != null)
            lastCdr.val = lcdr;

        return list;
    }

    public static Pair append(Pair lst1, Pair lst2) {
        return append(lst1, lst2, null);
    }

    public static Pair copy(Pair lst, Ref<Value> lastCdr) {
        return append(lst, null, lastCdr);
    }

    public static Pair copy(Pair lst) {
        return append(lst, null);
    }

    public static Pair take(Pair lst, int cnt, Ref<Value> lastCdr) {
        Pair list = null;
        Pair last = null;
        Value lcdr = null;

        for (Pair p = lst; p != null && cnt > 0; cnt--) {
            if (last == null) {
                last = new Pair(p.getCar(), null);
                list = last;
            } else {
                Pair pt = last;
                last = new Pair(p.getCar(), null);
                pt.setCdr(last);
            }
            lcdr = p.getCdr();
            p = Utils.getPair(lcdr);
        }
        if (lastCdr != null)
            lastCdr.val = lcdr;

        return list;
    }

    public static Pair take(Pair lst, int cnt) {
        return take(lst, cnt, null);
    }

    public static Pair reverse(Pair lst, Ref<Value> lastCdr) {
        Pair list = null;
        Pair last = null;

        for (; lst != null; lst = safeCdr(lst)) {
            last = lst;
            list = new Pair(lst.getCar(), list);
        }

        if (lastCdr != null)
            lastCdr.val = (last != null) ? last.getCdr() : null;

        return list;
    }

    public static Pair reverse(Pair lst) {
        return reverse(lst, null);
    }

    public static Pair nreverse(Pair lst, Ref<Value> lastCdr) {
        return nreconc(lst, null, lastCdr);
    }

    public static Pair nreverse(Pair lst) {
        return nreconc(lst, null, null);
    }

    public static Pair nreconc(Pair lst1, Pair lst2, Ref<Value> lastCdr) {
        Pair last = lst2;
        Value lcdr = null;

        for (; lst1 != null; lst1 = Utils.getPair(lcdr)) {
            lcdr = lst1.getCdr();
            lst1.setCdr(last);
            last = lst1;
        }

        if (lastCdr != null)
            lastCdr.val = lcdr;

        return last;
    }

    public static Pair nreconc(Pair lst1, Pair lst2) {
        return nreconc(lst1, lst2, null);
    }

    public static Pair nconc(Pair lst1, Pair lst2, Ref<Value> lastCdr) {
        Value lcdr = null;

        if (lst1 == null)
            lst1 = lst2;
        else {
            Pair p = lastPair(lst1);
            lcdr = p.getCdr();

            p.setCdr(lst2);
        }

        if (lastCdr != null)
            lastCdr.val = lcdr;

        return lst1;
    }

    public static Pair nconc(Pair lst1, Pair lst2) {
        return nconc(lst1, lst2, null);
    }

    public static Pair getProperty(Value prop, Pair lst) {
        while (lst != null) {
            if (lst.getCar() == prop)
                return lst;
            lst = safeCdr(lst.getCdr());
        }
        return null;
    }

    public static Pair remProperty(Value prop, Pair lst, Ref<Pair> oldProp) {
        Pair list = null;
        Pair last = null;
        Pair lcdr;

        boolean isProp = true;
        for (Pair p = lst; p != null; p = safeCdr(p)) {
            if (isProp && p.getCar() == prop) {
                if (oldProp != null)
                    oldProp.val = p;

                if (last == null)
                    return safeCdr(p.getCdr());
                else {
                    last.setCdr(safeCdr(p.getCdr()));
                    return list;
                }
            }
            if (last == null) {
                last = new Pair(p.getCar(), null);
                list = last;
            } else {
                lcdr = last;
                last = new Pair(p.getCar(), null);
                lcdr.setCdr(last);
            }
            isProp = !isProp;
        }

        return lst;
    }

    public static Pair remProperty(Value prop, Pair lst) {
        return remProperty(prop, lst, null);
    }

    public static Pair safeCdr(Value v) {
        return (v instanceof Pair) ? safeCdr((Pair)v) : null;
    }

    public static Pair safeCdr(Pair p) {
        Value v = p.getCdr();
        return (v instanceof Pair) ? (Pair)v : null;
    }

    public static Value car(Pair p) {
        return p == null ? null : p.getCar();
    }

    public static Value cdr(Pair p) {
        return p == null ? null : p.getCdr();
    }

    public static Value caar(Pair p) {
        if (p == null)
            return null;

        Value v = p.getCar();
        if (v instanceof Pair)
            return ((Pair) v).getCar();

        return null;
    }

    public static Value cadr(Pair p) {
        if (p == null)
            return null;

        Value v = p.getCdr();
        if (v instanceof Pair)
            return ((Pair) v).getCar();

        return null;
    }

    public static Value cdar(Pair p) {
        if (p == null)
            return null;

        Value v = p.getCar();
        if (v instanceof Pair)
            return ((Pair) v).getCdr();

        return null;
    }

    public static Value cddr(Pair p) {
        if (p == null)
            return null;

        Value v = p.getCdr();
        if (v instanceof Pair)
            return ((Pair) v).getCdr();

        return null;
    }

    public static int length(Pair lst, Ref<Value> lastCdr) {
        Value lcdr = null;

        int len = 0;
        while (lst != null) {
            len++;
            lcdr = lst.getCdr();
            lst = Utils.getPair(lcdr);
        }
        if (lastCdr != null)
            lastCdr.val = lcdr;

        return len;
    }

    public static int length(Pair lst) {
        return length(lst, null);
    }

    public static Pair nthPair(Pair lst, int idx) {
        if (idx < 0 || lst == null)
            return null;

        for (; lst != null; lst = safeCdr(lst)) {
            if (idx-- == 0)
                return lst;
        }

        return null;
    }

    public static Pair lastPair(Pair lst) {
        for (Pair p = lst; p != null; p = safeCdr(p))
            lst = p;

        return lst;
    }

    public static Value nth(Pair lst, int idx) {
        lst = nthPair(lst, idx);
        return lst != null ? lst.getCar() : null;
    }

    public static Value last(Pair lst) {
        lst = lastPair(lst);
        return lst != null ? lst.getCar() : null;
    }

    public static boolean isProperList(Pair lst) {
        return (lst == null) || lastPair(lst).getCdr() == null;
    }

    public static boolean isNull(Pair lst) {
        return lst == null;
    }

    public static boolean hasSingleElm(Pair lst) {
        return lst != null && lst.getCdr() == null;
    }

    public static boolean hasTwoElms(Pair lst) {
        if (lst == null)
            return false;

        return hasSingleElm(safeCdr(lst));
    }

    public static boolean hasNElms(Pair lst, int n) {
        if (n <= 0)
            return lst == null;

        for (; lst != null && n > 0; lst = safeCdr(lst))
            n--;

        return n == 0 && lst == null;
    }

    public static boolean hasNMoreElms(Pair lst, int n) {
        if (n <= 0)
            return true;

        for (; lst != null && n > 0; lst = safeCdr(lst))
            n--;

        return n == 0;
    }

    public static Pair find(Pair lst,
                            Function<Value, Boolean> fp,
                            Ref<Value> lastCdr) {
        Value lcdr = null;

        for (;lst != null; lst = safeCdr(lst)) {
            lcdr = lst.getCdr();
            if (fp.apply(lst.getCar()))
                break;
        }
        if (lastCdr != null && Utils.getPair(lcdr) == null)
            lastCdr.val = lcdr;

        return lst;
    }


    public static Pair find(Pair lst, Function<Value, Boolean> fp) {
        return find(lst, fp, null);
    }

    public static Pair find(Pair lst, Value elm) {
        for (;lst != null; lst = safeCdr(lst)) {
            if (lst.getCar() == elm)
                return lst;
        }

        return null;
    }

    public static Pair delete(Pair lst, int cnt, Function<Value, Boolean> fp) {
        if (cnt <= 0)
            return lst;

        Pair last = null;
        for (Pair p=lst; p != null; p = safeCdr(p)) {
            if (fp.apply(p.getCar())) {
                if (last != null)
                    last.setCdr(safeCdr(p));
                else
                    lst = safeCdr(p);
                if (--cnt == 0)
                    break;
            } else
                last = p;
        }

        return lst;
    }

    public static Pair remove(Pair lst, int cnt, Function<Value, Boolean> fp) {
        if (cnt <= 0 || lst == null)
            return lst;

        List<Value> jlst = new ArrayList<>();
        Pair lcdr = null;
        int last = 0;

        int removed = 0;
        for (Pair p = lst; p != null; p = safeCdr(p)) {
            if (fp.apply(p.getCar())) {
                last = jlst.size();
                lcdr = safeCdr(p);
                if (++removed == cnt)
                    break;
            } else {
                jlst.add(p.getCar());
            }
        }

        if (removed == 0)
            return lst;

        if (last == 0)
            return lcdr;

        Ref<Pair> lastPair = new Ref<>(null);

        lst = toList(jlst.subList(0, last), lastPair);
        lastPair.val.setCdr(lcdr);

        return lst;
    }

    public static Pair unique(Pair lst, Ref<Value> lastCdr,
                                        BiFunction<Value, Value, Boolean> fp) {

        if (lst == null || lst.getCdr() == null)
            return lst;

        List<Value> jlst = new ArrayList<>();
        Pair lcdr = null;
        Pair lapa = null;
        int last = 0;

        boolean removed = false;
        for (Pair p = lst; p != null; p = safeCdr(p)) {
            lapa = p;
            Value val = p.getCar();
            if (jlst.stream().anyMatch((v) -> fp.apply(val, v))) {
                last = jlst.size();
                lcdr = safeCdr(p);
                removed = true;
            } else {
                jlst.add(p.getCar());
            }
        }

        if (lastCdr != null)
            lastCdr.val = lapa.getCdr();

        if (lapa.getCdr() != null) {
            removed = true;
            last = jlst.size();
            lcdr = null;
        }

        if (!removed)
            return lst;

        if (last == 0)
            return lcdr;

        Ref<Pair> lastPair = new Ref<>(null);

        lst = toList(jlst.subList(0, last), lastPair);
        lastPair.val.setCdr(lcdr);

        return lst;
    }

    public static Pair union(Pair lst1, Pair lst2,
                             Ref<Value> lastCdr1, Ref<Value> lastCdr2,
                             BiFunction<Value, Value, Boolean> fp) {

        if (lst1 == null)
            return unique(lst2, lastCdr2, fp);

        if (lst2 == null)
            return unique(lst1, lastCdr1, fp);

        lst2 = unique(lst2, lastCdr2, fp);

        Pair lapa = null;

        List<Value> jlst = new ArrayList<>();

        for (Pair p = lst1; p != null; p = safeCdr(p)) {
            lapa = p;
            Value val = p.getCar();

            if (!jlst.stream().anyMatch((v) -> fp.apply(val, v))
                && find(lst2, (v) -> fp.apply(val, v)) == null)
                jlst.add(p.getCar());
        }

        if (lastCdr1 != null)
            lastCdr1.val = lapa.getCdr();

        if (jlst.isEmpty())
            return lst2;

        Ref<Pair> lastPair = new Ref<>(null);

        lst1 = toList(jlst, lastPair);
        lastPair.val.setCdr(lst2);

        return lst1;
    }

    public static Pair intersect(Pair lst1, Pair lst2,
                                 Ref<Value> lastCdr1, Ref<Value> lastCdr2,
                                 BiFunction<Value, Value, Boolean> fp) {

        if (lst1 == null || lst2 == null)
            return null;

        lst2 = unique(lst2, lastCdr2, fp);

        Pair lapa = null;
        Pair lrem = null;

        List<Value> jlst = new ArrayList<>();

        for (Pair p = lst1; p != null; p = safeCdr(p)) {
            lapa = p;
            Value val = p.getCar();

            if (!jlst.stream().anyMatch((v) -> fp.apply(val, v))
                && find(lst2, (v) -> fp.apply(val, v)) != null)
                jlst.add(p.getCar());
            else
                lrem = p;
        }

        if (lastCdr1 != null)
            lastCdr1.val = lapa.getCdr();

        if (lrem == null)
            return lst1;

        return toList(jlst);
    }

    public static Pair difference(Pair lst1, Pair lst2,
                                  Ref<Value> lastCdr1, Ref<Value> lastCdr2,
                                  BiFunction<Value, Value, Boolean> fp) {

        lst2 = unique(lst2, lastCdr2, fp);

        if (lst1 == null)
            return null;

        if (lst2 == null)
            return unique(lst1, lastCdr1, fp);

        Pair lapa = null;
        Pair lrem = null;

        List<Value> jlst = new ArrayList<>();

        for (Pair p = lst1; p != null; p = safeCdr(p)) {
            lapa = p;
            Value val = p.getCar();

            if (!jlst.stream().anyMatch((v) -> fp.apply(val, v))
                && find(lst2, (v) -> fp.apply(val, v)) == null)
                jlst.add(p.getCar());
            else
                lrem = p;
        }

        if (lastCdr1 != null)
            lastCdr1.val = lapa.getCdr();

        if (lrem == null)
            return lst1;

        return toList(jlst);
    }

    public static List<Value> toJavaList(Pair lst, Ref<Value> lastCdr) {
        List<Value> jLst = new ArrayList<>();
        Value lcdr = null;

        while (lst != null) {
            jLst.add(lst.getCar());
            lcdr = lst.getCdr();
            lst = Utils.getPair(lcdr);
        }
        if (lastCdr != null)
            lastCdr.val = lcdr;

        return jLst;
    }

    public static List<Value> toJavaList(Pair lst) {
        return toJavaList(lst, null);
    }

    public static <T> List<T> toJavaList(Function<Value, T> fn, Pair lst, Ref<Value> lastCdr) {
        List<T> jLst = new ArrayList<>();
        Value lcdr = null;

        while (lst != null) {
            jLst.add(fn.apply(lst.getCar()));
            lcdr = lst.getCdr();
            lst = Utils.getPair(lcdr);
        }
        if (lastCdr != null)
            lastCdr.val = lcdr;

        return jLst;
    }

    public static <T> List<T> toJavaList(Function<Value, T> fn, Pair lst) {
        return toJavaList(fn, lst, null);
    }

    public static Pair toList(List<? extends Value> jlst, Ref<Pair> lastPair) {
        if (jlst == null || jlst.isEmpty())
            return null;

        Pair list = null;
        Pair last = null;
        for (int i= jlst.size() -1; i >= 0; i--) {
            list = new Pair(jlst.get(i), list);
            if (last == null)
                last = list;
        }

        if (lastPair != null)
            lastPair.val = last;

        return list;
    }

    public static Pair toList(List<? extends Value> jlst) {
        return toList(jlst, null);
    }

    public static Pair toList(Iterable<? extends Value> itcol) {
        if (itcol == null)
            return null;

        Pair list = null;
        Pair last = null;

        for (Value val : itcol) {
            if (list == null) {
                list = Lst.create(val);
                last = list;
            } else {
                Pair p = last;
                last = Lst.create(val);
                p.setCdr(last);
            }
        }

        return list;
    }

    public interface FoldFun<T> {
        T apply(T init, Value v) throws LispException;
    }

    public static <T> T fold(FoldFun<T> fn,
                             T init,
                             Pair lst) throws LispException {
        return fold(fn, init, lst, null);
    }

    public static <T> T fold(FoldFun<T> fn,
                             T init,
                             Pair lst,
                             Ref<Value> lastCdr) throws LispException {

        Value lcdr = null;

        while (lst != null) {
            init = fn.apply(init, lst.getCar());
            lcdr = lst.getCdr();
            lst = Utils.getPair(lcdr);
        }
        if (lastCdr != null)
            lastCdr.val = lcdr;

        return init;
    }
}
