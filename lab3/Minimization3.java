package lab3;

import functions.*;
import matrixes.FunctionalMatrix;
import matrixes.Matrix;

import java.util.*;

import static matrixes.MatrixUtils.*;

public class Minimization3 {
    public static final double EPS = 1e-4;
    private static final int MAX_COUNT_OF_ITERATIONS = 10000;
    private static final double INITIAL_VALUE = 2.0;

    public static List<Map<String, Double>> gaussNewton(Sum function) {
        long start = System.nanoTime();
        final Set<String> variables = FunctionUtils.getAllVariables(function);
        final List<Map<String, Double>> res = new ArrayList<>();
        Map<String, Double> vector = initializeVector(variables);
        res.add(Map.copyOf(vector));
        int countIterations = 0;
        FunctionalMatrix jacobian = jacobian(function.getFunctions(), variables);
        while (countIterations < MAX_COUNT_OF_ITERATIONS) {
            double maxDiff = 0;
            Matrix J = evaluate(jacobian, vector);
            maxDiff = Math.max(maxDiff, gaussNewtonIteration(function.getFunctions(), J, vector, res));
            countIterations++;
            if (maxDiff <= EPS) {
                break;
            }
        }
        System.out.print(countIterations + " ");
        System.out.println((System.nanoTime() - start) / 1000000);
        System.out.println(res.get(res.size() - 1));
        return res;
    }

    private static double gaussNewtonIteration(List<Function> functions,
                                               Matrix J,
                                               Map<String, Double> vector,
                                               List<Map<String, Double>> res) {
        Matrix transposedJ = transpose(J);
        Map<String, Double> r = new HashMap<>();
        for (int i = 0; i < functions.size(); i++) {
            r.put("x" + i, functions.get(i).evaluate(vector));
        }
        Map<String, Double> direction = multByVector(multiply(inverse(multiply(transposedJ, J)), transposedJ), r);
        double maxDiff = getMaxDiffAndChangeVector(vector, direction);
        res.add(vector);
        return maxDiff;
    }

    private static double getMaxDiffAndChangeVector(
            Map<String, Double> vector,
            Map<String, Double> direction
    ) {
        double maxDiff = 0;
        for (var entry : direction.entrySet()) {
            maxDiff = Math.max(maxDiff, Math.abs(entry.getValue()));
            vector.put(entry.getKey(), vector.get(entry.getKey()) - entry.getValue());
        }
        return maxDiff;
    }

    private static Map<String, Double> initializeVector(Set<String> variables) {
        Map<String, Double> vector = new HashMap<>(variables.size());
        for (String variable : variables) {
            vector.put(variable, INITIAL_VALUE);
        }
        return vector;
    }

    public static Map<String, Double> polynomialRegression(List<Double> x, List<Double> y, int n) {
        List<Function> functions = new ArrayList<>();
        for (int i = 0; i < y.size(); i++) {
            List<Function> sum = new ArrayList<>();
            sum.add(new Variable("x0"));
            for (int j = 1; j <= n; j++) {
                sum.add(new Multiply(new Variable("x" + j), new Pow(new Const(x.get(i)), new Const(j))));
            }
            functions.add(new Subtract(new Sum(sum), new Const(y.get(i))));
        }
        var res = gaussNewton(new Sum(functions));
        return res.get(res.size() - 1);
    }
}
