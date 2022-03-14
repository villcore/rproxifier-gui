package com.luoboduner.moo.info.ui.form;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.luoboduner.moo.info.bean.AllDnsHostConfig;
import com.luoboduner.moo.info.bean.DnsHost;
import com.luoboduner.moo.info.service.DnsConfigService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.List;
import java.util.Optional;

public class DnsConfigForm {
    private static final Logger LOG = LoggerFactory.getLogger(DnsConfigForm.class);
    private static final String[] SERVER_TABLE_HEADERS = {
            "域名", "IP", "相关进程", "本机解析/网关解析"
    };

    private JPanel mainPanel;
    private JTable hostTable;
    private JTextField queryHostText;
    private JButton queryHostButton;
    private JLabel dnsConfigLabel;
    private JButton resolveAddButton;

    private static DnsConfigForm INSTANCE;

    public static DnsConfigForm getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DnsConfigForm();
        }
        return INSTANCE;
    }

    public DnsConfigForm() {
        // reloadDnsConfigTable("");
        addMouseListener();
        addKeyListener();
    }

    private void addMouseListener() {
        this.queryHostButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String queryStr = queryHostText.getText();
                reloadDnsConfigTable(queryStr);
                loadDnsConfigIpAddress();
            }
        });

        this.resolveAddButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                LOG.info("resolve add button clicked");
                loadDnsConfigIpAddress();
            }
        });
    }

    private void loadDnsConfigIpAddress() {
        queryHostText.setEditable(false);
        queryHostButton.setEnabled(false);
        resolveAddButton.setEnabled(false);

        new Thread(() -> {
            TableModel tableModel = hostTable.getModel();
            int rowCount = tableModel.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                String host = tableModel.getValueAt(i, 0).toString();
                int row = i;
                parseHost(host).ifPresent(inetAddress -> {
                    String address = inetAddress.getHostAddress();
                    LOG.info("parse {} => {}", host, address);
                    tableModel.setValueAt(address, row, 1);
                });
            }

            SwingUtilities.invokeLater(() -> {
                queryHostText.setEditable(true);
                queryHostButton.setEnabled(true);
                resolveAddButton.setEnabled(true);
                hostTable.setEnabled(true);
            });
        }).start();
    }

    private Optional<InetAddress> parseHost(String host) {
        try {
            return Optional.of(Inet4Address.getByName(host));
        } catch (Exception e) {
            LOG.error("Parse host {} error", host);
        }
        return Optional.empty();
    }

    private void addKeyListener() {
        this.queryHostText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getExtendedKeyCode() == KeyEvent.VK_ENTER) {
                    String queryStr = queryHostText.getText();
                    reloadDnsConfigTable(queryStr);
                }
            }
        });
    }

    public JPanel getMainPanel() {
        return this.mainPanel;
    }

    private void reloadDnsConfigTable(String queryString) {
        DnsConfigService.getInstance()
                .getAllDnsConfig(queryString)
                .ifPresent(this::reloadDnsConfigTable);
        loadDnsConfigIpAddress();
    }

    private void reloadDnsConfigTable(AllDnsHostConfig allDnsHostConfig) {
        String localDnsServer = allDnsHostConfig.getLocal_dns_server();
        String gatewayServer = allDnsHostConfig.getGateway_server();
        dnsConfigLabel.setText(
                String.format(
                        "DNS服务：%s | 网关地址：%s | DNS解析切换生效时间为30秒",
                        StringUtils.isBlank(localDnsServer) ? "未知" : StringUtils.trim(localDnsServer),
                        StringUtils.isBlank(gatewayServer) ? "未知" : StringUtils.trim(gatewayServer))
        );

        List<DnsHost> allDnsConfig = allDnsHostConfig.getAll_dns_config();
        loadDnsConfigTable(allDnsConfig);
    }

    private void loadDnsConfigTable(List<DnsHost> allDnsConfig) {
        DefaultTableModel tableModel = new DefaultTableModel(SERVER_TABLE_HEADERS, 0);
        for (DnsHost dnsHost : allDnsConfig) {
            tableModel.addRow(new Object[]{
                    dnsHost.getHost(),
                    "",
                    JSONUtil.toJsonStr(dnsHost.getRelated_process_vec()),
                    dnsHost.isReverse_resolve()
            });
        }
        hostTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        hostTable.clearSelection();
        hostTable.setModel(tableModel);

        String id = SERVER_TABLE_HEADERS[3];
        hostTable.getColumn(id).setCellRenderer(new StatusRenderer());
        hostTable.getColumn(id).setCellEditor(new StatusEditor());

        Font newFont = new Font("", Font.PLAIN, 12);
        hostTable.getTableHeader().setFont(newFont);
        hostTable.setFont(newFont);
        hostTable.updateUI();
    }

    private Optional<DnsHost> getHostConfigTableSelectedRow() {
        ListSelectionModel selectionModel = hostTable.getSelectionModel();
        int[] selectedRows = hostTable.getSelectedRows();
        LOG.info("select table rows {}", selectedRows);
        if (selectedRows.length >= 1) {
            int selectedRow = selectedRows[0];
            TableModel tableModel = hostTable.getModel();
            String host = tableModel.getValueAt(selectedRow, 0).toString();
            String relatedProcessVecJson = tableModel.getValueAt(selectedRow, 1).toString();
            List<String> relatedProcessVec = JSON.parseArray(relatedProcessVecJson, String.class);
            String reverse = tableModel.getValueAt(selectedRow, 2).toString();
            return Optional.of(DnsHost.create(host, relatedProcessVec, Boolean.parseBoolean(reverse)));
        }
        LOG.info("select table rows {}", selectionModel);
        return Optional.empty();
    }

    private void saveSelectedHostConfig(boolean reverse) {
        getHostConfigTableSelectedRow().ifPresent(dnsHost -> {
            dnsHost.setReverse_resolve(reverse);
            if (DnsConfigService.getInstance().saveDnsHostConfig(dnsHost)) {
                int[] selectedRows = hostTable.getSelectedRows();
                LOG.info("select table rows {}", selectedRows);
                if (selectedRows.length >= 1) {
                    int selectedRow = selectedRows[0];
                    TableModel tableModel = hostTable.getModel();
                    tableModel.setValueAt(reverse, selectedRow, 2);

                    for (int i = 0; i < selectedRow; i++) {
                        LOG.info("-{}", tableModel.getValueAt(selectedRow, 2));
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "修改失败", "修改失败", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
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
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        dnsConfigLabel = new JLabel();
        dnsConfigLabel.setText("DNS服务：未知 | 网关地址：未知");
        panel2.add(dnsConfigLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        queryHostText = new JTextField();
        queryHostText.setText("");
        queryHostText.setToolTipText("请输入域名，如果为空，查找全部域名");
        panel2.add(queryHostText, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        queryHostButton = new JButton();
        queryHostButton.setText("查询");
        panel2.add(queryHostButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        resolveAddButton = new JButton();
        resolveAddButton.setText("解析IP");
        panel2.add(resolveAddButton, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        mainPanel.add(scrollPane1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        hostTable = new JTable();
        scrollPane1.setViewportView(hostTable);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

    private class StatusEditor extends AbstractCellEditor implements TableCellEditor {

        private final StatusPanel theStatusPanel;

        public StatusEditor() {
            theStatusPanel = new StatusPanel();
        }

        @Override
        public Object getCellEditorValue() {
            return theStatusPanel.getStatus();
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            DnsResolveTypeEnum resolveType =
                    Boolean.parseBoolean(value.toString()) ?
                            DnsResolveTypeEnum.LOCAL_DNS_RESOLVE :
                            DnsResolveTypeEnum.GATEWAY_DNS_RESOLVE;

            theStatusPanel.setStatus(resolveType);
            if (isSelected) {
                theStatusPanel.setBackground(table.getSelectionBackground());
            } else {
                theStatusPanel.setBackground(table.getBackground());
            }
            return theStatusPanel;
        }
    }

    private class StatusRenderer extends StatusPanel implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            DnsResolveTypeEnum resolveType =
                    Boolean.parseBoolean(value.toString()) || StringUtils.equals(value.toString(), DnsResolveTypeEnum.GATEWAY_DNS_RESOLVE.toString()) ?
                            DnsResolveTypeEnum.GATEWAY_DNS_RESOLVE :
                            DnsResolveTypeEnum.LOCAL_DNS_RESOLVE;
            setStatus(resolveType);
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }
            return this;
        }
    }

    private enum DnsResolveTypeEnum {
        LOCAL_DNS_RESOLVE("本地解析"),
        GATEWAY_DNS_RESOLVE("网关解析"),
        UNKNOWN("未知");

        String text;

        DnsResolveTypeEnum(String text) {
            this.text = text;
        }
    }

    private class StatusPanel extends JPanel {

        private final JRadioButton localDnsResolveOption;
        private final JRadioButton gatewayDnsResolveOption;
        private final ButtonGroup buttonGroup = new ButtonGroup();

        StatusPanel() {
            super(new FlowLayout());
            setOpaque(true);
            localDnsResolveOption = createRadio(DnsResolveTypeEnum.LOCAL_DNS_RESOLVE);
            gatewayDnsResolveOption = createRadio(DnsResolveTypeEnum.GATEWAY_DNS_RESOLVE);
            localDnsResolveOption.addActionListener(e -> {
                LOG.info("local dns resolver option change");
                saveSelectedHostConfig(!localDnsResolveOption.isSelected());
            });
            gatewayDnsResolveOption.addActionListener(e -> {
                LOG.info("gateway dns resolver option change");
                saveSelectedHostConfig(gatewayDnsResolveOption.isSelected());
            });
        }

        private JRadioButton createRadio(DnsResolveTypeEnum dnsResolveTypeEnum) {
            JRadioButton jrb = new JRadioButton(dnsResolveTypeEnum.text);
            Font font = jrb.getFont();
            Font newFont = new Font(font.getFontName(), font.getStyle(), 12);
            jrb.setFont(newFont);
            jrb.setOpaque(false);
            add(jrb);
            buttonGroup.add(jrb);
            return jrb;
        }

        public DnsResolveTypeEnum getStatus() {
            if (localDnsResolveOption.isSelected()) {
                return DnsResolveTypeEnum.LOCAL_DNS_RESOLVE;
            } else if (gatewayDnsResolveOption.isSelected()) {
                return DnsResolveTypeEnum.GATEWAY_DNS_RESOLVE;
            } else {
                return DnsResolveTypeEnum.UNKNOWN;
            }
        }

        public void setStatus(DnsResolveTypeEnum dnsResolveTypeEnum) {
            if (dnsResolveTypeEnum == DnsResolveTypeEnum.LOCAL_DNS_RESOLVE) {
                localDnsResolveOption.setSelected(true);
                // saveSelectedHostConfig(false);
            } else if (dnsResolveTypeEnum == DnsResolveTypeEnum.GATEWAY_DNS_RESOLVE) {
                gatewayDnsResolveOption.setSelected(true);
                // saveSelectedHostConfig(true);
            }  //
        }
    }
}
