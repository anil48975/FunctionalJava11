package systemdesign;

public class InstanceHolder<T> {
    private final T INSTANCE;
    public InstanceHolder(T instance) {
        INSTANCE = instance;
    }
    public T getInstance() {
        return INSTANCE;
    }
}
