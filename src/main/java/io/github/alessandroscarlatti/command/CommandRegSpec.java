package io.github.alessandroscarlatti.command;

import io.github.alessandroscarlatti.project.ProjectContext;
import io.github.alessandroscarlatti.windows.reg.RegKey;
import io.github.alessandroscarlatti.windows.reg.AbstractRegSpec;
import io.github.alessandroscarlatti.windows.reg.RegValue;

import java.io.IOException;

import static io.github.alessandroscarlatti.windows.reg.RegType.REG_SZ;
import static java.util.Collections.singletonList;

/**
 * @author Alessandro Scarlatti
 * @since Tuesday, 10/1/2019
 */
public class CommandRegSpec extends AbstractRegSpec {

    // The command we are building reg keys for
    private Command rootCommand;

    // This reg key will only exist if this command is directly in the system context menu.
    private RegKey hkeyClassesRootDirectoryBackgroundShell;

    private ProjectContext projectContext;

    public CommandRegSpec(Command rootCommand, ProjectContext projectContext) {
        this.rootCommand = rootCommand;
        this.projectContext = projectContext;
    }

    @Override
    public void buildSpec() {
        // this command is in the system context menu since there is no parent.
        hkeyClassesRootDirectoryBackgroundShell = new RegKey("HKEY_CLASSES_ROOT\\Directory\\Background\\shell\\" + rootCommand.getRegName());
        hkeyClassesRootDirectoryBackgroundShell.addRegValue(new RegValue("MUIVerb", REG_SZ, rootCommand.getText()));

        RegKey commandRegKey = hkeyClassesRootDirectoryBackgroundShell.addChildRegKey("command");
        commandRegKey.addRegValue(new RegValue(null, REG_SZ, rootCommand.getBat().toAbsolutePath().toString()));

        // now build the install .reg file
        String regInstall = projectContext.getRegExportUtil().installToString(hkeyClassesRootDirectoryBackgroundShell);
        setRegInstall(regInstall);

        // now build the uninstall .reg file
        String regUninstall = projectContext.getRegExportUtil().uninstallToString(hkeyClassesRootDirectoryBackgroundShell);
        setRegUninstall(regUninstall);
    }

    @Override
    public String writeInstallRegScript() {
        return getRegInstall();
    }

    @Override
    public String writeUninstallRegScript() {
        return getRegUninstall();
    }

    @Override
    public String writeRestorePointRegScript() {
        return projectContext.getRegExportUtil().exportToString(hkeyClassesRootDirectoryBackgroundShell);
    }

    public RegKey getHkeyClassesRootDirectoryBackgroundShell() {
        return hkeyClassesRootDirectoryBackgroundShell;
    }
}
