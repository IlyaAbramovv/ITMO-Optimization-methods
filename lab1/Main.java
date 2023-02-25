package lab1;

import lab1.functions.*;
import lab1.paint.Painter;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Function function = new Add(new Arctg(new Variable("x")),
                new Exp(new Sin(new Multiply(new Const(2.0),new Variable("x"))))
        );
        Painter.drawFunction(function);
    }
}
