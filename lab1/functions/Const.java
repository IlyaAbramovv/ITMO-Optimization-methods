package lab1.functions;

import java.util.Map;

public class Const implements Function {
    private final double value;

    public Const(double value) {
        this.value = value;
    }

    @Override
    public double evaluate(Map<String, Double> map) {
        return value;
    }
}
