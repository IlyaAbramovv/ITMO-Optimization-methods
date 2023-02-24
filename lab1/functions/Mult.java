package lab1.functions;

import java.util.List;

public class Mult extends MultipleArgumentFunction {

    public Mult(List<Function> functions) {
        super(functions);
    }

    @Override
    double makeOperation(List<Double> list) {
        return list.stream().reduce(1.0, (a, b) -> a * b);
    }
}
