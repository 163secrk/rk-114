package ui;

import model.SortStep;
import model.SortStep.StepType;

import javax.swing.*;
import java.awt.*;
import java.util.Set;
import java.util.HashSet;

public class SortCanvas extends JPanel {

    private SortStep currentStep;
    private int arraySize;
    private Set<Integer> sortedIndices;
    private int completionAnimateIndex = -1;
    private long completionAnimateStartTime = 0;
    private static final int COMPLETION_ANIMATE_DURATION = 300;

    private static final Color COLOR_DEFAULT = new Color(100, 149, 237);
    private static final Color COLOR_COMPARE1 = new Color(255, 165, 0);
    private static final Color COLOR_COMPARE2 = new Color(255, 100, 100);
    private static final Color COLOR_SWAP = new Color(186, 85, 211);
    private static final Color COLOR_SORTED = new Color(60, 179, 113);
    private static final Color COLOR_PENDING_SORT = new Color(70, 130, 180);
    private static final Color COLOR_BACKGROUND = new Color(245, 247, 250);
    private static final Color COLOR_DESCRIPTION_BG = new Color(255, 255, 255, 230);

    public SortCanvas() {
        this.currentStep = null;
        this.sortedIndices = new HashSet<>();
        setBackground(COLOR_BACKGROUND);
        setDoubleBuffered(true);
    }

    public void setCurrentStep(SortStep step) {
        this.currentStep = step;
        if (step != null && step.getArray() != null) {
            this.arraySize = step.getArray().length;
            if (step.getType() == StepType.PLACE_SORTED && step.getIndex1() >= 0) {
                sortedIndices.add(step.getIndex1());
            } else if (step.getType() == StepType.COMPLETE) {
                if (step.getIndex1() >= 0) {
                    sortedIndices.add(step.getIndex1());
                }
                if (step.getIndex1() >= 0 && step.getIndex2() == -1) {
                    completionAnimateIndex = step.getIndex1();
                    completionAnimateStartTime = System.currentTimeMillis();
                }
            } else if (step.getType() == StepType.INIT) {
                sortedIndices.clear();
                completionAnimateIndex = -1;
            }
        }
        repaint();
    }

    public void resetState() {
        this.sortedIndices.clear();
        this.completionAnimateIndex = -1;
        this.currentStep = null;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        if (currentStep == null) {
            g2d.setColor(new Color(150, 150, 150));
            g2d.setFont(new Font("微软雅黑", Font.PLAIN, 20));
            String msg = "请从左侧选择算法并点击开始";
            FontMetrics fm = g2d.getFontMetrics();
            int x = (width - fm.stringWidth(msg)) / 2;
            int y = height / 2;
            g2d.drawString(msg, x, y);
            return;
        }

        paintDescription(g2d, width);

        int topPadding = 100;
        int bottomPadding = 60;
        int leftPadding = 50;
        int rightPadding = 50;

        int chartHeight = height - topPadding - bottomPadding;
        int chartWidth = width - leftPadding - rightPadding;

        int[] arr = currentStep.getArray();
        int n = arr.length;

        int maxVal = 0;
        for (int val : arr) {
            if (val > maxVal) maxVal = val;
        }
        if (maxVal == 0) maxVal = 1;

        double barTotalWidth = (double) chartWidth / n;
        double barWidth = Math.max(barTotalWidth * 0.75, 4);
        double gap = barTotalWidth - barWidth;

        g2d.setColor(new Color(220, 220, 230));
        g2d.setStroke(new BasicStroke(1));
        int gridLines = 5;
        for (int i = 0; i <= gridLines; i++) {
            int y = topPadding + (int) ((double) i / gridLines * chartHeight);
            g2d.drawLine(leftPadding, y, width - rightPadding, y);
            g2d.setColor(new Color(150, 150, 160));
            String label = String.valueOf(maxVal - (int) ((double) i / gridLines * maxVal));
            g2d.drawString(label, leftPadding - 35, y + 5);
            g2d.setColor(new Color(220, 220, 230));
        }

        for (int i = 0; i < n; i++) {
            double barHeight = (double) arr[i] / maxVal * chartHeight;
            double x = leftPadding + i * barTotalWidth + gap / 2;
            double y = topPadding + chartHeight - barHeight;

            Color barColor = getBarColor(i);
            boolean isAnimating = (i == completionAnimateIndex &&
                    System.currentTimeMillis() - completionAnimateStartTime < COMPLETION_ANIMATE_DURATION);

            if (isAnimating) {
                long elapsed = System.currentTimeMillis() - completionAnimateStartTime;
                float progress = (float) elapsed / COMPLETION_ANIMATE_DURATION;
                if (progress > 1) progress = 1;
                barColor = interpolateColor(COLOR_DEFAULT, COLOR_SORTED, progress);
                barHeight = barHeight * (1 + 0.15 * Math.sin(progress * Math.PI));
                y = topPadding + chartHeight - barHeight;
            }

            drawBar(g2d, (int) x, (int) y, (int) barWidth, (int) barHeight, barColor,
                    isHighlighted(i), arr[i], i);
        }

        g2d.setColor(new Color(120, 120, 130));
        g2d.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        for (int i = 0; i < n; i++) {
            double x = leftPadding + i * barTotalWidth + gap / 2 + barWidth / 2;
            g2d.drawString(String.valueOf(i + 1), (int) x - 6, topPadding + chartHeight + 20);
        }

        if (completionAnimateIndex >= 0 &&
                System.currentTimeMillis() - completionAnimateStartTime < COMPLETION_ANIMATE_DURATION) {
            repaint();
        }
    }

