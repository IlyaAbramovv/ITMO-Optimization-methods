package lab1.functions;

public class Sin extends SingleArgumentFunction {
    public Sin(Function function) {
        super(function);
    }

    @Override
    double makeOperation(double x) {
        return Math.sin(x);
    }
}
