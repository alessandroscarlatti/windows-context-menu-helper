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
    private String oldRegId; // the reg id that this project used to have. Only applicable for auto-generated ids.
    private boolean autoGenerateRegId;  // whether or not to auto-generate the project id.
    private String projectName;  // the base name of the project. By default should be the name of the directory containing the project.

    public static final String PROP_REG_ID = "project.reg.id";
    public static final String PROP_REG_ID_AUTO_GENERATE = "project.reg.id.autogenerate";
    public static final String PROP_PROJECT_NAME = "project.name";

    public static ProjectConfig fromProperties(Properties commandProperties) {
        ProjectConfig menuConfig = new ProjectConfig();
        menuConfig.setRegId(commandProperties.getProperty(PROP_REG_ID));
        menuConfig.setOldRegId(commandProperties.getProperty(PROP_REG_ID));
        menuConfig.setAutoGenerateRegId(parseBoolean(commandProperties.getProperty(PROP_REG_ID_AUTO_GENERATE)));
        menuConfig.setProjectName(commandProperties.getProperty(PROP_PROJECT_NAME));
        return menuConfig;
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public String getOldRegId() {
        return oldRegId;
    }

    public void setOldRegId(String oldRegId) {
        this.oldRegId = oldRegId;
    }

    public boolean getAutoGenerateRegId() {
        return autoGenerateRegId;
    }

    public void setAutoGenerateRegId(boolean autoGenerateRegId) {
        this.autoGenerateRegId = autoGenerateRegId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
