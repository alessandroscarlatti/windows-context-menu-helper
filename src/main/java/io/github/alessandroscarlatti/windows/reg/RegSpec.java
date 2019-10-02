package io.github.alessandroscarlatti.windows.reg;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Alessandro Scarlatti
 * @since Tuesday, 10/1/2019
 */
public interface RegSpec {

    void buildSpec();

    void exportSpec(OutputStream os) throws IOException;
}
