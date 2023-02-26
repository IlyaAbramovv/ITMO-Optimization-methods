package lab1;

import lab1.functions.*;

import java.util.*;

public class FunctionUtils {
    public static final double EPS = 1e-7;
    private static final double ALPHA = 1e-1;
    private static final int MAX_COUNT_OF_ITERATIONS = 10000;
    private static final double INITIAL_VALUE = 2.0;

    public static Map<String, Double> gradientDescent(Function function) {
        List<String> variables = getAllVariables(function);
        Map<String, Double> vector = new HashMap<>();
        for (String variable : variables) {
            vector.put(variable, INITIAL_VALUE);
        }
        double first = function.evaluate(vector), second;
        int countInterations = 0, countEvaluations = 1;
        while (countInterations < MAX_COUNT_OF_ITERATIONS) {
            Map<String, Double> gradient = getGradient(function, vector);
            for (var entry : gradient.entrySet()) {
                vector.put(entry.getKey(), vector.get(entry.getKey()) - ALPHA * entry.getValue());
            }
            second = function.evaluate(vector);
            countInterations++;
            countEvaluations += 1 + variables.size();
            if (Math.abs(second - first) < EPS) {
                break;
            }
            first = second;
        }
        System.out.println("Count of iterations: " + countInterations);
        System.out.println("Count of evaluations: " + countEvaluations);
        return vector;
    }

    public static double goldenRatio(Function function, double a, double b) {
        String variableName = FunctionUtils.getAllVariables(function).get(0);
        double fi = (1 + Math.sqrt(5)) / 2;
        while (b - a > EPS) {
            double x1 = b - (b - a) / fi, x2 = a + (b - a) / fi;
            double y1 = function.evaluate(Map.of(variableName, x1)), y2 = function.evaluate(Map.of(variableName, x2));
            if (y1 >= y2) {
                a = x1;
            } else {
                b = x2;
            }
        }
        return (a + b) / 2;
    }

    private static Map<String, Double> getGradient(Function function, Map<String, Double> vector) {
        Map<String, Double> gradient = new HashMap<>();
        for (var entry : vector.entrySet()) {
            gradient.put(entry.getKey(), function.differentiate(entry.getKey()).evaluate(vector));
        }
        return gradient;
    }

    public static List<String> getAllVariables(Function function) {
        Set<String> set = new HashSet<>();
        getAllVariablesRec(function, set);
        return new ArrayList<>(set);
    }

    private static void getAllVariablesRec(Function function, Set<String> set) {
        if (function instanceof SingleArgumentFunction) {
            getAllVariablesRec(((SingleArgumentFunction) function).getFunction(), set);
        } else if (function instanceof DoubleArgumentFunction) {
            getAllVariablesRec(((DoubleArgumentFunction) function).getFunction1(), set);
            getAllVariablesRec(((DoubleArgumentFunction) function).getFunction2(), set);
        } else if (function instanceof MultipleArgumentFunction) {
            ((MultipleArgumentFunction) function).getFunctions().forEach(function1 -> getAllVariablesRec(function1, set));
        } else if (function instanceof Variable) {
            set.add(((Variable) function).getValue());
        }
    }
}
