package interpreter;

import java.util.List;

public class Math {
    public static void add(List<JunoVariable> cmd, String scp, Interpreter interpreter) {
        if (cmd.get(2).getType().equals("i")) {
            int acc = 0;
            for (int i = 2; i < cmd.size(); i++) {
                acc += (Integer) cmd.get(i).get();
            }

            if (interpreter.variables.get(scp).has((String) cmd.get(1).get())) {
                interpreter.variables.get(scp).set((String) cmd.get(1).get(), Integer.toString(acc));
            } else {
                interpreter.variables.get(scp).add((String) cmd.get(1).get(), Integer.toString(acc), "i");
            }
        } else if (cmd.get(2).getType().equals("f")) {
            float acc = 0.0f;
            for (int i = 2; i < cmd.size(); i++) {
                acc += (Float) cmd.get(i).get();
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
