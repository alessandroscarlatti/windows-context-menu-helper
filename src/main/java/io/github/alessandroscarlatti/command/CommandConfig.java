package io.github.alessandroscarlatti.command;

import java.util.Properties;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 9/28/2019
 */
public class CommandConfig {

    private String regUid;

    public static CommandConfig fromProperties(Properties props) {
        // the command config read from command.properties
        return new CommandConfig();
    }

    public String getRegUid() {
        return regUid;
    }

    public void setRegUid(String regUid) {
        this.regUid = regUid;
    }
}
