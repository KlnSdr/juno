package interpreter;

import java.util.HashMap;

public class JunoScope {
    private final HashMap<String, JunoVariable> variables = new HashMap<>();

    public void set(String name, String value) {
        JunoVariable variable = variables.get(name);

        switch (variable.getType()) {
            case "i":
                variable.update(Integer.parseInt(value));
                break;
            case "s":
                variable.update(value);
                break;
            case "f":
                variable.update(Float.parseFloat(value));
                break;
            default:
                System.out.println("unknown type: " + variable.getType() + " for variable " + name);
                break;
        }
    }

    public void add(String name, String value, String type) {
        if (variables.containsKey(name)) {
            this.set(name, value);
            return;
        }
        switch (type.toLowerCase()) {
            case "i":
                variables.put(name, new JunoVariable<>(Integer.parseInt(value), type));
                break;
            case "s":
                variables.put(name, new JunoVariable<>(value, type));
                break;
            case "f":
                variables.put(name, new JunoVariable<>(Float.parseFloat(value), type));
                break;
            default:
                System.out.println("unknown type: " + type + " for variable " + name);
                break;
        }
    }

    public String getString(String name) {
        return (String) variables.get(name).get();
    }

    public Float getFloat(String name) {
        return (Float) variables.get(name).get();
    }

    public Integer getInteger(String name) {
        return (Integer) variables.get(name).get();
    }

    public boolean has(String name) {
        return variables.containsKey(name);
    }
}
