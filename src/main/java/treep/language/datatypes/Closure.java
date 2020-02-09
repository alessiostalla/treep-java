package treep.language.datatypes;

public class Closure extends Function {
    public final Environment enclosingEnvironment;

    public Closure(Environment enclosingEnvironment) {
        this.enclosingEnvironment = enclosingEnvironment;
    }
}
