package lab1;

import lab1.functions.*;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Function function = new Add(new Multiply(new Const(100.0),new Pow(new Subtract(new Variable("y"), new Pow(new Variable("x"), new Const(2.0))),new Const(2.0))),
                new Pow(new Subtract(new Const(1.0), new Variable("x")), new Const(2.0)));
        Function function1 = new Multiply(
                new Sin(new Sum(List.of(new Multiply(new Const(0.5), new Pow(new Variable("x"), new Const(2.0))), new Const(3.0),
                        new Negate(new Multiply(new Const(0.25), new Pow(new Variable("y"), new Const(2.0))))))),
                new Cos(new Sum(List.of(new Multiply(new Const(2.0), new Variable("x")), new Const(1.0), new Negate( new Exp(new Variable("y")))))));
        Map<String, Double> minimum = FunctionUtils.gradientDescent(function);
        System.out.println(minimum);
        System.out.println(function.evaluate(minimum));
    }
}
