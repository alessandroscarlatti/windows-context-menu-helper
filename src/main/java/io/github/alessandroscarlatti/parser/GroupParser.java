package io.github.alessandroscarlatti.parser;

import io.github.alessandroscarlatti.model.menu.Command;
import io.github.alessandroscarlatti.model.menu.ContextMenuItem;
import io.github.alessandroscarlatti.model.menu.Group;
import io.github.alessandroscarlatti.model.menu.Menu;
import io.github.alessandroscarlatti.project.Project;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 2/15/2020
 */
public class GroupParser {

    private Path groupDir;
    private Project project;
    private Menu parentMenu;

    public GroupParser(Path groupDir, Project project, Menu parentMenu) {
        this.groupDir = groupDir;
        this.project = project;
        this.parentMenu = parentMenu;
    }

    public Group parseGroup() {
        Group group = new Group();
        group.setName(parseGroupName());
        group.setRegUid(buildDefaultRegUid());

        List<Path> menuDirs = ProjectParser.findMenuDirs(groupDir);
        List<Path> commandDirs = ProjectParser.findCommandDirs(groupDir);
        List<ContextMenuItem> contextMenuItems = new ArrayList<>();

        for (Path menuDir : menuDirs) {
            Menu subMenu = new MenuParser(menuDir, project, parentMenu).parseMenu();
            contextMenuItems.add(subMenu);
        }

        for (Path commandDir : commandDirs) {
            Command command = new CommandParser(commandDir, project, parentMenu).parseCommand();
            contextMenuItems.add(command);
        }

        // sort the items, since they are still unordered
        ProjectParser.sortContextMenuItems(contextMenuItems);

        // add the menu items to the group
        // and to the parent menu
        for (ContextMenuItem contextMenuItem : contextMenuItems) {
            Group.connectGroupToChild(group, contextMenuItem);
            Menu.connectParentToChild(parentMenu, contextMenuItem);
        }

        return group;
    }

    private String parseGroupName() {
        // eg, "01_Group_Special Commands 1" = "Special Commands 1"
        String rawDirName = groupDir.getFileName().toString();
        return rawDirName.replaceAll(".*?Group_(.+)", "$1").trim();
    }

    private String buildDefaultRegUid() {
        return parseGroupName().replaceAll("\\s", "");
    }
}
