package lab1;

import functions.Function;
import functions.FunctionUtils;

import java.util.*;

public class Minimization1 {
    public static final double EPS = 1e-7;
    private static final int MAX_COUNT_OF_ITERATIONS = 10000;
    private static final double INITIAL_VALUE = 2.0;
    public static final double PHI = (1 + Math.sqrt(5)) / 2;
    private static final double ALPHA = 0.32;
    private static final double C1 = 1e-4;
    private static final double C2 = 0.9;

    private static int countOfGradientCountings, countOfEvaluations;

    public static List<Map<String, Double>> gradientDescent(Function function, Mode mode) {
        countOfGradientCountings = 0;
        countOfEvaluations = 0;
        Set<String> variables = FunctionUtils.getAllVariables(function);
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
            double alpha = ALPHA;
            if (mode == Mode.GOLDEN_RATIO) {
                alpha = getBestAlpha(function, vector, gradient, false, gradient);
            } else if (mode == Mode.WOLFE_CONDITIONS) {
                alpha = getBestAlpha(function, vector, gradient, true, gradient);
            }
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
        String variableName = "x";
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

    public static double getBestAlpha(Function function, Map<String, Double> vector,
                                      Map<String, Double> gradient, boolean checkWolfesConditions, Map<String, Double> searchDirection) {
        double b = 1, a = 0;
        while (b - a > 1e-9) {
            double x1 = b - (b - a) / PHI, x2 = a + (b - a) / PHI;
            var vector1 = new HashMap<>(vector);
            vector1.replaceAll((k, v) -> v + x1 * searchDirection.get(k));
            var vector2 = new HashMap<>(vector);
            vector2.replaceAll((k, v) -> v + x2 * searchDirection.get(k));
            double y1 = function.evaluate(vector1), y2 = function.evaluate(vector2);
            if (checkWolfesConditions) {
                if (checkWolfesConditions(function, vector, gradient, x1, searchDirection)) {
                    return x1;
                } else if (checkWolfesConditions(function, vector, gradient, x2, searchDirection)) {
                    return x2;
                }
            }
            if (y1 >= y2) {
                a = x1;
            } else {
                b = x2;
            }
        }
        return (a + b) / 2;
    }

    private static boolean checkWolfesConditions(Function function, Map<String, Double> vector, Map<String, Double> gradient, double alpha, Map<String, Double> searchDirection) {
        var vector1 = new HashMap<>(vector);
        vector1.replaceAll((k, v) -> v + alpha * searchDirection.get(k));

        List<Map.Entry<Double, Double>> zipped = new ArrayList<>();
        for (int i = 0; i < gradient.size(); i++) {
            zipped.add(Map.entry(gradient.get("x" + i), searchDirection.get("x" + i)));
        }

        boolean first = function.evaluate(vector1) <= function.evaluate(vector) + C1 * alpha *
                zipped.stream().mapToDouble(a -> a.getKey() * a.getValue()).sum();

        var gradient1 = FunctionUtils.getGradient(function, vector1);
        boolean second = gradient1.entrySet().stream().mapToDouble(entry ->
                entry.getValue() * searchDirection.get(entry.getKey())).sum()
                >= C2 * zipped.stream().mapToDouble(a -> a.getKey() * a.getValue()).sum();
        return first && second;
    }
}