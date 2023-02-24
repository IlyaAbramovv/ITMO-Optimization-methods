package lab1.functions;

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
}
