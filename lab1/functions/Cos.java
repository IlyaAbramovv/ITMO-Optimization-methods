package lab1.functions;

public class Cos extends SingleArgumentFunction {
    public Cos(Function function) {
        super(function);
    }

    @Override
    double makeOperation(double x) {
        return Math.cos(x);
    }

    @Override
    public Function differentiate(String d) {
        return new Multiply(new Negate(new Sin(function)), function.differentiate(d));
    }
}
