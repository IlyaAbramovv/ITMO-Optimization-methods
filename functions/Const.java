package functions;

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

    @Override
    public Function differentiate(String d) {
        return new Const(0.0);
    }

    public double getValue() {
        return value;
    }
}