    private void paintDescription(Graphics2D g2d, int width) {
        int descHeight = 60;
        int descX = 20;
        int descY = 15;
        int descWidth = width - 40;

        g2d.setColor(COLOR_DESCRIPTION_BG);
        g2d.fillRoundRect(descX, descY, descWidth, descHeight, 15, 15);
        g2d.setColor(new Color(200, 200, 210));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(descX, descY, descWidth, descHeight, 15, 15);

        StepType type = currentStep.getType();
        Color titleColor;
        String titleText;
        switch (type) {
            case COMPARE:
                titleColor = COLOR_COMPARE1;
                titleText = "🔍 比较操作";
                break;
            case SWAP:
                titleColor = COLOR_SWAP;
                titleText = "🔄 交换操作";
                break;
            case PLACE_SORTED:
                titleColor = COLOR_PENDING_SORT;
                titleText = "📌 元素就位";
                break;
            case COMPLETE:
                titleColor = COLOR_SORTED;
                titleText = "✅ 完成步骤";
                break;
            default:
                titleColor = new Color(80, 100, 130);
                titleText = "📋 操作说明";
        }

        g2d.setColor(titleColor);
        g2d.setFont(new Font("微软雅黑", Font.BOLD, 14));
        g2d.drawString(titleText, descX + 20, descY + 25);

        g2d.setColor(new Color(60, 60, 80));
        g2d.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        String desc = currentStep.getDescription();
        if (desc.length() > 70) {
            desc = desc.substring(0, 67) + "...";
        }
        g2d.drawString(desc, descX + 20, descY + 48);
    }

    private void drawBar(Graphics2D g2d, int x, int y, int width, int height,
                         Color color, boolean highlighted, int value, int index) {
        if (highlighted) {
            g2d.setColor(new Color(255, 215, 0, 100));
            g2d.fillRoundRect(x - 4, y - 4, width + 8, height + 8, 8, 8);
        }

        GradientPaint gradient = new GradientPaint(
                x, y, lighter(color, 30),
                x, y + height, darker(color, 20)
        );
        g2d.setPaint(gradient);
        g2d.fillRoundRect(x, y, width, height, 6, 6);

        g2d.setColor(darker(color, 40));
        g2d.setStroke(new BasicStroke(highlighted ? 2.5f : 1.2f));
        g2d.drawRoundRect(x, y, width, height, 6, 6);

        if (highlighted) {
            g2d.setColor(new Color(255, 255, 255, 60));
            g2d.fillRoundRect(x + 2, y + 2, width - 4, Math.max(height / 3, 4), 4, 4);
        }

        g2d.setColor(new Color(255, 255, 255));
        g2d.setFont(new Font("微软雅黑", Font.BOLD, Math.max(10, Math.min(14, width / 3))));
        FontMetrics fm = g2d.getFontMetrics();
        String valStr = String.valueOf(value);
        int textX = x + (width - fm.stringWidth(valStr)) / 2;
        int textY;
        if (height > 30) {
            textY = y + 20;
        } else {
            textY = y - 8;
            g2d.setColor(new Color(50, 50, 70));
        }
        g2d.drawString(valStr, textX, textY);
    }

    private Color getBarColor(int index) {
        StepType type = currentStep.getType();
        int i1 = currentStep.getIndex1();
        int i2 = currentStep.getIndex2();

        if (sortedIndices.contains(index) && type != StepType.COMPARE && type != StepType.SWAP) {
            return COLOR_SORTED;
        }

        switch (type) {
            case COMPARE:
                if (index == i1) return COLOR_COMPARE1;
                if (index == i2) return COLOR_COMPARE2;
                break;
            case SWAP:
                if (index == i1 || index == i2) return COLOR_SWAP;
                break;
            case PLACE_SORTED:
                if (index == i1) return COLOR_PENDING_SORT;
                break;
            case COMPLETE:
                if (sortedIndices.contains(index)) return COLOR_SORTED;
                break;
        }

        if (sortedIndices.contains(index)) {
            return COLOR_SORTED;
        }

        return COLOR_DEFAULT;
    }

    private boolean isHighlighted(int index) {
        StepType type = currentStep.getType();
        if (type == StepType.COMPARE || type == StepType.SWAP) {
            return index == currentStep.getIndex1() || index == currentStep.getIndex2();
        }
        if (type == StepType.PLACE_SORTED) {
            return index == currentStep.getIndex1();
        }
        return false;
    }

    private Color lighter(Color c, int amount) {
        int r = Math.min(255, c.getRed() + amount);
        int g = Math.min(255, c.getGreen() + amount);
        int b = Math.min(255, c.getBlue() + amount);
        return new Color(r, g, b);
    }

    private Color darker(Color c, int amount) {
        int r = Math.max(0, c.getRed() - amount);
        int g = Math.max(0, c.getGreen() - amount);
        int b = Math.max(0, c.getBlue() - amount);
        return new Color(r, g, b);
    }

    private Color interpolateColor(Color c1, Color c2, float t) {
        int r = (int) (c1.getRed() + (c2.getRed() - c1.getRed()) * t);
        int g = (int) (c1.getGreen() + (c2.getGreen() - c1.getGreen()) * t);
        int b = (int) (c1.getBlue() + (c2.getBlue() - c1.getBlue()) * t);
        return new Color(r, g, b);
    }
}
