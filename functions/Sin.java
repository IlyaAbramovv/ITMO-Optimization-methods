package functions;

public class Sin extends SingleArgumentFunction {
    public Sin(Function function) {
        super(function);
    }

    @Override
    double makeOperation(double x) {
        return Math.sin(x);
    }

    @Override
    public Function differentiate(String d) {
        return new Multiply(new Cos(function), function.differentiate(d));
    }
}
