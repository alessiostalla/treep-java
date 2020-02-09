package treep.math;

import treep.language.ASTBuilder;
import treep.parser.TreepParser;

import java.math.BigDecimal;

public class Math {

    public static void addSupportForNumbers(ASTBuilder astBuilder) {
        astBuilder.getObjectFactoryMap().put(TreepParser.NUMBER, literal -> new RealNumber(new BigDecimal(literal)));
    }

}
