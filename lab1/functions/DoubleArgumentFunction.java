package lab1.functions;

import java.util.Map;

public abstract class DoubleArgumentFunction implements Function {
    private final Function function1, function2;

    public DoubleArgumentFunction(Function function1, Function function2) {
        this.function1 = function1;
        this.function2 = function2;
    }

    @Override
    public double evaluate(Map<String, Double> map) {
        return makeOperation(function1.evaluate(map), function2.evaluate(map));
    }

    abstract double makeOperation(double x, double y);
}
