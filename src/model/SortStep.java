package model;

import java.util.Arrays;

public class SortStep {
    public enum StepType {
        COMPARE,
        SWAP,
        COMPARE_AND_SWAP,
        PLACE_SORTED,
        COMPLETE,
        INIT
    }

    private final int[] array;
    private final StepType type;
    private final int index1;
    private final int index2;
    private final String description;
    private final int comparisons;
    private final int swaps;

    public SortStep(int[] array, StepType type, int index1, int index2, String description, int comparisons, int swaps) {
        this.array = Arrays.copyOf(array, array.length);
        this.type = type;
        this.index1 = index1;
        this.index2 = index2;
        this.description = description;
        this.comparisons = comparisons;
        this.swaps = swaps;
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
}
