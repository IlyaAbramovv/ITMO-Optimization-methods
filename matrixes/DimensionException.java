package matrixes;

public class DimensionException extends IllegalArgumentException {
    public DimensionException(int size1, int size2) {
        super(String.format("Both objects must have the same size: %d != %d", size1, size2));
    }

    public static void assertCorrectDimensions(int size1, int size2) {
        if (size1 != size2) {
            throw new DimensionException(size1, size2);
        }
    }
}
