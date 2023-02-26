package lab1.functions;

import java.util.List;
import java.util.Map;

public abstract class MultipleArgumentFunction implements Function {
    protected final List<Function> functions;

    public MultipleArgumentFunction(List<Function> functions) {
        this.functions = functions;
    }

    @Override
    public double evaluate(Map<String, Double> map) {
        return makeOperation(functions.stream().map(function -> function.evaluate(map)).toList());
    }

    abstract double makeOperation(List<Double> list);

    public List<Function> getFunctions() {
        return functions;
    }
}
