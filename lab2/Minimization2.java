package lab2;

import functions.*;
import matrixes.VectorUtils;

import java.util.*;

import static lab1.Minimization1.getBestAlpha;

public class Minimization2 {
    public static final double EPS = 1e-4;
    private static final int MAX_COUNT_OF_ITERATIONS = 10000;
    private static final double INITIAL_VALUE = 1.0;
    private static final double GAMMA = 0.99;
    private static final double BETA1 = 0.9;
    private static final double BETA2 = 0.999;
    private static final double L1_ALPHA = 0.7;
    private static final double L2_ALPHA = 0.3;

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
        int epoch = (int) Math.ceil((double) function.getFunctions().size() / batchSize);
        while (countIterations < MAX_COUNT_OF_ITERATIONS) {
            double maxDiff = 0;
            for (int i = 0; i < epoch; i++) {
                maxDiff = Math.max(maxDiff, adaptiveOneTime(function, batchSize, variables, res, vector, countIterations + i, G));
            }
            countIterations += epoch;
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
        final double ALPHA = 0.001;

        var vectorX = vectors.get(0);
        var vectorV = vectors.get(1);
        var vectorS = vectors.get(2);

        var gradient = getGradientWithRespectToBatchSize(function, batchSize, variables, vectorX, countIterations);

        vectorV.replaceAll((s, d) -> BETA1 * d + (1 - BETA1) * gradient.get(s));
        vectorS.replaceAll((s, d) -> BETA2 * d + (1 - BETA2) * gradient.get(s) * gradient.get(s));

        var averagedV = new HashMap<>(Map.copyOf(vectorV));
        averagedV.replaceAll((s, d) -> d / (1 - Math.pow(BETA1, countIterations + 1)));

        var averagedS = new HashMap<>(Map.copyOf(vectorS));
        averagedS.replaceAll((s, d) -> d / (1 - Math.pow(BETA2, countIterations + 1)));

        double maxDiff = 0;
        for (var entry : gradient.entrySet()) {
            double diff = ALPHA * averagedV.get(entry.getKey()) / Math.sqrt(averagedS.get(entry.getKey()) + 1e-9);
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
        final double ALPHA = 0.001;
        var gradient = getGradientWithRespectToBatchSize(function, batchSize, variables, vector, countIterations);
        for (var v : variables) {
            for (var u : variables) {
                G.get(v).put(u, G.get(v).get(u) + gradient.get(v) * gradient.get(u));
            }
        }

        double maxDiff = 0;
        for (var entry : gradient.entrySet()) {
            double diff = ALPHA * gradient.get(entry.getKey()) / Math.sqrt(G.get(entry.getKey()).get(entry.getKey()) + 1e-8);
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


//        final double initial = 0.5;
//        double alpha = stepDecay(countIterations, initial, 0.25, 3);
        double alpha = getBestAlpha(function, vector, gradient, false, gradient);

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
        final double ALPHA = 0.001;

        var vectorX = vectors.get(0);
        var vectorV = vectors.get(1);
        Map<String, Double> gradient = getGradientWithRespectToBatchSize(
                function,
                batchSize,
                variables,
                VectorUtils.subtract(vectorX, VectorUtils.multiply(vectorV, ALPHA * GAMMA)),
                countIterations);

        vectorV.replaceAll((s, d) -> d * GAMMA);
        getMaxDiffAndChangeVector(vectorV, gradient, (GAMMA - 1));
        double maxDiff = getMaxDiffAndChangeVector(vectorX, vectorV, ALPHA);
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
        final double ALPHA = 0.001;

        var vectorX = vectors.get(0);
        var vectorV = vectors.get(1);
        Map<String, Double> gradient = getGradientWithRespectToBatchSize(
                function,
                batchSize,
                variables,
                vectorX,
                countIterations);

        vectorV.replaceAll((s, d) -> d * GAMMA + gradient.get(s) * (1 - GAMMA));

        double maxDiff = getMaxDiffAndChangeVector(vectorX, vectorV, ALPHA);
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
        final double ALPHA = 0.001;

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
            double diff = ALPHA * entry.getValue() / Math.sqrt(vectorV.get(entry.getKey()) + 1e-9);
            maxDiff = Math.max(maxDiff, Math.abs(diff));
            vectorX.put(entry.getKey(), vectorX.get(entry.getKey()) - diff);
        }

        res.add(Map.copyOf(vectorX));
        return maxDiff;
    }

    static Map<String, Double> initializeVector(Set<String> variables) {
        Map<String, Double> vector = new HashMap<>(variables.size());
        for (String variable : variables) {
            vector.put(variable, INITIAL_VALUE);
        }
        return vector;
    }

    private static List<Map<String, Double>> abstractGD(MultipleArgumentFunction function, int batchSize,
                                                        int vectorAmount, GDOneIterFunction oneIterFunction) {
        long start = System.nanoTime();
        final Set<String> variables = FunctionUtils.getAllVariables(function);
        final List<Map<String, Double>> res = new ArrayList<>();
        List<Map<String, Double>> vectors = new ArrayList<>(vectorAmount);
        for (int i = 0; i < vectorAmount; i++) {
            vectors.add(initializeVector(variables));
        }
        res.add(Map.copyOf(vectors.get(0)));
        int countIterations = 0;
        int epoch = (int) Math.ceil((double) function.getFunctions().size() / batchSize);
        while (countIterations < MAX_COUNT_OF_ITERATIONS) {
            double maxDiff = 0;
            for (int i = 0; i < epoch; i++) {
                maxDiff = Math.max(maxDiff, oneIterFunction.apply(function, batchSize, variables, vectors, countIterations + i, res));
            }
            countIterations += epoch;
            if (maxDiff <= EPS) {
                break;
            }
        }
        System.out.print(countIterations + " ");
        System.out.println((System.nanoTime() - start) / 1000000);
        System.out.println(res.get(res.size() - 1));
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

    private static double stepDecay(int iterNum, double initial, double d, int r) {
        return initial * Math.pow(d, 1 + (iterNum) / r);
    }

    public static Map<String, Double> linearRegression(List<Double> x, List<Double> y,
                                                       GradientDescentMode gdMode, int batchSize) {
        return polynomialRegression(x, y, 1, gdMode, batchSize, Regularization.DEFAULT);
    }

    //a_0 + a_1x + a_2x^2 + ... + a_nx^n
    public static Map<String, Double> polynomialRegression(List<Double> x, List<Double> y, int n,
                                                           GradientDescentMode gdMode, int batchSize, Regularization reg) {
        List<Function> functions = new ArrayList<>();
        for (int i = 0; i < y.size(); i++) {
            List<Function> sum = new ArrayList<>();
            for (int j = 0; j <= n; j++) {
                sum.add(new Multiply(new Variable("a" + j), new Pow(new Const(x.get(i)), new Const(j))));
            }
            functions.add(new Add(new Pow(new Subtract(new Sum(sum), new Const(y.get(i))), new Const(2.0)),
                    getRegularization(n, reg)));
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
        return res.get(res.size() - 1);
    }

    private static Function getRegularization(int n, Regularization reg) {
        return switch (reg) {
            case L2 -> L2(n);
            case L1 -> L1(n);
            case ELASTIC -> new Add(L1(n), L2(n));
            case DEFAULT -> new Const(0);
        };
    }

    private static Function L2(int n) {
        List<Function> sum = new ArrayList<>();
        for (int i = 0; i <= n; i++) {
            sum.add(new Pow(new Variable("a" + i), new Const(2.0)));
        }
        return new Multiply(new Const(L2_ALPHA), new Sum(sum));
    }

    private static Function L1(int n) {
        List<Function> sum = new ArrayList<>();
        for (int i = 0; i <= n; i++) {
            sum.add(new Sqrt(new Pow(new Variable("a" + i), new Const(2.0))));
        }
        return new Multiply(new Const(L1_ALPHA), new Sum(sum));
    }
}
