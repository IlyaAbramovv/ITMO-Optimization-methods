package lab1.functions;

public class Cos extends SingleArgumentFunction {
    public Cos(Function function) {
        super(function);
    }

    @Override
    double makeOperation(double x) {
        return Math.cos(x);
    }
}
