package io.github.alessandroscarlatti.menu;

import io.github.alessandroscarlatti.command.Command;
import io.github.alessandroscarlatti.project.ProjectContext;
import io.github.alessandroscarlatti.windows.menu.ContextMenuItem;
import io.github.alessandroscarlatti.windows.reg.RegKey;
import io.github.alessandroscarlatti.windows.reg.AbstractRegSpec;
import io.github.alessandroscarlatti.windows.reg.RegValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static io.github.alessandroscarlatti.windows.reg.RegType.REG_SZ;
import static java.util.stream.Collectors.toList;

/**
 * @author Alessandro Scarlatti
 * @since Tuesday, 10/1/2019
 */
public class MenuRegSpec extends AbstractRegSpec {

    // The menu we are building reg keys for
    private Menu rootMenu;

    // This reg key will only exist if this menu is the root menu
    private RegKey hkeyClassesRootDirectoryBackgroundShell;

    // This represents all child commands
    private List<RegKey> hkeyLocalMachineExplorerCommandStoreShells = new ArrayList<>();

    // the context for the project
    private ProjectContext projectContext;

    public MenuRegSpec(Menu rootMenu, ProjectContext projectContext) {
        this.rootMenu = rootMenu;
        this.projectContext = projectContext;
    }

    @Override
    public void buildSpec() {
        // this menu is the root menu since there is no parent.
        RegKey regKey = new RegKey("HKEY_CLASSES_ROOT\\Directory\\Background\\shell\\" + rootMenu.getRegName());
        hkeyClassesRootDirectoryBackgroundShell = regKey;

        // set the menu text
        regKey.addRegValue(new RegValue("MUIVerb", REG_SZ, rootMenu.getText()));

        // build any subcommands
        RegValue subCommandsRegValue = buildSubCommandsRegValue(rootMenu);
        regKey.addRegValue(subCommandsRegValue);

        // now build the install .reg file
        String regInstall = projectContext.getRegExportUtil().installToString(getAllSpecRegKeys());
        setRegInstall(regInstall);

        // now build the uninstall .reg file
        String regUninstall = projectContext.getRegExportUtil().uninstallToString(getAllSpecRegKeys());
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
        return projectContext.getRegExportUtil().exportToString(getAllSpecRegKeys());
    }

    private List<RegKey> getAllSpecRegKeys() {
        List<RegKey> regKeys = new ArrayList<>();
        regKeys.add(hkeyClassesRootDirectoryBackgroundShell);
        regKeys.addAll(hkeyLocalMachineExplorerCommandStoreShells);
        return regKeys;
    }

    private void buildRegKeysNotRootMenu(Menu menu, List<RegKey> parentSubCommands) {
        // this menu has a parent menu, so it's not a root menu.
        // add this menu to the list of subcommands
        // also add this menu to the command store reg keys
        RegKey regKey = new RegKey("HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\CommandStore\\shell\\" + menu.getRegName());
        hkeyLocalMachineExplorerCommandStoreShells.add(regKey);

        // set the menu text
        regKey.addRegValue(new RegValue("MUIVerb", REG_SZ, menu.getText()));

        // build any subcommands
        // and add to the list of subcommands for the parent menu
        regKey.addRegValue(buildSubCommandsRegValue(menu));
        parentSubCommands.add(regKey);
    }

    private void buildRegKeysNotRootCommand(Command command, List<RegKey> parentSubCommands) {
        // this command has a parent menu, so it's not a root command.
        // add this menu to the list of subcommands
        // also add this menu to the command store reg keys
        RegKey regKey = new RegKey("HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Explorer\\CommandStore\\shell\\" + command.getRegName());
        hkeyLocalMachineExplorerCommandStoreShells.add(regKey);

        // build any subcommands
        // and add to the list of subcommands for the parent menu

        RegKey commandRegKey = regKey.addChildRegKey("command");
        commandRegKey.addRegValue(new RegValue(null, REG_SZ, command.getBat().toAbsolutePath().toString()));

        // set the command text
        regKey.addRegValue(new RegValue("MUIVerb", REG_SZ, command.getText()));

        // add this to the parent subcommands list so that the parent can
        // properly reference this command in its SubCommands reg value.
        parentSubCommands.add(regKey);
    }

    private RegValue buildSubCommandsRegValue(Menu menu) {
        List<RegKey> listSubCommands = new ArrayList<>();
        for (ContextMenuItem child : menu.getChildren()) {
            if (child instanceof Menu) {
                // this is a child menu, build the reg values
                buildRegKeysNotRootMenu(((Menu) child), listSubCommands);
            }
            if (child instanceof Command) {
                // this is a child command
                buildRegKeysNotRootCommand((Command) child, listSubCommands);
            }
        }

        // get the registry key name of each of subcommand
        List<String> strListSubCommands = listSubCommands.stream()
            .map(RegKey::getShortKeyName)
            .collect(toList());

        // Windows specification for this value is semicolon-delimited
        String strSubCommands = String.join(";", strListSubCommands);
        return new RegValue("SubCommands", REG_SZ, strSubCommands);
    }

}
