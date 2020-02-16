package io.github.alessandroscarlatti.command;

import io.github.alessandroscarlatti.menu.Menu;
import io.github.alessandroscarlatti.project.ProjectContext;
import io.github.alessandroscarlatti.project.ProjectParser;
import io.github.alessandroscarlatti.windows.menu.Icon;

import java.nio.file.Path;
import java.util.Properties;

import static io.github.alessandroscarlatti.command.CommandConfig.PROP_REG_UID;
import static io.github.alessandroscarlatti.project.ProjectParser.overlayProperties;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 9/28/2019
 */
public class CommandParser {

    private Path commandDir;  // the dir containing this command
    private CommandConfig commandConfig;  // the config from command.properties
    private Menu parentMenu; // the parent menu, may be null if no parent
    private ProjectContext projectContext;

    public CommandParser(Path commandDir, ProjectContext projectContext, Menu parentMenu) {
        this.commandDir = commandDir;
        this.projectContext = projectContext;
        this.parentMenu = parentMenu;
    }

    public Command parseCommand() {

        commandConfig = parseCommandConfig();

        Command command = new Command(commandConfig, projectContext);
        command.setBat(parseCommandBat());
        command.setIcon(parseCommandIcon());
        command.setText(parseCommandText());
        return command;
    }

    private CommandConfig parseCommandConfig() {
        // combine properties
        Properties commandProperties = overlayProperties(new Properties[]{
            defaultCommandProperties(), // hardcoded defaults
            userCommandProperties()     // overlay any user-provided properties
        });

        CommandConfig commandConfig = CommandConfig.fromProperties(commandProperties);

        return commandConfig;
    }

    private Properties defaultCommandProperties() {
        Properties props = new Properties();
        props.setProperty(PROP_REG_UID, buildDefaultRegUid());
        return props;
    }

    private Properties userCommandProperties() {
        // can read from a .properties file (if exists)
        Path configFile = ProjectParser.findFirstFileByExample(commandDir, "command.properties");

        // user-provided properties file is not required
        if (configFile == null)
            // return empty properties
            return new Properties();
        else
            // parse the menu config file
            return ProjectParser.readPropertiesFile(configFile);
    }

    private Path parseCommandBat() {
        return ProjectParser.findFirstFileByExample(commandDir, "command.bat");
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
