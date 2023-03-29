package matrixes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MatrixUtils {
    public static Matrix multiply(Matrix a, Matrix b) {
        int n = a.size();
        double[][] res = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double sum = 0;
                for (int k = 0; k < n; k++) {
                    sum += a.getMatrix()[i][k] * b.getMatrix()[k][j];
                }
                res[i][j] = sum;
            }
        }
        return new Matrix(res);
    }

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
        double[][] matrix = new double[n][n];
        for (int i = 0; i < n; i++) {
            matrix[i][i] = 1;
        }
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

    public static Matrix transpose(Matrix a) {
        int n = a.size();
        double[][] matrix = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[j][i] = a.getMatrix()[i][j];
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
}