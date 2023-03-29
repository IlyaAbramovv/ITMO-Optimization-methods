package functions;

public class Negate extends SingleArgumentFunction {
    public Negate(Function function) {
        super(function);
    }

    @Override
    double makeOperation(double x) {
        return -x;
    }

    @Override
    public Function differentiate(String d) {
        return new Negate(function.differentiate(d));
    }
}
