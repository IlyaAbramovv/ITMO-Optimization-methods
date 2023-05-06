package matrixes;

import functions.Function;

public class FunctionalMatrix {
    private Function[][] matrix;

    public FunctionalMatrix(Function[][] matrix) {
        this.matrix = matrix;
    }

    public Function[][] getMatrix() {
        return matrix;
    }

    public int size() {
        return matrix.length;
    }

    public Function[] getNthRow(int n) {
        return getMatrix()[n];
    }

    public Function get(int i, int j) {
        return getMatrix()[i][j];
    }
}