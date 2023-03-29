import functions.*;

import java.util.*;

public class Minimization {
    public static final double EPS = 1e-7;
    private static final int MAX_COUNT_OF_ITERATIONS = 10000;
    private static final double INITIAL_VALUE = 2.0;
    private static final double PHI = (1 + Math.sqrt(5)) / 2;
    private static final double ALPHA = 0.22;
    private static final double C1 = 1e-3;
    private static final double C2 = 1 - 1e-3;

    public static List<Map<String, Double>> gradientDescent(Sum function, Mode mode, int batchSize) {
        long start = System.currentTimeMillis();
        Set<String> variables = FunctionUtils.getAllVariables(function);
        Map<String, Double> vector = new HashMap<>();
        List<Map<String, Double>> res = new ArrayList<>();
        for (String variable : variables) {
            vector.put(variable, INITIAL_VALUE);
        }
        res.add(Map.copyOf(vector));
        int countInterations = 0;
        while (countInterations < MAX_COUNT_OF_ITERATIONS) {
            List<Map<String, Double>> gradients = new ArrayList<>();
            for (int i = 0; i < batchSize; i++) {
                var grad = FunctionUtils.getGradient(
                        function.getFunctions().get((i + batchSize * countInterations) % function.getFunctions().size()), vector);
                gradients.add(grad);
            }
            Map<String, Double> gradient = new HashMap<>();
            for (String variable : variables) {
                double sum = 0;
                for (var grad : gradients) {
                    sum += grad.get(variable);
                }
                gradient.put(variable, sum);
            }
            double alpha = ALPHA;
            if (mode == Mode.GOLDEN_RATIO) {
                alpha = getBestAlpha(function, vector, gradient, false);
            } else if (mode == Mode.WOLFE_CONDITIONS) {
                alpha = getBestAlpha(function, vector, gradient, true);
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
        System.out.println(System.currentTimeMillis() - start + " ms");
        return res;
    }

    public static List<Map<String, Double>> gradientDescent(Function function, Mode mode) {
        return gradientDescent(new Sum(List.of(function)), mode, 1);
    }

    public static double[] linearRegression(List<Double> x, List<Double> y) {
        List<Function> functions = new ArrayList<>();
        for (int i = 0; i < y.size(); i++) {
            functions.add(new Pow(new Subtract(new Const(y.get(i)), new Add(new Multiply(new Variable("a"),
                    new Const(x.get(i))), new Variable("b"))), new Const(2.0)));
        }
        Sum function = new Sum(functions);
        var res = gradientDescent(function, Mode.GOLDEN_RATIO, 1);
        double a = res.get(res.size() - 1).get("a");
        double b = res.get(res.size() - 1).get("b");
        return new double[]{a, b};
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

    private static double getBestAlpha(Function function, Map<String, Double> vector,
                                       Map<String, Double> gradient, boolean checkWolfesConditions) {
        double b = 1, a = 0;
        while (b - a > EPS) {
            double x1 = b - (b - a) / PHI, x2 = a + (b - a) / PHI;
            var vector1 = new HashMap<>(vector);
            vector1.replaceAll((k, v) -> v - x1 * gradient.get(k));
            var vector2 = new HashMap<>(vector);
            vector2.replaceAll((k, v) -> v - x2 * gradient.get(k));
            double y1 = function.evaluate(vector1), y2 = function.evaluate(vector2);
            if (checkWolfesConditions) {
                if (checkWolfesConditions(function, vector, gradient, x1)) {
                    return x1;
                } else if (checkWolfesConditions(function, vector, gradient, x2)) {
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

    private static boolean checkWolfesConditions(Function function, Map<String, Double> vector, Map<String, Double> gradient, double alpha) {
        var vector1 = new HashMap<>(vector);
        vector1.replaceAll((k, v) -> v - alpha * gradient.get(k));
        boolean first = function.evaluate(vector1) <= function.evaluate(vector) + C1 * alpha *
                gradient.values().stream().map(val -> -val * val).reduce(0.0, Double::sum);
        var gradient1 = FunctionUtils.getGradient(function, vector1);
        boolean second = gradient1.entrySet().stream().map(entry ->
                -entry.getValue() * gradient.get(entry.getKey())).reduce(0.0, Double::sum)
                >= C2 * gradient.values().stream().map(val -> -val * val).reduce(0.0, Double::sum);
        return first && second;
    }
}
