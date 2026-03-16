package com.model;

/**
 * Represents an Account Type of a User
 * @author Samuel Britton
 */
public enum AccountType {
    STUDENT,
    EDITOR,
    ADMIN;

    public String toDisplayString() {
        String name = this.name();
        return name.charAt(0) + name.substring(1).toLowerCase();
    }
}
