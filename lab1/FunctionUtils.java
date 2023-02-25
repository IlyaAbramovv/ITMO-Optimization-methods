package lab1;

import lab1.functions.*;

import java.util.*;

public class FunctionUtils {
    public static final double EPS = 1e-5;
    private static final double ALPHA = 1e-1;
    private static final int MAX_COUNT_OF_ITERATIONS = 10000;
    private static final double INITIAL_VALUE = 2.0;

    public static Map<String, Double> gradientDescent(Function function) {
        List<String> variables = getAllVariables(function);
        Map<String, Double> vector = new HashMap<>();
        for (String variable : variables) {
            vector.put(variable, INITIAL_VALUE);
        }
        int countInterations = 0, countEvaluations = 0;
        while (countInterations < MAX_COUNT_OF_ITERATIONS) {
            Map<String, Double> gradient = getGradient(function, vector);
            countEvaluations += 1 + variables.size();
            double maxDiff = Integer.MAX_VALUE;
            for (var entry : gradient.entrySet()) {
                maxDiff = Math.min(maxDiff, Math.abs(ALPHA * entry.getValue()));
                vector.put(entry.getKey(), vector.get(entry.getKey()) - ALPHA * entry.getValue());
            }
            countInterations++;
            if (maxDiff < EPS) {
                break;
            }
        }
        System.out.println("Count of iterations: " + countInterations);
        System.out.println("Count of evaluations: " + countEvaluations);
        return vector;
    }

    private static Map<String, Double> getGradient(Function function, Map<String, Double> vector) {
        Map<String, Double> gradient = new HashMap<>();
        double val = function.evaluate(vector);
        for (var entry : vector.entrySet()) {
            entry.setValue(entry.getValue() + EPS);
            double val1 = function.evaluate(vector);
            entry.setValue(entry.getValue() - EPS);
            gradient.put(entry.getKey(), (val1 - val) / EPS);
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
