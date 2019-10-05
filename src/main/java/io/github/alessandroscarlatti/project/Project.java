package io.github.alessandroscarlatti.project;

import io.github.alessandroscarlatti.windows.menu.ContextMenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 9/28/2019
 */
public class Project {

    // these are the context menu items parsed from the project dir
    private List<ContextMenuItem> contextMenuItems = new ArrayList<>();

    // this class will contain methods for syncing, installing, uninstalling, reverting, etc.

    public void buildRegSpecs() {
        for (ContextMenuItem contextMenuItem : contextMenuItems) {
            if (contextMenuItem.getRegSpec() != null)
                contextMenuItem.getRegSpec().buildSpec();
        }
    }

    public void exportRegSpecs() {
        for (ContextMenuItem contextMenuItem : contextMenuItems) {
            if (contextMenuItem.getRegSpec() != null) {
                try {
                    contextMenuItem.getRegSpec().writeInstallRegScript(System.out);
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<ContextMenuItem> getContextMenuItems() {
        return contextMenuItems;
    }

    public void setContextMenuItems(List<ContextMenuItem> contextMenuItems) {
        this.contextMenuItems = contextMenuItems;
    }
}
