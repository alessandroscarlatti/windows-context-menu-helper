package io.github.alessandroscarlatti.windows.reg;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 9/28/2019
 */
public class RegValue {
    private String name;  // the Name column in regedit
    private RegType regType;  // the Type column in regedit
    private String data;  // the Data column in regedit

    public RegValue() {
    }

    public RegValue(String name, RegType regType, String data) {
        this.name = name;
        this.regType = regType;
        this.data = data;
    }

    @Override
    public String toString() {
        return "RegValue{" +
            "name='" + name + '\'' +
            ", regType=" + regType +
            ", data='" + data + '\'' +
            '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RegType getRegType() {
        return regType;
    }

    public void setRegType(RegType regType) {
        this.regType = regType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
