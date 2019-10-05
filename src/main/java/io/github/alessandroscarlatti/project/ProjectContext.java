package io.github.alessandroscarlatti.project;

import io.github.alessandroscarlatti.windows.reg.RegExportUtil;

import java.nio.file.Path;

/**
 * @author Alessandro Scarlatti
 * @since Friday, 10/4/2019
 */
public class ProjectContext {

    private Path projectDir;
    private Path syncDir;
    private ProjectConfig projectConfig = new ProjectConfig();
    private RegExportUtil regExportUtil;

    public ProjectConfig getProjectConfig() {
        return projectConfig;
    }

    public void setProjectConfig(ProjectConfig projectConfig) {
        this.projectConfig = projectConfig;
    }

    public RegExportUtil getRegExportUtil() {
        return regExportUtil;
    }

    public void setRegExportUtil(RegExportUtil regExportUtil) {
        this.regExportUtil = regExportUtil;
    }

    public Path getProjectDir() {
        return projectDir;
    }

    public void setProjectDir(Path projectDir) {
        this.projectDir = projectDir;
    }

    public Path getSyncDir() {
        return syncDir;
    }

    public void setSyncDir(Path syncDir) {
        this.syncDir = syncDir;
    }
}
