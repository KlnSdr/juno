import interpreter.Interpreter;

public class Main {
    public static void main(String[] args) {
        Interpreter interpreter = new Interpreter();
        interpreter.run(new String[]{
                "set a i 10",
                "set b i 20",/*
                "add a &a &b",
                "set str s \"Hello World\"",
                "set str2 s \"test",
                "set float f 5",
                "set flt F 7.4",
                "out \"hello world du focker\nline two\" \"hier geht es weiter\"",
                "set _counter i 42",
                "out &a \" / \" *counter",
                "set c f 2.5",
                "set d f 2.5",
                "add e &c &d",
                "out &e",
                "sub e &e 3.45",
                "out &e",
                "scp test",
                "set a i 1",
                "out &a",
                "dscp test",
                "scp main",
                "out &a",
                "dscp global",
                "dscp nonextistent",
                "out &a &z",
                "prg a",
                "prg z",
                "out &a &z",
                "mir m b",
                "mir n y",
                "set txt s \"Hallo \"",
                "out &txt",
                "con txt &txt \"Welt\"",
                "out &txt",*/
                "dec fib > a:i b:i",
                "   add c &a &b",
                "   out &a \" + \" &b \" = \" &c",
                "dn",
                "!fib &a &b",
                "add a &a &b",
                "!fib &a &b",
                "dec sayHello > name:s age:i",
                "   out callScope: &callScope",
                "   out \"Hello, \" &name \"!\"",
                "   sub age &age 18",
                "   # todo multiply with -1",
                "   out \"In \" &age \" years you will be 18 years old.\"",
                "dn",
                "!sayHello \"John Doe\" 12",
                "if 1 !<= 2",
                "   out \"1 <= 2\"",
                "fi",
                "out \"this is the end my only friend the end\""});
        System.out.println("Program finished.");
    }
}
