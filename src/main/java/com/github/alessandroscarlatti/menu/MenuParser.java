package com.github.alessandroscarlatti.menu;

import com.github.alessandroscarlatti.command.Command;
import com.github.alessandroscarlatti.command.CommandParser;
import com.github.alessandroscarlatti.project.ProjectParser;
import com.github.alessandroscarlatti.windows.ContextMenuItem;
import com.github.alessandroscarlatti.windows.Icon;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static com.github.alessandroscarlatti.project.ProjectParser.*;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 9/28/2019
 */
public class MenuParser {

    private Path menuDir;  // the dir containing this menu
    private MenuConfig menuConfig;  // the config read from menu.properties

    public MenuParser(Path menuDir) {
        this.menuDir = menuDir;
    }

    public Menu parseMenu() {
        // parse a menu object from this dir
        menuConfig = parseMenuConfig();

        // find the dirs in the menu dir that contain menus or commands
        List<Path> menuDirs = findMenuDirs(menuDir);
        List<Path> commandDirs = findCommandDirs(menuDir);

        List<ContextMenuItem> contextMenuItems = new ArrayList<>();

        // now parse these into context menu items
        for (Path menuDir : menuDirs) {
            Menu menu = new MenuParser(menuDir).parseMenu();
            contextMenuItems.add(menu);
        }

        for (Path commandDir : commandDirs) {
            Command command = new CommandParser(commandDir).parseCommand();
            contextMenuItems.add(command);
        }

        // sort the items, since they are still unordered
        sortContextMenuItems(contextMenuItems);

        // create the menu
        Menu menu = new Menu();
        menu.setChildren(contextMenuItems);
        menu.setText(parseMenuText());
        menu.setIcon(parseMenuIcon());
        return menu;
    }

    private MenuConfig parseMenuConfig() {
        // can read from a .properties file (if exists)
        Path configFile = findFirstFileByExample(menuDir, "menu.properties");

        if (configFile == null)
            return new MenuConfig();

        MenuConfig menuConfig = new MenuConfig();

        // parse the menu config file
        Properties properties = readPropertiesFile(configFile);

        return menuConfig;
    }

    private String parseMenuText() {
        // eg, "01_Menu_Some Menu" = "Some Menu"
        String rawDirName = menuDir.getFileName().toString();
        return rawDirName.replaceAll("^.+?Menu_", "").trim();
    }

    private Icon parseMenuIcon() {
        Path iconFile = findIconFile(menuDir);

        if (iconFile != null)
            return new Icon(iconFile);  // use icon found in the directory
        else
            return null;  // use no icon
    }

}
