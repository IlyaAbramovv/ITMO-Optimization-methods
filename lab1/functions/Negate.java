package lab1.functions;

public class Negate extends SingleArgumentFunction {
    public Negate(Function function) {
        super(function);
    }

    @Override
    double makeOperation(double x) {
        return -x;
    }
}
