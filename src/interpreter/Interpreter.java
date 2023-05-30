package interpreter;

import java.util.ArrayList;
import java.util.HashMap;

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
            String[] splitCmd = cmd.split(" ");
            String cmdName = splitCmd[0];

            switch (cmdName.toLowerCase()) {
                case "set":
                    if (splitCmd.length != 4) {
                        System.out.println("Invalid set command: " + cmd);
                        break;
                    }
                    System.out.println("Setting " + splitCmd[1] + "(" + splitCmd[2] + ")" + " to " + splitCmd[3]);
                    variables.get(scp).add(splitCmd[1], splitCmd[3], splitCmd[2]);
                    break;
                case "get":
                    break;
                default:
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
}
