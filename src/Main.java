import interpreter.Interpreter;

public class Main {
    public static void main(String[] args) {
        Interpreter interpreter = new Interpreter();
        interpreter.runFile("main.juno");
    }
}
