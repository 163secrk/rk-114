package algorithm;

import model.SortStep;
import model.SortStep.StepType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class QuickSort implements SortAlgorithm {

    @Override
    public String getName() {
        return "快速排序";
    }

    @Override
    public String getDescription() {
        return "快速排序（Quick Sort）\n\n" +
               "• 时间复杂度：O(n log n) 平均 / O(n²) 最坏\n" +
               "• 空间复杂度：O(log n)\n" +
               "• 稳定性：不稳定排序\n\n" +
               "算法思想：\n" +
               "选择一个基准元素（pivot），将数组分为两部分：\n" +
               "小于pivot的放左边，大于pivot的放右边。\n" +
               "然后递归地对左右两部分进行排序。";
    }

    @Override
    public List<SortStep> generateSteps(int[] inputArray) {
        int[] arr = Arrays.copyOf(inputArray, inputArray.length);
        List<SortStep> steps = new ArrayList<>();
        int n = arr.length;
        int[] counters = new int[2];
        int comparisons = counters[0];
        int swaps = counters[1];

        steps.add(new SortStep(arr, StepType.INIT, -1, -1,
                "开始快速排序，数组长度：" + n,
                comparisons, swaps, -1, 0, n - 1));

        quickSortIterative(arr, 0, n - 1, steps, counters);

        comparisons = counters[0];
        swaps = counters[1];

        for (int i = 0; i < n; i++) {
            int[] tempArr = Arrays.copyOf(arr, n);
            steps.add(new SortStep(tempArr, StepType.COMPLETE, i, -1,
                    String.format("✓ 第%d个元素 %d 确认就位", i + 1, arr[i]),
                    comparisons, swaps, -1, -1, -1));
        }

        steps.add(new SortStep(arr, StepType.COMPLETE, -1, -1,
                String.format("🎉 排序完成！共进行 %d 次比较，%d 次交换", comparisons, swaps),
                comparisons, swaps, -1, -1, -1));

        return steps;
    }

    private void quickSortIterative(int[] arr, int low, int high,
                                    List<SortStep> steps, int[] counters) {
        int comparisons = counters[0];
        int swaps = counters[1];
        Stack<int[]> stack = new Stack<>();
        stack.push(new int[]{low, high});

        while (!stack.isEmpty()) {
            int[] range = stack.pop();
            int l = range[0];
            int h = range[1];

            if (l < h) {
                steps.add(new SortStep(arr, StepType.PARTITION_START, -1, -1,
                        String.format("开始处理分区 [%d, %d]", l + 1, h + 1),
                        comparisons, swaps, -1, l, h));

                int pivotIdx = h;
                steps.add(new SortStep(arr, StepType.PIVOT_SELECT, pivotIdx, -1,
                        String.format("选择第%d个元素 %d 作为基准（pivot）",
                                pivotIdx + 1, arr[pivotIdx]),
                        comparisons, swaps, pivotIdx, l, h));

                int i = l - 1;
                for (int j = l; j < h; j++) {
                    comparisons++;
                    if (arr[j] <= arr[h]) {
                        steps.add(new SortStep(arr, StepType.COMPARE, j, h,
                                String.format("比较第%d个元素 %d ≤ pivot %d，需要放到左边",
                                        j + 1, arr[j], arr[h]),
                                comparisons, swaps, pivotIdx, l, h));

                        i++;
                        if (i != j) {
                            steps.add(new SortStep(arr, StepType.SWAP, i, j,
                                    String.format("交换第%d和第%d个元素", i + 1, j + 1),
                                    comparisons, swaps, pivotIdx, l, h));

                            int temp = arr[i];
                            arr[i] = arr[j];
                            arr[j] = temp;
                            swaps++;

                            steps.add(new SortStep(arr, StepType.SWAP, i, j,
                                    String.format("交换完成：第%d个 = %d, 第%d个 = %d",
                                            i + 1, arr[i], j + 1, arr[j]),
                                    comparisons, swaps, pivotIdx, l, h));
                        } else {
                            steps.add(new SortStep(arr, StepType.COMPARE, i, j,
                                    String.format("第%d个元素已在正确位置", j + 1),
                                    comparisons, swaps, pivotIdx, l, h));
                        }
                    } else {
                        steps.add(new SortStep(arr, StepType.COMPARE, j, h,
                                String.format("比较第%d个元素 %d > pivot %d，留在右边",
                                        j + 1, arr[j], arr[h]),
                                comparisons, swaps, pivotIdx, l, h));
                    }
                }

                int pivotFinalPos = i + 1;
                if (pivotFinalPos != h) {
                    steps.add(new SortStep(arr, StepType.SWAP, pivotFinalPos, h,
                            String.format("将pivot放到正确位置：交换第%d和第%d个元素",
                                    pivotFinalPos + 1, h + 1),
                            comparisons, swaps, h, l, h));

                    int temp = arr[pivotFinalPos];
                    arr[pivotFinalPos] = arr[h];
                    arr[h] = temp;
                    swaps++;
                }

                steps.add(new SortStep(arr, StepType.PLACE_SORTED, pivotFinalPos, -1,
                        String.format("元素 %d 已就位（位置%d）",
                                arr[pivotFinalPos], pivotFinalPos + 1),
                        comparisons, swaps, -1, l, h));

                steps.add(new SortStep(arr, StepType.PARTITION_END, -1, -1,
                        String.format("分区 [%d, %d] 处理完成，pivot在位置%d",
                                l + 1, h + 1, pivotFinalPos + 1),
                        comparisons, swaps, -1, l, h));

                stack.push(new int[]{pivotFinalPos + 1, h});
                stack.push(new int[]{l, pivotFinalPos - 1});
            } else if (l == h) {
                steps.add(new SortStep(arr, StepType.PLACE_SORTED, l, -1,
                        String.format("单元素分区，元素 %d 已就位（位置%d）",
                                arr[l], l + 1),
                        comparisons, swaps, -1, l, h));
            }
        }

        counters[0] = comparisons;
        counters[1] = swaps;
    }
}
