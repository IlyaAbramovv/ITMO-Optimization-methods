package lab1.functions;

public class Sqrt extends SingleArgumentFunction {
    public Sqrt(Function function) {
        super(function);
    }

    @Override
    double makeOperation(double x) {
        if (x < 0) {
            throw new InvalidEvaluationException("Argument of square root must be greater than or equal to zero");
        }
        return Math.sqrt(x);
    }
}
