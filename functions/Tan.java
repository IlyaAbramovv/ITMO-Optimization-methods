package functions;

public class Tan extends SingleArgumentFunction {
    public Tan(Function function) {
        super(function);
    }

    @Override
    double makeOperation(double x) {
        if (x % (Math.PI / 2) == 0) {
            throw new InvalidEvaluationException("Division by zero");
        }
        return Math.tan(x);
    }

    @Override
    public Function differentiate(String d) {
        return new Divide(function.differentiate(d), new Pow(new Cos(function), new Const(2.0)));
    }
}
