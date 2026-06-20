package algorithm;

import model.SortStep;
import model.SortStep.StepType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BubbleSort implements SortAlgorithm {

    @Override
    public String getName() {
        return "冒泡排序";
    }

    @Override
    public String getDescription() {
        return "冒泡排序（Bubble Sort）\n\n" +
               "• 时间复杂度：O(n²)\n" +
               "• 空间复杂度：O(1)\n" +
               "• 稳定性：稳定排序\n\n" +
               "算法思想：\n" +
               "重复遍历数组，比较相邻元素，如果顺序错误就交换它们。\n" +
               "每一轮遍历后，最大的元素会\"冒泡\"到数组末端。";
    }

    @Override
    public List<SortStep> generateSteps(int[] inputArray) {
        int[] arr = Arrays.copyOf(inputArray, inputArray.length);
        List<SortStep> steps = new ArrayList<>();
        int n = arr.length;
        int comparisons = 0;
        int swaps = 0;

        steps.add(new SortStep(arr, StepType.INIT, -1, -1,
                "开始冒泡排序，数组长度：" + n, 0, 0));

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                comparisons++;
                if (arr[j] > arr[j + 1]) {
                    steps.add(new SortStep(arr, StepType.COMPARE, j, j + 1,
                            String.format("比较第%d和第%d个元素：%d > %d，需要交换",
                                    j + 1, j + 2, arr[j], arr[j + 1]),
                            comparisons, swaps));

                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swaps++;
                    swapped = true;

                    steps.add(new SortStep(arr, StepType.SWAP, j, j + 1,
                            String.format("交换第%d和第%d个元素，完成一次交换",
                                    j + 1, j + 2),
                            comparisons, swaps));
                } else {
                    steps.add(new SortStep(arr, StepType.COMPARE, j, j + 1,
                            String.format("比较第%d和第%d个元素：%d ≤ %d，无需交换",
                                    j + 1, j + 2, arr[j], arr[j + 1]),
                            comparisons, swaps));
                }
            }

            steps.add(new SortStep(arr, StepType.PLACE_SORTED, n - 1 - i, -1,
                    String.format("第%d轮结束，元素 %d 已就位（位置%d）",
                            i + 1, arr[n - 1 - i], n - i),
                    comparisons, swaps));

            if (!swapped) {
                break;
            }
        }

        for (int i = 0; i < n; i++) {
            int[] tempArr = Arrays.copyOf(arr, n);
            steps.add(new SortStep(tempArr, StepType.COMPLETE, i, -1,
                    String.format("✓ 第%d个元素 %d 确认就位", i + 1, arr[i]),
                    comparisons, swaps));
        }

        steps.add(new SortStep(arr, StepType.COMPLETE, -1, -1,
                String.format("🎉 排序完成！共进行 %d 次比较，%d 次交换", comparisons, swaps),
                comparisons, swaps));

        return steps;
    }
}
