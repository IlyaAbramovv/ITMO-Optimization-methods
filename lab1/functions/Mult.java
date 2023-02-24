package lab1.functions;

import java.util.List;
import java.util.Map;

public class Mult implements Function {
    private final List<Function> functions;

    public Mult(List<Function> functions) {
        this.functions = functions;
    }

    @Override
    public double evaluate(Map<String, Double> map) {
        return functions.stream().mapToDouble(function -> function.evaluate(map)).reduce(1, (a, b) -> a * b);
    }
}
