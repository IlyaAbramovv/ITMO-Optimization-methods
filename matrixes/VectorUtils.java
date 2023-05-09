package matrixes;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public class VectorUtils {
    public static Map<String, Double> add(Map<String, Double> vector, Map<String, Double> other) {
        return vector.entrySet().stream()
                .map(e -> Map.entry(e.getKey(), e.getValue() + other.get(e.getKey())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static Map<String, Double> multiply(Map<String, Double> vector, double value) {
        return vector.entrySet().stream()
                .map(e -> Map.entry(e.getKey(), e.getValue() * value))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static Map<String, Double> subtract(Map<String, Double> vector, Map<String, Double> other) {
        return vector.entrySet().stream()
                .map(e -> Map.entry(e.getKey(), e.getValue() - other.get(e.getKey())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static Map<String, Double> multiply(Map<String, Double> vector, Map<String, Double> other) {
        return vector.entrySet().stream()
                .map(e -> Map.entry(e.getKey(), e.getValue() * other.get(e.getKey())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static Map<String, Double> negate(Map<String, Double> vector) {
        return vector.entrySet().stream().map(e -> Map.entry(e.getKey(), -e.getValue())).collect(Collectors.toMap(
                Map.Entry::getKey, Map.Entry::getValue));
    }

    public static double scalar(Map<String, Double> v1, Map<String, Double> v2) {
        DimensionException.assertCorrectDimensions(v1.size(), v2.size());
        double res = 0;
        for (int i = 0; i < v1.size(); i++) {
            String key = "x" + i;
            res += v1.get(key) * v2.get(key);
        }
        return res;
    }

    public static Matrix toMatrixProduct(Map<String, Double> v1, Map<String, Double> v2) {
        DimensionException.assertCorrectDimensions(v1.size(), v2.size());
        double[][] res = new double[v1.size()][v2.size()];
        for (int i = 0; i < v1.size(); i++) {
            for (int j = 0; j < v2.size(); j++) {
                res[i][j] = v1.get("x" + i) * v2.get("x" + j);
            }
        }
        return new Matrix(res);
    }

    public static double getMaxDiff(Map<String, Double> vec) {
        return vec.values().stream().map(Math::abs).max(Double::compare).get();
    }

    public static double getNorm(Map<String, Double> vec) {
        return Math.sqrt(vec.values().stream().map(val -> val * val).reduce(0.0, Double::sum));
    }
}
