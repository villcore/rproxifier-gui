package com.luoboduner.moo.info.ui.form;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.luoboduner.moo.info.bean.NetworkInterface;
import com.luoboduner.moo.info.bean.NetworkOverview;
import com.luoboduner.moo.info.service.NetworkOverviewService;
import com.luoboduner.moo.info.util.Scheduler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class NetOverviewForm {

    private static final Logger LOG = LoggerFactory.getLogger(NetOverviewForm.class);
    private static final NetOverviewForm INSTANCE = new NetOverviewForm();

    public static NetOverviewForm getInstance() {
        return INSTANCE;
    }

    private JPanel mainPanel;
    private JToggleButton button1;
    private JTextPane textPane1;
    private JComboBox<String> networkListComboBox;
    private JPanel tooltipPanel;
    private JTable connectionTable;

    public NetOverviewForm() {
        loadNetOverview();
        Scheduler.scheduleAtFixedRate(() -> SwingUtilities.invokeLater(this::loadNetOverview), 5L, 5L, TimeUnit.SECONDS);
        this.button1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (button1.isSelected()) {
                    if (startRproxifier()) {
                        button1.setText("已运行 ▶");
                        loadNetOverview();
                    } else {
                        JOptionPane.showMessageDialog(null, "启动Rproxifier网络服务失败", "启动失败", JOptionPane.ERROR_MESSAGE);
                        button1.setSelected(false);
                    }
                } else {
                    if (stopRproxifier()) {
                        button1.setText("已停止 ❚❚");
                        loadNetOverview();
                    } else {
                        JOptionPane.showMessageDialog(null, "停止Rproxifier网络服务失败", "停止失败", JOptionPane.ERROR_MESSAGE);
                        button1.setSelected(true);
                    }
                }
            }
        });
    }

    private boolean startRproxifier() {
        return getSelectedNetworkInterface()
                .filter(networkInterface -> NetworkOverviewService.getInstance().startNetwork(networkInterface))
                .isPresent();
    }

    private boolean stopRproxifier() {
        return getSelectedNetworkInterface()
                .filter(networkInterface -> NetworkOverviewService.getInstance().stopNetwork(networkInterface))
                .isPresent();
    }

    private Optional<NetworkInterface> getSelectedNetworkInterface() {
        String selectedNetworkInterface = Objects.requireNonNull(networkListComboBox.getSelectedItem()).toString();
        return NetworkInterface.formFormat(selectedNetworkInterface);
    }

    public JPanel getMainPanel() {
        return this.mainPanel;
    }

    private void loadNetOverview() {
        NetworkOverviewService.getInstance().getNetworkOverview().ifPresent(this::loadNetOverview);
    }

    private void loadNetOverview(NetworkOverview networkOverview) {
        List<NetworkInterface> networkInterfaceList = networkOverview.getInterface_list();
        NetworkInterface bindInterface = networkOverview.getBind_interface();
        boolean networkStated = networkOverview.isNetwork_state();
        if (networkStated) {
            this.networkListComboBox.setEditable(false);
            this.button1.setText("已运行 ▶");
            this.button1.setSelected(true);
            this.networkListComboBox.removeAllItems();
            for (int i = 0; i < networkInterfaceList.size(); i++) {
                NetworkInterface networkInterface = networkInterfaceList.get(i);
                networkListComboBox.addItem(NetworkInterface.format(networkInterface));
                if (StringUtils.equals(networkInterface.getInterface_name(), bindInterface.getInterface_name())) {
                    networkListComboBox.setSelectedIndex(i);
                }
            }
            setNetworkOverviewText(networkOverview);
            return;
        }

        this.button1.setText("已停止 ❚❚");
        this.button1.setSelected(false);
        this.networkListComboBox.removeAllItems();
        networkInterfaceList.forEach(networkInterface -> {
            networkListComboBox.addItem(NetworkInterface.format(networkInterface));
        });
        setNetworkOverviewText(networkOverview);
    }

    private void setNetworkOverviewText(NetworkOverview networkOverview) {
        StringBuilder sb = new StringBuilder();
        sb.append("Rproxifier运行状态：").append(networkOverview.isNetwork_state() ? "已运行" : "已停止").append("\n");
        List<NetworkInterface> networkInterfaceList = networkOverview.getInterface_list();
        for (int i = 0; i < networkInterfaceList.size(); i++) {
            NetworkInterface networkInterface = networkInterfaceList.get(0);
            sb.append(String.format("网卡 %s：%s\n", i + 1, NetworkInterface.format(networkInterface)));
        }

//        NetworkInterface bindNetworkInterface = networkOverview.getBind_interface();
//        if (StringUtils.isNotBlank(bindNetworkInterface.getInterface_name()) || StringUtils.isNotBlank(bindNetworkInterface.getIp_addr())) {
//            sb.append("Rproxifier DNS绑定网卡：").append(NetworkInterface.format(bindNetworkInterface));
//        } else {
//            sb.append("Rproxifier DNS绑定网卡：无");
//        }
        textPane1.setText(sb.toString());
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
        mainPanel.setLayout(new GridLayoutManager(1, 1, new Insets(30, 50, 30, 50), -1, -1));
        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-4473925)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 1, new Insets(0, 10, 0, 10), -1, -1));
        mainPanel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-4473925)), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("出口网卡：");
        panel2.add(label1);
        networkListComboBox = new JComboBox();
        panel2.add(networkListComboBox);
        button1 = new JToggleButton();
        button1.setText("Button");
        panel2.add(button1);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(10, 0, 10, 0), -1, -1));
        panel1.add(panel3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        textPane1 = new JTextPane();
        panel3.add(textPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
