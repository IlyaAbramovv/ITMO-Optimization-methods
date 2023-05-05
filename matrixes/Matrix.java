package matrixes;

public class Matrix {
    private double[][] matrix;

    public Matrix(double[][] matrix) {
        this.matrix = matrix;
    }

    public double[][] getMatrix() {
        return matrix;
    }

    public int size() {
        return matrix.length;
    }

    public double[] getNthRow(int n) {
        return getMatrix()[n];
    }

    public double get(int i, int j) {
        return getMatrix()[i][j];
    }
}