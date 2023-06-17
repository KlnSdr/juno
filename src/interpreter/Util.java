package interpreter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Util {
    public static String[] curateInstructions(String[] instructions) {
        ArrayList<String> curated = new ArrayList<>();

        for (String line : instructions) {
            String curatedLine = line.split("#")[0].trim();
            if (curatedLine.length() > 0) {
                curated.add(curatedLine);
            }
        }

        return curated.toArray(new String[0]);
    }

    public static List<JunoVariable> replaceAllVars(String[] strings, String currentScope, Interpreter interpreter) {
        List<JunoVariable> output = new ArrayList<>();
        for (String string : strings) {
            output.addAll(Util.replaceVars(string, currentScope, interpreter));
        }
        return output;
    }

    public static List<JunoVariable> replaceVars(String cmdPart, String currentScope, Interpreter interpreter) {
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

                        if (!interpreter.variables.get(scopes[i]).has(part)) {
                            output.add(new JunoVariable(part, "s"));
                            continue;
                        }

                        output.add(interpreter.variables.get(scopes[i]).getRaw(part));
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

    public static String[] reuniteStrings(String[] strings) {
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

        if (buffer.size() > 0 && buffer.get(buffer.size() - 1).endsWith("\"")) {
            String tmp = String.join(" ", buffer);
            tmp = tmp.substring(1, tmp.length() - 1);
            reunited.add(tmp);
            buffer.clear();
        } else if (buffer.size() > 0) {
            reunited.addAll(buffer);
        }
        return reunited.toArray(new String[0]);
    }

    public static JunoTuple<JunoVariable, String> convertParamToType(String rawParam, JunoVariable argument) {
        String[] splitParam = rawParam.split(":");
        if (splitParam.length < 2) {
            System.out.println("could not extract type info from param: " + rawParam);
            return new JunoTuple<>(argument, "s");
        }
        switch (splitParam[splitParam.length - 1]) {
            case "i":
                return new JunoTuple<>(new JunoVariable<>(Integer.parseInt(argument.get().toString()), "i"), "i");
            case "s":
                return new JunoTuple<>(argument, "s");
            case "f":
                return new JunoTuple<>(new JunoVariable<>(Float.parseFloat(argument.get().toString()), "f"), "f");
            default:
                System.out.println("unknown type: " + argument.getType() + " for variable " + rawParam);
                return new JunoTuple<>(argument, "s");
        }
    }

    public static String[] readFile(String path) {
        ArrayList<String> output = new ArrayList<>();
        try (BufferedReader buffer = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = buffer.readLine()) != null) {
                output.add(line);
            }
        } catch (Exception e) {
            System.out.println("could not read file: " + path);
        }
        return output.toArray(new String[0]);
    }

    public static boolean performIf(JunoVariable val1, JunoVariable val2, String operator) {
        switch (val1.getType()) {
            case "i":
                return intIf(val1, val2, operator);
            case "f":
                return floatIf(val1, val2, operator);
            case "s":
                return stringIf(val1, val2, operator);
            default:
                System.out.println("unknown type: " + val1.getType());
                return false;
        }
    }

    private static boolean intIf(JunoVariable val1, JunoVariable val2, String operator) {
        ArrayList<Boolean> results = new ArrayList<>();
        boolean result = false;
        boolean isInverted = false;

        for (String operationSymbol : operator.split("")) {
            switch (operationSymbol) {
                case "!":
                    isInverted = !isInverted;
                    break;
                case "=":
                    results.add(val1.get().equals(val2.get()));
                    break;
                case ">":
                    results.add((int) val1.get() > (int) val2.get());
                    break;
                case "<":
                    results.add((int) val1.get() < (int) val2.get());
                    break;
                default:
                    System.out.println("unknown operator: " + operationSymbol);
                    return false;
            }
        }

        result = results.contains(true);
        return isInverted != result;
    }

    private static boolean floatIf(JunoVariable val1, JunoVariable val2, String operator) {
        ArrayList<Boolean> results = new ArrayList<>();
        boolean result = false;
        boolean isInverted = false;

        for (String operationSymbol : operator.split("")) {
            switch (operationSymbol) {
                case "!":
                    isInverted = !isInverted;
                    break;
                case "=":
                    results.add(val1.get().equals(val2.get()));
                    break;
                case ">":
                    results.add((float) val1.get() > (float) val2.get());
                    break;
                case "<":
                    results.add((float) val1.get() < (float) val2.get());
                    break;
                default:
                    System.out.println("unknown operator: " + operationSymbol);
                    return false;
            }
        }

        result = results.contains(true);
        return isInverted != result;
    }

    private static boolean stringIf(JunoVariable val1, JunoVariable val2, String operator) {
        ArrayList<Boolean> results = new ArrayList<>();
        boolean result = false;
        boolean isInverted = false;

        for (String operationSymbol : operator.split("")) {
            switch (operationSymbol) {
                case "!":
                    isInverted = !isInverted;
                    break;
                case "=":
                    results.add(val1.get().equals(val2.get()));
                    break;
                case ">":
                    results.add(((String) val1.get()).length() > ((String) val2.get()).length());
                    break;
                case "<":
                    results.add(((String) val1.get()).length() < ((String) val2.get()).length());
                    break;
                default:
                    System.out.println("unknown operator: " + operationSymbol);
                    return false;
            }
        }

        result = results.contains(true);
        return isInverted != result;
    }
}
