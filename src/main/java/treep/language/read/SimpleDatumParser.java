package treep.language.read;

import treep.language.Object;
import treep.language.datatypes.symbol.NameSpace;
import treep.language.datatypes.tree.Cons;
import treep.language.eval.SimpleEvaluator;
import treep.math.RealNumber;

import java.math.BigDecimal;

public class SimpleDatumParser implements DatumParser {

    public final NameSpace nameSpace;

    public SimpleDatumParser(NameSpace nameSpace) {
        this.nameSpace = nameSpace;
    }

    @Override
    public Object parse(String text) {
        char c = text.charAt(0);
        if(c >= '0' && c <= '9') {
            return new RealNumber(new BigDecimal(text));
        } else {
            return nameSpace.intern(text);
        }
    }
}
