package lab1.functions;

public class Divide extends DoubleArgumentFunction {
    public Divide(Function function1, Function function2) {
        super(function1, function2);
    }

    @Override
    double makeOperation(double x, double y) {
        if (y == 0.0) {
            throw new InvalidEvaluationException("Division by zero");
        }
        return x / y;
    }

    @Override
    public Function differentiate(String d) {
        return new Divide(new Subtract(new Multiply(function1.differentiate(d), function2), new Multiply(function2.differentiate(d), function1)),
                new Pow(function2, new Const(2.0)));
    }
}
