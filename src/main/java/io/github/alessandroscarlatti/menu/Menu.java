package io.github.alessandroscarlatti.menu;

import io.github.alessandroscarlatti.windows.menu.ContextMenuItem;
import io.github.alessandroscarlatti.windows.menu.Icon;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 9/28/2019
 */
public class Menu implements ContextMenuItem {

    private List<ContextMenuItem> children = new ArrayList<>();  // the child items contained inside this menu, may be commands, sub-menus, or separators
    private Icon icon;  // the icon displayed for this menu
    private String text;  // the text displayed for this menu
    private String regName;  // the name of the Registry key for this menu, eg, SomeTool.SomeMenu

    @Override
    public String toString() {
        return "Menu{" +
            "text='" + text + '\'' +
            ", children=" + children +
            '}';
    }

    public List<ContextMenuItem> getChildren() {
        return children;
    }

    public void setChildren(List<ContextMenuItem> children) {
        this.children = children;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getRegName() {
        return regName;
    }

    public void setRegName(String regName) {
        this.regName = regName;
    }
}
