package algorithm;

import model.SortStep;
import java.util.List;

public interface SortAlgorithm {
    String getName();
    String getDescription();
    List<SortStep> generateSteps(int[] array);
}
