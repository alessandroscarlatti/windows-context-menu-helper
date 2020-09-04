package io.github.alessandroscarlatti.model.menu;

import io.github.alessandroscarlatti.reg.RegSpec;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 2/15/2020
 */
public class Group implements ContextMenuItem {

    private String name;
    private List<ContextMenuItem> children = new ArrayList<>();
    private Menu parent;
    private String regUid;

    public static void connectGroupToChild(Group parent, ContextMenuItem child) {
        parent.getChildren().add(child);

        if (child instanceof Menu) {
            ((Menu) child).setGroup(parent);
        }

        if (child instanceof Command) {
            ((Command) child).setGroup(parent);
        }
    }

    public boolean isFirstInGroup(ContextMenuItem contextMenuItem) {
        return children.indexOf(contextMenuItem) == 0;

    }

    public boolean isLastInGroup(ContextMenuItem contextMenuItem) {
        if (children.size() == 0)
            return false;
        else
            return children.indexOf(contextMenuItem) == children.size() - 1;
    }

    @Override
    public RegSpec getRegSpec() {
        return null;
    }

    @Override
    public String toString() {
        return "Group{" +
            "name='" + name + '\'' +
            ", children=" + children +
            '}';
    }

    @Override
    public String getName() {
        return null;
    }

    public List<ContextMenuItem> getChildren() {
        return children;
    }

    public void setChildren(List<ContextMenuItem> children) {
        this.children = children;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Menu getParent() {
        return parent;
    }

    public void setParent(Menu parent) {
        this.parent = parent;
    }

    public String getRegUid() {
        return regUid;
    }

    public void setRegUid(String regUid) {
        this.regUid = regUid;
    }
}
