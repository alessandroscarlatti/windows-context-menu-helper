package io.github.alessandroscarlatti.project;

import java.util.Properties;

import static io.github.alessandroscarlatti.project.ProjectParser.parseBoolean;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 9/28/2019
 */
public class ProjectConfig {

    // any settings configured by the user
    private String regId;  // the reg id for this project. All items will have this prefix. eg, MyProject.
    private boolean autoGenerateRegId;  // whether or not to autogenerate the project id.

    public static final String PROP_REG_ID = "project.reg.id";
    public static final String PROP_REG_ID_AUTO_GENERATE = "project.reg.id.autogenerate";

    public static ProjectConfig fromProperties(Properties commandProperties) {
        ProjectConfig menuConfig = new ProjectConfig();
        menuConfig.setRegId(commandProperties.getProperty(PROP_REG_ID));
        menuConfig.setAutoGenerateRegId(parseBoolean(commandProperties.getProperty(PROP_REG_ID_AUTO_GENERATE)));
        return menuConfig;
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public boolean getAutoGenerateRegId() {
        return autoGenerateRegId;
    }

    public void setAutoGenerateRegId(boolean autoGenerateRegId) {
        this.autoGenerateRegId = autoGenerateRegId;
    }
}
