package lab1;

import lab1.functions.*;

import java.util.Map;


public class Main {
    public static void main(String[] args) {
        Function function = new Log(new Sin(new Variable("x")), new Cos(new Variable("x"))).differentiate("x");
        System.out.println(function.evaluate(Map.of("x", 0.5)));
        System.out.println(function.evaluate(Map.of("x", 0.1)));
        var ans = FunctionUtils.gradientDescent(function);
        for (var entry : ans.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("Minimum: " + function.evaluate(ans));
        System.out.println(FunctionUtils.goldenRatio(function, -10, 10));
    }
}
