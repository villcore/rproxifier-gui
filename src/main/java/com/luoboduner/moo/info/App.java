package com.luoboduner.moo.info;

import com.formdev.flatlaf.extras.FlatDesktop;
import com.formdev.flatlaf.extras.FlatInspector;
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector;
import com.formdev.flatlaf.util.SystemInfo;
import com.luoboduner.moo.info.ui.Init;
import com.luoboduner.moo.info.ui.dialog.SettingDialog;
import com.luoboduner.moo.info.ui.form.MainWindow;
import com.luoboduner.moo.info.ui.frame.MainFrame;
import com.luoboduner.moo.info.util.ConfigUtil;
import com.luoboduner.moo.info.util.UIUtil;
import com.luoboduner.moo.info.util.UpgradeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

@Slf4j
public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static ConfigUtil config = ConfigUtil.getInstance();

    public static MainFrame mainFrame;

    public static oshi.SystemInfo si;

    public static void main(String[] args) {
        if (SystemInfo.isMacOS) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("apple.awt.application.name", "MooInfo");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "MooInfo");
            if (UIUtil.isDarkLaf()) {
                System.setProperty("apple.awt.application.appearance", "system");
            }

                        FlatDesktop.setPreferencesHandler(() -> {
                try {
                    SettingDialog dialog = new SettingDialog();

                    dialog.pack();
                    dialog.setVisible(true);
                } catch (Exception e2) {
                    log.error(ExceptionUtils.getStackTrace(e2));
                }
            });
            FlatDesktop.setQuitHandler(FlatDesktop.QuitResponse::performQuit);

        }

        Init.initTheme();

        // install inspectors
        FlatInspector.install( "ctrl shift alt X" );
        FlatUIDefaultsInspector.install( "ctrl shift alt Y" );

        mainFrame = new MainFrame();
        mainFrame.init();
        // mainFrame.pack();
        mainFrame.setVisible(true);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if (config.isDefaultMaxWindow() || screenSize.getWidth() <= 1366) {
            // The window is automatically maximized at low resolution
            mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }

        UpgradeUtil.smoothUpgrade();

        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        si = new oshi.SystemInfo();

        Init.initGlobalFont();
        mainFrame.setContentPane(MainWindow.getInstance().getMainPanel());
        MainWindow.getInstance().init();
        Init.initAllTab();
        Init.initOthers();
    }
}
