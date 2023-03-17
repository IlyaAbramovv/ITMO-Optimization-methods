package lab1;

import lab1.functions.*;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        /*arctan(x)(x-4)*/
        Function f1 = new Multiply(new Subtract(new Variable("x"), new Const(4.0)), new Arctan(new Variable("x")));
        var res1 = Minimization.gradientDescent(f1, Mode.WOLFE_CONDITIONS);
        for (var entry : res1.get(res1.size() - 1).entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println();
        /*(x1 - 3)^2 + (x2 - 5)^2 + (x3  - 4)^2 + (x4 + 19)^2 + x5^2*/
        Function f2 = new Sum(List.of(
                new Pow(new Subtract(new Variable("x1"), new Const(3.0)), new Const(2.0)),
                new Pow(new Subtract(new Variable("x2"), new Const(5.0)), new Const(2.0)),
                new Pow(new Subtract(new Variable("x3"), new Const(4.0)), new Const(2.0)),
                new Pow(new Add(new Variable("x4"), new Const(19.0)), new Const(2.0)),
                new Pow(new Variable("x5"), new Const(2.0))
        ));
        var res2 = Minimization.gradientDescent(f2, Mode.WOLFE_CONDITIONS);
        for (var entry : res2.get(res2.size() - 1).entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
