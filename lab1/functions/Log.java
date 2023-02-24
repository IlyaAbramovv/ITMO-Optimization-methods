package lab1.functions;

public class Log extends DoubleArgumentFunction {
    public Log(Function function1, Function function2) {
        super(function1, function2);
    }

    @Override
    double makeOperation(double x, double y) {
        if (x == 1) {
            throw new InvalidEvaluationException("Base of the logarithm can't be equals to 1");
        }
        if (x <= 0) {
            throw new InvalidEvaluationException("Base of the logarithm must be greater than 0");
        }
        if (y <= 0) {
            throw new InvalidEvaluationException("Argument of the logarithm must be greater than 0");
        }
        return Math.log(y) / Math.log(x);
    }
}
