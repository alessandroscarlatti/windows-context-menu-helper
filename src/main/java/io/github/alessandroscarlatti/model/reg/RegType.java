package io.github.alessandroscarlatti.model.reg;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 9/28/2019
 */
public enum RegType {
    // data types corresponding to the Type column in regedit
    REG_DWORD("REG_DWORD"),
    REG_SZ("REG_SZ");

    private String regName;  // the string token for this type

    RegType(String regName) {
        this.regName = regName;
    }

    public String getRegName() {
        return regName;
    }
}
