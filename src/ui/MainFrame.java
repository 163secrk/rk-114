package ui;

import algorithm.SortAlgorithm;
import model.SortStep;
import model.SortStep.StepType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Random;

public class MainFrame extends JFrame {

    private final AlgorithmListPanel algorithmListPanel;
    private final SortCanvas sortCanvas;
    private final ControlPanel controlPanel;

    private int[] currentArray;
    private List<SortStep> steps;
    private int currentStepIndex;
    private boolean isStarted;
    private Timer animationTimer;
    private int speed = 50;

    private static final int DEFAULT_ARRAY_SIZE = 12;
    private static final int DEFAULT_MIN_VALUE = 5;
    private static final int DEFAULT_MAX_VALUE = 99;

    public MainFrame() {
        super("算法可视化学习工具");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 820);
        setMinimumSize(new Dimension(1024, 680));
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        algorithmListPanel = new AlgorithmListPanel();
        sortCanvas = new SortCanvas();
        controlPanel = new ControlPanel();

        setupLayout();
        setupControlListener();
        generateRandomArray();

        setVisible(true);
    }

    private void setupLayout() {
        JPanel rootPanel = new JPanel(new BorderLayout(0, 0));
        rootPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        rootPanel.setBackground(new Color(245, 247, 250));

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                algorithmListPanel,
                createCanvasWrapper()
        );
        splitPane.setDividerLocation(270);
        splitPane.setResizeWeight(0.0);
        splitPane.setDividerSize(6);
        splitPane.setBorder(null);
        splitPane.setBackground(new Color(200, 200, 210));
        splitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, e -> {
            int loc = (int) e.getNewValue();
            if (loc < 200) {
                splitPane.setDividerLocation(200);
            }
        });

        JPanel canvasContainer = new JPanel(new BorderLayout(0, 0));
        canvasContainer.setBackground(new Color(245, 247, 250));
        canvasContainer.setBorder(new EmptyBorder(8, 8, 0, 8));
        canvasContainer.add(splitPane, BorderLayout.CENTER);
        canvasContainer.add(controlPanel, BorderLayout.SOUTH);

        rootPanel.add(createHeader(), BorderLayout.NORTH);
        rootPanel.add(canvasContainer, BorderLayout.CENTER);

        setContentPane(rootPanel);
    }

    private Component createCanvasWrapper() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 0));
        wrapper.setBackground(new Color(245, 247, 250));

        JPanel canvasPanel = new JPanel(new BorderLayout());
        canvasPanel.setBackground(Color.WHITE);
        canvasPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 210), 1, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        canvasPanel.add(sortCanvas, BorderLayout.CENTER);
        wrapper.add(canvasPanel, BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel createHeader() {
        final Color c1 = new Color(60, 100, 180);
        final Color c2 = new Color(100, 149, 237);
        JPanel header = new JPanel(new BorderLayout(15, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                        RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, c1, 0, getHeight(), c2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setBorder(new EmptyBorder(10, 18, 10, 18));
        header.setBackground(c1);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("🎓 算法可视化学习工具");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Algorithm Visualization Learning Platform  —  理解算法，一步一步来");
        subtitleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(220, 230, 255));
        subtitleLabel.setBorder(new EmptyBorder(2, 0, 0, 2));

        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);

        header.add(titlePanel, BorderLayout.WEST);

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        infoPanel.setOpaque(false);

        JLabel sizeLabel = new JLabel("📊 数组大小：" + DEFAULT_ARRAY_SIZE);
        sizeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        sizeLabel.setForeground(Color.WHITE);
        infoPanel.add(sizeLabel);

        JLabel rangeLabel = new JLabel("🔢 数值范围：" + DEFAULT_MIN_VALUE + " ~ " + DEFAULT_MAX_VALUE);
        rangeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        rangeLabel.setForeground(Color.WHITE);
        infoPanel.add(rangeLabel);

        header.add(infoPanel, BorderLayout.EAST);

        return header;
    }

    private void setupControlListener() {
        controlPanel.setControlListener(new ControlPanel.ControlListener() {
            @Override
            public void onPlayPause() {
                handlePlayPause();
            }

            @Override
            public void onStepForward() {
                handleStepForward();
            }

            @Override
            public void onStepBackward() {
                handleStepBackward();
            }

            @Override
            public void onReset() {
                handleReset();
            }

            @Override
            public void onStart() {
                handleStart();
            }

            @Override
            public void onGenerateNew() {
                handleGenerateNew();
            }

            @Override
            public void onSpeedChanged(int newSpeed) {
                speed = newSpeed;
                if (animationTimer != null && animationTimer.isRunning()) {
                    animationTimer.setDelay(speedToDelay(speed));
                }
            }
        });
    }

    private void generateRandomArray() {
        Random random = new Random();
        currentArray = new int[DEFAULT_ARRAY_SIZE];
        for (int i = 0; i < DEFAULT_ARRAY_SIZE; i++) {
            currentArray[i] = DEFAULT_MIN_VALUE +
                    random.nextInt(DEFAULT_MAX_VALUE - DEFAULT_MIN_VALUE + 1);
        }
    }

    private void handleStart() {
        if (isStarted) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "可视化已经在运行中，是否重新开始？",
                    "确认",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (choice != JOptionPane.YES_OPTION) return;
        }
        stopAnimationTimer();

        SortAlgorithm algo = algorithmListPanel.getSelectedAlgorithm();
        steps = algo.generateSteps(currentArray);
        currentStepIndex = 0;
        isStarted = true;

        sortCanvas.resetState();
        updateUIForStep();

        controlPanel.setControlsEnabled(true);
        controlPanel.setPlaying(false);

        algorithmListPanel.setStatus("✅ 已启动：" + algo.getName());

        startAnimationTimer();
        controlPanel.setPlaying(true);
    }

    private void handleGenerateNew() {
        stopAnimationTimer();
        generateRandomArray();
        currentStepIndex = 0;
        steps = null;
        isStarted = false;

        sortCanvas.resetState();
        controlPanel.setControlsEnabled(false);
        controlPanel.setPlaying(false);
        controlPanel.setStepCount(0, 0);
        controlPanel.setCompareCount(0);
        controlPanel.setSwapCount(0);
        controlPanel.setProgress(0);

        algorithmListPanel.setStatus("🎲 已生成新的随机数组");
    }

    private void handlePlayPause() {
        if (!isStarted) return;
        if (animationTimer != null && animationTimer.isRunning()) {
            stopAnimationTimer();
            controlPanel.setPlaying(false);
        } else {
            startAnimationTimer();
            controlPanel.setPlaying(true);
        }
    }

    private void handleStepForward() {
        if (!isStarted) return;
        stopAnimationTimer();
        controlPanel.setPlaying(false);
        if (currentStepIndex < steps.size() - 1) {
            currentStepIndex++;
            updateUIForStep();
        }
    }

    private void handleStepBackward() {
        if (!isStarted) return;
        stopAnimationTimer();
        controlPanel.setPlaying(false);
        if (currentStepIndex > 0) {
            currentStepIndex--;
            sortCanvas.resetState();
            for (int i = 0; i <= currentStepIndex; i++) {
                SortStep s = steps.get(i);
                if (s.getType() == StepType.PLACE_SORTED && s.getIndex1() >= 0) {
                    sortCanvas.setCurrentStep(s);
                }
            }
            updateUIForStep();
        }
    }

    private void handleReset() {
        stopAnimationTimer();
        currentStepIndex = 0;
        sortCanvas.resetState();
        if (steps != null && !steps.isEmpty()) {
            updateUIForStep();
        }
        controlPanel.setPlaying(false);
    }

    private void startAnimationTimer() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
        animationTimer = new Timer(speedToDelay(speed), e -> {
            if (currentStepIndex < steps.size() - 1) {
                currentStepIndex++;
                updateUIForStep();
            } else {
                stopAnimationTimer();
                controlPanel.setPlaying(false);
                algorithmListPanel.setStatus("🎉 排序完成！");
            }
        });
        animationTimer.setCoalesce(true);
        animationTimer.start();
    }

    private void stopAnimationTimer() {
        if (animationTimer != null) {
            animationTimer.stop();
            animationTimer = null;
        }
    }

    private int speedToDelay(int speed) {
        int maxDelay = 2000;
        int minDelay = 10;
        return maxDelay - (int) ((double) (speed - 1) / 99 * (maxDelay - minDelay));
    }

    private void updateUIForStep() {
        if (steps == null || steps.isEmpty()) return;
        SortStep step = steps.get(currentStepIndex);
        sortCanvas.setCurrentStep(step);

        controlPanel.setStepCount(currentStepIndex + 1, steps.size());
        controlPanel.setCompareCount(step.getComparisons());
        controlPanel.setSwapCount(step.getSwaps());
        controlPanel.setProgress((double) currentStepIndex / (steps.size() - 1));
    }
}
