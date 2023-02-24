package lab1.functions;

public class Ln extends SingleArgumentFunction {
    public Ln(Function function) {
        super(function);
    }

    @Override
    double makeOperation(double x) {
        if (x <= 0) {
            throw new InvalidEvaluationException("Argument of the logarithm must be greater than 0");
        }
        return Math.log(x);
    }
}
