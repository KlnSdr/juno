import interpreter.Interpreter;

public class Main {
    public static void main(String[] args) {
        Interpreter interpreter = new Interpreter();
        interpreter.run(new String[]{"set a i 10", "set b i 20", "add a &a &b", "set str s \"Hello World\"", "set str2 s \"test", "set float f 5", "set flt F 7.4", "out \"hello world du focker\nline two\" \"hier geht es weiter\""});
        System.out.println("Program finished.");
    }
}
