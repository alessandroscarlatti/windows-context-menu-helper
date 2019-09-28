package io.github.alessandroscarlatti.menu;

import java.util.Properties; /**
 * @author Alessandro Scarlatti
 * @since Saturday, 9/28/2019
 */
public class MenuConfig {
    // any settings for menu from the menu.properties file
    private String regUid;  // the unique prefix for all registry items in this menu, eg, "SomeTool", where would be used eg, "SomeTool.SomeMenu"

    private static final String PROP_REG_UID = "menu.reg.id";

    public static MenuConfig fromProperties(Properties commandProperties) {
        MenuConfig menuConfig = new MenuConfig();
        menuConfig.setRegUid(commandProperties.getProperty(PROP_REG_UID));
        return menuConfig;
    }

    public String getRegUid() {
        return regUid;
    }

    public void setRegUid(String regUid) {
        this.regUid = regUid;
    }
}
