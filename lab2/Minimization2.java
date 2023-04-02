package lab2;

import functions.*;
import matrixes.VectorUtils;

import java.util.*;

import static lab1.Minimization1.getBestAlpha;

public class Minimization2 {
    public static final double EPS = 1e-7;
    private static final int MAX_COUNT_OF_ITERATIONS = 10000;
    private static final double INITIAL_VALUE = 2.0;
    private static final double ALPHA = 0.22;
    private static final double GAMMA = 0.99;
    private static final double BETA1 = 0.9;
    private static final double BETA2 = 0.999;
    public static final double ALPHA_FOR_ADA = 0.8;

    public static List<Map<String, Double>> gradientDescent(MultipleArgumentFunction function, int batchSize) {
        return abstractGD(function, batchSize, 1, Minimization2::gdOneTime);
    }

    public static List<Map<String, Double>> nesterovGradientDescent(MultipleArgumentFunction function, int batchSize) {
        return abstractGD(function, batchSize, 2, Minimization2::nesterovOneTime);
    }

    public static List<Map<String, Double>> momentumGradientDescent(MultipleArgumentFunction function, int batchSize) {
        return abstractGD(function, batchSize, 2, Minimization2::momentumOneTime);
    }

    public static List<Map<String, Double>> rmsPropGradientDescent(MultipleArgumentFunction function, int batchSize) {
        return abstractGD(function, batchSize, 2, Minimization2::rmsPropOneTime);
    }

    public static List<Map<String, Double>> adamGradientDescent(MultipleArgumentFunction function, int batchSize) {
        return abstractGD(function, batchSize, 3, Minimization2::adamOneTime);
    }

    public static List<Map<String, Double>> adaptiveGradientDescent(MultipleArgumentFunction function, int batchSize) {
        long start = System.nanoTime();
        final Set<String> variables = FunctionUtils.getAllVariables(function);
        final List<Map<String, Double>> res = new ArrayList<>();

        Map<String, Double> vector = initializeVector(variables);
        res.add(Map.copyOf(vector));
        int countIterations = 0;
        Map<String, Map<String, Double>> G = new HashMap<>();
        for (var v : variables) {
            for (var u : variables) {
                G.putIfAbsent(u, new HashMap<>());
                G.get(u).put(v, 0d);
            }
        }

        while (countIterations < MAX_COUNT_OF_ITERATIONS) {
            double maxDiff = adaptiveOneTime(function, batchSize, variables, res, vector, countIterations, G);

            countIterations++;
            if (maxDiff <= EPS) {
                break;
            }
        }
        System.out.println((System.nanoTime() - start) / 1e6 + " ms");
        return res;
    }

    private static double adamOneTime(
            MultipleArgumentFunction function,
            int batchSize,
            Set<String> variables,
            List<Map<String, Double>> vectors,
            int countIterations,
            List<Map<String, Double>> res) {

        var vectorX = vectors.get(0);
        var vectorV = vectors.get(1);
        var vectorS = vectors.get(2);

        var gradient = getGradientWithRespectToBatchSize(function, batchSize, variables, vectorX, countIterations);

        vectorV.replaceAll((s, d) -> BETA1 * d + (1 - BETA1) * gradient.get(s));
        vectorS.replaceAll((s, d) -> BETA2 * d + (1 - BETA2) * gradient.get(s) * gradient.get(s));

        var averagedV = new HashMap<>(Map.copyOf(vectorV));
        averagedV.replaceAll((s, d) -> d / (1 - Math.pow(BETA1, countIterations + 1)));

        var averagedS = new HashMap<>(Map.copyOf(vectorS));
        averagedV.replaceAll((s, d) -> d / (1 - Math.pow(BETA2, countIterations + 1)));

        double maxDiff = 0;
        for (var entry : gradient.entrySet()) {
            double diff = ALPHA * averagedV.get(entry.getKey()) / Math.sqrt(averagedS.get(entry.getKey()) + 1e-8);
            maxDiff = Math.max(maxDiff, Math.abs(diff));
            vectorX.put(entry.getKey(), vectorX.get(entry.getKey()) - diff);
        }

        res.add(Map.copyOf(vectorX));
        return maxDiff;
    }


