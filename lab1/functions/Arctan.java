package lab1.functions;

public class Arctan extends SingleArgumentFunction {
    public Arctan(Function function) {
        super(function);
    }

    @Override
    double makeOperation(double x) {
        return Math.atan(x);
    }

    @Override
    public Function differentiate(String d) {
        return new Divide(function.differentiate(d), new Add(new Const(1.0), new Pow(function, new Const(2.0))));
    }
}
