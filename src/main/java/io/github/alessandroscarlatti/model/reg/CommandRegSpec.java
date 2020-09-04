package io.github.alessandroscarlatti.model.reg;

import io.github.alessandroscarlatti.model.menu.Command;
import io.github.alessandroscarlatti.model.menu.Menu;
import io.github.alessandroscarlatti.model.reg.AbstractRegSpec;
import io.github.alessandroscarlatti.model.reg.RegKey;
import io.github.alessandroscarlatti.model.reg.RegValue;
import io.github.alessandroscarlatti.project.Project;

import java.util.Arrays;
import java.util.List;

import static io.github.alessandroscarlatti.model.reg.RegType.REG_SZ;

/**
 * @author Alessandro Scarlatti
 * @since Tuesday, 10/1/2019
 */
public class CommandRegSpec extends AbstractRegSpec {

    // The command we are building reg keys for
    private Command rootCommand;

    // This reg key will only exist if this command is directly in the system context menu.
    private RegKey hkeyClassesRootDirectoryBackgroundShell;

    private Project project;

    private static final String HKCR_DIR_SHELL_PATH = "HKEY_CLASSES_ROOT\\Directory\\Background\\shell\\";

    public CommandRegSpec(Command rootCommand, Project project) {
        this.rootCommand = rootCommand;
        this.project = project;
    }

    @Override
    public void buildSpec() {
        // this command is in the system context menu since there is no parent.
        rootCommand.setRegName(parseRegName(rootCommand, project));
        hkeyClassesRootDirectoryBackgroundShell = new RegKey(HKCR_DIR_SHELL_PATH + rootCommand.getRegName());
        hkeyClassesRootDirectoryBackgroundShell.addRegValue(new RegValue("MUIVerb", REG_SZ, rootCommand.getText()));

        if (rootCommand.getIcon() != null) {
            hkeyClassesRootDirectoryBackgroundShell.addRegValue(new RegValue("Icon", REG_SZ, rootCommand.getIcon().getFile().toString()));
        }

        if (rootCommand.getGroup() != null) {
            // if this command is in a group find if it is the first or last command
        }

        RegKey commandRegKey = hkeyClassesRootDirectoryBackgroundShell.addChildRegKey("command");
        commandRegKey.addRegValue(new RegValue(null, REG_SZ, rootCommand.getBat().toAbsolutePath().toString()));

        // now build the install .reg file
        String regInstall = project.getRegExportUtil().installToString(hkeyClassesRootDirectoryBackgroundShell);
        setRegInstall(regInstall);

        // now build the uninstall .reg file
        String regUninstall = project.getRegExportUtil().uninstallToString(hkeyClassesRootDirectoryBackgroundShell);
        setRegUninstall(regUninstall);
    }

    public static String parseRegName(Command command,  Project project) {
        // create a reg name of the form {menu.reg.id}.{condensed menu text name + unique id}
        StringBuilder sb = new StringBuilder(command.getRegUid());

        if (command.getGroup() != null) {
            sb.insert(0, ".");
            sb.insert(0, command.getGroup().getRegUid());
        }

        Menu parentMenu = command.getParent();
        while (parentMenu != null) {
            sb.insert(0, ".");
            sb.insert(0, parentMenu.getRegUid());
            parentMenu = parentMenu.getParent();
        }
        sb.insert(0, ".");
        sb.insert(0, project.getRegId());
        return sb.toString();
    }

    public List<RegKey> getAllRegKeysByPrefix(String prefix) {
        // get all the reg keys that would be part of the project
        return project.getRegExportUtil().getChildKeysByPrefix(Arrays.asList(
            new RegKey(HKCR_DIR_SHELL_PATH)
        ), prefix);
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
        return project.getRegExportUtil().exportToString(hkeyClassesRootDirectoryBackgroundShell);
    }

    public RegKey getHkeyClassesRootDirectoryBackgroundShell() {
        return hkeyClassesRootDirectoryBackgroundShell;
    }
}
