package io.github.alessandroscarlatti.menu;

import io.github.alessandroscarlatti.command.Command;
import io.github.alessandroscarlatti.windows.menu.ContextMenuItem;
import io.github.alessandroscarlatti.windows.menu.Icon;
import io.github.alessandroscarlatti.windows.reg.RegSpec;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 9/28/2019
 */
public class Menu implements ContextMenuItem {

    private Menu parent;
    private List<ContextMenuItem> children = new ArrayList<>();  // the child items contained inside this menu, may be commands, sub-menus, or separators
    private Icon icon;  // the icon displayed for this menu
    private String text;  // the text displayed for this menu
    private String regName;  // the name of the Registry key for this menu, eg, SomeTool.SomeMenu

    // the reg spec to use for this context menu
    // only a root level menu has a reg spec.
    private RegSpec regSpec;

    public Menu() {
        regSpec = new AbstractMenuRegSpec(this);
    }

    public static void connectParentToChild(Menu parent, ContextMenuItem child) {
        parent.getChildren().add(child);

        if (child instanceof Menu) {
            ((Menu) child).setParent(parent);
            ((Menu) child).setRegSpec(null);  // no reg spec for a child menu
        }

        if (child instanceof Command) {
            ((Command) child).setParent(parent);
            ((Command) child).setRegSpec(null);  // no reg spec for a child command
            ((Command) child).setRegSpec(null);  // no reg spec for a child command
        }
    }

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

    public Menu getParent() {
        return parent;
    }

    public void setParent(Menu parent) {
        this.parent = parent;
    }

    public RegSpec getRegSpec() {
        return regSpec;
    }

    public void setRegSpec(RegSpec regSpec) {
        this.regSpec = regSpec;
    }
}
