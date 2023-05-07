package matrixes;

import functions.Function;

import java.util.*;
import java.util.function.DoubleBinaryOperator;

public class MatrixUtils {
    public static Matrix generateMatrix(int n, int k) {
        Matrix a = generateOrthogonalMatrix(n);
        double[][] matrixb = new double[n][n];
        List<Integer> lambdas = new ArrayList<>();
        lambdas.add(k);
        if (n > 1) {
            lambdas.add(1);
        }
        for (int i = 2; i < n; i++) {
            lambdas.add(new Random().nextInt(k) % k + 1);
        }
        lambdas.sort(Integer::compareTo);
        int i = n - 1;
        for (int lambda : lambdas) {
            matrixb[i][i] = lambda;
            i--;
        }
        Matrix b = new Matrix(matrixb);
        Matrix c = transpose(a);
        return multiply(multiply(a, b), c);
    }

    public static Matrix generateMatrix(int n) {
        double[][] matrix = eye(n).getMatrix();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double[] temp = matrix[j];
                double rand = new Random().nextInt(4) - 2;
                if (rand == 0) {
                    rand++;
                }
                for (int k = 0; k < n; k++) {
                    temp[k] = temp[k] * rand;
                }
                for (int k = 0; k < n; k++) {
                    matrix[i][k] += temp[k];
                }
            }
        }
        return new Matrix(matrix);
    }

    public static Matrix generateOrthogonalMatrix(int n) {
        double[][] matrix = MatrixUtils.generateMatrix(n).getMatrix();
        for (int i = 1; i < n; i++) {
            for (int k = 0; k < i; k++) {
                double[] orthogonalVector = getProjection(matrix[i], matrix[k]);
                for (int j = 0; j < n; j++) {
                    matrix[i][j] -= orthogonalVector[j];
                }
            }
        }
        for (int i = 0; i < n; i++) {
            normalize(matrix[i]);
        }
        return new Matrix(matrix);
    }

    public static void normalize(double[] vector) {
        double norma = 0;
        for (double value : vector) {
            norma += value * value;
        }
        norma = Math.sqrt(norma);
        for (int i = 0; i < vector.length; i++) {
            vector[i] /= norma;
        }
    }

    public static double[] getProjection(double[] v, double[] u) {
        double[] res = new double[v.length];
        double scalar = 0, norma = 0;
        for (int i = 0; i < v.length; i++) {
            scalar += v[i] * u[i];
            norma += u[i] * u[i];
        }
        for (int i = 0; i < u.length; i++) {
            res[i] = (scalar / norma) * u[i];
        }
        return res;
    }

    public static Matrix eye(int dim) {
        double[][] arr = new double[dim][dim];
        for (int i = 0; i < dim; i++) {
            arr[i][i] = 1;
        }
        return new Matrix(arr);
    }

    // Suppose all variables in vector has name "x{$i}"
    public static Map<String, Double> multByVector(Matrix matrix, Map<String, Double> vector) {
        int matrixDim = matrix.size();
        Map<String, Double> res = new HashMap<>();
        for (int i = 0; i < matrixDim; i++) {
            res.put("x" + i, scalarProduct(matrix.getNthRow(i), vector));
        }
        return res;
    }

    private static double scalarProduct(double[] row, Map<String, Double> vector) {
        DimensionException.assertCorrectDimensions(row.length, vector.size());
        double res = 0;
        for (int i = 0; i < row.length; i++) {
            res += row[i] * vector.get("x" + i);
        }
        return res;
    }

    private static Matrix biMatrixOperation(Matrix m1, Matrix m2, DoubleBinaryOperator f) {
        DimensionException.assertCorrectDimensions(m1.size(), m2.size());
        double[][] res = new double[m1.size()][m2.size()];
        for (int i = 0; i < m1.size(); i++) {
            for (int j = 0; j < m2.size(); j++) {
                res[i][j] = f.applyAsDouble(m1.get(i, j), m2.get(i, j));
            }
        }
        return new Matrix(res);
    }

    public static Matrix subtract(Matrix m1, Matrix m2) {
        return biMatrixOperation(m1, m2, (a, b) -> a - b);
    }

    public static Matrix add(Matrix m1, Matrix m2) {
        return biMatrixOperation(m1, m2, Double::sum);
    }

    public static FunctionalMatrix jacobian(List<Function> functions, Set<String> variables) {
        Function[][] res = new Function[functions.size()][variables.size()];
        for (int i = 0; i < functions.size(); i++) {
            for (int j = 0; j < variables.size(); j++) {
                res[i][j] = functions.get(i).differentiate("x" + j);
            }
        }
        return new FunctionalMatrix(res);
    }

    public static Matrix evaluate(FunctionalMatrix m, Map<String, Double> vector) {
        Function[][] matrix = m.getMatrix();
        double[][] res = new double[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                res[i][j] = matrix[i][j].evaluate(vector);
            }
        }
        return new Matrix(res);
    }

    public static Matrix inverse(Matrix a) {
        double temp;
        int n = a.size();
        double[][] matrix = new double[n][2 * n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = a.getMatrix()[i][j];
            }
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < 2 * n; j++) {
                if (j == (i + n)) {
                    matrix[i][j] = 1;
                }
            }
        }
        for (int i = n - 1; i > 0; i--) {
            if (matrix[i - 1][0] < matrix[i][0]) {
                double[] tempArr = matrix[i];
                matrix[i] = matrix[i - 1];
                matrix[i - 1] = tempArr;
            }
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (j != i) {
                    temp = matrix[j][i] / matrix[i][i];
                    for (int k = 0; k < 2 * n; k++) {
                        matrix[j][k] -= matrix[i][k] * temp;
                    }
                }
            }
        }
        for (int i = 0; i < n; i++) {
            temp = matrix[i][i];
            for (int j = 0; j < 2 * n; j++) {
                matrix[i][j] = matrix[i][j] / temp;
            }
        }
        double[][] res = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = n; j < 2 * n; j++) {
                res[i][j - n] = matrix[i][j];
            }
        }
        return new Matrix(res);
    }


    public static Matrix transpose(Matrix m) {
        double[][] matrix = m.getMatrix();
        int rows = matrix.length;
        int columns = matrix[0].length;
        double[][] transpose = new double[columns][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                transpose[j][i] = matrix[i][j];
            }
        }
        return new Matrix(transpose);
    }

    public static Matrix multiply(Matrix m1, Matrix m2) {
        double[][] A = m1.getMatrix();
        double[][] B = m2.getMatrix();
        int m = A.length;
        int n = A[0].length;
        int p = B[0].length;
        double[][] C = new double[m][p];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < p; j++) {
                double sum = 0;
                for (int k = 0; k < n; k++) {
                    sum += A[i][k] * B[k][j];
                }
                C[i][j] = sum;
            }
        }
        return new Matrix(C);
    }

    public static Matrix multiply(Matrix m, double x) {
        double[][] res = new double[m.size()][m.size()];
        for (int i = 0; i < m.size(); i++) {
            for (int j = 0; j < m.size(); j++) {
                res[i][j] = m.get(i, j) * x;
            }
        }
        return new Matrix(res);
    }
}