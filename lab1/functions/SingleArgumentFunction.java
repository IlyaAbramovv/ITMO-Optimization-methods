package lab1.functions;

import java.util.Map;

public abstract class SingleArgumentFunction implements Function {
    private final Function function;

    protected SingleArgumentFunction(Function function) {
        this.function = function;
    }

    @Override
    public double evaluate(Map<String, Double> map) {
        return makeOperation(function.evaluate(map));
    }

    abstract double makeOperation(double x);

    public Function getFunction() {
        return function;
    }
}
