package lab1.functions;

import java.util.List;

public class Sum extends MultipleArgumentFunction {

    public Sum(List<Function> functions) {
        super(functions);
    }

    @Override
    double makeOperation(List<Double> list) {
        return list.stream().reduce(0.0, Double::sum);
    }
}
