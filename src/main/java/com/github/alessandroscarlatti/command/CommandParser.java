package com.github.alessandroscarlatti.command;

import java.nio.file.Path;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 9/28/2019
 */
public class CommandParser {

    private Path commandDir;  // the dir containing this command
    private CommandConfig commandConfig;  // the config from command.properties

    public CommandParser(Path commandDir) {
        this.commandDir = commandDir;
    }

    public Command parseCommand() {
        Command command = new Command();

        return command;
    }
}
