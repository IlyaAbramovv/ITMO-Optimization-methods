package lab1.functions;

public class Add extends DoubleArgumentFunction {
    public Add(Function function1, Function function2) {
        super(function1, function2);
    }

    @Override
    double makeOperation(double x, double y) {
        return x + y;
    }
}
