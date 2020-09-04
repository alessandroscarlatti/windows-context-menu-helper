package io.github.alessandroscarlatti.command;

import io.github.alessandroscarlatti.group.Group;
import io.github.alessandroscarlatti.menu.Menu;
import io.github.alessandroscarlatti.project.ProjectContext;
import io.github.alessandroscarlatti.model.menu.ContextMenuItem;
import io.github.alessandroscarlatti.model.menu.Icon;
import io.github.alessandroscarlatti.model.reg.AbstractRegSpec;

import java.nio.file.Path;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 9/28/2019
 */
public class Command implements ContextMenuItem {

    private Path bat;  // the bat that will be executed when calling this command
    private Icon icon; // the icon that will be used on the menu for this command
    private String text;  // the text that will be displayed on the menu for this command
    private String regName;  // the name of the Registry key for this command, eg, SomeTool.SomeCommand
    private Menu parent;
    private Group group;
    private CommandConfig config;

    private ProjectContext projectContext;

    // the reg spec to use for this command
    // only a root level command has a reg spec.
    private AbstractRegSpec regSpec;

    public Command(CommandConfig config, ProjectContext projectContext) {
        this.config = config;
        regSpec = new CommandRegSpec(this, projectContext);
        this.projectContext = projectContext;
    }

    @Override
    public String getName() {
        return regName;
    }

    @Override
    public String toString() {
        return "Command{" +
            "text='" + text + '\'' +
            ", bat=" + bat +
            '}';
    }

    public Path getBat() {
        return bat;
    }

    public void setBat(Path bat) {
        this.bat = bat;
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

    public AbstractRegSpec getRegSpec() {
        return regSpec;
    }

    public void setRegSpec(AbstractRegSpec regSpec) {
        this.regSpec = regSpec;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public CommandConfig getConfig() {
        return config;
    }

    public void setConfig(CommandConfig config) {
        this.config = config;
    }
}
