package interpreter;

import java.util.HashMap;
import java.util.List;

public class Interpreter {
    public static final String[] blacklistFunctionAndScopeNames = {"main", "global"};
    public final HashMap<String, JunoFunction> functions = new HashMap<>();
    public final HashMap<String, JunoScope> variables = new HashMap<>();

    public void run(String[] program) {
        functions.put("main", new JunoFunction(new String[0], Util.curateInstructions(program)));
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
            List<JunoVariable> processedCmd = Util.replaceAllVars(Util.reuniteStrings(cmd.split(" ")), scp, this);
            String cmdName = (String) processedCmd.get(0).get();

            switch (cmdName.toLowerCase()) {
                case "set":
                    if (processedCmd.size() != 4) {
                        System.out.println("Invalid set command: " + cmd);
                        break;
                    }
                    String saveScp = scp;
                    if (((String) processedCmd.get(1).get()).startsWith("_")) {
                        processedCmd.get(1).update(((String) processedCmd.get(1).get()).substring(1));
                        saveScp = "global";
                    }

                    System.out.println("Setting " + processedCmd.get(1).get() + "(" + processedCmd.get(2).get() + ")" + " to " + processedCmd.get(3).get());
                    variables.get(saveScp).add((String) processedCmd.get(1).get(), (String) processedCmd.get(3).get(), ((String) processedCmd.get(2).get()).toLowerCase());
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
                    Math.add(processedCmd, scp, this);
                    break;
                case "sub":
                    if (processedCmd.size() < 4) {
                        System.out.println("Invalid add command: " + cmd);
                        break;
                    }
                    Math.subtract(processedCmd, scp, this);
                    break;
                case "scp":
                    if (processedCmd.size() != 2) {
                        System.out.println("Invalid scp command: " + cmd);
                        break;
                    }
                    scp = (String) processedCmd.get(1).get();
                    if (!this.variables.containsKey(scp)) {
                        this.variables.put(scp, new JunoScope());
                    }
                    break;
                case "dscp":
                    if (processedCmd.size() != 2) {
                        System.out.println("Invalid dscp command: " + cmd);
                        break;
                    }
                    String scpToDelete = (String) processedCmd.get(1).get();
                    if (scpToDelete.equals("global")) {
                        System.out.println("Cannot delete global scope.");
                        break;
                    }

                    this.variables.remove(scpToDelete);
                    break;
                case "prg":
                    if (processedCmd.size() != 2) {
                        System.out.println("Invalid prg command: " + cmd);
                        break;
                    }
                    String varToDelete = (String) processedCmd.get(1).get();
                    String scpToPrg = scp;
                    if (varToDelete.startsWith("_")) {
                        varToDelete = varToDelete.substring(1);
                        scpToPrg = "global";
                    }
                    variables.get(scpToPrg).remove(varToDelete);
                    break;
                default:
                    System.out.println("Unknown command: " + cmdName);
                    break;
            }
        }
    }
}
