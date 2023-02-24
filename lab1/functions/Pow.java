package lab1.functions;

public class Pow extends DoubleArgumentFunction {
    public Pow(Function function1, Function function2) {
        super(function1, function2);
    }

    @Override
    double makeOperation(double x, double y) {
        return Math.pow(x, y);
    }
}
