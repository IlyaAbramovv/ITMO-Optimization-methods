package matrixes;

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
}
