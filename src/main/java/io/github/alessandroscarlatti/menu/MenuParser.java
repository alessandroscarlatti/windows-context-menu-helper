package io.github.alessandroscarlatti.menu;

import io.github.alessandroscarlatti.command.Command;
import io.github.alessandroscarlatti.command.CommandParser;
import io.github.alessandroscarlatti.project.ProjectConfig;
import io.github.alessandroscarlatti.windows.ContextMenuItem;
import io.github.alessandroscarlatti.windows.Icon;
import io.github.alessandroscarlatti.project.ProjectParser;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static io.github.alessandroscarlatti.project.ProjectParser.overlayProperties;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 9/28/2019
 */
public class MenuParser {

    private Path menuDir;  // the dir containing this menu
    private MenuConfig menuConfig;  // the config read from menu.properties
    private ProjectConfig projectConfig;  // the config for the project containing this menu
    private MenuConfig parentMenuConfig;  // the config for the parent menu, may be null if no parent config

    public MenuParser(Path menuDir, ProjectConfig projectConfig, MenuConfig parentMenuConfig) {
        this.menuDir = menuDir;
        this.projectConfig = projectConfig;
        this.parentMenuConfig = parentMenuConfig;
    }

    public Menu parseMenu() {
        // parse a menu object from this dir
        menuConfig = parseMenuConfig();

        // find the dirs in the menu dir that contain menus or commands
        List<Path> menuDirs = ProjectParser.findMenuDirs(menuDir);
        List<Path> commandDirs = ProjectParser.findCommandDirs(menuDir);

        List<ContextMenuItem> contextMenuItems = new ArrayList<>();

        // now parse these into context menu items
        for (Path menuDir : menuDirs) {
            Menu menu = new MenuParser(menuDir, projectConfig, menuConfig).parseMenu();
            contextMenuItems.add(menu);
        }

        for (Path commandDir : commandDirs) {
            Command command = new CommandParser(commandDir, menuConfig).parseCommand();
            contextMenuItems.add(command);
        }

        // sort the items, since they are still unordered
        ProjectParser.sortContextMenuItems(contextMenuItems);

        // create the menu
        Menu menu = new Menu();
        menu.setChildren(contextMenuItems);
        menu.setText(parseMenuText());
        menu.setIcon(parseMenuIcon());
        menu.setRegName(parseRegName());
        return menu;
    }

    private MenuConfig parseMenuConfig() {
        Properties commandProperties = overlayProperties(new Properties[]{
            defaultMenuProperties(), // hardcoded defaults
            userMenuProperties()     // overlay any user-provided properties
        });

        MenuConfig menuConfig = MenuConfig.fromProperties(commandProperties);

        // every menu should inherit the parent's reg prefix
        if (parentMenuConfig != null)
            menuConfig.setRegUid(parentMenuConfig.getRegUid());

        return menuConfig;
    }

    private Properties defaultMenuProperties() {
        return new Properties();
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

    private String parseRegName() {
        // create a reg name of the form {menu.reg.id}.{condensed menu text name + unique id}
        String regMenuName = parseMenuText().replaceAll("\\s", "");
        return menuConfig.getRegUid() + "." + regMenuName;
    }
}
