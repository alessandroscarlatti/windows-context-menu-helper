package io.github.alessandroscarlatti.reg;

/**
 * @author Alessandro Scarlatti
 * @since Tuesday, 10/1/2019
 */
public interface RegSpec {

    String getInstallRegScript();

    String getUninstallRegScript();

    String getRestorePointRegScript();
}
