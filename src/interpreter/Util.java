package interpreter;

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
                            output.add(new JunoVariable(indicator[i] + part, "s"));
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

        if (buffer.size() > 0) {
            System.out.println("Invalid string: " + String.join(" ", buffer));
            return strings;
        }
        return reunited.toArray(new String[0]);
    }
}
