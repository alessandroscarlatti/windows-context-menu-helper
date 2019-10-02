package io.github.alessandroscarlatti.windows.reg;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Alessandro Scarlatti
 * @since Tuesday, 10/1/2019
 */
public abstract class RegSpec {

    private String regInstall;
    private String regUninstall;
    private String regBackup;

    public abstract void buildSpec();

    public abstract void exportSpec(OutputStream os) throws IOException;

    public String getRegInstall() {
        return regInstall;
    }

    public void setRegInstall(String regInstall) {
        this.regInstall = regInstall;
    }

    public String getRegUninstall() {
        return regUninstall;
    }

    public void setRegUninstall(String regUninstall) {
        this.regUninstall = regUninstall;
    }

    public String getRegBackup() {
        return regBackup;
    }

    public void setRegBackup(String regBackup) {
        this.regBackup = regBackup;
    }
}
