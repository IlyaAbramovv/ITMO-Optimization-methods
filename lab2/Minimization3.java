package lab2;

import functions.*;
import matrixes.Matrix;
import matrixes.MatrixUtils;

import java.util.*;

import static functions.FunctionUtils.getAllVariables;
import static functions.FunctionUtils.getGradient;
import static lab1.Minimization1.getBestAlpha;
import static lab2.Minimization2.initializeVector;
import static matrixes.MatrixUtils.multByVector;
import static matrixes.VectorUtils.*;

public class Minimization3 {

    private static final int MAX_ITERS = 10_000;

    public static Map<String, Double> bfgs(MultipleArgumentFunction function) {
        final double tolerance = 1e-7;

        int k = 0;
        var variables = getAllVariables(function);
        var initial = initializeVector(variables);
        var grad = getGradient(function, initial);
        var oldGrad = grad;
        Matrix I = MatrixUtils.eye(initial.size());
        Matrix Hk = I;
        var x = initial;
        var oldX = x;

        while (getNorm(grad) > tolerance && k++ < MAX_ITERS) {
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
            Hk = MatrixUtils.add(MatrixUtils.multiply(A1, MatrixUtils.multiply(Hk, A2)), toMatrixProduct(multiply(s, ro), s));
        }
        return x;
    }

    public static Map<String, Double> lbfgs(MultipleArgumentFunction function, int m) {
        final double tolerance = 1e-7;

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

        while (getNorm(grad) > tolerance && k++ < MAX_ITERS) {
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


    public static void main(String[] args) {
        MultipleArgumentFunction fun = new Sum(List.of(
                new Pow(new Variable("x0"), new Const(2)),
                new Negate(new Multiply(new Variable("x0"), new Variable("x1"))),
                new Pow(new Variable("x1"), new Const(2)),
                new Multiply(new Variable("x0"), new Const(9)),
                new Negate(new Multiply(new Variable("x1"), new Const(6))),
                new Const(20)
        ));

//        List<Map<String, Double>> res = Minimization2.rmsPropGradientDescent(fun, 2);
//        System.out.println(res.get(res.size() - 1));
        System.out.println(lbfgs(fun, 3));
        System.out.println(bfgs(fun));
    }
}
