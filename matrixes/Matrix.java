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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                sb.append(matrix[i][j] + " ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}