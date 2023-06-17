package interpreter;

import java.util.List;

public class Math {
    public static void add(List<JunoVariable> cmd, String scp, Interpreter interpreter) {
        addWithFactor(cmd, scp, interpreter, 1);
    }

    public static void subtract(List<JunoVariable> cmd, String scp, Interpreter interpreter) {
        addWithFactor(cmd, scp, interpreter, -1);
    }

    public static void multiply(List<JunoVariable> cmd , String scp, Interpreter interpreter) {
        if (cmd.get(2).getType().equals("i")) {
            int acc = (Integer) cmd.get(2).get();
            for (int i = 3; i < cmd.size(); i++) {
                if (cmd.get(i).getType().equals("s")) {
                    cmd.set(i, new JunoVariable(Integer.parseInt((String) cmd.get(i).get()), "f"));
                }
                acc *= (Integer) cmd.get(i).get();
            }

            saveResultToVar(cmd, scp, interpreter, Integer.toString(acc), "i");
        } else if (cmd.get(2).getType().equals("f")) {
            float acc = (Float) cmd.get(2).get();
            for (int i = 3; i < cmd.size(); i++) {
                if (cmd.get(i).getType().equals("s")) {
                    cmd.set(i, new JunoVariable(Float.parseFloat((String) cmd.get(i).get()), "f"));
                }
                acc *= (Float) cmd.get(i).get();
            }

            saveResultToVar(cmd, scp, interpreter, Float.toString(acc), "f");
        } else {
            System.out.println("Invalid type for mlt command: " + cmd.get(1).getType() + ". Expected i or f.");
        }
    }

    public static void divide(List<JunoVariable> cmd, String scp, Interpreter interpreter) {
        if (cmd.get(2).getType().equals("i")) {
            int acc = (Integer) cmd.get(2).get();
            for (int i = 3; i < cmd.size(); i++) {
                if (cmd.get(i).getType().equals("s")) {
                    cmd.set(i, new JunoVariable(Integer.parseInt((String) cmd.get(i).get()), "i"));
                }
                acc /= (Integer) cmd.get(i).get();
            }

            saveResultToVar(cmd, scp, interpreter, Integer.toString(acc), "i");
        } else if (cmd.get(2).getType().equals("f")) {
            float acc = (Float) cmd.get(2).get();
            for (int i = 3; i < cmd.size(); i++) {
                if (cmd.get(i).getType().equals("s")) {
                    cmd.set(i, new JunoVariable(Float.parseFloat((String) cmd.get(i).get()), "f"));
                }
                acc /= (Float) cmd.get(i).get();
            }

            saveResultToVar(cmd, scp, interpreter, Float.toString(acc), "f");
        } else {
            System.out.println("Invalid type for div command: " + cmd.get(1).getType() + ". Expected i or f.");
        }
    }

    public static void modulo(List<JunoVariable> cmd, String scp, Interpreter interpreter) {
        if (cmd.get(2).getType().equals("i")) {
            int acc = (Integer) cmd.get(2).get();
            for (int i = 3; i < cmd.size(); i++) {
                if (cmd.get(i).getType().equals("s")) {
                    cmd.set(i, new JunoVariable(Integer.parseInt((String) cmd.get(i).get()), "i"));
                }
                acc %= (Integer) cmd.get(i).get();
            }

            saveResultToVar(cmd, scp, interpreter, Integer.toString(acc), "i");
        } else if (cmd.get(2).getType().equals("f")) {
            float acc = (Float) cmd.get(2).get();
            for (int i = 3; i < cmd.size(); i++) {
                if (cmd.get(i).getType().equals("s")) {
                    cmd.set(i, new JunoVariable(Float.parseFloat((String) cmd.get(i).get()), "f"));
                }
                acc %= (Float) cmd.get(i).get();
            }

            saveResultToVar(cmd, scp, interpreter, Float.toString(acc), "f");
        } else {
            System.out.println("Invalid type for mod command: " + cmd.get(1).getType() + ". Expected i or f.");
        }
    }

    public static void shiftLeft(List<JunoVariable> cmd, String scp, Interpreter interpreter) {
        if (cmd.get(2).getType().equals("i")) {
            int acc = (Integer) cmd.get(2).get();
            for (int i = 3; i < cmd.size(); i++) {
                if (cmd.get(i).getType().equals("s")) {
                    cmd.set(i, new JunoVariable(Integer.parseInt((String) cmd.get(i).get()), "i"));
                }
                acc <<= (Integer) cmd.get(i).get();
            }

            saveResultToVar(cmd, scp, interpreter, Integer.toString(acc), "i");
        } else {
            System.out.println("Invalid type for shl command: " + cmd.get(1).getType() + ". Expected i.");
        }
    }

    public static void shiftRight(List<JunoVariable> cmd, String scp, Interpreter interpreter) {
        if (cmd.get(2).getType().equals("i")) {
            int acc = (Integer) cmd.get(2).get();
            for (int i = 3; i < cmd.size(); i++) {
                if (cmd.get(i).getType().equals("s")) {
                    cmd.set(i, new JunoVariable(Integer.parseInt((String) cmd.get(i).get()), "i"));
                }
                acc >>= (Integer) cmd.get(i).get();
            }

            saveResultToVar(cmd, scp, interpreter, Integer.toString(acc), "i");
        } else {
            System.out.println("Invalid type for shr command: " + cmd.get(1).getType() + ". Expected i.");
        }
    }

    private static void saveResultToVar(List<JunoVariable> cmd, String scp, Interpreter interpreter, String value, String type) {
        if (interpreter.variables.get(scp).has((String) cmd.get(1).get())) {
            interpreter.variables.get(scp).set((String) cmd.get(1).get(), value);
        } else {
            interpreter.variables.get(scp).add((String) cmd.get(1).get(), value, type);
        }
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

            saveResultToVar(cmd, scp, interpreter, Integer.toString(acc), "i");
        } else if (cmd.get(2).getType().equals("f")) {
            float acc = (Float) cmd.get(2).get();
            for (int i = 3; i < cmd.size(); i++) {
                if (cmd.get(i).getType().equals("s")) {
                    cmd.set(i, new JunoVariable(Float.parseFloat((String) cmd.get(i).get()), "f"));
                }
                acc += factor * (Float) cmd.get(i).get();
            }

            saveResultToVar(cmd, scp, interpreter, Float.toString(acc), "f");
        } else {
            System.out.println("Invalid type for add command: " + cmd.get(1).getType() + ". Expected i or f.");
        }
    }
}
