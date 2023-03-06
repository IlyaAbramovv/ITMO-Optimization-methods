package lab1;

import lab1.functions.Function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GradientDescent {
    public static final double EPS = 1e-7;
    private static final int MAX_COUNT_OF_ITERATIONS = 10000;
    private static final double INITIAL_VALUE = 2.0;
    public static final double PHI = (1 + Math.sqrt(5)) / 2;
    public static final double ALPHA = 0.3;

    private static int countOfGradientCountings, countOfEvaluations;

    public static List<Map<String, Double>> gradientDescent(Function function, boolean constAlpha) {
        countOfGradientCountings = 0;
        countOfEvaluations = 0;
        List<String> variables = FunctionUtils.getAllVariables(function);
        Map<String, Double> vector = new HashMap<>();
        List<Map<String, Double>> res = new ArrayList<>();
        for (String variable : variables) {
            vector.put(variable, INITIAL_VALUE);
        }
        res.add(Map.copyOf(vector));
        int countInterations = 0;
        while (countInterations < MAX_COUNT_OF_ITERATIONS) {
            Map<String, Double> gradient = FunctionUtils.getGradient(function, vector);
            countOfGradientCountings++;
            countOfEvaluations += gradient.size();
            double alpha = constAlpha ? ALPHA : getAlpha(function, vector, gradient);
            double maxDiff = 0;
            for (var entry : gradient.entrySet()) {
                double diff = alpha * entry.getValue();
                maxDiff = Math.max(maxDiff, Math.abs(diff));
                vector.put(entry.getKey(), vector.get(entry.getKey()) - diff);
            }
            res.add(Map.copyOf(vector));
            countInterations++;
            if (maxDiff <= EPS) {
                break;
            }
        }
        System.out.println("Count of gradient countings: " + countOfGradientCountings);
        System.out.println("Count of function evaluations: " + countOfEvaluations);
        return res;
    }

    public static double goldenRatio(Function function, double a, double b) {
        String variableName = FunctionUtils.getAllVariables(function).get(0);
        while (b - a > EPS) {
            double x1 = b - (b - a) / PHI, x2 = a + (b - a) / PHI;
            double y1 = function.evaluate(Map.of(variableName, x1)), y2 = function.evaluate(Map.of(variableName, x2));
            if (y1 >= y2) {
                a = x1;
            } else {
                b = x2;
            }
        }
        return (a + b) / 2;
    }

    private static double getAlpha(Function function, Map<String, Double> vector, Map<String, Double> gradient) {
        double b = 100, a = 0;
        while (b - a > EPS) {
            double x1 = b - (b - a) / PHI, x2 = a + (b - a) / PHI;
            var vector1 = new HashMap<>(vector);
            vector1.replaceAll((k, v) -> v - x1 * gradient.get(k));
            var vector2 = new HashMap<>(vector);
            vector2.replaceAll((k, v) -> v - x2 * gradient.get(k));
            double y1 = function.evaluate(vector1), y2 = function.evaluate(vector2);
            countOfEvaluations += 2;
            if (y1 >= y2) {
                a = x1;
            } else {
                b = x2;
            }
        }
        return (a + b) / 2;
    }
}
