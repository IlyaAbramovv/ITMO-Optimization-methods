package lab3;

import functions.*;
import matrixes.FunctionalMatrix;
import matrixes.Matrix;
import matrixes.MatrixUtils;

import java.util.*;

import static functions.FunctionUtils.getAllVariables;
import static functions.FunctionUtils.getGradient;
import static lab1.Minimization1.getBestAlpha;
import static matrixes.MatrixUtils.*;
import static matrixes.VectorUtils.*;
import static matrixes.VectorUtils.add;
import static matrixes.VectorUtils.multiply;
import static matrixes.VectorUtils.scalar;
import static matrixes.VectorUtils.subtract;
import static matrixes.MatrixUtils.multiply;

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
    public static Map<String, Double> bfgs(MultipleArgumentFunction function) {
        int k = 0;
        var variables = getAllVariables(function);
        var initial = initializeVector(variables);
        var grad = getGradient(function, initial);
        var oldGrad = grad;
        Matrix I = MatrixUtils.eye(initial.size());
        Matrix Hk = I;
        var x = initial;
        var oldX = x;

        while (getNorm(grad) > EPS && k++ < MAX_COUNT_OF_ITERATIONS) {
            var pk = negate(multByVector(Hk, grad));
            double alpha = getBestAlpha(function, x, grad, true);
            oldX = x;
            x = add(x, multiply(pk, alpha));
            var s = subtract(x, oldX);
            oldGrad = grad;
            grad = getGradient(function, x);
            var y = subtract(grad, oldGrad);

            double ro = 1.0 / scalar(y, s);
            Matrix A1 = MatrixUtils.subtract(I, toMatrixProduct(multiply(s, ro), y));
            Matrix A2 = MatrixUtils.subtract(I, toMatrixProduct(multiply(y, ro), s));
            Hk = MatrixUtils.add(MatrixUtils.multiply(A1, multiply(Hk, A2)), toMatrixProduct(multiply(s, ro), s));
        }
        return x;
    }

    public static Map<String, Double> lbfgs(MultipleArgumentFunction function, int m) {
        int k = 0;
        var variables = getAllVariables(function);
        var initial = initializeVector(variables);
        var grad = getGradient(function, initial);
        var oldGrad = subtract(grad, grad);
        var x = initial;
        var oldX = x;
        var I = MatrixUtils.eye(initial.size());

        Deque<Map<String, Double>> sList = new ArrayDeque<>(m);
        Deque<Map<String, Double>> yList = new ArrayDeque<>(m);
        Deque<Double> rhoList = new ArrayDeque<>(m);
        Deque<Double> alphaList = new ArrayDeque<>(m);

        while (getNorm(grad) > EPS && k++ < MAX_COUNT_OF_ITERATIONS) {
            var q = grad;

            var sIt = sList.iterator();
            var yIt = yList.iterator();
            var rhoIt = rhoList.iterator();
            while (sIt.hasNext()) {
                double rho = rhoIt.next();
                var s = sIt.next();
                var y = yIt.next();
                double alpha = scalar(s, q) * rho;
                q = subtract(q, multiply(y, alpha));
            }

            Matrix Hk0;
            if (k != 1) {
                double gamma = scalar(sList.getFirst(), yList.getFirst()) / scalar(yList.getFirst(), yList.getFirst());
                Hk0 = MatrixUtils.multiply(I, gamma);
            } else {
                Hk0 = I;
            }
            var r = multByVector(Hk0, q);

            sIt = sList.descendingIterator();
            yIt = yList.descendingIterator();
            rhoIt = rhoList.descendingIterator();
            var alphaIt = alphaList.descendingIterator();
            while (sIt.hasNext()) {
                double rho = rhoIt.next();
                var y = yIt.next();
                var s = sIt.next();
                double alpha = alphaIt.next();
                double beta = rho * scalar(y, r);
                r = add(r, multiply(s, alpha - beta));
            }
            if (sList.size() == m) {
                removeLastElements(sList, yList, rhoList, alphaList);
            }
            double alpha = getBestAlpha(function, x, grad, true);
            oldX = x;
            x = subtract(x, multiply(r, alpha));
            grad = getGradient(function, x);
            sList.addFirst(subtract(x, oldX));
            yList.addFirst(subtract(grad, oldGrad));
            rhoList.addFirst(1.0 / scalar(yList.getFirst(), sList.getFirst()));
            alphaList.addFirst(alpha);
            oldGrad = grad;
        }
        return x;
    }

    private static void removeLastElements(Deque<?>... deques) {
        Arrays.stream(deques).forEach(Deque::pollLast);
    }
}
