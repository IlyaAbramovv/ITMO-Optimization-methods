package lab1.functions;

import java.util.Map;

public class Variable implements Function {
    private final String value;

    public Variable(String value) {
        this.value = value;
    }

    @Override
    public double evaluate(Map<String, Double> map) {
        for (var entry : map.entrySet()) {
            if (entry.getKey().equals(value)) {
                return entry.getValue();
            }
        }
        return 0;
    }

    public String getValue() {
        return value;
    }
}
