package com.model;

/**
 * Represents an Account Type of a User
 * @author Samuel Britton
 */
public enum AccountType {
    STUDENT,
    EDITOR,
    ADMIN;

    /**
     * Returns the title-case version of the account type name.
     * Used when writing to JSON so the value matches what DataLoader expects.
     * @return the account type as a title-case string
     */
    public String toDisplayString() {
        String name = this.name();
        return name.charAt(0) + name.substring(1).toLowerCase();
    }
}