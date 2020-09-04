package io.github.alessandroscarlatti.parser;

import io.github.alessandroscarlatti.model.menu.Command;
import io.github.alessandroscarlatti.model.menu.Icon;
import io.github.alessandroscarlatti.model.menu.Menu;
import io.github.alessandroscarlatti.project.Project;

import java.nio.file.Path;
import java.util.Properties;

import static io.github.alessandroscarlatti.util.ProjectUtils.findFirstFileByExample;
import static io.github.alessandroscarlatti.util.ProjectUtils.overlayProperties;
import static io.github.alessandroscarlatti.util.ProjectUtils.readPropertiesFile;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 9/28/2019
 */
public class CommandParser {

    private Path commandDir;  // the dir containing this command
    private Menu parentMenu; // the parent menu, may be null if no parent
    private Project project;

    private static final String PROP_REG_UID = "command.reg.id";


    public CommandParser(Path commandDir, Project project, Menu parentMenu) {
        this.commandDir = commandDir;
        this.project = project;
        this.parentMenu = parentMenu;
    }

    public Command parseCommand() {
        Command command = new Command(project);

        // combine properties
        Properties commandProperties = overlayProperties(new Properties[]{
            defaultCommandProperties(), // hardcoded defaults
            userCommandProperties()     // overlay any user-provided properties
        });

        command.setRegUid(commandProperties.getProperty(PROP_REG_UID));
        command.setBat(parseCommandBat());
        command.setIcon(parseCommandIcon());
        command.setText(parseCommandText());
        return command;
    }

    private Properties defaultCommandProperties() {
        Properties props = new Properties();
        props.setProperty(PROP_REG_UID, buildDefaultRegUid());
        return props;
    }

    private Properties userCommandProperties() {
        // can read from a .properties file (if exists)
        Path configFile = findFirstFileByExample(commandDir, "command.properties");

        // user-provided properties file is not required
        if (configFile == null)
            // return empty properties
            return new Properties();
        else
            // parse the menu config file
            return readPropertiesFile(configFile);
    }

    private Path parseCommandBat() {
        return findFirstFileByExample(commandDir, "command.bat");
    }

    private String parseCommandText() {
        // eg, "01_Menu_Some Menu" = "Some Menu"
        String rawDirName = commandDir.getFileName().toString();
        return rawDirName.replaceAll(".*?Command_(.+)", "$1").trim();
    }

    private Icon parseCommandIcon() {
        Path iconFile = ProjectParser.findIconFile(commandDir);

        if (iconFile != null)
            return new Icon(iconFile);  // use icon found in the directory
        else
            return null;  // use no icon
    }

    private String buildDefaultRegUid() {
        return parseCommandText().replaceAll("\\s", "");
    }
}
