package io.github.alessandroscarlatti.command;

import io.github.alessandroscarlatti.menu.MenuConfig;
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
    private MenuConfig parentMenuConfig; // the config for the parent menu, may be null if no parent

    public CommandParser(Path commandDir, MenuConfig parentMenuConfig) {
        this.commandDir = commandDir;
        this.parentMenuConfig = parentMenuConfig;
    }

    public Command parseCommand() {

        commandConfig = parseCommandConfig();

        Command command = new Command();
        command.setBat(parseCommandBat());
        command.setIcon(parseCommandIcon());
        command.setText(parseCommandText());
        command.setRegName(parseRegName());
        return command;
    }

    private CommandConfig parseCommandConfig() {
        // combine properties
        Properties commandProperties = overlayProperties(new Properties[]{
            defaultCommandProperties(), // hardcoded defaults
            userCommandProperties()     // overlay any user-provided properties
        });

        CommandConfig commandConfig = CommandConfig.fromProperties(commandProperties);

        // every command should inherit the parent's reg prefix
        if (parentMenuConfig != null)
            commandConfig.setRegUid(parentMenuConfig.getRegUid());

        return commandConfig;
    }

    private Properties defaultCommandProperties() {
        Properties props = new Properties();
        props.setProperty(PROP_REG_UID, parentMenuConfig == null ? buildDefaultRegUid() : parentMenuConfig.getRegUid());
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

    private String parseRegName() {
        // create a reg name of the form {menu.reg.id}.{condensed menu text name + unique id}
        String regCommandName = parseCommandText().replaceAll("\\s", "");
        return commandConfig.getRegUid() + "." + regCommandName;
    }

    private String buildDefaultRegUid() {
        return parseCommandText().replaceAll("\\s", "");
    }
}
