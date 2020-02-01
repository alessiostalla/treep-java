package treep.ast;

public interface ObjectFactory<T extends Object> {

    T make(String literal);

}
