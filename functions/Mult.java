package functions;

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
            List<Function> copy = new ArrayList<>(this.functions);
            copy.set(i, copy.get(i).differentiate(d));
            list.add(new Mult(copy));
        }
        return new Sum(list);
    }
}
