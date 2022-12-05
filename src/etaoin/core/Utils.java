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

import etaoin.Interpreter;
import etaoin.core.chars.*;
import etaoin.core.in_out.*;
import etaoin.core.list.*;
import etaoin.core.mapping.*;
import etaoin.core.numbers.*;
import etaoin.core.predicates.*;
import etaoin.core.symbol.*;
import etaoin.data.*;

public class Utils {

    public static Pair getPairArg(Pair lst, int idx) {
        Value v = Lst.nth(lst, idx);
        if (v instanceof Pair)
            return (Pair)v;

        return null;
    }

    public static Num getNumArg(Pair lst, int idx) {
        Value v = Lst.nth(lst, idx);
        if (v instanceof Num)
            return (Num)v;
        return null;
    }

    public static Num.Int getIntArg(Pair lst, int idx) {
        Value v = Lst.nth(lst, idx);
        if (v instanceof Num.Int)
            return (Num.Int)v;
        return null;
    }

    public static Num.Real getRealArg(Pair lst, int idx) {
        Value v = Lst.nth(lst, idx);
        if (v instanceof Num.Real)
            return (Num.Real)v;
        return null;
    }

    public static Symbol getSymbArg(Pair lst, int idx) {
        Value v = Lst.nth(lst, idx);
        if (v instanceof Symbol)
            return (Symbol)v;
        return null;
    }

    public static Str getStrArg(Pair lst, int idx) {
        Value v = Lst.nth(lst, idx);
        if (v instanceof Str)
            return (Str)v;
        return null;
    }

    public static int getCharArg(Pair lst, int idx) {
        return getChar(Lst.nth(lst, idx));
    }

    public static Port getPortArg(Pair lst, int idx) {
        return getPort(Lst.nth(lst, idx));
    }

    public static Func getFuncArg(Pair lst, int idx) {
        return getFunc(Lst.nth(lst, idx));
    }

    //------------------------------------------------------------------

    public static Pair getPair(Value val) {
        if (val instanceof Pair)
            return (Pair)val;

        return null;
    }

    public static Num getNum(Value val) {
        if (val instanceof Num)
            return (Num) val;
        return null;
    }

    public static Num.Int getInt(Value val) {
        if (val instanceof Num.Int)
            return (Num.Int) val;
        return null;
    }

    public static Num.Real getReal(Value val) {
        if (val instanceof Num.Real)
            return (Num.Real) val;
        return null;
    }

    public static Symbol getSymb(Value val) {
        if (val instanceof Symbol)
            return (Symbol)val;

        return null;
    }

    public static Str getStr(Value val) {
        if (val instanceof Str)
            return (Str)val;
        return null;
    }

    public static int getChar(Value val) {
        if (val instanceof Num.Int) {
            Num.Int n = (Num.Int) val;
            if (n.getValue() <= 0 || n.getValue() > 65535)
                return -1;
            return (int) n.getValue();
        }

        if (val instanceof Symbol) {
            Symbol s = (Symbol) val;
            return s.getName().charAt(0);
        }

        return -2;
    }

    public static Port getPort(Value val) {
        if (val instanceof Port)
            return (Port)val;
        return null;
    }

    public static Func getFunc(Value val) {
        if (val instanceof Func)
            return (Func)val;
        return null;
    }

    //------------------------------------------------------------------

    public static boolean isNil(Value val) {
        return val == null || val == Constant.Nil;
    }

    public static Value checkNull(Value val) {
        return val == null ? Constant.Nil : val;
    }

    //------------------------------------------------------------------

