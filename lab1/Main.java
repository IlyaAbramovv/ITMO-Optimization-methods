package lab1;

import lab1.functions.*;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Function function = new Sum(List.of(new Pow(new Variable("x"), new Const(2.0)),
                new Multiply(new Const(2.0), new Pow(new Variable("y"), new Const(2.0))),
                new Exp(new Add(new Variable("x"), new Variable("y")))));
        Map<String, Double> minimum = FunctionUtils.gradientDescent(function);
        System.out.println(minimum);
        System.out.println(function.evaluate(minimum));
    }
}
