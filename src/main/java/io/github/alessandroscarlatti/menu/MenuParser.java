package io.github.alessandroscarlatti.menu;

import io.github.alessandroscarlatti.command.Command;
import io.github.alessandroscarlatti.command.CommandParser;
import io.github.alessandroscarlatti.group.Group;
import io.github.alessandroscarlatti.group.GroupParser;
import io.github.alessandroscarlatti.project.ProjectContext;
import io.github.alessandroscarlatti.windows.menu.ContextMenuItem;
import io.github.alessandroscarlatti.windows.menu.Icon;
import io.github.alessandroscarlatti.project.ProjectParser;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static io.github.alessandroscarlatti.menu.MenuConfig.PROP_REG_UID;
import static io.github.alessandroscarlatti.menu.MenuConfig.PROP_TARGET_DESKTOP_ENABLED;
import static io.github.alessandroscarlatti.menu.MenuConfig.PROP_TARGET_DIRECTORY_ENABLED;
import static io.github.alessandroscarlatti.project.ProjectParser.overlayProperties;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 9/28/2019
 */
public class MenuParser {

    private Path menuDir;  // the dir containing this menu
    private MenuConfig menuConfig;  // the config read from menu.properties
    private Menu parentMenu;  // the parent menu, may be null if this is the root menu
    private ProjectContext projectContext;

    public MenuParser(Path menuDir, ProjectContext projectContext, Menu parentMenu) {
        this.menuDir = menuDir;
        this.projectContext = projectContext;
        this.parentMenu = parentMenu;
    }

    public Menu parseMenu() {
        // parse a menu object from this dir
        menuConfig = parseMenuConfig();

        // build a menu
        Menu menu = new Menu(menuConfig, projectContext);
        menu.setText(parseMenuText());
        menu.setIcon(parseMenuIcon());

        // find the dirs in the menu dir that contain menus or commands
        List<Path> groupDirs = ProjectParser.findGroupDirs(menuDir);
        List<Path> menuDirs = ProjectParser.findMenuDirs(menuDir);
        List<Path> commandDirs = ProjectParser.findCommandDirs(menuDir);

        List<ContextMenuItem> contextMenuItems = new ArrayList<>();

        // now parse these into context menu items
        for (Path groupDir : groupDirs) {
            Group group = new GroupParser(groupDir, projectContext, menu).parseGroup();
            contextMenuItems.add(group);
        }

        for (Path menuDir : menuDirs) {
            Menu subMenu = new MenuParser(menuDir, projectContext, menu).parseMenu();
            contextMenuItems.add(subMenu);
        }

        for (Path commandDir : commandDirs) {
            Command command = new CommandParser(commandDir, projectContext, menu).parseCommand();
            contextMenuItems.add(command);
        }

        // sort the items, since they are still unordered
        ProjectParser.sortContextMenuItems(contextMenuItems);

        for (ContextMenuItem contextMenuItem : contextMenuItems) {
            Menu.connectParentToChild(menu, contextMenuItem);
        }

        return menu;
    }

    private MenuConfig parseMenuConfig() {
        Properties commandProperties = overlayProperties(new Properties[]{
            defaultMenuProperties(), // hardcoded defaults
            userMenuProperties()     // overlay any user-provided properties
        });

        MenuConfig menuConfig = MenuConfig.fromProperties(commandProperties);
        if (menuConfig.getRegUid().isEmpty()) {
            menuConfig.setRegUid(buildDefaultRegUid());
        }
        return menuConfig;
    }

    private Properties defaultMenuProperties() {
        Properties props = new Properties();
        props.setProperty(PROP_TARGET_DESKTOP_ENABLED, "true");
        props.setProperty(PROP_TARGET_DIRECTORY_ENABLED, "true");

        // choose the default reg prefix
        // or inherit the parent's reg prefix
        props.setProperty(PROP_REG_UID, buildDefaultRegUid());

        return props;
    }

    private Properties userMenuProperties() {
        // can read from a .properties file (if exists)
        Path configFile = ProjectParser.findFirstFileByExample(menuDir, "menu.properties");

        // user-provided properties file is not required
        if (configFile == null)
            // return empty properties
            return new Properties();
        else
            // parse the menu config file
            return ProjectParser.readPropertiesFile(configFile);
    }

    private String parseMenuText() {
        // eg, "01_Menu_Some Menu" = "Some Menu"
        String rawDirName = menuDir.getFileName().toString();
        return rawDirName.replaceAll(".*?Menu_(.+)", "$1").trim();
    }

    private Icon parseMenuIcon() {
        Path iconFile = ProjectParser.findIconFile(menuDir);

        if (iconFile != null)
            return new Icon(iconFile);  // use icon found in the directory
        else
            return null;  // use no icon
    }

    private String buildDefaultRegUid() {
        return parseMenuText().replaceAll("\\s", "");
    }
}
