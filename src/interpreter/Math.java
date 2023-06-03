package interpreter;

import java.util.List;

public class Math {
    public static void add(List<JunoVariable> cmd, String scp, Interpreter interpreter) {
        addWithFactor(cmd, scp, interpreter, 1);
    }

    public static void subtract(List<JunoVariable> cmd, String scp, Interpreter interpreter) {
        addWithFactor(cmd, scp, interpreter, -1);
    }

    private static void addWithFactor(List<JunoVariable> cmd, String scp, Interpreter interpreter, int factor) {
        if (cmd.get(2).getType().equals("i")) {
            int acc = (Integer) cmd.get(2).get();
            for (int i = 3; i < cmd.size(); i++) {
                if (cmd.get(i).getType().equals("s")) {
                    cmd.set(i, new JunoVariable(Integer.parseInt((String) cmd.get(i).get()), "f"));
                }
                acc += factor * (Integer) cmd.get(i).get();
            }

            if (interpreter.variables.get(scp).has((String) cmd.get(1).get())) {
                interpreter.variables.get(scp).set((String) cmd.get(1).get(), Integer.toString(acc));
            } else {
                interpreter.variables.get(scp).add((String) cmd.get(1).get(), Integer.toString(acc), "i");
            }
        } else if (cmd.get(2).getType().equals("f")) {
            float acc = (Float) cmd.get(2).get();
            for (int i = 3; i < cmd.size(); i++) {
                if (cmd.get(i).getType().equals("s")) {
                    cmd.set(i, new JunoVariable(Float.parseFloat((String) cmd.get(i).get()), "f"));
                }
                acc += factor * (Float) cmd.get(i).get();
            }

            if (interpreter.variables.get(scp).has((String) cmd.get(1).get())) {
                interpreter.variables.get(scp).set((String) cmd.get(1).get(), Float.toString(acc));
            } else {
                interpreter.variables.get(scp).add((String) cmd.get(1).get(), Float.toString(acc), "f");
            }
        } else {
            System.out.println("Invalid type for add command: " + cmd.get(1).getType() + ". Expected i or f.");
        }
    }
}
