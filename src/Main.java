import interpreter.Interpreter;

public class Main {
    public static void main(String[] args) {
        Interpreter interpreter = new Interpreter();
        interpreter.run(new String[]{"set a i 10", "set b i 20", "add a &a &b", "set str s \"Hello World\"", "set str2 s \"test", "set float f 5", "set flt F 7.4", "out \"hello world du focker\nline two\" \"hier geht es weiter\"", "set _counter i 42", "out &a \" / \" *counter", "set c f 2.5", "set d f 2.5", "add e &c &d", "out &e", "sub e &e 3.45", "out &e"});
        System.out.println("Program finished.");
    }
}
