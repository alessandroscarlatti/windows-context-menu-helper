package com.github.alessandroscarlatti.command;

import com.github.alessandroscarlatti.windows.ContextMenuItem;
import com.github.alessandroscarlatti.windows.Icon;

import java.nio.file.Path;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 9/28/2019
 */
public class Command implements ContextMenuItem {

    private Path bat;  // the bat that will be executed when calling this command
    private Icon icon; // the icon that will be used on the menu for this command
    private String text;  // the text that will be displayed on the menu for this command
    private String regParent;  // the full path to the location of this command key in the Registry, eg, HKEY_CLASSES_ROOT\Directory\Background\shell
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

    public String getRegParent() {
        return regParent;
    }

    public void setRegParent(String regParent) {
        this.regParent = regParent;
    }

    public String getRegName() {
        return regName;
    }

    public void setRegName(String regName) {
        this.regName = regName;
    }
}
