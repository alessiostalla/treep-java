package treep;

public interface ObjectFactory<T extends Object> {

    T get(String literal);

}
