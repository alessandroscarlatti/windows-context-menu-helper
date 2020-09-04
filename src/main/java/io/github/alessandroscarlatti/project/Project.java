package io.github.alessandroscarlatti.project;

import io.github.alessandroscarlatti.model.reg.MenuRegSpec;
import io.github.alessandroscarlatti.model.menu.ContextMenuItem;
import io.github.alessandroscarlatti.model.reg.RegKey;
import io.github.alessandroscarlatti.util.RegExportUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static io.github.alessandroscarlatti.util.ProjectUtils.executeBat;
import static io.github.alessandroscarlatti.util.ProjectUtils.resourceStr;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 9/28/2019
 */
public class Project {

    private static final Logger log = LoggerFactory.getLogger(Project.class);

    // these are the context menu items parsed from the project dir
    private List<ContextMenuItem> contextMenuItems;

    private Path projectDir;
    private Path syncDir;
    private RegExportUtil regExportUtil;

    // any settings configured by the user
    private String regId;  // the reg id for this project. All items will have this prefix. eg, MyProject.
    private String oldRegId; // the reg id that this project used to have. Only applicable for auto-generated ids.
    private boolean autoGenerateRegId;  // whether or not to auto-generate the project id.
    private String projectName;  // the base name of the project. By default should be the name of the directory containing the project.


    public Project() {
    }

    public Project(List<ContextMenuItem> contextMenuItems) {
        this.contextMenuItems = contextMenuItems;
    }

    // this class will contain methods for syncing, installing, uninstalling, reverting, etc.

    public void buildRegSpecs() {
        for (ContextMenuItem contextMenuItem : contextMenuItems) {
            if (contextMenuItem.getRegSpec() != null)
                contextMenuItem.getRegSpec().buildSpec();
        }
    }

    public void exportRegSpecs(Path syncDir) {
        try {
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
                    installBats.append("reg import \"" + strItemsSyncDir + "\\Install.reg\" & %CHECK_ERROR%\n");

                    // create the uninstall script
                    String uninstallScript = contextMenuItem.getRegSpec().writeUninstallRegScript();
                    log.info("UNINSTALL SCRIPT:");
                    log.info(uninstallScript);
                    Files.write(itemSyncDir.resolve("Uninstall.reg"), uninstallScript.getBytes());
                    uninstallBats.append("reg import \"" + strItemsSyncDir + "\\Uninstall.reg\" & %CHECK_ERROR%\n");

                    // create the restore script
                    String restoreScript = contextMenuItem.getRegSpec().writeRestorePointRegScript();
                    log.info("RESTORE SCRIPT:");
                    log.info(restoreScript);
                    Files.write(itemSyncDir.resolve("Restore.reg"), restoreScript.getBytes());
                    restoreBats.append("reg import \"" + strItemsSyncDir + "\\Restore.reg\" & %CHECK_ERROR%\n");
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
            MenuRegSpec menuRegSpec = new MenuRegSpec(null, this);
            List<RegKey> regKeysToRemove = menuRegSpec.getAllRegKeysByPrefix(getRegId());
            String regUninstall = getRegExportUtil().uninstallToString(regKeysToRemove);
            Files.write(syncDir.resolve("Uninstall.reg"), regUninstall.getBytes());

            // now build the restore.reg
            String regRestore = getRegExportUtil().exportToString(regKeysToRemove);
            Files.write(syncDir.resolve("Restore.reg"), regRestore.getBytes());

            // now write the bats
            Files.write(syncDir.resolve("Uninstall.bat"), resourceStr("/io/github/alessandroscarlatti/Uninstall.template.bat").getBytes());
            Files.write(syncDir.resolve("Restore.bat"), resourceStr("/io/github/alessandroscarlatti/Restore.template.bat").getBytes());
        } catch (Exception e) {
            throw new RuntimeException("Error exporting reg specs.", e);
        }
    }

    public void executeSync() {
        // run the sync task
        Path syncDir = getSyncDir().resolve("Sync_" + fileTimestamp());
        exportRegSpecs(syncDir);
        exportRegSpecs(getSyncDir().resolve("Sync_Last"));

        // execute the Uninstall bat
        executeBat(syncDir.resolve("UninstallAll.bat"));

        // execute the Install bat
        executeBat(syncDir.resolve("InstallAll.bat"));
    }

    public void executeUninstall() {
        Path syncDir = getSyncDir().resolve("Uninstall_" + fileTimestamp());
        exportRegSpecs(syncDir);

        // execute the Uninstall bat
        executeBat(syncDir.resolve("UninstallAll.bat"));
    }

    public void executeGenerate() {
        // export the reg specs, but don't actually execute the sync
        Path syncDir = getSyncDir().resolve("Sync_" + fileTimestamp());
        exportRegSpecs(syncDir);
        exportRegSpecs(getSyncDir().resolve("Sync_Last"));
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

    public RegExportUtil getRegExportUtil() {
        return regExportUtil;
    }

    public void setRegExportUtil(RegExportUtil regExportUtil) {
        this.regExportUtil = regExportUtil;
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