    public static void initCoreFunc(Interpreter in) {

        in.addFunction(new AndOr(AndOr.FType.AND));
        in.addFunction(new AndOr(AndOr.FType.OR));
        in.addFunction(new Cond());
        in.addFunction(new If());
        in.addFunction(new Comment());
        in.addFunction(new Prog2());
        in.addFunction(new ProgN());
        in.addFunction(new ProgV());
        in.addFunction(new Lambda(in.LAMBDA));
        in.addFunction(new Lambda(in.LAMBDA_FEXPR), in.LAMBDA_FEXPR);
        in.addFunction(new Lambda(in.LAMBDA_MACRO), in.LAMBDA_MACRO);
        in.addFunction(new Arg());
        in.addFunction(new Listify());
        in.addFunction(new Expand(Expand.FType.EXPAND));
        in.addFunction(new Expand(Expand.FType.EXPAND_ALL));
        in.addFunction(new Eval(Eval.FType.EVAL));
        in.addFunction(new Eval(Eval.FType.EVAL_BEFORE));
        in.addFunction(new Eval(Eval.FType.EVAL_AFTER));
        in.addFunction(new Apply());
        in.addFunction(new Function());
        in.addFunction(new FunCall());
        in.addFunction(new Prog());
        in.addFunction(new Go());
        in.addFunction(new Return());
        in.addFunction(new Do());
        in.addFunction(new Catch());
        in.addFunction(new Throw());
        in.addFunction(new DeFun());
        in.addFunction(new ErrSet());
        in.addFunction(new Err());
        in.addFunction(new Error());
        in.addFunction(new RunTime(RunTime.FType.RUNTIME));
        in.addFunction(new RunTime(RunTime.FType.TIME));
        in.addFunction(new Status(Status.FType.STATUS));
        in.addFunction(new Status(Status.FType.SSTATUS));
        in.addFunction(new MakObList());
        in.addFunction(new Trace(Trace.FType.TRACE));
        in.addFunction(new Trace(Trace.FType.UNTRACE));
        in.addFunction(new Quit(Quit.FType.QUIT));
        in.addFunction(new Quit(Quit.FType.EXIT));
        in.addFunction(new Quit(Quit.FType.BYE));
        in.addFunction(new Break());
        in.addFunction(new BreakInfo(BreakInfo.FType.BRK_LEVEL));
        in.addFunction(new BreakInfo(BreakInfo.FType.BRK_TAG));
        in.addFunction(new BreakInfo(BreakInfo.FType.BRK_FORM));
        in.addFunction(new BreakInfo(BreakInfo.FType.BRK_DEPTH));
        in.addFunction(new BreakInfo(BreakInfo.FType.BRK_ENV));
        in.addFunction(new BreakInfo(BreakInfo.FType.BRK_GET));
        in.addFunction(new BreakInfo(BreakInfo.FType.BRK_SET));
        in.addFunction(new BreakInfo(BreakInfo.FType.BRK_PUT));
        in.addFunction(new BreakInfo(BreakInfo.FType.BRK_VARS));
        in.addFunction(new BreakInfo(BreakInfo.FType.BRK_VALS));
        in.addFunction(new BreakInfo(BreakInfo.FType.BACKTRACE));
    }

    public static void initMappFunc(Interpreter in) {

        in.addFunction(new MapCar(MapCar.FType.MAPCAR));
        in.addFunction(new MapCar(MapCar.FType.MAPCAN));
        in.addFunction(new MapCar(MapCar.FType.MAPC));
        in.addFunction(new MapCar(MapCar.FType.MAPLIST));
        in.addFunction(new MapCar(MapCar.FType.MAPCON));
        in.addFunction(new MapCar(MapCar.FType.MAP));
    }

