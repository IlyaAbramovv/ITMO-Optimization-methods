package lab1;

import lab1.functions.*;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Function function = new Sum(List.of(
                new Pow(new Variable("x"), new Const(2.0)),
                new Pow(new Variable("y"), new Const(2.0)),
                new Negate(new Multiply(new Const(3.0), new Variable("y"))),
                new Multiply(new Const(2.0), new Variable("x")),
                new Multiply(new Variable("x"), new Variable("y")),
                new Const(4.0)
        ));
        var ans = GradientDescent.gradientDescent(function, true);
        for (var step : ans) {
            for (var beb : step.entrySet()) {
                System.out.print(beb.getKey() + ": " + beb.getValue() + ", ");
            }
            System.out.println();
        }
    }
}
