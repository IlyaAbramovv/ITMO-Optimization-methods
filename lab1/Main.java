package lab1;

import lab1.functions.*;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Function function = new Arccos(new Variable("x1"));
        System.out.println(function.evaluate(Map.of("x1", -0.0, "x2", -0.125)));
    }
}
