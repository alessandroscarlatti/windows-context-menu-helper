package io.github.alessandroscarlatti.menu;

import java.util.Properties;

import static io.github.alessandroscarlatti.project.ProjectParser.parseBoolean;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 9/28/2019
 */
public class MenuConfig {
    // any settings for menu from the menu.properties file
    private String regUid;  // the unique prefix for all registry items in this menu, eg, "SomeTool", where would be used eg, "SomeTool.SomeMenu"
    private boolean targetDesktopEnabled;
    private boolean targetDirectoryEnabled;

    public static final String PROP_REG_UID = "menu.reg.id";
    public static final String PROP_TARGET_DESKTOP_ENABLED = "menu.target.desktop.enabled";
    public static final String PROP_TARGET_DIRECTORY_ENABLED = "menu.target.directory.enabled";

    public static MenuConfig fromProperties(Properties commandProperties) {
        MenuConfig menuConfig = new MenuConfig();
        menuConfig.setRegUid(commandProperties.getProperty(PROP_REG_UID));
        menuConfig.setTargetDesktopEnabled(parseBoolean(commandProperties.getProperty(PROP_TARGET_DESKTOP_ENABLED)));
        menuConfig.setTargetDirectoryEnabled(parseBoolean(commandProperties.getProperty(PROP_TARGET_DIRECTORY_ENABLED)));
        return menuConfig;
    }

    public String getRegUid() {
        return regUid;
    }

    public void setRegUid(String regUid) {
        this.regUid = regUid;
    }

    public boolean getTargetDesktopEnabled() {
        return targetDesktopEnabled;
    }

    public void setTargetDesktopEnabled(boolean targetDesktopEnabled) {
        this.targetDesktopEnabled = targetDesktopEnabled;
    }

    public boolean getTargetDirectoryEnabled() {
        return targetDirectoryEnabled;
    }

    public void setTargetDirectoryEnabled(boolean targetDirectoryEnabled) {
        this.targetDirectoryEnabled = targetDirectoryEnabled;
    }
}
