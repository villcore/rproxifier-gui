package com.luoboduner.moo.info.ui.form;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.luoboduner.moo.info.bean.ProcessRegexRouteRule;
import com.luoboduner.moo.info.bean.RegexRouteRule;
import com.luoboduner.moo.info.bean.RouteRuleTypeEnum;
import com.luoboduner.moo.info.service.RouteRuleService;
import com.luoboduner.moo.info.ui.dialog.GlobalRouteRuleDiagForm;
import com.luoboduner.moo.info.ui.dialog.ProcessRouteRuleDiagForm;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.util.tuples.Pair;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RouteRuleForm {

    private static final Logger LOG = LoggerFactory.getLogger(RouteRuleForm.class);
    private static final String[] GLOBAL_ROUTE_RULE_TABLE_HEADERS = {
            "序号",
            "正则规则", "路由类型", "代理配置",
    };

    private static final String[] PROCESS_ROUTE_RULE_TABLE_HEADERS = {
            "序号",
            "进程路径",
            "正则规则", "路由类型", "代理配置",
    };
    private static RouteRuleForm INSTANCE;

    private JPanel mainPanel;
    private JPanel globalRulePanel;
    private JPanel processRulePanel;
    private JTable globalRouteRuleTable;
    private JTable processRuleTable;
    private JButton addGlobalRuleButton;
    private JButton removeGlobalRuleButton;
    private JButton modifyGlobalRuleButton;
    private JButton moveUpGlobalRuleButton;
    private JButton moveDownGlobalRuleButton;
    private JButton queryGlobalRuleButton;
    private JButton queryProcessRuleButton;
    private JButton addProcessRuleButton;
    private JButton removeProcessRuleButton;
    private JButton modifyProcessRuleButton;
    private JButton moveUpProcessRuleButton;
    private JButton moveDownProcessRuleButton;

    public static RouteRuleForm getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RouteRuleForm();
        }
        return INSTANCE;
    }

    public JPanel getMainPanel() {
        return this.mainPanel;
    }

    public RouteRuleForm() {
        $$$setupUI$$$();
        addGlobalButtonActionListener();
        addProcessButtonActionListener();
        refreshGlobalRouteRuleTable();
        refreshProcessRouteRuleTable();
    }

    private void addGlobalButtonActionListener() {
        this.addGlobalRuleButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JDialog addGlobalRouteRuleDiag = new GlobalRouteRuleDiagForm("新增全局路由规则");
                addGlobalRouteRuleDiag.setSize(100, 100);
                addGlobalRouteRuleDiag.pack();
                addGlobalRouteRuleDiag.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        refreshGlobalRouteRuleTable();
                    }
                });
                addGlobalRouteRuleDiag.setVisible(true);
            }
        });

        this.queryGlobalRuleButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                refreshGlobalRouteRuleTable();
            }
        });

        this.removeGlobalRuleButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                getGlobalRouteRuleTableSelectedRow()
                        .ifPresent(regexRouteRule -> {
                            if (RouteRuleService.getInstance().removeGlobalRouteRule(regexRouteRule)) {
                                JOptionPane.showMessageDialog(null, "删除成功", "删除成功", JOptionPane.PLAIN_MESSAGE);
                                refreshGlobalRouteRuleTable();
                            } else {
                                JOptionPane.showMessageDialog(null, "删除失败", "删除失败", JOptionPane.ERROR_MESSAGE);
                            }
                        });
            }
        });

        this.moveUpGlobalRuleButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                moveUpGlobalRouteRule();
            }
        });

        this.moveDownGlobalRuleButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                moveDownGlobalRouteRule();
            }
        });

        this.modifyGlobalRuleButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                getGlobalRouteRuleTableSelectedRow().ifPresent(regexRouteRule -> {
                    JDialog addGlobalRouteRuleDiag = new GlobalRouteRuleDiagForm("修改全局路由规则", regexRouteRule);
                    addGlobalRouteRuleDiag.setSize(100, 100);
                    addGlobalRouteRuleDiag.pack();
                    addGlobalRouteRuleDiag.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            refreshGlobalRouteRuleTable();
                        }
                    });
                    addGlobalRouteRuleDiag.setVisible(true);
                });
            }
        });
    }

    private void moveUpGlobalRouteRule() {
        int[] selectedRows = globalRouteRuleTable.getSelectedRows();
        if (selectedRows.length < 1) {
            return;
        }

        int selectedRow = selectedRows[0];
        int rowCount = globalRouteRuleTable.getRowCount();
        if (selectedRow <= 0) {
            return;
        }

        ArrayList<RegexRouteRule> routeRuleList = new ArrayList<>(rowCount);
        for (int i = 0; i < rowCount; i++) {
            parseRouteRuleFromRow(i).ifPresent(routeRuleList::add);
        }

        RegexRouteRule selectedRouteRule = routeRuleList.get(selectedRow);
        RegexRouteRule preRouteRule = routeRuleList.get(selectedRow - 1);
        routeRuleList.set(selectedRow, preRouteRule);
        routeRuleList.set(selectedRow - 1, selectedRouteRule);
        RouteRuleService.getInstance().saveGlobalRouteRule(routeRuleList);
        refreshGlobalRouteRuleTable();
    }

    private void moveDownGlobalRouteRule() {
        int[] selectedRows = globalRouteRuleTable.getSelectedRows();
        if (selectedRows.length < 1) {
            return;
        }

        int selectedRow = selectedRows[0];
        int rowCount = globalRouteRuleTable.getRowCount();
        if (selectedRow >= rowCount - 1) {
            return;
        }

        ArrayList<RegexRouteRule> routeRuleList = new ArrayList<>(rowCount);
        for (int i = 0; i < rowCount; i++) {
            parseRouteRuleFromRow(i).ifPresent(routeRuleList::add);
        }

        RegexRouteRule selectedRouteRule = routeRuleList.get(selectedRow);
        RegexRouteRule nextRouteRule = routeRuleList.get(selectedRow + 1);
        routeRuleList.set(selectedRow, nextRouteRule);
        routeRuleList.set(selectedRow + 1, selectedRouteRule);
        RouteRuleService.getInstance().saveGlobalRouteRule(routeRuleList);
        refreshGlobalRouteRuleTable();
    }

    private void refreshGlobalRouteRuleTable() {
        List<RegexRouteRule> globalRegexRouteRuleList = RouteRuleService.getInstance().getAllGlobalRouteRule();
        loadGlobalRouteRuleTable(globalRegexRouteRuleList);
    }

    private void loadGlobalRouteRuleTable(List<RegexRouteRule> globalRegexRouteRuleList) {
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel tableModel = new DefaultTableModel(GLOBAL_ROUTE_RULE_TABLE_HEADERS, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            for (int i = 0; i < globalRegexRouteRuleList.size(); i++) {
                RegexRouteRule routeRule = globalRegexRouteRuleList.get(i);
                LOG.info("Route rule = {}", routeRule);
                int row = i + 1;
                Pair<RouteRuleTypeEnum, String> ruleConfig = RegexRouteRule.getRouteRuleType(routeRule.getRoute_rule());
                tableModel.addRow(new Object[]{

                        // 序号
                        String.valueOf(row),

                        // 正则规则
                        routeRule.getHost_regex(),

                        // 路由类型
                        ruleConfig.getA().getTitle(),

                        // 代理配置
                        ruleConfig.getB()
                });
            }

            globalRouteRuleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            globalRouteRuleTable.clearSelection();
            globalRouteRuleTable.setModel(tableModel);
            globalRouteRuleTable.setShowGrid(true);

            // set font smaller
            Font newFont = new Font("", Font.PLAIN, 12);
            globalRouteRuleTable.getTableHeader().setFont(newFont);
            globalRouteRuleTable.getTableHeader().setLayout(new FlowLayout());
            globalRouteRuleTable.setFont(newFont);
        });
    }

    private Optional<RegexRouteRule> getGlobalRouteRuleTableSelectedRow() {
        int[] selectedRows = globalRouteRuleTable.getSelectedRows();
        if (selectedRows.length >= 1) {
            int selectedRow = selectedRows[0];
            return parseRouteRuleFromRow(selectedRow);
        }
        return Optional.empty();
    }

    private Optional<RegexRouteRule> parseRouteRuleFromRow(int selectedRow) {
        TableModel tableModel = globalRouteRuleTable.getModel();
        String host_regex = tableModel.getValueAt(selectedRow, 1).toString();
        String route_rule_type = tableModel.getValueAt(selectedRow, 2).toString();
        String route_rule_config = tableModel.getValueAt(selectedRow, 3).toString();

        RouteRuleTypeEnum routeRuleType = RouteRuleTypeEnum.titleOf(route_rule_type);
        RegexRouteRule routeRule = new RegexRouteRule();
        routeRule.setHost_regex(host_regex);
        switch (routeRuleType) {
            case DIRECT:
            case REJECT:
                routeRule.setRoute_rule(routeRuleType.getCode());
                break;
            case PROBE:
            case PROXY:
                routeRule.setRoute_rule(Collections.singletonMap(routeRuleType.getCode(), route_rule_config));
                break;
        }
        return Optional.of(routeRule);
    }

    private Optional<ProcessRegexRouteRule> parseProcessRouteRuleFromRow(int selectedRow) {
        TableModel tableModel = processRuleTable.getModel();
        String processPath = tableModel.getValueAt(selectedRow, 1).toString();
        String host_regex = tableModel.getValueAt(selectedRow, 2).toString();
        String route_rule_type = tableModel.getValueAt(selectedRow, 3).toString();
        String route_rule_config = tableModel.getValueAt(selectedRow, 4).toString();

        RouteRuleTypeEnum routeRuleType = RouteRuleTypeEnum.titleOf(route_rule_type);
        RegexRouteRule routeRule = new RegexRouteRule();
        routeRule.setHost_regex(host_regex);
        switch (routeRuleType) {
            case DIRECT:
            case REJECT:
                routeRule.setRoute_rule(routeRuleType.getCode());
                break;
            case PROBE:
            case PROXY:
                routeRule.setRoute_rule(Collections.singletonMap(routeRuleType.getCode(), route_rule_config));
                break;
        }

        ProcessRegexRouteRule processRegexRouteRule = new ProcessRegexRouteRule();
        processRegexRouteRule.setProcess_path(processPath);
        processRegexRouteRule.setRoute_rule(routeRule);
        return Optional.of(processRegexRouteRule);
    }

    private void addProcessButtonActionListener() {
        this.addProcessRuleButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JDialog addGlobalRouteRuleDiag = new ProcessRouteRuleDiagForm("新增进程路由规则");
                addGlobalRouteRuleDiag.setSize(100, 100);
                addGlobalRouteRuleDiag.pack();
                addGlobalRouteRuleDiag.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        refreshProcessRouteRuleTable();
                    }
                });
                addGlobalRouteRuleDiag.setVisible(true);
            }
        });

        this.queryProcessRuleButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                refreshProcessRouteRuleTable();
            }
        });

        this.removeProcessRuleButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                getProcessRouteRuleTableSelectedRow()
                        .ifPresent(processRegexRouteRule -> {
                            if (RouteRuleService.getInstance().removeProcessRouteRule(processRegexRouteRule)) {
                                JOptionPane.showMessageDialog(null, "删除成功", "删除成功", JOptionPane.PLAIN_MESSAGE);
                                refreshProcessRouteRuleTable();
                            } else {
                                JOptionPane.showMessageDialog(null, "删除失败", "删除失败", JOptionPane.ERROR_MESSAGE);
                            }
                        });
            }
        });

        this.moveUpProcessRuleButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                moveUpProcessRouteRule();
            }
        });

        this.moveDownProcessRuleButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                moveDownProcessRouteRule();
            }
        });

        this.modifyProcessRuleButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                getProcessRouteRuleTableSelectedRow().ifPresent(processRegexRouteRule -> {
                    JDialog addProxyRouteRuleDiag = new ProcessRouteRuleDiagForm("修改进程路由规则", processRegexRouteRule);
                    addProxyRouteRuleDiag.setSize(100, 100);
                    addProxyRouteRuleDiag.pack();
                    addProxyRouteRuleDiag.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            refreshProcessRouteRuleTable();
                        }
                    });
                    addProxyRouteRuleDiag.setVisible(true);
                });
            }
        });
    }

    private void refreshProcessRouteRuleTable() {
        List<ProcessRegexRouteRule> processRegexRouteRuleList = RouteRuleService.getInstance().getAllProcessRouteRule();
        loadProcessRouteRuleTable(processRegexRouteRuleList);
    }

    private void loadProcessRouteRuleTable(List<ProcessRegexRouteRule> processRegexRouteRuleList) {
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel tableModel = new DefaultTableModel(PROCESS_ROUTE_RULE_TABLE_HEADERS, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            for (int i = 0; i < processRegexRouteRuleList.size(); i++) {
                ProcessRegexRouteRule processRouteRule = processRegexRouteRuleList.get(i);
                RegexRouteRule routeRule = processRouteRule.getRoute_rule();

                LOG.info("process Route rule = {}", processRouteRule);
                int row = i + 1;
                Pair<RouteRuleTypeEnum, String> ruleConfig = RegexRouteRule.getRouteRuleType(routeRule.getRoute_rule());
                tableModel.addRow(new Object[]{

                        // 序号
                        String.valueOf(row),

                        // 进程路径
                        String.valueOf(processRouteRule.getProcess_path()),

                        // 正则规则
                        routeRule.getHost_regex(),

                        // 路由类型
                        ruleConfig.getA().getTitle(),

                        // 代理配置
                        ruleConfig.getB()
                });
            }

            processRuleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            processRuleTable.clearSelection();
            processRuleTable.setModel(tableModel);
            processRuleTable.setShowGrid(true);

            // set font smaller
            Font newFont = new Font("", Font.PLAIN, 12);
            processRuleTable.getTableHeader().setFont(newFont);
            processRuleTable.getTableHeader().setLayout(new FlowLayout());
            processRuleTable.setFont(newFont);


            TableColumnModel tableHeaderColumnModel = processRuleTable.getTableHeader().getColumnModel();
            tableHeaderColumnModel.getColumn(0).setPreferredWidth(10);
            tableHeaderColumnModel.getColumn(1).setPreferredWidth(200);

            // set column width
            TableColumnModel tableColumnModel = processRuleTable.getColumnModel();
            tableColumnModel.getColumn(0).setPreferredWidth(10);
            tableColumnModel.getColumn(1).setPreferredWidth(400);
        });
    }

    private Optional<ProcessRegexRouteRule> getProcessRouteRuleTableSelectedRow() {
        int[] selectedRows = processRuleTable.getSelectedRows();
        if (selectedRows.length >= 1) {
            int selectedRow = selectedRows[0];
            return parseProcessRouteRuleFromRow(selectedRow);
        }
        return Optional.empty();
    }

    private void moveUpProcessRouteRule() {
        int[] selectedRows = processRuleTable.getSelectedRows();
        if (selectedRows.length < 1) {
            return;
        }

        int selectedRow = selectedRows[0];
        int rowCount = processRuleTable.getRowCount();
        if (selectedRow <= 0) {
            return;
        }

        List<ProcessRegexRouteRule> processRegexRouteRules = new ArrayList<>(rowCount);
        for (int i = 0; i < rowCount; i++) {
            parseProcessRouteRuleFromRow(i).ifPresent(processRegexRouteRules::add);
        }

        ProcessRegexRouteRule selectedRouteRule = processRegexRouteRules.get(selectedRow);
        ProcessRegexRouteRule preRouteRule = processRegexRouteRules.get(selectedRow - 1);
        if (!StringUtils.equals(selectedRouteRule.getProcess_path(), preRouteRule.getProcess_path())) {
            JOptionPane.showMessageDialog(null, "向上移动仅支持相同进程路径间数据移动", "移动失败", JOptionPane.ERROR_MESSAGE);
            return;
        }

        processRegexRouteRules.set(selectedRow, preRouteRule);
        processRegexRouteRules.set(selectedRow - 1, selectedRouteRule);
        processRegexRouteRules = processRegexRouteRules.stream()
                .filter(processRegexRouteRule -> StringUtils.equals(selectedRouteRule.getProcess_path(), processRegexRouteRule.getProcess_path()))
                .collect(Collectors.toList());
        LOG.info("Move up relation data = {}", processRegexRouteRules);
        RouteRuleService.getInstance().saveProcessRouteRule(processRegexRouteRules);
        refreshProcessRouteRuleTable();
    }

    private void moveDownProcessRouteRule() {
        int[] selectedRows = processRuleTable.getSelectedRows();
        if (selectedRows.length < 1) {
            return;
        }

        int selectedRow = selectedRows[0];
        int rowCount = processRuleTable.getRowCount();
        if (selectedRow <= 0) {
            return;
        }

        List<ProcessRegexRouteRule> processRegexRouteRules = new ArrayList<>(rowCount);
        for (int i = 0; i < rowCount; i++) {
            parseProcessRouteRuleFromRow(i).ifPresent(processRegexRouteRules::add);
        }

        ProcessRegexRouteRule selectedRouteRule = processRegexRouteRules.get(selectedRow);
        ProcessRegexRouteRule nextRouteRule = processRegexRouteRules.get(selectedRow + 1);
        if (!StringUtils.equals(selectedRouteRule.getProcess_path(), nextRouteRule.getProcess_path())) {
            JOptionPane.showMessageDialog(null, "向下移动仅支持相同进程路径间数据移动", "移动失败", JOptionPane.ERROR_MESSAGE);
            return;
        }

        processRegexRouteRules.set(selectedRow, nextRouteRule);
        processRegexRouteRules.set(selectedRow + 1, selectedRouteRule);
        processRegexRouteRules = processRegexRouteRules.stream()
                .filter(processRegexRouteRule -> StringUtils.equals(selectedRouteRule.getProcess_path(), processRegexRouteRule.getProcess_path()))
                .collect(Collectors.toList());
        RouteRuleService.getInstance().saveProcessRouteRule(processRegexRouteRules);
        refreshProcessRouteRuleTable();
    }


    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(2, 2, new Insets(10, 50, 10, 50), -1, -1));
        globalRulePanel = new JPanel();
        globalRulePanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(globalRulePanel, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        globalRulePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-4473925)), "全局规则", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        globalRulePanel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        queryGlobalRuleButton = new JButton();
        queryGlobalRuleButton.setText("查询规则");
        panel1.add(queryGlobalRuleButton);
        addGlobalRuleButton = new JButton();
        addGlobalRuleButton.setText("增加规则");
        panel1.add(addGlobalRuleButton);
        removeGlobalRuleButton = new JButton();
        removeGlobalRuleButton.setText("删除规则");
        panel1.add(removeGlobalRuleButton);
        modifyGlobalRuleButton = new JButton();
        modifyGlobalRuleButton.setText("修改规则");
        panel1.add(modifyGlobalRuleButton);
        moveUpGlobalRuleButton = new JButton();
        moveUpGlobalRuleButton.setText("上移⬆");
        panel1.add(moveUpGlobalRuleButton);
        moveDownGlobalRuleButton = new JButton();
        moveDownGlobalRuleButton.setText("下移⬇");
        panel1.add(moveDownGlobalRuleButton);
        final JScrollPane scrollPane1 = new JScrollPane();
        globalRulePanel.add(scrollPane1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        globalRouteRuleTable = new JTable();
        scrollPane1.setViewportView(globalRouteRuleTable);
        processRulePanel = new JPanel();
        processRulePanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(processRulePanel, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        processRulePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-4473925)), "进程规则（配置后覆盖全局规则，上移，下移仅支持相同进程路径间移动）", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, new Color(-4473925)));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        processRulePanel.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        queryProcessRuleButton = new JButton();
        queryProcessRuleButton.setText("查询规则");
        panel2.add(queryProcessRuleButton);
        addProcessRuleButton = new JButton();
        addProcessRuleButton.setText("增加规则");
        panel2.add(addProcessRuleButton);
        removeProcessRuleButton = new JButton();
        removeProcessRuleButton.setText("删除规则");
        panel2.add(removeProcessRuleButton);
        modifyProcessRuleButton = new JButton();
        modifyProcessRuleButton.setText("修改规则");
        panel2.add(modifyProcessRuleButton);
        moveUpProcessRuleButton = new JButton();
        moveUpProcessRuleButton.setText("上移⬆");
        panel2.add(moveUpProcessRuleButton);
        moveDownProcessRuleButton = new JButton();
        moveDownProcessRuleButton.setText("下移⬇");
        panel2.add(moveDownProcessRuleButton);
        final JScrollPane scrollPane2 = new JScrollPane();
        processRulePanel.add(scrollPane2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        processRuleTable = new JTable();
        scrollPane2.setViewportView(processRuleTable);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
