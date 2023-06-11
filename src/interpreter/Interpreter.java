package interpreter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Interpreter {
    public static final String[] blacklistFunctionAndScopeNames = {"main", "global"};
    public final HashMap<String, JunoFunction> functions = new HashMap<>();
    public final HashMap<String, JunoScope> variables = new HashMap<>();
    private InterpreterMode mode = InterpreterMode.NORMAL;
    private String scp;

    public void run(String[] program) {
        functions.put("main", new JunoFunction(new String[0], Util.curateInstructions(program)));
        variables.put("main", new JunoScope());

        variables.put("global", new JunoScope());
        scp = "main";
        this.runFunction("main");
    }

    private void runFunction(String functionName) {
        if (!functions.containsKey(functionName)) {
            System.out.println("Function " + functionName + " does not exist.");
            return;
        }

        ArrayList<String> instructionBuffer = new ArrayList<>();
        ArrayList<String> paramBuffer = new ArrayList<>();
        String bufferFunctionName = "";

        JunoFunction function = functions.get(functionName);

        for (String cmd : function.getInstructions()) {
            if (mode == InterpreterMode.FUNCTION) {
                if (cmd.equalsIgnoreCase("dn")) {
                    if (Arrays.stream(Interpreter.blacklistFunctionAndScopeNames).noneMatch(bufferFunctionName::equalsIgnoreCase)) {
                        this.functions.put(bufferFunctionName, new JunoFunction(paramBuffer.toArray(new String[0]), instructionBuffer.toArray(new String[0])));
                    } else {
                        System.out.println("Function " + bufferFunctionName + " is a reserved function name.");
                    }
                    mode = InterpreterMode.NORMAL;
                    instructionBuffer.clear();
                    paramBuffer.clear();
                    bufferFunctionName = "";
                    continue;
                }
                instructionBuffer.add(cmd);
                continue;
            }

            List<JunoVariable> processedCmd = Util.replaceAllVars(Util.reuniteStrings(cmd.split(" ")), scp, this);
            String cmdName = (String) processedCmd.get(0).get();

            if (cmdName.startsWith("!")) {
                cmdName = cmdName.substring(1);
                if (!this.functions.containsKey(cmdName)) {
                    System.out.println("Function " + cmdName + " does not exist.");
                    continue;
                }

                if (!this.variables.containsKey(cmdName)) {
                    this.variables.put(cmdName, new JunoScope());
                }
                String oldScp = scp;
                scp = cmdName;
                String[] params = this.functions.get(cmdName).getParams();
                if (processedCmd.size() - 1 != params.length) {
                    System.out.println("Invalid number of parameters for function " + cmdName + ". Expected " + params.length + ", got " + (processedCmd.size() - 2) + ".");
                    continue;
                }
                for (int i = 0; i < params.length; i++) {
                    String varName = params[i].substring(0, params[i].lastIndexOf(":"));
                    JunoTuple<JunoVariable, String> var = Util.convertParamToType(params[i], processedCmd.get(i + 1));
                    this.variables.get(cmdName).add(varName, var._1().get().toString(), var._2());
                }
                this.variables.get(cmdName).add("callScope", oldScp, "s");
                this.runFunction(cmdName);
                scp = oldScp;
                continue;
            }

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
                case "mir":
                    if (processedCmd.size() != 3) {
                        System.out.println("Invalid mir command: " + cmd);
                        break;
                    }
                    String targetName = (String) processedCmd.get(1).get();
                    String sourceName = (String) processedCmd.get(2).get();
                    JunoVariable sourceVar = variables.get(scp).getRaw(sourceName);
                    if (sourceVar == null) {
                        System.out.println("Variable " + sourceName + " does not exist.");
                        break;
                    }
                    String scpToMir = scp;

                    if (targetName.startsWith("_")) {
                        targetName = targetName.substring(1);
                        scpToMir = "global";
                    }

                    this.variables.get(scpToMir).add(targetName, sourceVar.get().toString(), sourceVar.getType());
                    break;
                case "con":
                    if (processedCmd.size() < 3) {
                        System.out.println("Invalid con command: " + cmd);
                        break;
                    }

                    String targetVar = (String) processedCmd.get(1).get();
                    String scpToCon = scp;
                    String acc = "";
                    if (targetVar.startsWith("_")) {
                        targetVar = targetVar.substring(1);
                        scpToCon = "global";
                    }

                    for (int i = 2; i < processedCmd.size(); i++) {
                        acc += processedCmd.get(i).get().toString();
                    }
                    this.variables.get(scpToCon).add(targetVar, acc, "s");
                    break;
                case "dec":
                    if (processedCmd.size() < 2) {
                        System.out.println("Invalid dec command: " + cmd);
                        break;
                    }
                    this.mode = InterpreterMode.FUNCTION;
                    bufferFunctionName = processedCmd.get(1).get().toString();

                    if (processedCmd.size() > 3 && processedCmd.get(2).get().toString().equals(">")) {
                        for (int i = 3; i < processedCmd.size(); i++) {
                            paramBuffer.add(processedCmd.get(i).get().toString());
                        }
                    }
                    break;
                case "end":
                    return;
                default:
                    System.out.println("Unknown command: " + cmdName);
                    break;
            }
        }
    }
}
