package lab1.functions;

import java.util.List;

public class Pow extends DoubleArgumentFunction {
    public Pow(Function function1, Function function2) {
        super(function1, function2);
    }

    @Override
    double makeOperation(double x, double y) {
        return Math.pow(x, y);
    }

    @Override
    public Function differentiate(String d) {
        if (function2 instanceof Const) {
            return new Mult(List.of(function1.differentiate(d), function2, new Pow(function1, new Const(((Const) function2).getValue() - 1))));
        } else if (function1 instanceof Const) {
            return new Mult(List.of(this, function2.differentiate(d), new Ln(function1)));
        } else {
            return new Multiply(this, new Multiply(function2, new Ln(function1)).differentiate(d));
        }
    }
}
