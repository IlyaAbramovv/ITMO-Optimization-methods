package lab1.functions;

public class Arccos extends SingleArgumentFunction {
    public Arccos(Function function) {
        super(function);
    }

    @Override
    double makeOperation(double x) {
        if (x > 1 || x < -1) {
            throw new InvalidEvaluationException("Arguments of arccos must be in [-1; 1]");
        }
        return Math.acos(x);
    }
}
