package lab1.functions;

public class Arcsin extends SingleArgumentFunction {
    public Arcsin(Function function) {
        super(function);
    }

    @Override
    double makeOperation(double x) {
        if (x > 1 || x < -1) {
            throw new InvalidEvaluationException("Arguments of arcsin must be in [-1; 1]");
        }
        return Math.asin(x);
    }
}
