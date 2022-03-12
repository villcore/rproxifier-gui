package com.luoboduner.moo.info.ui.form;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.luoboduner.moo.info.bean.ProxyServerConfig;
import com.luoboduner.moo.info.service.ProxyServerConfigService;
import com.luoboduner.moo.info.ui.dialog.ProxyServerConfigDiagForm;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Optional;

public class ProxyConfigForm {

    private static final Logger LOG = LoggerFactory.getLogger(ProxyConfigForm.class);

    private JPanel mainPanel;
    private JPanel buttonPanel;
    private JButton addServerBtn;
    private JButton removeServerBtn;
    private JButton modifyServerBtn;
    private JButton testConnectionBtn;
    private JButton listServerBtn;
    private JTable serverConfigTable;

    private static final String[] SERVER_TABLE_HEADERS = {
            "名称", "地址", "端口", "是否连通"
    };
    private static ProxyConfigForm INSTANCE;

    public static ProxyConfigForm getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ProxyConfigForm();
        }
        return INSTANCE;
    }

    public ProxyConfigForm() {
        $$$setupUI$$$();
        addButtonListener();
        refreshProxyServerConfigList();
    }

    private void addButtonListener() {
        this.listServerBtn.addMouseListener(this.onListServerBtnClicked());
        this.addServerBtn.addMouseListener(this.onAddServerBtnClicked());
        this.removeServerBtn.addMouseListener(this.onRemoveServerBtnClick());
        this.modifyServerBtn.addMouseListener(this.onModifyServerBtnClick());
        this.testConnectionBtn.addMouseListener(this.onTestConnectionBtnClick());
    }

    private MouseListener onListServerBtnClicked() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                LOG.info("ListServerBtn clicked");
                List<ProxyServerConfig> allProxyServerConfigList = ProxyServerConfigService.getInstance().getAllProxyServerConfig();
                loadServerTable(allProxyServerConfigList);
            }
        };
    }

    private MouseListener onAddServerBtnClicked() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                LOG.info("AddServerBtn clicked");
                ProxyServerConfigDiagForm dialog = new ProxyServerConfigDiagForm("添加代理");
                dialog.setSize(100, 100);
                dialog.pack();
                dialog.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        refreshProxyServerConfigList();
                    }
                });
                dialog.setVisible(true);
            }
        };
    }

    private MouseListener onRemoveServerBtnClick() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                LOG.info("RemoveServerBtn clicked");
                JOptionPane.showMessageDialog(null, "删除成功", "删除成功", JOptionPane.PLAIN_MESSAGE);
                getServerConfigTableSelectedRow().ifPresent(proxy -> removeProxyServerConfig(proxy));
            }
        };
    }

    private void removeProxyServerConfig(ProxyServerConfig proxyServerConfig) {
        if (ProxyServerConfigService.getInstance().removeProxyServerConfig(proxyServerConfig)) {
            refreshProxyServerConfigList();
        }
    }

    private void refreshProxyServerConfigList() {
        List<ProxyServerConfig> allProxyServerConfigList = ProxyServerConfigService.getInstance().getAllProxyServerConfig();
        loadServerTable(allProxyServerConfigList);
    }

    private Optional<ProxyServerConfig> getServerConfigTableSelectedRow() {
        ListSelectionModel selectionModel = serverConfigTable.getSelectionModel();
        int[] selectedRows = serverConfigTable.getSelectedRows();
        if (selectedRows.length >= 1) {
            int selectedRow = selectedRows[0];
            TableModel tableModel = serverConfigTable.getModel();
            String name = tableModel.getValueAt(selectedRow, 0).toString();
            String addr = tableModel.getValueAt(selectedRow, 1).toString();
            String port = tableModel.getValueAt(selectedRow, 2).toString();
            String available = tableModel.getValueAt(selectedRow, 3).toString();
            return Optional.of(ProxyServerConfig.create(name, addr, NumberUtils.toShort(port), Boolean.parseBoolean(available)));
        }
        LOG.info("select table rows {}", selectionModel);
        return Optional.empty();
    }

    private MouseListener onModifyServerBtnClick() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                LOG.info("ModifyServerBtn clicked");
                getServerConfigTableSelectedRow().ifPresent(proxyServerConfig -> {
                    ProxyServerConfigDiagForm dialog = new ProxyServerConfigDiagForm("修改代理", proxyServerConfig);
                    dialog.setSize(100, 100);
                    dialog.pack();
                    dialog.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            refreshProxyServerConfigList();
                        }
                    });
                    dialog.setVisible(true);
                });
            }
        };
    }

    private MouseListener onTestConnectionBtnClick() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                JOptionPane.showMessageDialog(null, "功能暂未实现", "功能暂未实现", JOptionPane.PLAIN_MESSAGE);
            }
        };
    }

    public JPanel getMainPanel() {
        return this.mainPanel;
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
        mainPanel.setLayout(new GridLayoutManager(2, 1, new Insets(10, 50, 10, 50), -1, -1));
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setEnabled(false);
        mainPanel.add(buttonPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        listServerBtn = new JButton();
        listServerBtn.setText("查询配置");
        buttonPanel.add(listServerBtn);
        addServerBtn = new JButton();
        addServerBtn.setText("添加配置");
        buttonPanel.add(addServerBtn);
        removeServerBtn = new JButton();
        removeServerBtn.setText("删除配置");
        buttonPanel.add(removeServerBtn);
        modifyServerBtn = new JButton();
        modifyServerBtn.setText("修改配置");
        buttonPanel.add(modifyServerBtn);
        testConnectionBtn = new JButton();
        testConnectionBtn.setText("测试连通");
        buttonPanel.add(testConnectionBtn);
        final JScrollPane scrollPane1 = new JScrollPane();
        mainPanel.add(scrollPane1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        serverConfigTable = new JTable();
        serverConfigTable.setDoubleBuffered(true);
        scrollPane1.setViewportView(serverConfigTable);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }


    private void loadServerTable(List<ProxyServerConfig> serverConfigList) {
        DefaultTableModel tableModel = new DefaultTableModel(SERVER_TABLE_HEADERS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (ProxyServerConfig proxyServerConfig : serverConfigList) {
            tableModel.addRow(new Object[]{
                    proxyServerConfig.getName(),
                    proxyServerConfig.getAddr(),
                    proxyServerConfig.getPort(),
                    proxyServerConfig.isAvailable()
            });
        }

        serverConfigTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        serverConfigTable.clearSelection();
        serverConfigTable.setModel(tableModel);
        serverConfigTable.setShowGrid(true);

        Font newFont = new Font("", Font.PLAIN, 12);
        serverConfigTable.getTableHeader().setFont(newFont);
        serverConfigTable.setFont(newFont);
    }
}