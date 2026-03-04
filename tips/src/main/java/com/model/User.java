package com.model;

/**
 * Abstract base class representing a system user.
 */
public abstract class User {

    private int userId;
    private String username;
    private String password;
    private String email;
    private AccountType accountType;

    /** Constructs a new User with the specified attributes.
     * @param userId Unique identifier for the user
     * @param username The user's login name
     * @param password The user's password (should be hashed in production)
     * @param email The user's email address
     * @param accountType The type of account (e.g., STUDENT, ADMIN)
     */
    public User(int userId, String username, String password, String email, AccountType accountType) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.accountType = accountType;
    }

    /**
     * Returns the unique user ID.
     * @return userId
     */
    public int getUserId() { 
        return userId;
    }

    /** 
     * Returns the user's login name.
     * @return username
     */
    public String getUsername() { 
        return username; 
    }

    /** 
     * Returns the user's email address.
     * @return email
     */
    public String getEmail() { 
        return email; 
    }

    /** 
     * Returns the user's account type.
     * @return account type
     */
    public AccountType getAccountType() { 
        return accountType; 
    }

    public void setPassword(String password) { this.password = password; }
    public void setEmail(String email) { this.email = email; }
    public void setAccountType(AccountType accountType) { this.accountType = accountType; }

    /**
     * Registers this user in the system.
     * @return this user
     */
    public User createAccount() {
        UserList.getInstance().addUser(this);
        return this;
    }

    /**
     * Validates a password.
     * @return true if it matches
     */
    public boolean validatePassword(String input) {
        if (password == null) return input == null;
        return password.equals(input);
    }

    /**
     * Logs the user in.
     * @return Session if authentication succeeds, otherwise null
     */
    public Session login(String inputPassword) {
        if (!validatePassword(inputPassword)) {
            return null;
        }
        return new Session(this); // Requires Session class
    }

    /** Logs the user out. */
    public void logout() {
        // Add session cleanup logic if needed
    }

    public String toString() {
        return "User{id=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", accountType=" + accountType + '}';
    }
}