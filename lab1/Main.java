package lab1;

import lab1.functions.*;
import lab1.paint.Painter;

public class Main {
    public static void main(String[] args) {
        Function function = new Add(new Arctg(new Variable("x")),
                new Exp(new Sin(new Multiply(new Const(2.0), new Variable("x"))))
        );
        var res = FunctionUtils.gradientDescent(function);
        System.out.println("x" + ": " + res.get("x"));
        System.out.println("y" + ": " + function.evaluate(res));
        Painter.drawFunction(function);
    }
}