    private static double adaptiveOneTime(
            MultipleArgumentFunction function,
            int batchSize,
            Set<String> variables,
            List<Map<String, Double>> res,
            Map<String, Double> vector,
            int countIterations,
            Map<String, Map<String, Double>> G
    ) {
        var gradient = getGradientWithRespectToBatchSize(function, batchSize, variables, vector, countIterations);
        for (var v : variables) {
            for (var u : variables) {
                G.get(v).put(u, G.get(v).get(u) + gradient.get(v) * gradient.get(u));
            }
        }

        double maxDiff = 0;
        for (var entry : gradient.entrySet()) {
            double diff = ALPHA_FOR_ADA * gradient.get(entry.getKey()) / Math.sqrt(G.get(entry.getKey()).get(entry.getKey()));
            maxDiff = Math.max(maxDiff, Math.abs(diff));
            vector.put(entry.getKey(), vector.get(entry.getKey()) - diff);
        }
        res.add(Map.copyOf(vector));
        return maxDiff;
    }

    private static double gdOneTime(
            MultipleArgumentFunction function,
            int batchSize,
            Set<String> variables,
            List<Map<String, Double>> vectors,
            int countIterations,
            List<Map<String, Double>> res
    ) {
        var vector = vectors.get(0);
        Map<String, Double> gradient = getGradientWithRespectToBatchSize(
                function,
                batchSize,
                variables,
                vector,
                countIterations);

        double alpha = getBestAlpha(function, vector, gradient, false);

        double maxDiff = getMaxDiffAndChangeVector(
                vector, gradient, alpha);
        res.add(Map.copyOf(vector));
        return maxDiff;
    }

    private static double getMaxDiffAndChangeVector(
            Map<String, Double> vector,
            Map<String, Double> gradient,
            double alpha
    ) {
        double maxDiff = 0;
        for (var entry : gradient.entrySet()) {
            double diff = alpha * entry.getValue();
            maxDiff = Math.max(maxDiff, Math.abs(diff));
            vector.put(entry.getKey(), vector.get(entry.getKey()) - diff);
        }
        return maxDiff;
    }

    private static double nesterovOneTime(
            MultipleArgumentFunction function,
            int batchSize,
            Set<String> variables,
            List<Map<String, Double>> vectors,
            int countIterations,
            List<Map<String, Double>> res
    ) {
        var vectorX = vectors.get(0);
        var vectorV = vectors.get(1);
        Map<String, Double> gradient = getGradientWithRespectToBatchSize(
                function,
                batchSize,
                variables,
                VectorUtils.subtract(vectorX, VectorUtils.multiply(vectorV, ALPHA * GAMMA)),
                countIterations);

        double alpha = getBestAlpha(function, vectorX, gradient, false);
        vectorV.replaceAll((s, d) -> d * GAMMA);
        double maxDiff = getMaxDiffAndChangeVector(vectorV, gradient, (GAMMA - 1)
        );
        vectorX.replaceAll((s, d) -> d - vectorV.get(s) * alpha);
        res.add(Map.copyOf(vectorX));

        return maxDiff;
    }

    private static double momentumOneTime(
            MultipleArgumentFunction function,
            int batchSize,
            Set<String> variables,
            List<Map<String, Double>> vectors,
            int countIterations,
            List<Map<String, Double>> res
    ) {
        var vectorX = vectors.get(0);
        var vectorV = vectors.get(1);
        Map<String, Double> gradient = getGradientWithRespectToBatchSize(
                function,
                batchSize,
                variables,
                vectorX,
                countIterations);

        double alpha = getBestAlpha(function, vectorX, gradient, false);
        vectorV.replaceAll((s, d) -> d * GAMMA + gradient.get(s) * (1 - GAMMA));

        double maxDiff = getMaxDiffAndChangeVector(vectorX, vectorV, alpha
        );
        res.add(Map.copyOf(vectorX));
        return maxDiff;
    }