    public static void initNumFunc(Interpreter in) {

        in.addFunction(new Add());
        in.addFunction(new Mul());
        in.addFunction(new Add1());
        in.addFunction(new Sub1());
        in.addFunction(new Rem());
        in.addFunction(new Gcd());
        in.addFunction(new Expt());
        in.addFunction(new FAdd());
        in.addFunction(new FSub());
        in.addFunction(new FMul());
        in.addFunction(new FDiv());
        in.addFunction(new FAdd1());
        in.addFunction(new FSub1());
        in.addFunction(new FRem());
        in.addFunction(new RAdd());
        in.addFunction(new RSub());
        in.addFunction(new RMul());
        in.addFunction(new RDiv());
        in.addFunction(new RAdd1());
        in.addFunction(new RSub1());
        in.addFunction(new FSqrt());
        in.addFunction(new RSqrt());
        in.addFunction(new Exp());
        in.addFunction(new Log());
        in.addFunction(new Sin());
        in.addFunction(new Cos());
        in.addFunction(new Atan());
        in.addFunction(new ZeroP());
        in.addFunction(new PlusP());
        in.addFunction(new MinusP());
        in.addFunction(new NEql());
        in.addFunction(new NGreat());
        in.addFunction(new NLess());
        in.addFunction(new GreaterP());
        in.addFunction(new LessP());
        in.addFunction(new Min());
        in.addFunction(new Max());
        in.addFunction(new Fix());
        in.addFunction(new Real());
        in.addFunction(new Random());
        in.addFunction(new Boole());
        in.addFunction(new ChSign(ChSign.FType.ABS));
        in.addFunction(new ChSign(ChSign.FType.MINUS));
        Symbol div = in.addFunction(new Div());
        Symbol sub = in.addFunction(new Sub());

        in.getSymbol("*DIF", sub);
        in.getSymbol("*QUO", div);
    }

    public static void initListFunc(Interpreter in) {

        in.addFunction(new CarCdr(CarCdr.FType.CAR));
        in.addFunction(new CarCdr(CarCdr.FType.CDR));

        in.addFunction(new CXr("CAAR"));
        in.addFunction(new CXr("CADR"));
        in.addFunction(new CXr("CDAR"));
        in.addFunction(new CXr("CDDR"));

        in.addFunction(new CXr("CAAAR"));
        in.addFunction(new CXr("CAADR"));
        in.addFunction(new CXr("CADAR"));
        in.addFunction(new CXr("CADDR"));
        in.addFunction(new CXr("CDAAR"));
        in.addFunction(new CXr("CDADR"));
        in.addFunction(new CXr("CDDAR"));
        in.addFunction(new CXr("CDDDR"));

        in.addFunction(new CXr("CAAAAR"));
        in.addFunction(new CXr("CAAADR"));
        in.addFunction(new CXr("CAADAR"));
        in.addFunction(new CXr("CAADDR"));
        in.addFunction(new CXr("CADAAR"));
        in.addFunction(new CXr("CADADR"));
        in.addFunction(new CXr("CADDAR"));
        in.addFunction(new CXr("CADDDR"));
        in.addFunction(new CXr("CDAAAR"));
        in.addFunction(new CXr("CDAADR"));
        in.addFunction(new CXr("CDADAR"));
        in.addFunction(new CXr("CDADDR"));
        in.addFunction(new CXr("CDDAAR"));
        in.addFunction(new CXr("CDDADR"));
        in.addFunction(new CXr("CDDDAR"));
        in.addFunction(new CXr("CDDDDR"));

        in.addFunction(new Cons());
        in.addFunction(new NCons());
        in.addFunction(new XCons());
        in.addFunction(new Last());
        in.addFunction(new Length());
        in.addFunction(new List());
        in.addFunction(new Append());
        in.addFunction(new Reverse());
        in.addFunction(new NConc());
        in.addFunction(new NReverse());
        in.addFunction(new NReConc());
        in.addFunction(new RplaCar());
        in.addFunction(new RplaCdr());
        in.addFunction(new Assoc(Assoc.FType.ASSOC));
        in.addFunction(new Assoc(Assoc.FType.ASSQ));
        in.addFunction(new Assoc(Assoc.FType.SASSOC));
        in.addFunction(new Assoc(Assoc.FType.SASSQ));
        in.addFunction(new Member(Member.FType.MEMBER));
        in.addFunction(new Member(Member.FType.MEMQ));
        in.addFunction(new Delete(Delete.FType.DELETE));
        in.addFunction(new Delete(Delete.FType.DELQ));
        in.addFunction(new Remove(Remove.FType.REMOVE));
        in.addFunction(new Remove(Remove.FType.REMQ));
        in.addFunction(new Unique(Unique.FType.UNIQUE));
        in.addFunction(new Unique(Unique.FType.UNIQ));
        in.addFunction(new SetUnion(SetUnion.FType.SET_UNION));
        in.addFunction(new SetUnion(SetUnion.FType.SETQ_UNION));
        in.addFunction(new SetIntersect(SetIntersect.FType.SET_INTERSECT));
        in.addFunction(new SetIntersect(SetIntersect.FType.SETQ_INTERSECT));
        in.addFunction(new SetDiff(SetDiff.FType.SET_DIFF));
        in.addFunction(new SetDiff(SetDiff.FType.SETQ_DIFF));
    }

