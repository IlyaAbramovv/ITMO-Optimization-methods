package lab1.functions;

import java.util.ArrayList;
import java.util.List;

public class Mult extends MultipleArgumentFunction {

    public Mult(List<Function> functions) {
        super(functions);
    }

    @Override
    double makeOperation(List<Double> list) {
        return list.stream().reduce(1.0, (a, b) -> a * b);
    }

    @Override
    public Function differentiate(String d) {
        List<Function> list = new ArrayList<>();
        for (int i = 0; i < functions.size(); i++) {
            list.add(this);
        }
        for (int i = 0; i < functions.size(); i++) {
            list.set(i, functions.get(i).differentiate(d));
        }
        return new Sum(list);
    }
}
