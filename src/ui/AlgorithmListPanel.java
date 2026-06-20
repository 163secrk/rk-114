package ui;

import algorithm.SortAlgorithm;
import algorithm.BubbleSort;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AlgorithmListPanel extends JPanel {

    private final List<SortAlgorithm> algorithms;
    private SortAlgorithm selectedAlgorithm;
    private final JList<String> algorithmJList;
    private final JTextArea descArea;
    private final JLabel statusLabel;

    public AlgorithmListPanel() {
        this.algorithms = new ArrayList<>();
        this.algorithms.add(new BubbleSort());
        this.selectedAlgorithm = algorithms.get(0);

        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setPreferredSize(new Dimension(260, 0));
        setBackground(new Color(250, 250, 252));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(250, 250, 252));
        JLabel titleLabel = new JLabel("📚 算法列表");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        titleLabel.setForeground(new Color(50, 60, 80));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        add(titlePanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setOpaque(false);

        descArea = new JTextArea(selectedAlgorithm.getDescription());
        descArea.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        descArea.setEditable(false);
        descArea.setWrapStyleWord(true);
        descArea.setLineWrap(true);
        descArea.setBackground(new Color(248, 250, 252));
        descArea.setForeground(new Color(60, 60, 80));
        descArea.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (SortAlgorithm algo : algorithms) {
            listModel.addElement("  " + algo.getName());
        }
        algorithmJList = new JList<>(listModel);
        algorithmJList.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        algorithmJList.setSelectedIndex(0);
        algorithmJList.setFixedCellHeight(40);
        algorithmJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        algorithmJList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
                if (isSelected) {
                    label.setBackground(new Color(100, 149, 237));
                    label.setForeground(Color.WHITE);
                    label.setFont(new Font("微软雅黑", Font.BOLD, 14));
                } else {
                    label.setBackground(Color.WHITE);
                    label.setForeground(new Color(60, 60, 80));
                }
                return label;
            }
        });
        algorithmJList.addListSelectionListener(e -> {
            int idx = algorithmJList.getSelectedIndex();
            if (idx >= 0) {
                selectedAlgorithm = algorithms.get(idx);
                descArea.setText(selectedAlgorithm.getDescription());
            }
        });

        JScrollPane listScrollPane = new JScrollPane(algorithmJList);
        listScrollPane.setBorder(new TitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 210)),
                "选择算法",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("微软雅黑", Font.PLAIN, 12),
                new Color(100, 100, 120)
        ));
        listScrollPane.setBackground(Color.WHITE);
        centerPanel.add(listScrollPane, BorderLayout.CENTER);

        JScrollPane descScrollPane = new JScrollPane(descArea);
        descScrollPane.setBorder(new TitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 210)),
                "算法说明",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("微软雅黑", Font.PLAIN, 12),
                new Color(100, 100, 120)
        ));
        descScrollPane.setPreferredSize(new Dimension(0, 180));
        centerPanel.add(descScrollPane, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        statusLabel = new JLabel("🎯 点击开始按钮启动可视化");
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(100, 100, 120));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        bottomPanel.add(statusLabel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public SortAlgorithm getSelectedAlgorithm() {
        return selectedAlgorithm;
    }

    public void setStatus(String text) {
        statusLabel.setText(text);
    }
}
