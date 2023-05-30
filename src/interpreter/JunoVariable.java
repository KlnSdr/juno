package interpreter;

public class JunoVariable<T> {
    private final String type;
    private T value;

    public JunoVariable(T value, String type) {
        this.value = value;
        this.type = type;
    }

    public void update(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    public String getType() {
        return type;
    }
}
