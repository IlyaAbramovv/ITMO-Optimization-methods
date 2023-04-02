import functions.MultipleArgumentFunction;

import java.util.List;
import java.util.Map;
import java.util.Set;

@FunctionalInterface
public interface GDOneIterFunction {
    double apply(
            MultipleArgumentFunction function,
            int batchSize,
            Set<String> variables,
            List<Map<String, Double>> vectors,
            int countIterations,
            Mode mode,
            List<Map<String, Double>> res
    );
}
