package com.model;

/**
 * Represents a generic user within the system
 * @author Thomas Dunn, James Gessler
 */
public abstract class User {
    private int userId;
    private String username;
    private String password;
    private String email;
    private accountType accountType;

    /**
     * Initialize a user with their ID, credentials, and account classification
     * @param userId User ID to initialize
     * @param username Username to initialize
     * @param password Password to initialize
     * @param email Email to initiliaze
     * @param accountType Type of account
     */
    public User(int userId, String username, String password, String email, 
        accountType accountType) {

    }

    /**
     * Facilitates the account creation process
     * @return The newly created user object
     */
    public User createAccount() {
        
    }

    /**
     * Authenticates the user credentials
     * @return A session object if the authentication is successful
     */
    public Session login() {
        return null;
    }

    /**
     * Clears the current session and logs the user out
     */
    public void logout() {

    }


}