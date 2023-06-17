import interpreter.Interpreter;

public class Main {
    private static final String version = "1.1.0";

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("       _   _    _   _   _    ____  ");
            System.out.println("      | | | |  | | | \\ | |  / __ \\ ");
            System.out.println("      | | | |  | | |  \\| | | |  | |");
            System.out.println("  _   | | | |  | | | . ` | | |  | |");
            System.out.println(" | |__| | | |__| | | |\\  | | |__| |");
            System.out.println("  \\____/   \\____/  |_| \\_|  \\____/ ");
            System.out.println("v." + version);
            System.out.println("Usage: java -jar <path to juno.jar> [options]");
            System.out.println("Options:");
            System.out.println("  --calls, -c <value>   Set the maximum number of calls");
            System.out.println("  --file, -f <name>     Set the filename");
            System.exit(0);
        }

        int maxCalls = 100_000;
        String fileName = "";

        // Parsing command line arguments
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("--calls") || arg.equals("-c")) {
                // Make sure there is another argument available
                if (i + 1 < args.length) {
                    maxCalls = Integer.parseInt(args[i + 1]);
                    i++; // Skip the next argument since it's the value
                } else {
                    System.err.println("Missing value for maxCalls argument.");
                }
            } else if (arg.equals("--file") || arg.equals("-f")) {
                // Make sure there is another argument available
                if (i + 1 < args.length) {
                    fileName = args[i + 1];
                    i++; // Skip the next argument since it's the value
                } else {
                    System.err.println("Missing value for fileName argument.");
                }
            }
        }

        Interpreter interpreter = new Interpreter(maxCalls);
        interpreter.runFile(fileName);
    }
}
