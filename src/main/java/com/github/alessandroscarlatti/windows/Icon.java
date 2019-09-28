package com.github.alessandroscarlatti.windows;

import java.nio.file.Path;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 9/28/2019
 */
public class Icon {
    private Path file; // the file containing the icon resource.  May be an .ico or an .exe, eg.
    private Integer resourceIndex; // the resourceIndex of the imange within the resource file.  May be null in the case of an .ico.

    public Icon() {
    }

    public Icon(Path file) {
        this.file = file;
    }

    public Icon(Path file, Integer resourceIndex) {
        this.file = file;
        this.resourceIndex = resourceIndex;
    }

    @Override
    public String toString() {
        return "Icon{" +
            "file=" + file +
            ", resourceIndex=" + resourceIndex +
            '}';
    }

    public Path getFile() {
        return file;
    }

    public void setFile(Path file) {
        this.file = file;
    }

    public Integer getResourceIndex() {
        return resourceIndex;
    }

    public void setResourceIndex(Integer resourceIndex) {
        this.resourceIndex = resourceIndex;
    }
}
