package treep;

//TODO revise, implement apply varargs and have other overloads throw exceptions
public abstract class Operator extends Object {

    public abstract Object apply(Object... arguments);

    public Object apply() {
        return apply(new Object[0]);
    }

    public Object apply(Object argument) {
        return apply(new Object[] { argument });
    }

    public Object apply(Object argument1, Object argument2) {
        return apply(new Object[] { argument1, argument2 });
    }

    public Object apply(Object argument1, Object argument2, Object argument3) {
        return apply(new Object[] { argument1, argument2, argument3 });
    }

}
