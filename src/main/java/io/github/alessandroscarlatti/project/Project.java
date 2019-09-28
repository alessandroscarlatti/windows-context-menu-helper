package io.github.alessandroscarlatti.project;

import io.github.alessandroscarlatti.windows.menu.ContextMenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 9/28/2019
 */
public class Project {

    private List<ContextMenuItem> contextMenuItems = new ArrayList<>();

    // this class will contain methods for syncing, installing, uninstalling, reverting, etc.

    public List<ContextMenuItem> getContextMenuItems() {
        return contextMenuItems;
    }

    public void setContextMenuItems(List<ContextMenuItem> contextMenuItems) {
        this.contextMenuItems = contextMenuItems;
    }
}
