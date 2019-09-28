package io.github.alessandroscarlatti.command;

import io.github.alessandroscarlatti.windows.menu.ContextMenuItem;
import io.github.alessandroscarlatti.windows.menu.Icon;

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
}
