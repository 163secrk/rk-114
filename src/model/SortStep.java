package model;

import java.util.Arrays;

public class SortStep {
    public enum StepType {
        COMPARE,
        SWAP,
        COMPARE_AND_SWAP,
        PLACE_SORTED,
        COMPLETE,
        INIT,
        PIVOT_SELECT,
        PARTITION_START,
        PARTITION_END
    }

    private final int[] array;
    private final StepType type;
    private final int index1;
    private final int index2;
    private final String description;
    private final int comparisons;
    private final int swaps;
    private final int pivotIndex;
    private final int leftBound;
    private final int rightBound;

    public SortStep(int[] array, StepType type, int index1, int index2, String description, int comparisons, int swaps) {
        this(array, type, index1, index2, description, comparisons, swaps, -1, -1, -1);
    }

    public SortStep(int[] array, StepType type, int index1, int index2, String description,
                    int comparisons, int swaps, int pivotIndex, int leftBound, int rightBound) {
        this.array = Arrays.copyOf(array, array.length);
        this.type = type;
        this.index1 = index1;
        this.index2 = index2;
        this.description = description;
        this.comparisons = comparisons;
        this.swaps = swaps;
        this.pivotIndex = pivotIndex;
        this.leftBound = leftBound;
        this.rightBound = rightBound;
    }

    public int[] getArray() {
        return array;
    }

    public StepType getType() {
        return type;
    }

    public int getIndex1() {
        return index1;
    }

    public int getIndex2() {
        return index2;
    }

    public String getDescription() {
        return description;
    }

    public int getComparisons() {
        return comparisons;
    }

    public int getSwaps() {
        return swaps;
    }

    public int getPivotIndex() {
        return pivotIndex;
    }

    public int getLeftBound() {
        return leftBound;
    }

    public int getRightBound() {
        return rightBound;
    }
}
