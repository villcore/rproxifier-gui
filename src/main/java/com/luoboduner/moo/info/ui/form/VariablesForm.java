package com.luoboduner.moo.info.ui.form;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.luoboduner.moo.info.App;
import lombok.Getter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.Map;
import java.util.Properties;

/**
 * VariablesForm
 *
 * @author <a href="https://github.com/rememberber">RememBerBer</a>
 * @since 2021/11/12.
 */
@Getter
public class VariablesForm {
    private static final Log logger = LogFactory.get();

    private static VariablesForm variablesForm;
    private JPanel mainPanel;
    private JTable sysEnvVarTable;
    private JTable javaPropsTable;
    private JLabel sysEnvLabel;
    private JLabel javaPropLabel;

    private static final double[] COLUMN_WIDTH_PERCENT = {0.38, 0.62};

    public static VariablesForm getInstance() {
        if (variablesForm == null) {
            variablesForm = new VariablesForm();
        }
        return variablesForm;
    }

    public static void init() {
        variablesForm = getInstance();

        initUi();
        initInfo();
    }

    private static void initUi() {
        getInstance().getSysEnvVarTable().setShowGrid(true);
        getInstance().getJavaPropsTable().setShowGrid(true);

        Font emphaticFont = new Font(getInstance().getMainPanel().getFont().getName(), Font.BOLD, getInstance().getMainPanel().getFont().getSize() + 2);
        getInstance().getSysEnvLabel().setFont(emphaticFont);
        getInstance().getJavaPropLabel().setFont(emphaticFont);
    }

    private static void initInfo() {
        initSysEnvVarTable();
        initJavaPropsTable();
    }

    public static void initSysEnvVarTable() {
        String[] headerNames = {"Key", "Value"};
        DefaultTableModel model = new DefaultTableModel(null, headerNames);

        Map<String, String> map = System.getenv();
        Object[] data;
        for (Map.Entry<String, String> envEntry : map.entrySet()) {
            data = new Object[2];
            data[0] = envEntry.getKey();
            data[1] = envEntry.getValue();
            model.addRow(data);
        }

        JTable sysEnvVarTable = getInstance().getSysEnvVarTable();
        sysEnvVarTable.setModel(model);
        resizeColumns(sysEnvVarTable.getColumnModel());
    }

    public static void initJavaPropsTable() {
        String[] headerNames = {"Key", "Value"};
        DefaultTableModel model = new DefaultTableModel(null, headerNames);

        Properties properties = System.getProperties();
        Object[] data;
        for (Map.Entry<Object, Object> objectObjectEntry : properties.entrySet()) {
            data = new Object[2];
            data[0] = objectObjectEntry.getKey();
            data[1] = objectObjectEntry.getValue();
            model.addRow(data);
        }

        JTable javaPropsTable = getInstance().getJavaPropsTable();
        javaPropsTable.setModel(model);
        resizeColumns(javaPropsTable.getColumnModel());
    }

    private static void resizeColumns(TableColumnModel tableColumnModel) {
        TableColumn column;
        int tW = App.mainFrame.getWidth() - 20;
        int cantCols = tableColumnModel.getColumnCount();
        for (int i = 0; i < cantCols; i++) {
            column = tableColumnModel.getColumn(i);
            int pWidth = (int) Math.round(COLUMN_WIDTH_PERCENT[i] * tW);
            column.setPreferredWidth(pWidth);
        }
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
        mainPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(4, 1, new Insets(20, 20, 20, 20), -1, -1));
        mainPanel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(10, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        sysEnvLabel = new JLabel();
        sysEnvLabel.setText("System environment variables");
        panel2.add(sysEnvLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel1.add(scrollPane1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        sysEnvVarTable = new JTable();
        scrollPane1.setViewportView(sysEnvVarTable);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 2, new Insets(10, 0, 0, 0), -1, -1));
        panel1.add(panel3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        javaPropLabel = new JLabel();
        javaPropLabel.setText("Java properties");
        panel3.add(javaPropLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel3.add(spacer2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        panel1.add(scrollPane2, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        javaPropsTable = new JTable();
        scrollPane2.setViewportView(javaPropsTable);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
