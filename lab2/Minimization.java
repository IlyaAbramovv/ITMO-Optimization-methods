package lab2;

import functions.*;

import java.util.*;

public class Minimization {
    public static final double EPS = 1e-7;
    private static final int MAX_COUNT_OF_ITERATIONS = 10000;
    private static final double INITIAL_VALUE = 2.0;
    private static final double ALPHA = 0.22;

    public static List<Map<String, Double>> gradientDescent(Sum function, int batchSize) {
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

    public static double[] linearRegression(List<Double> x, List<Double> y) {
        List<Function> functions = new ArrayList<>();
        for (int i = 0; i < y.size(); i++) {
            functions.add(new Pow(new Subtract(new Const(y.get(i)), new Add(new Multiply(new Variable("a"),
                    new Const(x.get(i))), new Variable("b"))), new Const(2.0)));
        }
        Sum function = new Sum(functions);
        var res = gradientDescent(function, 1);
        double a = res.get(res.size() - 1).get("a");
        double b = res.get(res.size() - 1).get("b");
        return new double[]{a, b};
    }
}
