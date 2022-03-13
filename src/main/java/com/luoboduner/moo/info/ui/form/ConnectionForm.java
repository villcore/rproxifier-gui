package com.luoboduner.moo.info.ui.form;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.luoboduner.moo.info.bean.ActiveConnection;
import com.luoboduner.moo.info.bean.ConnectionRouteRule;
import com.luoboduner.moo.info.bean.RegexRouteRule;
import com.luoboduner.moo.info.bean.RouteRuleTypeEnum;
import com.luoboduner.moo.info.service.ActiveConnectionService;
import com.luoboduner.moo.info.util.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.util.tuples.Pair;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ConnectionForm {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionForm.class);
    private static final ConnectionForm INSTANCE = new ConnectionForm();
    private static final String[] SERVER_TABLE_HEADERS = {
            "序号",
            "PID", "进程名称", "进程路径",
            "本地地址", "本地端口",
            "目的地址", "目的端口",
            "发送速度⬆", "接收速度⬇",
            "发送流量⬆", "接收流量⬇",
            "最近活动",
            "打开时间",
            "路由规则"
            // "强制关闭",
    };

    public static ConnectionForm getInstance() {
        return INSTANCE;
    }

    private JPanel mainPanel;
    private JPanel tooltipPanel;
    private JTable connectionTable;
    private JButton refreshButton;
    private JCheckBox autoRefreshCheckbox;
    private ScheduledFuture<?> autuRefreshFuture;

    public ConnectionForm() {
        this.refreshConnectionTable();
        this.refreshButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                refreshConnectionTable();
            }
        });

        this.autoRefreshCheckbox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (autoRefreshCheckbox.isSelected()) {
                    if (autuRefreshFuture != null) {
                        autuRefreshFuture.cancel(true);
                    }
                    autuRefreshFuture = Scheduler.scheduleAtFixedRate(ConnectionForm.this::refreshConnectionTable, 5L, 5L, TimeUnit.SECONDS);
                } else {
                    if (autuRefreshFuture != null) {
                        autuRefreshFuture.cancel(true);
                        autuRefreshFuture = null;
                    }
                }
            }
        });
    }

    public JPanel getMainPanel() {
        return this.mainPanel;
    }

    private void refreshConnectionTable() {
        LOG.info("Refresh connection table");
        ActiveConnectionService.getInstance()
                .getAllActiveConnection()
                .ifPresent(this::loadConnectionList);
    }

    private void loadConnectionList(List<ActiveConnection> activeConnectionList) {
        activeConnectionList.sort(new Comparator<ActiveConnection>() {
            @Override
            public int compare(ActiveConnection o1, ActiveConnection o2) {
                return o1.getDst_addr().compareTo(o2.getDst_addr());
            }
        });

        SwingUtilities.invokeLater(() -> {
            DefaultTableModel tableModel = new DefaultTableModel(SERVER_TABLE_HEADERS, 0);
            for (int i = 0; i < activeConnectionList.size(); i++) {
                ActiveConnection activeConnection = activeConnectionList.get(i);
                int row = i + 1;
                // LOG.info(" active connection = {}", activeConnection);
                byte[] emojiBytes = new byte[]{(byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0x81};
                tableModel.addRow(new Object[]{

                        // 序号
                        String.valueOf(row),

                        // PID
                        activeConnection.getPid(),

                        // 进程名称
                        activeConnection.getProcess_name(),

                        // 执行路径
                        activeConnection.getProcess_execute_path(),

                        // 本地地址
                        activeConnection.getSrc_addr(),

                        // 本地端口
                        activeConnection.getSrc_port(),

                        // 目的地址
                        activeConnection.getDst_addr(),

                        // 目的端口
                        activeConnection.getDst_port(),

                        // 发送速度（KB）
                        getShowTransTxSpeed(activeConnection),

                        // 接收速度（KB）
                        getShowTransRxSpeed(activeConnection),

                        // 发送流量（KB）
                        getShowByte(activeConnection.getTx()),

                        // 接收流量（KB）
                        getShowByte(activeConnection.getRx()),

                        // 最近活动
                        getShowTime(activeConnection.getLatest_touch_timestamp()),

                        // 打开时间
                        getShowTime(activeConnection.getStart_timestamp()),

                        // 路由规则
                        getShowConnectionRouteRule(activeConnection.getRoute_rule()),

                        // 强制关闭
                        //"暂无"
                });
            }

            connectionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            connectionTable.clearSelection();
            connectionTable.setModel(tableModel);
            connectionTable.setShowGrid(true);

            // set font smaller
            Font newFont = new Font("", Font.PLAIN, 12);
            connectionTable.getTableHeader().setFont(newFont);
            connectionTable.getTableHeader().setLayout(new FlowLayout());
            connectionTable.setFont(newFont);
            TableColumnModel tableHeaderColumnModel = connectionTable.getTableHeader().getColumnModel();
            tableHeaderColumnModel.getColumn(0).setPreferredWidth(10);
            tableHeaderColumnModel.getColumn(1).setPreferredWidth(20);
            tableHeaderColumnModel.getColumn(2).setPreferredWidth(30);
            tableHeaderColumnModel.getColumn(3).setPreferredWidth(200);
            tableHeaderColumnModel.getColumn(4).setPreferredWidth(30);
            tableHeaderColumnModel.getColumn(5).setPreferredWidth(20);
            tableHeaderColumnModel.getColumn(6).setPreferredWidth(200);
            tableHeaderColumnModel.getColumn(7).setPreferredWidth(20);
            tableHeaderColumnModel.getColumn(8).setPreferredWidth(20);
            tableHeaderColumnModel.getColumn(9).setPreferredWidth(20);
            tableHeaderColumnModel.getColumn(10).setPreferredWidth(20);
            tableHeaderColumnModel.getColumn(11).setPreferredWidth(20);
            tableHeaderColumnModel.getColumn(12).setPreferredWidth(20);
            tableHeaderColumnModel.getColumn(13).setPreferredWidth(20);
            tableHeaderColumnModel.getColumn(14).setPreferredWidth(150);

            // set column width
            TableColumnModel tableColumnModel = connectionTable.getColumnModel();
            tableColumnModel.getColumn(0).setPreferredWidth(1);
            tableColumnModel.getColumn(1).setPreferredWidth(20);
            tableColumnModel.getColumn(2).setPreferredWidth(30);
            tableColumnModel.getColumn(3).setPreferredWidth(300);
            tableColumnModel.getColumn(4).setPreferredWidth(30);
            tableColumnModel.getColumn(5).setPreferredWidth(20);
            tableColumnModel.getColumn(6).setPreferredWidth(150);
            tableColumnModel.getColumn(7).setPreferredWidth(20);
            tableColumnModel.getColumn(8).setPreferredWidth(20);
            tableColumnModel.getColumn(9).setPreferredWidth(20);
            tableColumnModel.getColumn(10).setPreferredWidth(20);
            tableColumnModel.getColumn(11).setPreferredWidth(20);
            tableColumnModel.getColumn(12).setPreferredWidth(20);
            tableColumnModel.getColumn(13).setPreferredWidth(20);
            tableColumnModel.getColumn(14).setPreferredWidth(150);
            // connectionTable.updateUI();
        });
    }

    private String getShowTransTxSpeed(ActiveConnection connection) {
        long transByteCount = connection.getTx() - connection.getPre_tx();
        long transTimeElapsed = connection.getLatest_touch_timestamp() - connection.getPre_touch_timestamp();
        if (transByteCount <= 0 || transTimeElapsed <= 0) {
            return getShowByte(0L);
        }

        return getShowByte(transByteCount / transTimeElapsed);
    }

    private String getShowTransRxSpeed(ActiveConnection connection) {
        long transByteCount = connection.getRx() - connection.getPre_rx();
        long transTimeElapsed = connection.getLatest_touch_timestamp() - connection.getPre_touch_timestamp();
        if (transByteCount <= 0 || transTimeElapsed <= 0) {
            return getShowByte(0L);
        }

        return getShowByte(transByteCount / transTimeElapsed);
    }

    private String getShowTime(Long timestamp) {
        long secondFromNow = (System.currentTimeMillis() / 1000 - timestamp);
        if (secondFromNow <= 60) {
            return secondFromNow + "秒前";
        } else {
            return secondFromNow / 60 + "分前";
        }
    }

    private String getShowByte(Long byteCount) {
        if (byteCount <= 1024) {
            return byteCount + "B";
        }

        long KB = byteCount / 1024;
        if (KB < 1024) {
            return KB + "KB";
        }

        long MB = KB / 1024;
        if (MB < 1024) {
            return MB + "MB";
        }

        long GB = MB / 1024;
        if (GB < 1024) {
            return GB + "MB";
        }
        return "未知";
    }

    private String getShowConnectionRouteRule(ConnectionRouteRule routeRule) {
        StringBuilder sb = new StringBuilder();
        if (routeRule.hit_global_rule) {
            sb.append("全局｜");
        }

        if (routeRule.hit_process_rule) {
            sb.append("进程｜");
        }

        sb.append(routeRule.host_regex).append("｜");
        Pair<RouteRuleTypeEnum, String> routeRuleType = RegexRouteRule.getRouteRuleType(routeRule.route_rule);
        switch (routeRuleType.getA()) {
            case REJECT:
            case DIRECT:
                sb.append(routeRuleType.getA());
                break;
            case PROBE:
                if (routeRule.need_proxy) {
                    sb.append(routeRuleType.getA()).append("->").append(routeRuleType.getB());
                } else {
                    sb.append(routeRuleType.getA()).append("->").append(RouteRuleTypeEnum.DIRECT.toString());
                }
                break;
            case PROXY:
                sb.append(routeRuleType.getA()).append("->").append(routeRuleType.getB());
                break;
        }
        return sb.toString();
    }

//    private Optional<DnsHost> getHostConfigTableSelectedRow() {
//        ListSelectionModel selectionModel = hostTable.getSelectionModel();
//        int[] selectedRows = hostTable.getSelectedRows();
//        LOG.info("select table rows {}", selectedRows);
//        if (selectedRows.length >= 1) {
//            int selectedRow = selectedRows[0];
//            TableModel tableModel = hostTable.getModel();
//            String host =  tableModel.getValueAt(selectedRow, 0).toString();
//            String reverse = tableModel.getValueAt(selectedRow, 2).toString();
//            return Optional.of(DnsHost.create(host, Boolean.parseBoolean(reverse)));
//        }
//        LOG.info("select table rows {}", selectionModel);
//        return Optional.empty();
//    }

//    private class StatusEditor extends AbstractCellEditor implements TableCellEditor {
//
//        private final DnsConfigForm.StatusPanel theStatusPanel;
//
//        public StatusEditor() {
//            theStatusPanel = new DnsConfigForm.StatusPanel();
//        }
//
//        @Override
//        public Object getCellEditorValue() {
//            return theStatusPanel.getStatus();
//        }
//
//        @Override
//        public Component getTableCellEditorComponent(JTable table, Object value,
//                                                     boolean isSelected, int row, int column) {
//            DnsConfigForm.DnsResolveTypeEnum resolveType =
//                    Boolean.parseBoolean(value.toString()) ?
//                            DnsConfigForm.DnsResolveTypeEnum.LOCAL_DNS_RESOLVE :
//                            DnsConfigForm.DnsResolveTypeEnum.GATEWAY_DNS_RESOLVE;
//
//            theStatusPanel.setStatus(resolveType);
//            if (isSelected) {
//                theStatusPanel.setBackground(table.getSelectionBackground());
//            } else {
//                theStatusPanel.setBackground(table.getBackground());
//            }
//            return theStatusPanel;
//        }
//    }

//    private class StatusRenderer extends DnsConfigForm.StatusPanel implements TableCellRenderer {
//
//        @Override
//        public Component getTableCellRendererComponent(JTable table, Object value,
//                                                       boolean isSelected, boolean hasFocus, int row, int column) {
//            DnsConfigForm.DnsResolveTypeEnum resolveType =
//                    Boolean.parseBoolean(value.toString()) || StringUtils.equals(value.toString(), DnsConfigForm.DnsResolveTypeEnum.GATEWAY_DNS_RESOLVE.toString()) ?
//                            DnsConfigForm.DnsResolveTypeEnum.GATEWAY_DNS_RESOLVE :
//                            DnsConfigForm.DnsResolveTypeEnum.LOCAL_DNS_RESOLVE;
//            setStatus(resolveType);
//            if (isSelected) {
//                setBackground(table.getSelectionBackground());
//            } else {
//                setBackground(table.getBackground());
//            }
//            return this;
//        }
//    }
//
//    private enum DnsResolveTypeEnum {
//        LOCAL_DNS_RESOLVE("本地解析"),
//        GATEWAY_DNS_RESOLVE("网关解析"),
//        UNKNOWN("未知");
//
//        String text;
//
//        DnsResolveTypeEnum(String text) {
//            this.text = text;
//        }
//    }
//
//    private class StatusPanel extends JPanel {
//
//        private final JRadioButton localDnsResolveOption;
//        private final JRadioButton gatewayDnsResolveOption;
//        private final ButtonGroup buttonGroup = new ButtonGroup();
//
//        StatusPanel() {
//            super(new FlowLayout());
//            setOpaque(true);
//            localDnsResolveOption = createRadio(DnsConfigForm.DnsResolveTypeEnum.LOCAL_DNS_RESOLVE);
//            gatewayDnsResolveOption = createRadio(DnsConfigForm.DnsResolveTypeEnum.GATEWAY_DNS_RESOLVE);
//            localDnsResolveOption.addActionListener(e -> {
//                LOG.info("local dns resolver option change");
//                saveSelectedHostConfig(!localDnsResolveOption.isSelected());
//            });
//            gatewayDnsResolveOption.addActionListener(e -> {
//                LOG.info("gateway dns resolver option change");
//                saveSelectedHostConfig(gatewayDnsResolveOption.isSelected());
//            });
//        }
//
//        private JRadioButton createRadio(DnsConfigForm.DnsResolveTypeEnum dnsResolveTypeEnum) {
//            JRadioButton jrb = new JRadioButton(dnsResolveTypeEnum.text);
//            Font font = jrb.getFont();
//            Font newFont = new Font(font.getFontName(), font.getStyle(), font.getSize() - 4);
//            jrb.setFont(newFont);
//            jrb.setOpaque(false);
//            add(jrb);
//            buttonGroup.add(jrb);
//            return jrb;
//        }
//
//        public DnsConfigForm.DnsResolveTypeEnum getStatus() {
//            if (localDnsResolveOption.isSelected()) {
//                return DnsConfigForm.DnsResolveTypeEnum.LOCAL_DNS_RESOLVE;
//            } else if (gatewayDnsResolveOption.isSelected()) {
//                return DnsConfigForm.DnsResolveTypeEnum.GATEWAY_DNS_RESOLVE;
//            } else {
//                return DnsConfigForm.DnsResolveTypeEnum.UNKNOWN;
//            }
//        }
//
//        public void setStatus(DnsConfigForm.DnsResolveTypeEnum dnsResolveTypeEnum) {
//            if (dnsResolveTypeEnum == DnsConfigForm.DnsResolveTypeEnum.LOCAL_DNS_RESOLVE) {
//                localDnsResolveOption.setSelected(true);
//                // saveSelectedHostConfig(false);
//            } else if (dnsResolveTypeEnum == DnsConfigForm.DnsResolveTypeEnum.GATEWAY_DNS_RESOLVE) {
//                gatewayDnsResolveOption.setSelected(true);
//                // saveSelectedHostConfig(true);
//            }  //
//        }
//    }

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
        tooltipPanel = new JPanel();
        tooltipPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(tooltipPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        mainPanel.add(scrollPane1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        connectionTable = new JTable();
        scrollPane1.setViewportView(connectionTable);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