    private static double rmsPropOneTime(
            MultipleArgumentFunction function,
            int batchSize,
            Set<String> variables,
            List<Map<String, Double>> vectors,
            int countIterations,
            List<Map<String, Double>> res
    ) {
        var vectorX = vectors.get(0);
        var vectorV = vectors.get(1);
        Map<String, Double> gradient = getGradientWithRespectToBatchSize(
                function,
                batchSize,
                variables,
                vectorX,
                countIterations);

        vectorV.replaceAll((s, d) -> d * GAMMA + gradient.get(s) * gradient.get(s) * (1 - GAMMA));

        double maxDiff = 0;
        for (var entry : gradient.entrySet()) {
            double diff = ALPHA * entry.getValue() / Math.sqrt(vectorV.get(entry.getKey()) + EPS);
            maxDiff = Math.max(maxDiff, Math.abs(diff));
            vectorX.put(entry.getKey(), vectorX.get(entry.getKey()) - diff);
        }

        res.add(Map.copyOf(vectorX));
        return maxDiff;
    }

    private static Map<String, Double> initializeVector(Set<String> variables) {
        Map<String, Double> vector = new HashMap<>(variables.size());
        for (String variable : variables) {
            vector.put(variable, INITIAL_VALUE);
        }
        return vector;
    }

    private static List<Map<String, Double>> abstractGD(MultipleArgumentFunction function, int batchSize, int vectorAmount,
                                                        GDOneIterFunction oneIterFunction) {
        long start = System.nanoTime();
        final Set<String> variables = FunctionUtils.getAllVariables(function);
        final List<Map<String, Double>> res = new ArrayList<>();

        List<Map<String, Double>> vectors = new ArrayList<>(vectorAmount);
        for (int i = 0; i < vectorAmount; i++) {
            vectors.add(initializeVector(variables));
        }
        res.add(Map.copyOf(vectors.get(0)));
        int countIterations = 0;
        while (countIterations < MAX_COUNT_OF_ITERATIONS) {

            double maxDiff = oneIterFunction.apply(function, batchSize, variables, vectors, countIterations, res);
            countIterations++;
            if (maxDiff <= EPS) {
                break;
            }
        }
        System.out.println((System.nanoTime() - start) / 1e6 + " ms");
        return res;
    }


    private static Map<String, Double> getGradientWithRespectToBatchSize(
            MultipleArgumentFunction function,
            int batchSize,
            Set<String> variables,
            Map<String, Double> vector,
            int countIterations
    ) {
        List<Map<String, Double>> gradients = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            var grad = FunctionUtils.getGradient(
                    function.getFunctions().get((i + batchSize * countIterations) % function.getFunctions().size()), vector);
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
        return gradient;
    }

    public static double[] linearRegression(List<Double> x, List<Double> y, GradientDescentMode gdMode, int batchSize) {
        List<Function> functions = new ArrayList<>();
        for (int i = 0; i < y.size(); i++) {
            functions.add(new Pow(new Subtract(new Const(y.get(i)), new Add(new Multiply(new Variable("a"),
                    new Const(x.get(i))), new Variable("b"))), new Const(2.0)));
        }
        Sum function = new Sum(functions);
        var res = switch (gdMode) {
            case COMMON -> gradientDescent(function, batchSize);
            case NESTEROV -> nesterovGradientDescent(function, batchSize);
            case MOMENTUM -> momentumGradientDescent(function, batchSize);
            case RMSPROP -> rmsPropGradientDescent(function, batchSize);
            case ADAM -> adamGradientDescent(function, batchSize);
            case ADAPTIVE -> adaptiveGradientDescent(function, batchSize);
        };
        double a = res.get(res.size() - 1).get("a");
        double b = res.get(res.size() - 1).get("b");
        return new double[]{a, b};
    }
}
