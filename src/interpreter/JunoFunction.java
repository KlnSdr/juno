package interpreter;

public class JunoFunction {
    private final String[] params;
    private final String[] instructions;

    public JunoFunction(String[] params, String[] instructions) {
        this.params = params;
        this.instructions = instructions;
    }

    public String[] getParams() {
        return params;
    }

    public String[] getInstructions() {
        return instructions;
    }
}
