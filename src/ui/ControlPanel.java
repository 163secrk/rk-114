package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.event.ActionListener;

public class ControlPanel extends JPanel {

    public interface ControlListener {
        void onPlayPause();
        void onStepForward();
        void onStepBackward();
        void onReset();
        void onStart();
        void onGenerateNew();
        void onSpeedChanged(int speed);
    }

    private final JButton playPauseBtn;
    private final JButton stepBackBtn;
    private final JButton stepForwardBtn;
    private final JButton resetBtn;
    private final JButton startBtn;
    private final JButton generateBtn;
    private final JSlider speedSlider;
    private final JLabel speedValueLabel;

    private final JLabel stepCountLabel;
    private final JLabel compareCountLabel;
    private final JLabel swapCountLabel;
    private final JLabel progressLabel;

    private final JLabel stateLabel;

    private ControlListener listener;
    private boolean isPlaying = false;

    public ControlPanel() {
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setBackground(new Color(248, 249, 252));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 5));
        leftPanel.setOpaque(false);

        startBtn = createButton("▶ 开始", new Color(60, 179, 113));
        startBtn.setToolTipText("生成新数组并开始可视化");
        leftPanel.add(startBtn);

        leftPanel.add(Box.createHorizontalStrut(5));

        generateBtn = createButton("🎲 新数组", new Color(100, 149, 237));
        generateBtn.setToolTipText("生成新的随机数组");
        leftPanel.add(generateBtn);

        leftPanel.add(Box.createHorizontalStrut(5));
        JSeparator sep1 = new JSeparator(JSeparator.VERTICAL);
        sep1.setPreferredSize(new Dimension(2, 35));
        sep1.setForeground(new Color(200, 200, 210));
        leftPanel.add(sep1);
        leftPanel.add(Box.createHorizontalStrut(5));

        playPauseBtn = createButton("⏸ 暂停", new Color(255, 165, 0));
        playPauseBtn.setToolTipText("播放 / 暂停");
        playPauseBtn.setEnabled(false);
        leftPanel.add(playPauseBtn);

        stepBackBtn = createButton("⏪ 上一步", new Color(140, 140, 160));
        stepBackBtn.setToolTipText("退回到上一步");
        stepBackBtn.setEnabled(false);
        leftPanel.add(stepBackBtn);

        stepForwardBtn = createButton("⏩ 下一步", new Color(140, 140, 160));
        stepForwardBtn.setToolTipText("执行下一步");
        stepForwardBtn.setEnabled(false);
        leftPanel.add(stepForwardBtn);

        resetBtn = createButton("↻ 重置", new Color(255, 100, 100));
        resetBtn.setToolTipText("重置到初始状态");
        resetBtn.setEnabled(false);
        leftPanel.add(resetBtn);

        add(leftPanel, BorderLayout.WEST);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 0));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(2, 0, 2, 10));

        JPanel speedPanel = new JPanel(new BorderLayout(8, 0));
        speedPanel.setOpaque(false);
        speedPanel.setBorder(new TitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 210)),
                "速度",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("微软雅黑", Font.PLAIN, 11),
                new Color(100, 100, 120)
        ));

        speedSlider = new JSlider(JSlider.HORIZONTAL, 1, 100, 50);
        speedSlider.setOpaque(false);
        speedSlider.setPreferredSize(new Dimension(180, 30));
        speedSlider.setUI(new BasicSliderUI(speedSlider) {
            @Override
            public void paintTrack(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                int trackY = trackRect.y + trackRect.height / 2 - 2;
                g2d.setColor(new Color(200, 200, 220));
                g2d.fillRoundRect(trackRect.x, trackY, trackRect.width, 4, 4, 4);
                int fillWidth = (int) ((double) slider.getValue() / slider.getMaximum() * trackRect.width);
                GradientPaint grad = new GradientPaint(
                        trackRect.x, trackY, new Color(100, 149, 237),
                        trackRect.x + fillWidth, trackY, new Color(60, 179, 113)
                );
                g2d.setPaint(grad);
                g2d.fillRoundRect(trackRect.x, trackY, fillWidth, 4, 4, 4);
            }

            @Override
            public void paintThumb(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                int x = thumbRect.x + 4;
                int y = thumbRect.y + 4;
                int size = thumbRect.width - 8;
                g2d.setColor(new Color(100, 149, 237));
                g2d.fillOval(x, y, size, size);
                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.fillOval(x + 3, y + 3, size - 10, size - 10);
                g2d.setColor(new Color(80, 100, 150));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawOval(x, y, size, size);
            }
        });

        speedValueLabel = new JLabel("50 步/秒");
        speedValueLabel.setFont(new Font("Consolas", Font.BOLD, 12));
        speedValueLabel.setForeground(new Color(80, 100, 150));
        speedValueLabel.setPreferredSize(new Dimension(70, 20));
        speedValueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        speedSlider.addChangeListener(e -> {
            int val = speedSlider.getValue();
            speedValueLabel.setText(val + " 步/秒");
            if (listener != null) {
                listener.onSpeedChanged(val);
            }
        });

        JLabel slowLabel = new JLabel("🐢 慢");
        slowLabel.setFont(new Font("微软雅黑", Font.PLAIN, 10));
        slowLabel.setForeground(new Color(150, 150, 160));
        JLabel fastLabel = new JLabel("快 🐇");
        fastLabel.setFont(new Font("微软雅黑", Font.PLAIN, 10));
        fastLabel.setForeground(new Color(150, 150, 160));

        JPanel sliderContainer = new JPanel(new BorderLayout(0, 0));
        sliderContainer.setOpaque(false);
        sliderContainer.add(slowLabel, BorderLayout.WEST);
        sliderContainer.add(speedSlider, BorderLayout.CENTER);
        sliderContainer.add(fastLabel, BorderLayout.EAST);

        speedPanel.add(sliderContainer, BorderLayout.CENTER);
        speedPanel.add(speedValueLabel, BorderLayout.EAST);

        centerPanel.add(speedPanel, BorderLayout.CENTER);

        stateLabel = new JLabel("⏹ 待机");
        stateLabel.setFont(new Font("微软雅黑", Font.BOLD, 13));
        stateLabel.setForeground(new Color(150, 150, 160));
        stateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        stateLabel.setBorder(new EmptyBorder(0, 10, 0, 10));
        centerPanel.add(stateLabel, BorderLayout.WEST);

        add(centerPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 3));
        rightPanel.setOpaque(false);

        stepCountLabel = createStatLabel("步数", "0 / 0",
                new Color(100, 149, 237));
        rightPanel.add(stepCountLabel);

        compareCountLabel = createStatLabel("比较", "0",
                new Color(255, 165, 0));
        rightPanel.add(compareCountLabel);

        swapCountLabel = createStatLabel("交换", "0",
                new Color(255, 100, 100));
        rightPanel.add(swapCountLabel);

        progressLabel = createStatLabel("进度", "0%",
                new Color(60, 179, 113));
        rightPanel.add(progressLabel);

        add(rightPanel, BorderLayout.EAST);

        ActionListener btnListener = e -> {
            if (listener == null) return;
            Object src = e.getSource();
            if (src == playPauseBtn) listener.onPlayPause();
            else if (src == stepForwardBtn) listener.onStepForward();
            else if (src == stepBackBtn) listener.onStepBackward();
            else if (src == resetBtn) listener.onReset();
            else if (src == startBtn) listener.onStart();
            else if (src == generateBtn) listener.onGenerateNew();
        };

        playPauseBtn.addActionListener(btnListener);
        stepForwardBtn.addActionListener(btnListener);
        stepBackBtn.addActionListener(btnListener);
        resetBtn.addActionListener(btnListener);
        startBtn.addActionListener(btnListener);
        generateBtn.addActionListener(btnListener);
    }

    private JButton createButton(String text, Color baseColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("微软雅黑", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(baseColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getText().length() * 14 + 20, 35));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));

        Color hover = brighter(baseColor, 20);
        Color pressed = darker(baseColor, 20);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (btn.isEnabled()) btn.setBackground(hover);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (btn.isEnabled()) btn.setBackground(baseColor);
            }
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                if (btn.isEnabled()) btn.setBackground(pressed);
            }
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                if (btn.isEnabled()) btn.setBackground(hover);
            }
        });

        btn.addPropertyChangeListener("enabled", e -> {
            if (!btn.isEnabled()) {
                btn.setBackground(new Color(200, 200, 210));
                btn.setForeground(new Color(160, 160, 170));
            } else {
                btn.setBackground(baseColor);
                btn.setForeground(Color.WHITE);
            }
        });

        return btn;
    }

    private JLabel createStatLabel(String title, String value, Color color) {
        JPanel panel = new JPanel(new BorderLayout(0, -2));
        panel.setOpaque(false);
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 210), 1, true),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("微软雅黑", Font.PLAIN, 10));
        titleLbl.setForeground(new Color(120, 120, 130));
        titleLbl.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Consolas", Font.BOLD, 15));
        valueLbl.setForeground(color);
        valueLbl.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(titleLbl, BorderLayout.NORTH);
        panel.add(valueLbl, BorderLayout.SOUTH);
        panel.setPreferredSize(new Dimension(85, 42));

        JLabel wrapper = new JLabel();
        wrapper.setLayout(new BorderLayout());
        wrapper.add(panel, BorderLayout.CENTER);
        wrapper.putClientProperty("valueLabel", valueLbl);
        wrapper.setOpaque(false);
        return wrapper;
    }

    public void setControlListener(ControlListener listener) {
        this.listener = listener;
    }

    public void setPlaying(boolean playing) {
        this.isPlaying = playing;
        if (playing) {
            playPauseBtn.setText("⏸ 暂停");
            playPauseBtn.setBackground(new Color(255, 165, 0));
            stateLabel.setText("▶ 播放中");
            stateLabel.setForeground(new Color(60, 179, 113));
        } else {
            playPauseBtn.setText("▶ 播放");
            playPauseBtn.setBackground(new Color(60, 179, 113));
            stateLabel.setText("⏸ 已暂停");
            stateLabel.setForeground(new Color(255, 165, 0));
        }
    }

    public void setControlsEnabled(boolean started) {
        playPauseBtn.setEnabled(started);
        stepForwardBtn.setEnabled(started);
        stepBackBtn.setEnabled(started);
        resetBtn.setEnabled(started);
        if (!started) {
            stateLabel.setText("⏹ 待机");
            stateLabel.setForeground(new Color(150, 150, 160));
        }
    }

    public void setStepCount(int current, int total) {
        updateStatLabel(stepCountLabel, current + " / " + total);
    }

    public void setCompareCount(int count) {
        updateStatLabel(compareCountLabel, String.valueOf(count));
    }

    public void setSwapCount(int count) {
        updateStatLabel(swapCountLabel, String.valueOf(count));
    }

    public void setProgress(double percent) {
        int p = Math.max(0, Math.min(100, (int) Math.round(percent * 100)));
        updateStatLabel(progressLabel, p + "%");
    }

    private void updateStatLabel(JLabel wrapper, String value) {
        JLabel valLbl = (JLabel) wrapper.getClientProperty("valueLabel");
        if (valLbl != null) {
            valLbl.setText(value);
        }
    }

    public int getSpeed() {
        return speedSlider.getValue();
    }

    private Color brighter(Color c, int amount) {
        return new Color(
                Math.min(255, c.getRed() + amount),
                Math.min(255, c.getGreen() + amount),
                Math.min(255, c.getBlue() + amount)
        );
    }

    private Color darker(Color c, int amount) {
        return new Color(
                Math.max(0, c.getRed() - amount),
                Math.max(0, c.getGreen() - amount),
                Math.max(0, c.getBlue() - amount)
        );
    }
}
