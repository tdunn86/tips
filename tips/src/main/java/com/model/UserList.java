package com.model;

import java.util.ArrayList;

/**
 * Manages the collection of all users in the system.
 * Owns all user-related business logic including login and registration.
 * Implemented as a singleton.
 * @author Thomas Dunn, James Gessler
 */
public class UserList {
    private static UserList instance;
    private ArrayList<User> users;

    /**
     * Constructs the UserList by loading all users from the JSON file.
     */
    private UserList() {
        this.users = DataLoader.getUsers();
    }

    /**
     * Returns the single instance of UserList, creating it if needed.
     * @return the UserList instance
     */
    public static UserList getInstance() {
        if (instance == null) instance = new UserList();
        return instance;
    }

    /**
     * Finds a user by their username.
     * The search is case insensitive.
     * @param username the username to search for
     * @return the matching user, or null if not found
     */
    public User getUser(String username) {
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) return user;
        }
        return null;
    }

    /**
     * Finds a user by their unique ID.
     * @param userId the ID to search for
     * @return the matching user, or null if not found
     */
    public User getUserById(int userId) {
        for (User user : users) {
            if (user.getUserId() == userId) return user;
        }
        return null;
    }

    /**
     * Validates the given credentials and returns the user if correct.
     * @param username the username to check
     * @param password the password to check
     * @return the matching user if credentials are valid, null otherwise
     */
    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty()) return null;
        if (password == null || password.trim().isEmpty()) return null;
        User user = getUser(username);
        if (user != null && user.validatePassword(password)) return user;
        return null;
    }

    /**
     * Creates and adds a new user with the given details.
     * Returns null if the username is blank, the password is blank,
     * or the username is already taken.
     * @param username the new user's username
     * @param password the new user's password
     * @param email the new user's email address
     * @param accountType the account type to assign
     * @return the created User, or null if registration failed
     */
    public User registerUser(String username, String password, String email, AccountType accountType) {
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Registration failed: username cannot be blank.");
            return null;
        }
        if (password == null || password.trim().isEmpty()) {
            System.out.println("Registration failed: password cannot be blank.");
            return null;
        }
        if (hasUser(username)) {
            System.out.println("Registration failed: username '" + username + "' already exists.");
            return null;
        }

        int newId = getNextUserId();
        User newUser;
        switch (accountType) {
            case EDITOR: newUser = new Editor(newId, username, password, email); break;
            case ADMIN:  newUser = new Admin(newId, username, password, email);  break;
            default:     newUser = new Student(newId, username, password, email); break;
        }
        users.add(newUser);
        return newUser;
    }

    /**
     * Adds an existing user to the list.
     * Does nothing if the user is null or the username is already taken.
     * @param user the user to add
     */
    public void addUser(User user) {
        if (user != null && !hasUser(user.getUsername())) users.add(user);
    }

    /**
     * Removes a user from the list.
     * @param user the user to remove
     */
    public void removeUser(User user) {
        users.remove(user);
    }

    /**
     * Returns a copy of all users in the list.
     * @return all users
     */
    public ArrayList<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    /**
     * Returns whether a user with the given username exists in the list.
     * @param username the username to check
     * @return true if the username is taken, false otherwise
     */
    public boolean hasUser(String username) {
        return getUser(username) != null;
    }

    /**
     * Returns the next available user ID.
     * Calculated as one more than the highest existing ID.
     * @return the next user ID
     */
    public int getNextUserId() {
        return users.stream().mapToInt(User::getUserId).max().orElse(0) + 1;
    }

    /**
     * Saves all users to the JSON file.
     */
    public void save() {
        DataWriter.saveUsers();
    }
}
