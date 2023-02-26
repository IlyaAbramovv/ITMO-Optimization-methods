package lab1;

import lab1.functions.*;

import java.util.List;


public class Main {
    public static void main(String[] args) {
        Function function = new Multiply(
                new Add(new Variable("x"), new Const(1.0)),
                new Cos(new Multiply(new Const(4.0), new Arctan(new Variable("x"))))
        );
        var ans = FunctionUtils.gradientDescent(function);
        for (var entry : ans.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("Minimum: " + function.evaluate(ans));
        System.out.println(FunctionUtils.goldenRatio(function, -10, 10));
    }
}
