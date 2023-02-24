package lab1.functions;

public class Multiply extends DoubleArgumentFunction {
    public Multiply(Function function1, Function function2) {
        super(function1, function2);
    }

    @Override
    double makeOperation(double x, double y) {
        return x * y;
    }
}
