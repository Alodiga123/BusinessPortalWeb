package com.alodiga.businessportal.enumeration;

/**
 *
 * @author henry
 */
public enum BusinessType {
    NATURAL("naturalPerson"),
    LEGAL("legalPerson");

    private String label;

    private BusinessType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
