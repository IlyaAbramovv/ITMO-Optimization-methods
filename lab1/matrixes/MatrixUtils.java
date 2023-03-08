package lab1.matrixes;

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

    public static Matrix genMatrix(int n, int k) {
        Matrix a = genMatrix(n);
        double[][] matrixB = new double[n][n];
        matrixB[0][0] = k;
        if (n > 1) {
            matrixB[1][1] = 1;
        }
        for (int i = 2; i < n; i++) {
            matrixB[i][i] = new Random().nextInt(k) % k + 1;
        }
        Matrix c = inverse(a);
        return multiply(multiply(a, new Matrix(matrixB)), c);
    }

    public static Matrix genMatrix(int n) {
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
}
