package lab1.functions;

public class Arctg extends SingleArgumentFunction {
    public Arctg(Function function) {
        super(function);
    }

    @Override
    double makeOperation(double x) {
        return Math.atan(x);
    }
}
