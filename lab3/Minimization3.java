package lab3;

import functions.*;
import matrixes.FunctionalMatrix;
import matrixes.Matrix;
import matrixes.MatrixUtils;
import matrixes.VectorUtils;

import java.util.*;

import static functions.FunctionUtils.getAllVariables;
import static functions.FunctionUtils.getGradient;
import static lab1.Minimization1.getBestAlpha;
import static matrixes.MatrixUtils.multiply;
import static matrixes.MatrixUtils.*;
import static matrixes.VectorUtils.add;
import static matrixes.VectorUtils.multiply;
import static matrixes.VectorUtils.subtract;
import static matrixes.VectorUtils.*;

public class Minimization3 {
    public static final double EPS = 1e-4;
    private static final int MAX_COUNT_OF_ITERATIONS = 10000;
    private static final double INITIAL_VALUE = 2.0;

    public static List<Map<String, Double>> gaussNewton(List<Function> functions) {
        return abstractMethod(functions, Method.GAUSS_NEWTON);
    }

    public static List<Map<String, Double>> powellDogLeg(List<Function> functions, double delta) {
        return abstractMethod(functions, Method.POWELL_DOG_LEG, delta);
    }

    private static Map<String, Double> getGaussNewtonChange(List<Function> functions,
                                                            Matrix J,
                                                            Map<String, Double> vector) {
        Matrix transposedJ = transpose(J);
        Map<String, Double> r = new HashMap<>();
        for (int i = 0; i < functions.size(); i++) {
            r.put("x" + i, functions.get(i).evaluate(vector));
        }
        return multByVector(multiply(inverse(multiply(transposedJ, J)), transposedJ), r);
    }

    private static Map<String, Double> getPowellDogLegChange(List<Function> functions,
                                                             Matrix J,
                                                             Map<String, Double> vector,
                                                             double delta) {
        Map<String, Double> gaussNewton = getGaussNewtonChange(functions, J, vector);
        if (VectorUtils.getNorm(gaussNewton) <= delta) {
            return gaussNewton;
        }
        Map<String, Double> steepestDescent = getSteepestDescentChange(functions, J, vector);
        double sDNorm = getNorm(steepestDescent);
        double JSDNorm = getNorm(multByVector(J, steepestDescent));
        double t = (sDNorm * sDNorm) / (JSDNorm * JSDNorm);
        if (sDNorm * t > delta) {
            return VectorUtils.multiply(steepestDescent, delta / sDNorm);
        }
        var sd = VectorUtils.multiply(steepestDescent, t);
        var x = gaussNewton;
        double s = findS(gaussNewton, sd, delta);
        return VectorUtils.add(sd, VectorUtils.multiply(VectorUtils.subtract(x, sd), s));
    }

    private static double findS(Map<String, Double> gn, Map<String, Double> sd, double delta) {
        var dif = VectorUtils.subtract(gn, sd);
        double scalar = VectorUtils.scalar(sd, dif);
        double y = getNorm(dif);
        double x = getNorm(sd);
        double discriminant = 4 * scalar * scalar + 4 * (delta * delta - x * x) * y * y;
        double s = (-2 * scalar + Math.sqrt(discriminant)) / (2 * y * y);
        return s;
    }

    private static Map<String, Double> getSteepestDescentChange(List<Function> functions,
                                                                Matrix J,
                                                                Map<String, Double> vector) {
        Matrix transposedJ = transpose(J);
        Map<String, Double> r = new HashMap<>();
        for (int i = 0; i < functions.size(); i++) {
            r.put("x" + i, functions.get(i).evaluate(vector));
        }
        return multByVector(transposedJ, r);
    }

    private static List<Map<String, Double>> abstractMethod(List<Function> functions, Method method, double... args) {
        long start = System.nanoTime();
        final Set<String> variables = FunctionUtils.getAllVariables(new Sum(functions));
        final List<Map<String, Double>> res = new ArrayList<>();
        Map<String, Double> vector = initializeVector(variables);
        res.add(Map.copyOf(vector));
        int countIterations = 0;
        FunctionalMatrix jacobian = jacobian(functions, variables);
        while (countIterations < MAX_COUNT_OF_ITERATIONS) {
            if (countIterations == 50) {
                System.out.println();
            }
            double maxDiff = 0;
            Matrix J = evaluate(jacobian, vector);
            Map<String, Double> direction;
            if (method == Method.GAUSS_NEWTON) {
                direction = getGaussNewtonChange(functions, J, vector);
            } else {
                direction = getPowellDogLegChange(functions, J, vector, args[0]);
            }
            double diff = getMaxDiffAndChangeVector(vector, direction);
            maxDiff = Math.max(maxDiff, diff);
            res.add(vector);
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

    public static Map<String, Double> polynomialRegression(List<Double> x, List<Double> y, int n, Method method, double... args) {
        List<Function> functions = new ArrayList<>();
        for (int i = 0; i < y.size(); i++) {
            List<Function> sum = new ArrayList<>();
            sum.add(new Variable("x0"));
            for (int j = 1; j <= n; j++) {
                sum.add(new Multiply(new Variable("x" + j), new Pow(new Const(x.get(i)), new Const(j))));
            }
            functions.add(new Pow(new Subtract(new Sum(sum), new Const(y.get(i))), new Const(2.0)));
        }
        Map<String, Double> res = bfgs(new Sum(functions));
        return res;
//        if (method == Method.GAUSS_NEWTON) {
//            res = gaussNewton(new Sum(functions));
//        } else {
//            res = powellDogLeg(new Sum(functions), args[0]);
//        }
    }

    public static Map<String, Double> bfgs(MultipleArgumentFunction function) {
        int k = 0;
        var variables = getAllVariables(function);
        var initial = initializeVector(variables);
        var grad = getGradient(function, initial);
        var oldGrad = grad;
        Matrix I = eye(initial.size());
        Matrix Hk = I;
        var x = initial;
        var oldX = x;

        while (getMaxDiff(grad) > EPS && k++ < MAX_COUNT_OF_ITERATIONS) {
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
            Hk = MatrixUtils.add(multiply(A1, multiply(Hk, A2)), toMatrixProduct(multiply(s, ro), s));
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
        var I = eye(initial.size());

        Deque<Map<String, Double>> sList = new ArrayDeque<>(m);
        Deque<Map<String, Double>> yList = new ArrayDeque<>(m);
        Deque<Double> rhoList = new ArrayDeque<>(m);
        Deque<Double> alphaList = new ArrayDeque<>(m);

        while (getMaxDiff(grad) > EPS && k++ < MAX_COUNT_OF_ITERATIONS) {
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
