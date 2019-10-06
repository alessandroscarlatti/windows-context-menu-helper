package io.github.alessandroscarlatti.project;

import io.github.alessandroscarlatti.menu.MenuRegSpec;
import io.github.alessandroscarlatti.windows.menu.ContextMenuItem;
import io.github.alessandroscarlatti.windows.reg.RegKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static io.github.alessandroscarlatti.WindowsContextMenuHelper.executeBat;
import static io.github.alessandroscarlatti.WindowsContextMenuHelper.resourceStr;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 9/28/2019
 */
public class Project {

    private static final Logger log = LoggerFactory.getLogger(Project.class);

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

    public Path exportRegSpecs(String prefix) {
        try {
            Path syncDir = context.getSyncDir().resolve(prefix + fileTimestamp());

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
                    log.info("INSTALL SCRIPT:");
                    log.info(installScript);
                    Files.write(itemSyncDir.resolve("Install.reg"), installScript.getBytes());
                    installBats.append("regedit /s \"" + strItemsSyncDir + "\\Install.reg\" & %CHECK_ERROR%\n");

                    // create the uninstall script
                    String uninstallScript = contextMenuItem.getRegSpec().writeUninstallRegScript();
                    log.info("UNINSTALL SCRIPT:");
                    log.info(uninstallScript);
                    Files.write(itemSyncDir.resolve("Uninstall.reg"), uninstallScript.getBytes());
                    uninstallBats.append("regedit /s \"" + strItemsSyncDir + "\\Uninstall.reg\" & %CHECK_ERROR%\n");

                    // create the restore script
                    String restoreScript = contextMenuItem.getRegSpec().writeRestorePointRegScript();
                    log.info("RESTORE SCRIPT:");
                    log.info(restoreScript);
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

            Files.createDirectories(syncDir);
            Files.write(syncDir.resolve("InstallAll.bat"), installBat.getBytes());
            Files.write(syncDir.resolve("UninstallAll.bat"), uninstallBat.getBytes());
            Files.write(syncDir.resolve("RestoreAll.bat"), restoreBat.getBytes());

            // now build the uninstall.reg
            MenuRegSpec menuRegSpec = new MenuRegSpec(null, context);
            List<RegKey> regKeysToRemove = menuRegSpec.getAllRegKeysByPrefix(context.getProjectConfig().getRegId());
            String regUninstall = context.getRegExportUtil().uninstallToString(regKeysToRemove);
            Files.write(syncDir.resolve("Uninstall.reg"), regUninstall.getBytes());

            // now build the restore.reg
            String regRestore = context.getRegExportUtil().exportToString(regKeysToRemove);
            Files.write(syncDir.resolve("Restore.reg"), regRestore.getBytes());

            // now write the bats
            Files.write(syncDir.resolve("Uninstall.bat"), resourceStr("/io/github/alessandroscarlatti/Uninstall.template.bat").getBytes());
            Files.write(syncDir.resolve("Restore.bat"), resourceStr("/io/github/alessandroscarlatti/Restore.template.bat").getBytes());

            return syncDir;
        } catch (Exception e) {
            throw new RuntimeException("Error exporting reg specs.", e);
        }
    }

    public void executeSync() {
        // run the sync task
        Path syncDir = exportRegSpecs("Sync_");

        // execute the Uninstall bat
        executeBat(syncDir.resolve("Uninstall.bat"));

        // execute the Install bat
        executeBat(syncDir.resolve("InstallAll.bat"));
    }

    public void executeUninstall() {
        Path syncDir = exportRegSpecs("Uninstall_");

        // execute the Uninstall bat
        executeBat(syncDir.resolve("Uninstall.bat"));
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
