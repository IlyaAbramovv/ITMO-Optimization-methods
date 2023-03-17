package lab1.functions;

import lab1.matrixes.MatrixUtils;

import java.util.*;

public class FunctionUtils {
    public static Map<String, Double> getGradient(Function function, Map<String, Double> vector) {
        Map<String, Double> gradient = new HashMap<>();
        for (var entry : vector.entrySet()) {
            gradient.put(entry.getKey(), function.differentiate(entry.getKey()).evaluate(vector));
        }
        return gradient;
    }

    public static Set<String> getAllVariables(Function function) {
        Set<String> set = new HashSet<>();
        getAllVariablesRec(function, set);
        return set;
    }

    private static void getAllVariablesRec(Function function, Set<String> set) {
        if (function instanceof SingleArgumentFunction) {
            getAllVariablesRec(((SingleArgumentFunction) function).getFunction(), set);
        } else if (function instanceof DoubleArgumentFunction) {
            getAllVariablesRec(((DoubleArgumentFunction) function).getFunction1(), set);
            getAllVariablesRec(((DoubleArgumentFunction) function).getFunction2(), set);
        } else if (function instanceof MultipleArgumentFunction) {
            ((MultipleArgumentFunction) function).getFunctions().
                    forEach(function1 -> getAllVariablesRec(function1, set));
        } else if (function instanceof Variable) {
            set.add(((Variable) function).getValue());
        }
    }

    public static Function generateFunction(int n, int k) {
        double[][] A = MatrixUtils.generateMatrix(n, k).getMatrix();
        double[] b = new double[n];
        for (int i = 0; i < n; i++) {
            b[i] = new Random().nextDouble() * k;
        }
        double c = new Random().nextDouble() * k;
        List<Function> xAx = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                xAx.add(new Mult(List.of(new Const(A[i][j]),
                        new Variable("x" + (i + 1)), new Variable("x" + (j + 1)))));
            }
        }
        List<Function> bx = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            bx.add(new Multiply(new Variable("x" + (i + 1)), new Const(b[i])));
        }
        return new Sum(List.of(
                new Sum(List.copyOf(xAx)),
                new Sum(List.copyOf(bx)),
                new Const(c)
        ));
    }
}
