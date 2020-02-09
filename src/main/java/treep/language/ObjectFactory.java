package treep.language;

public interface ObjectFactory<T extends Object> {

    T get(String literal);

}
