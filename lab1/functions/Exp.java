package lab1.functions;

public class Exp extends SingleArgumentFunction {
    public Exp(Function function) {
        super(function);
    }

    @Override
    double makeOperation(double x) {
        return Math.exp(x);
    }
}
