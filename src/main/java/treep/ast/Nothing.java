package treep.ast;

public class Nothing extends Object implements Tree {

    public static final Nothing AT_ALL = new Nothing();

    private Nothing() {}

    @Override
    public Tree with(Object object) {
        return new Node(object);
    }
}
