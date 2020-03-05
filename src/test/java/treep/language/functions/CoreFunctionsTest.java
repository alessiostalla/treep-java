package treep.language.functions;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;
import treep.language.Object;
import treep.language.Symbols;
import treep.language.datatypes.Environment;
import treep.language.datatypes.Function;
import treep.language.datatypes.symbol.Symbol;
import treep.language.datatypes.tree.Cons;
import treep.language.datatypes.tree.Nothing;
import treep.language.datatypes.tree.Tree;
import treep.language.eval.SimpleEvaluator;
import treep.language.read.ASTBuilder;
import treep.language.read.SimpleDatumParser;
import treep.language.read.TreepLexer;
import treep.language.read.TreepParser;
import treep.math.RealNumber;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class CoreFunctionsTest {

    @Test
    public void appendNoArgsIsNil() {
        assertEquals(Nothing.AT_ALL, new append().apply());
    }

    @Test
    public void appendNilIsNil() {
        assertEquals(Nothing.AT_ALL, new append().apply(Nothing.AT_ALL));
    }

    @Test
    public void appendConsIsCons() {
        Cons cons = new Cons(Nothing.AT_ALL);
        assertEquals(cons, new append().apply(cons));
    }

    @Test
    public void appendConsToNilIsCons() {
        Cons cons = new Cons(Nothing.AT_ALL);
        assertEquals(cons, new append().apply(Nothing.AT_ALL, cons));
    }

    @Test
    public void appendNilToConsIsCons() {
        Cons cons = new Cons(Nothing.AT_ALL);
        Object result = new append().apply(cons, Nothing.AT_ALL);
        assertTrue(result instanceof Cons);
        assertEquals(cons.head, ((Cons) result).head);
        assertEquals(cons.tail, ((Cons) result).tail);
    }

    @Test
    public void appendConsToConsIsList() {
        RealNumber one = new RealNumber(new BigDecimal("1"));
        RealNumber two = new RealNumber(new BigDecimal("2"));
        Cons cons1 = new Cons(one);
        Cons cons2 = new Cons(two);
        Object result = new append().apply(cons1, cons2);
        assertTrue(result instanceof Cons);
        assertEquals(one, ((Cons) result).head);
        assertEquals(two, ((Cons) result).tail.getHead());
    }

    @Test
    public void appendConsToConsToConsIsList() {
        RealNumber one = new RealNumber(new BigDecimal("1"));
        RealNumber two = new RealNumber(new BigDecimal("2"));
        RealNumber three = new RealNumber(new BigDecimal("3"));
        Cons cons1 = new Cons(one);
        Cons cons2 = new Cons(two);
        Cons cons3 = new Cons(three);
        Object result = new append().apply(cons1, cons2, cons3);
        assertTrue(result instanceof Cons);
        assertEquals(one, ((Cons) result).head);
        Tree tail = ((Cons) result).tail;
        assertTrue(tail instanceof Cons);
        assertEquals(two, tail.getHead());
        assertEquals(three, tail.getTail().getHead());
    }
}
