import interpreter.Interpreter;

public class Main {
    public static void main(String[] args) {
        Interpreter interpreter = new Interpreter();
        interpreter.run(new String[]{"set a i 10", "set b i 20", "add a &a &b"});
        System.out.println("Program finished.");
    }
}
