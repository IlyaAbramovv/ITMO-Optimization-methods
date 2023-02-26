package lab1.functions;

import java.util.Map;

public interface Function {
    double evaluate(Map<String, Double> map);

    Function differentiate(String d);
}
