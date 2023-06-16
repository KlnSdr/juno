package interpreter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Interpreter {
    public static final String[] blacklistFunctionAndScopeNames = {"main", "global", "loop"};
    public final HashMap<String, JunoFunction> functions = new HashMap<>();
    public final HashMap<String, JunoScope> variables = new HashMap<>();
    private final int maxCalls;
    private int calls = 0;
    private boolean isUnsafe = false;
    private InterpreterMode mode = InterpreterMode.NORMAL;
    private String scp;

    public Interpreter() {
        maxCalls = 100_000;
    }

    public Interpreter(int maxCalls) {
        this.maxCalls = maxCalls;
    }

    public void run(String[] program) {
        functions.put("main", new JunoFunction(new String[0], Util.curateInstructions(program)));
        variables.put("main", new JunoScope());

        variables.put("global", new JunoScope());
        scp = "main";
        this.runFunction("main");
    }

    public void runFile(String path) {
        run(Util.readFile(path));
    }

    private void runFunction(String functionName) {
        if (!functions.containsKey(functionName)) {
            System.out.println("Function " + functionName + " does not exist.");
            return;
        }

        ArrayList<String> instructionBuffer = new ArrayList<>();
        ArrayList<String> paramBuffer = new ArrayList<>();
        String bufferFunctionName = "";
        InterpreterMode prevMode = mode;

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
            } else if (mode == InterpreterMode.IGNORE_IF) {
                if (cmd.equalsIgnoreCase("fi")) {
                    mode = prevMode;
                }
                continue;
            } else if (mode == InterpreterMode.RECORD_LOOP) {
                if (cmd.equalsIgnoreCase("pool")) {
                    this.functions.put("loop", new JunoFunction(new String[0], instructionBuffer.toArray(new String[0])));
                    this.mode = InterpreterMode.LOOP;
                    while (this.mode == InterpreterMode.LOOP && this.calls <= this.maxCalls) {
                        runFunction("loop");
                    }
                    this.functions.remove("loop");
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
                case "unsafe":
                    isUnsafe = true;
                    break;
                case "if":
                    if (processedCmd.size() < 4) {
                        System.out.println("Invalid if command: " + cmd);
                        break;
                    }
                    // info: all values are treated as floats "for now" (tm)
                    Float val1 = Float.parseFloat(processedCmd.get(1).get().toString());
                    Float val2 = Float.parseFloat(processedCmd.get(3).get().toString());
                    String op = processedCmd.get(2).get().toString();
                    boolean result = false;
                    ArrayList<Boolean> results = new ArrayList<>();
                    boolean invertResult = false;

                    for (String operationSymbol : op.split("")) {
                        switch (operationSymbol) {
                            case "=":
                                results.add(val1.equals(val2));
                                break;
                            case ">":
                                results.add(val1 > val2);
                                break;
                            case "<":
                                results.add(val1 < val2);
                                break;
                            case "!":
                                invertResult = true;
                                break;
                            default:
                                System.out.println("Unknown operation symbol: " + operationSymbol);
                                break;
                        }
                    }

                    result = results.contains(true);
                    result = invertResult ^ result; // same as invertResult != result, but i just WANT to use the XOR operator, sorry not sorry
                    if (!result) {
                        prevMode = this.mode;
                        this.mode = InterpreterMode.IGNORE_IF;
                    }
                    break;
                case "fi":
                    // just so there is no "Unknown command" written to the console
                    break;
                case "break":
                    this.mode = InterpreterMode.NORMAL;
                    break;
                case "loop":
                    prevMode = this.mode;
                    this.mode = InterpreterMode.RECORD_LOOP;
                    instructionBuffer = new ArrayList<>();
                    break;
                case "ld":
                    if (processedCmd.size() < 2) {
                        System.out.println("Invalid ld command: " + cmd);
                        break;
                    }
                    String fileName = processedCmd.get(1).get().toString();
                    runFile(fileName);
                    break;
                default:
                    System.out.println("Unknown command: " + cmdName);
                    break;
            }
            if (!isUnsafe) {
                this.calls++;
            }
            if (this.calls > this.maxCalls) {
                System.out.println("Program exceeded maximum number of calls (" + this.maxCalls + ").");
                return;
            }
        }
        // System.out.println(this.calls);
    }
}
