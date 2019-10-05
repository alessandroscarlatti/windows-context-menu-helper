package io.github.alessandroscarlatti.project;

import io.github.alessandroscarlatti.windows.menu.ContextMenuItem;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static io.github.alessandroscarlatti.WindowsContextMenuHelper.resourceStr;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 9/28/2019
 */
public class Project {

    // these are the context menu items parsed from the project dir
    private List<ContextMenuItem> contextMenuItems;

    private ProjectContext context;

    public Project(ProjectContext context, List<ContextMenuItem> contextMenuItems) {
        this.contextMenuItems = contextMenuItems;
        this.context = context;
    }

    // this class will contain methods for syncing, installing, uninstalling, reverting, etc.

    public void buildRegSpecs() {
        for (ContextMenuItem contextMenuItem : contextMenuItems) {
            if (contextMenuItem.getRegSpec() != null)
                contextMenuItem.getRegSpec().buildSpec();
        }
    }

    public void exportRegSpecs() {
        try {
            Path syncDir = context.getSyncDir().resolve("Sync_" + fileTimestamp());

            // build the install, uninstall, and restore bats
            StringBuilder installBats = new StringBuilder();
            StringBuilder uninstallBats = new StringBuilder();
            StringBuilder restoreBats = new StringBuilder();

            for (ContextMenuItem contextMenuItem : contextMenuItems) {
                if (contextMenuItem.getRegSpec() != null) {

                    // create the sync dir for this item
                    Path itemSyncDir = syncDir.resolve("Sync_" + contextMenuItem.getName());
                    String strItemsSyncDir = itemSyncDir.getFileName().toString();
                    Files.createDirectories(itemSyncDir);

                    // create the install script
                    String installScript = contextMenuItem.getRegSpec().writeInstallRegScript();
                    System.out.println("INSTALL SCRIPT:");
                    System.out.println(installScript);
                    Files.write(itemSyncDir.resolve("Install.reg"), installScript.getBytes());
                    installBats.append("regedit /s \"" + strItemsSyncDir + "\\Install.reg\" & %CHECK_ERROR%\n");

                    // create the uninstall script
                    String uninstallScript = contextMenuItem.getRegSpec().writeUninstallRegScript();
                    System.out.println("UNINSTALL SCRIPT:");
                    System.out.println(uninstallScript);
                    Files.write(itemSyncDir.resolve("Uninstall.reg"), uninstallScript.getBytes());
                    uninstallBats.append("regedit /s \"" + strItemsSyncDir + "\\Uninstall.reg\" & %CHECK_ERROR%\n");

                    // create the restore script
                    String restoreScript = contextMenuItem.getRegSpec().writeRestorePointRegScript();
                    System.out.println("RESTORE SCRIPT:");
                    System.out.println(restoreScript);
                    Files.write(itemSyncDir.resolve("Restore.reg"), restoreScript.getBytes());
                    restoreBats.append("regedit /s \"" + strItemsSyncDir + "\\Restore.reg\" & %CHECK_ERROR%\n");
                }
            }

            // write the bats

            String installBat = resourceStr("/io/github/alessandroscarlatti/InstallAll.template.bat")
                .replace("${INSTALL_BATS}", installBats);
            String uninstallBat = resourceStr("/io/github/alessandroscarlatti/UninstallAll.template.bat")
                .replace("${UNINSTALL_BATS}", uninstallBats);
            String restoreBat = resourceStr("/io/github/alessandroscarlatti/RestoreAll.template.bat")
                .replace("${RESTORE_BATS}", restoreBats);

            Files.write(syncDir.resolve("InstallAll.bat"), installBat.getBytes());
            Files.write(syncDir.resolve("UninstallAll.bat"), uninstallBat.getBytes());
            Files.write(syncDir.resolve("RestoreAll.bat"), restoreBat.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("Error exporting reg specs.", e);
        }
    }

    public static String fileTimestamp() {
        return DateTimeFormatter.ofPattern("YYYY-MM-dd_HH-mm-ss.SSS").format(LocalDateTime.now());
    }

    public List<ContextMenuItem> getContextMenuItems() {
        return contextMenuItems;
    }

    public void setContextMenuItems(List<ContextMenuItem> contextMenuItems) {
        this.contextMenuItems = contextMenuItems;
    }
}
