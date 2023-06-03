package interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Interpreter {
    private static final String[] blacklistFunctionAndScopeNames = {"main", "global"};
    private final HashMap<String, JunoFunction> functions = new HashMap<>();
    private final HashMap<String, JunoScope> variables = new HashMap<>();

    public void run(String[] program) {
        functions.put("main", new JunoFunction(new String[0], this.curateInstructions(program)));
        variables.put("main", new JunoScope());

        variables.put("global", new JunoScope());
        this.runFunction("main");
    }

    private void runFunction(String functionName) {
        if (!functions.containsKey(functionName)) {
            System.out.println("Function " + functionName + " does not exist.");
            return;
        }

        JunoFunction function = functions.get(functionName);
        String scp = functionName;

        for (String cmd : function.getInstructions()) {
            List<JunoVariable> processedCmd = this.replaceAllVars(this.reuniteStrings(cmd.split(" ")), scp);
            String cmdName = (String) processedCmd.get(0).get();

            switch (cmdName.toLowerCase()) {
                case "set":
                    if (processedCmd.size() != 4) {
                        System.out.println("Invalid set command: " + cmd);
                        break;
                    }
                    System.out.println("Setting " + processedCmd.get(1).get() + "(" + processedCmd.get(2).get() + ")" + " to " + processedCmd.get(3).get());
                    variables.get(scp).add((String) processedCmd.get(1).get(), (String) processedCmd.get(3).get(), ((String) processedCmd.get(2).get()).toLowerCase());
                    break;
                case "out":
                    processedCmd.remove(0);
                    processedCmd.forEach((var) -> System.out.print(var.get()));
                    System.out.println();
                    break;
                case "add":
                    if (processedCmd.size() < 4) {
                        System.out.println("Invalid add command: " + cmd);
                        break;
                    }
                    if (processedCmd.get(2).getType().equals("i")) {
                        int acc = 0;
                        for (int i = 2; i < processedCmd.size(); i++) {
                            acc += (Integer) processedCmd.get(i).get();
                        }

                        if (this.variables.get(scp).has((String) processedCmd.get(1).get())) {
                            this.variables.get(scp).set((String) processedCmd.get(1).get(), Integer.toString(acc));
                        } else {
                            this.variables.get(scp).add((String) processedCmd.get(1).get(), Integer.toString(acc), "i");
                        }
                    } else if (processedCmd.get(2).getType().equals("f")) {
                        float acc = 0.0f;
                        for (int i = 2; i < processedCmd.size(); i++) {
                            acc += (Float) processedCmd.get(i).get();
                        }

                        if (this.variables.get(scp).has((String) processedCmd.get(1).get())) {
                            this.variables.get(scp).set((String) processedCmd.get(1).get(), Float.toString(acc));
                        } else {
                            this.variables.get(scp).add((String) processedCmd.get(1).get(), Float.toString(acc), "f");
                        }
                    } else {
                        System.out.println("Invalid type for add command: " + processedCmd.get(1).getType() + ". Expected i or f.");
                        break;
                    }
                    break;
                default:
                    System.out.println("Unknown command: " + cmdName);
                    break;
            }
        }
    }

    private String[] curateInstructions(String[] instructions) {
        ArrayList<String> curated = new ArrayList<>();

        for (String line : instructions) {
            String curatedLine = line.split("#")[0].trim();
            if (curatedLine.length() > 0) {
                curated.add(curatedLine);
            }
        }

        return curated.toArray(new String[0]);
    }

    private List<JunoVariable> replaceAllVars(String[] strings, String currentScope) {
        List<JunoVariable> output = new ArrayList<>();
        for (String string : strings) {
            output.addAll(this.replaceVars(string, currentScope));
        }
        return output;
    }

    private List<JunoVariable> replaceVars(String cmdPart, String currentScope) {
        String[] indicator = {"&", "\\*"};
        String[] scopes = {currentScope, "global"};
        List<JunoVariable> input = new ArrayList<>();
        input.add(new JunoVariable(cmdPart, "s"));

        List<JunoVariable> output = new ArrayList<>();

        for (int i = 0; i < indicator.length; i++) {
            for (JunoVariable var : input) {
                if (!var.getType().equals("s")) {
                    output.add(var);
                    continue;
                }
                String[] splitted = ((String) var.get()).split(indicator[i]);
                boolean isNextVar = false;
                for (String part : splitted) {
                    if (isNextVar) {
                        isNextVar = false;

                        if (!this.variables.get(scopes[i]).has(part)) {
                            output.add(new JunoVariable(indicator[i] + part, "s"));
                            continue;
                        }

                        output.add(this.variables.get(scopes[i]).getRaw(part));
                    } else if (!isNextVar && part.equals("")) {
                        isNextVar = true;
                    } else {
                        output.add(new JunoVariable(part, "s"));
                    }
                }
            }
            input = output;
            output = new ArrayList<>();
        }
        return input;
    }

    private String[] reuniteStrings(String[] strings) {
        ArrayList<String> reunited = new ArrayList<>();
        ArrayList<String> buffer = new ArrayList<>();
        boolean inString = false;

        for (String string : strings) {
            if (inString) {
                buffer.add(string);
                if (string.endsWith("\"")) {
                    inString = false;
                    String tmp = String.join(" ", buffer);
                    tmp = tmp.substring(1, tmp.length() - 1);
                    reunited.add(tmp);
                    buffer.clear();
                }
            } else if (string.startsWith("\"")) {
                inString = true;
                buffer.add(string);
            } else {
                reunited.add(string);
            }
        }

        if (buffer.size() > 0) {
            System.out.println("Invalid string: " + String.join(" ", buffer));
            return strings;
        }
        return reunited.toArray(new String[0]);
    }
}