    public static void initPredFunc(Interpreter in) {

        in.addFunction(new Atom());
        in.addFunction(new SymbolP());
        in.addFunction(new FixP());
        in.addFunction(new FloatP());
        in.addFunction(new NumberP());
        in.addFunction(new TypeP());
        in.addFunction(new StringP());
        in.addFunction(new SubrP());
        in.addFunction(new Equal(Equal.FType.EQUAL));
        in.addFunction(new Equal(Equal.FType.EQ));
        in.addFunction(new CompSymStr(CompSymStr.FType.SAMEPNAMEP));
        in.addFunction(new CompSymStr(CompSymStr.FType.ALPHALESSP));
        in.addFunction(new Null(Null.FType.NULL));
        in.addFunction(new Null(Null.FType.NOT));
    }

    public static void initSymbFunc(Interpreter in) {

        in.addFunction(new Set());
        in.addFunction(new SetQ());
        in.addFunction(new SymEval());
        in.addFunction(new Bound(Bound.FType.BOUNDP));
        in.addFunction(new Bound(Bound.FType.BOUND));
        in.addFunction(new Bound(Bound.FType.DEFINEDP));
        in.addFunction(new MakeUnbound());
        in.addFunction(new Get());
        in.addFunction(new GetL());
        in.addFunction(new PutProp());
        in.addFunction(new DefProp());
        in.addFunction(new RemProp());
        in.addFunction(new PList());
        in.addFunction(new GenSym());
        in.addFunction(new RemOb());
        in.addFunction(new Intern());

        if (Value.HAS_STRING) {
            in.addFunction(new GetPName());
        }
    }

    public static void initCharsFunc(Interpreter in) {

        in.addFunction(new Ascii());
        in.addFunction(new GetChar(GetChar.FType.GETCHAR));
        in.addFunction(new GetChar(GetChar.FType.GETCHARN));
        in.addFunction(new MakeName(MakeName.FType.IMPLODE));
        in.addFunction(new MakeName(MakeName.FType.MAKNAM));
        in.addFunction(new MakeName(MakeName.FType.MAKSTR));
        in.addFunction(new ReadList());
        in.addFunction(new Explode(Explode.FType.EXPLODE));
        in.addFunction(new Explode(Explode.FType.EXPLODEC));
        in.addFunction(new Explode(Explode.FType.EXPLODEN));
        in.addFunction(new FlatSize(FlatSize.FType.FLATSIZE));
        in.addFunction(new FlatSize(FlatSize.FType.FLATC));

        if (Value.HAS_STRING) {
            in.addFunction(new Catenate());
            in.addFunction(new Index());
            in.addFunction(new StringLength());
            in.addFunction(new SubStr());
            in.addFunction(new MakeAtom());
            in.addFunction(new StringCase(StringCase.FType.UPCASE));
            in.addFunction(new StringCase(StringCase.FType.DOWNCASE));
        }
    }

    public static void initInOutFunc(Interpreter in) {

        in.addFunction(new Print(Print.FType.PRINT));
        in.addFunction(new Print(Print.FType.PRIN1));
        in.addFunction(new Print(Print.FType.PRINC));
        in.addFunction(new TerPri());
        in.addFunction(new Tyo());
        in.addFunction(new Read(Read.FType.READ));
        in.addFunction(new Read(Read.FType.READCH));
        in.addFunction(new Read(Read.FType.READLINE));
        in.addFunction(new Tyi());
        in.addFunction(new TyiPeek());
        in.addFunction(new Open(Open.FType.OPENI));
        in.addFunction(new Open(Open.FType.OPENO));
        in.addFunction(new Open(Open.FType.OPENA));
        in.addFunction(new Close());
        in.addFunction(new Load());
    }
}
