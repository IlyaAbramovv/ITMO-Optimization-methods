package lab1.functions;

import java.util.List;
import java.util.Map;

public class Sum implements Function {
    private final List<Function> functions;

    public Sum(List<Function> functions) {
        this.functions = functions;
    }

    @Override
    public double evaluate(Map<String, Double> map) {
        return functions.stream().mapToDouble(function -> function.evaluate(map)).reduce(0, Double::sum);
    }
}
