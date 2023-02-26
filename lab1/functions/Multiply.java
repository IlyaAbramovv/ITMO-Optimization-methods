package lab1.functions;

public class Multiply extends DoubleArgumentFunction {
    public Multiply(Function function1, Function function2) {
        super(function1, function2);
    }

    @Override
    double makeOperation(double x, double y) {
        return x * y;
    }

    @Override
    public Function differentiate(String d) {
        return new Add(new Multiply(function1, function2.differentiate(d)), new Multiply(function1.differentiate(d), function2));
    }
}
