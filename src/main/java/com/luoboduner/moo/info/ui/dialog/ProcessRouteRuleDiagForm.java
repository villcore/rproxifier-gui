package com.luoboduner.moo.info.ui.dialog;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.luoboduner.moo.info.App;
import com.luoboduner.moo.info.bean.ProcessRegexRouteRule;
import com.luoboduner.moo.info.bean.ProxyServerConfig;
import com.luoboduner.moo.info.bean.RegexRouteRule;
import com.luoboduner.moo.info.bean.RouteRuleTypeEnum;
import com.luoboduner.moo.info.service.ProxyServerConfigService;
import com.luoboduner.moo.info.service.RouteRuleService;
import com.luoboduner.moo.info.util.ComponentUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.util.tuples.Pair;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ProcessRouteRuleDiagForm extends JDialog {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessRouteRuleDiagForm.class);
    private static final String INVALID_PROXY_SERVER_CONFIG = "请在【代理配置】中添加服务配置";

    private JPanel mainPanel;
    private JTextField processRouteRuleRegexText;
    private JComboBox<String> processRouteTypeComboBox;
    private JPanel proxyConfigPanel;
    private JComboBox<String> processProxyServerConfigComboBox;
    private JButton processRouteRuleSaveButton;
    private JButton processRouteRuleCancelButton;
    private JTextField processPathText;
    private JButton processChooserButton;

    public ProcessRouteRuleDiagForm(String title) {
        super(App.mainFrame, title);
        ComponentUtil.setPreferSizeAndLocateToCenter(this, 0.35, 0.34);
        setContentPane(mainPanel);
        setModal(true);
        this.proxyConfigPanel.setVisible(false);

        initComboBox();
        addButtonMouseListener();
    }

    public ProcessRouteRuleDiagForm(String title, ProcessRegexRouteRule processRegexRouteRule) {
        super(App.mainFrame, title);
        ComponentUtil.setPreferSizeAndLocateToCenter(this, 0.35, 0.34);
        setContentPane(mainPanel);
        setModal(true);
        this.proxyConfigPanel.setVisible(false);

        initComboBox(processRegexRouteRule);
        addButtonMouseListener();
    }

    private void initComboBox() {
        initProcessRouteTypeComboBox();
        initProcessProxyServerConfigComboBox();
    }

    private void initProcessRouteTypeComboBox() {
        // config globalRouteTypeComboBox
        for (RouteRuleTypeEnum routeRuleType : RouteRuleTypeEnum.values()) {
            this.processRouteTypeComboBox.addItem(routeRuleType.getTitle());
        }

        this.processRouteTypeComboBox.addItemListener(e -> {
            RouteRuleTypeEnum selectedGlobalRouteType = getSelectedProcessRouteType();
            switch (selectedGlobalRouteType) {
                case DIRECT:
                case REJECT:
                    proxyConfigPanel.setVisible(false);
                    break;
                case PROXY:
                case PROBE:
                    proxyConfigPanel.setVisible(true);
                    break;
            }
        });
    }

    private RouteRuleTypeEnum getSelectedProcessRouteType() {
        int selectedProcessRouteTypeIndex = processRouteTypeComboBox.getSelectedIndex();
        return RouteRuleTypeEnum.values()[selectedProcessRouteTypeIndex];
    }

    private void initProcessProxyServerConfigComboBox() {
        List<ProxyServerConfig> proxyServerConfigList = ProxyServerConfigService.getInstance().getAllProxyServerConfig();
        if (proxyServerConfigList.isEmpty()) {
            this.processProxyServerConfigComboBox.addItem(INVALID_PROXY_SERVER_CONFIG);
        } else {
            proxyServerConfigList.forEach(
                    proxyServerConfig -> processProxyServerConfigComboBox.addItem(proxyServerConfig.getName())
            );
        }
    }

    private void initComboBox(ProcessRegexRouteRule processRegexRouteRule) {
        initProcessRouteTypeComboBox();
        initProcessProxyServerConfigComboBox();

        RegexRouteRule regexRouteRule = processRegexRouteRule.getRoute_rule();
        processPathText.setText(processRegexRouteRule.getProcess_path());
        processPathText.setEditable(false);
        processRouteRuleRegexText.setText(regexRouteRule.host_regex);
        processRouteRuleRegexText.setEditable(false);

        Pair<RouteRuleTypeEnum, String> routeRuleConfigPair = RegexRouteRule.getRouteRuleType(regexRouteRule.route_rule);
        RouteRuleTypeEnum routeRuleType = routeRuleConfigPair.getA();
        String routeRuleConfig = routeRuleConfigPair.getB();
        RouteRuleTypeEnum[] routeRuleTypeEnums = RouteRuleTypeEnum.values();
        for (int i = 0; i < routeRuleTypeEnums.length; i++) {
            if (routeRuleType == routeRuleTypeEnums[i]) {
                processRouteTypeComboBox.setSelectedIndex(i);
            }
        }

        switch (routeRuleType) {
            case DIRECT:
            case REJECT:
                proxyConfigPanel.setVisible(false);
                break;
            case PROXY:
            case PROBE:
                proxyConfigPanel.setVisible(true);
                break;
        }

        // config globalProxyServerConfigComboBox
        List<ProxyServerConfig> proxyServerConfigList = ProxyServerConfigService.getInstance().getAllProxyServerConfig();
        if (proxyServerConfigList.isEmpty()) {
            this.processProxyServerConfigComboBox.addItem(INVALID_PROXY_SERVER_CONFIG);
        } else {
            for (int i = 0; i < proxyServerConfigList.size(); i++) {
                ProxyServerConfig proxyServerConfig = proxyServerConfigList.get(i);
                if (StringUtils.equals(proxyServerConfig.getName(), routeRuleConfig)) {
                    processProxyServerConfigComboBox.setSelectedIndex(i);
                }
            }
        }
    }

    private void addButtonMouseListener() {
        processRouteRuleSaveButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                saveProcessRouteRule();
            }
        });

        processRouteRuleCancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ProcessRouteRuleDiagForm.this.dispose();
            }
        });

        processChooserButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileView(new FileView() {
                    @Override
                    public Icon getIcon(File f) {
                        return FileSystemView.getFileSystemView().getSystemIcon(f);
                    }
                });
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setMultiSelectionEnabled(false);
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    LOG.info("Selected File is {}", selectedFile);
                    processPathText.setText(selectedFile.getAbsolutePath());
                }
            }
        });
    }

    private void saveProcessRouteRule() {
        String processPath = StringUtils.trim(processPathText.getText());
        if (StringUtils.isBlank(processPath)) {
            JOptionPane.showMessageDialog(this, "请输入/选择进程路径", "请输入/选择进程路径", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String processRouteRuleRuleRegex = StringUtils.trim(processRouteRuleRegexText.getText());
        if (StringUtils.isBlank(processRouteRuleRuleRegex)) {
            JOptionPane.showMessageDialog(this, "请输入正则规则", "请输入正则规则", JOptionPane.ERROR_MESSAGE);
            return;
        }

        RouteRuleTypeEnum routeRuleType = getSelectedProcessRouteType();
        String selectedGlobalServerConfig = getSelectedProcessServerConfig().trim();
        switch (routeRuleType) {
            case DIRECT:
                break;
            case PROXY:
            case PROBE:
                if (StringUtils.equals(selectedGlobalServerConfig, INVALID_PROXY_SERVER_CONFIG)) {
                    JOptionPane.showMessageDialog(this, INVALID_PROXY_SERVER_CONFIG, INVALID_PROXY_SERVER_CONFIG, JOptionPane.ERROR_MESSAGE);
                    return;
                }
                break;
        }


        Object routeRule;
        switch (routeRuleType) {
            case DIRECT:
                routeRule = RouteRuleTypeEnum.DIRECT.getCode();
                break;
            case REJECT:
                routeRule = RouteRuleTypeEnum.REJECT.getCode();
                break;
            case PROXY:
                routeRule = Collections.singletonMap(RouteRuleTypeEnum.PROXY.getCode(), selectedGlobalServerConfig);
                break;
            case PROBE:
                routeRule = Collections.singletonMap(RouteRuleTypeEnum.PROBE.getCode(), selectedGlobalServerConfig);
                break;
            default:
                routeRule = null;
        }

        RegexRouteRule regexRouteRule = new RegexRouteRule();
        regexRouteRule.setHost_regex(processRouteRuleRuleRegex);
        regexRouteRule.setRoute_rule(routeRule);
        ProcessRegexRouteRule processRegexRouteRule = new ProcessRegexRouteRule();
        processRegexRouteRule.setProcess_path(processPath);
        processRegexRouteRule.setRoute_rule(regexRouteRule);
        if (RouteRuleService.getInstance().addProcessRouteRule(processRegexRouteRule)) {
            JOptionPane.showMessageDialog(this, "添加成功", "添加成功", JOptionPane.PLAIN_MESSAGE);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "添加失败", "添加失败", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getSelectedProcessServerConfig() {
        return Objects.requireNonNull(processProxyServerConfigComboBox.getSelectedItem()).toString();
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
        mainPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 50, 30, 50), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(30, 0, 0, 0), -1, -1));
        mainPanel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("新增进程路由规则");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel2.add(label1, gbc);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(5, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("正则规则：");
        panel4.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        processRouteRuleRegexText = new JTextField();
        panel4.add(processRouteRuleRegexText, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel5, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("路由类型：");
        panel5.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        processRouteTypeComboBox = new JComboBox();
        panel5.add(processRouteTypeComboBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel6, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        proxyConfigPanel = new JPanel();
        proxyConfigPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel6.add(proxyConfigPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        proxyConfigPanel.add(panel7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("代理配置：");
        panel7.add(label4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        processProxyServerConfigComboBox = new JComboBox();
        panel7.add(processProxyServerConfigComboBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(1, 2, new Insets(10, 0, 0, 0), -1, -1));
        panel3.add(panel8, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        processRouteRuleSaveButton = new JButton();
        processRouteRuleSaveButton.setText("保存");
        panel8.add(processRouteRuleSaveButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        processRouteRuleCancelButton = new JButton();
        processRouteRuleCancelButton.setText("退出");
        panel8.add(processRouteRuleCancelButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel9, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("进程路径：");
        panel9.add(label5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        processPathText = new JTextField();
        panel9.add(processPathText, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        processChooserButton = new JButton();
        processChooserButton.setText("选择进程");
        panel9.add(processChooserButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
