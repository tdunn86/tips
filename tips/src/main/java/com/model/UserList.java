package com.model;

import java.util.ArrayList;

/**
 * A singleton class that manages the collection of all users.
 * Owns all user-related business logic.
 * @author Thomas Dunn, James Gessler
 */
public class UserList {
    private static UserList instance;
    private ArrayList<User> users;

    private UserList() {
        this.users = DataLoader.getUsers();
    }

    public static UserList getInstance() {
        if (instance == null) instance = new UserList();
        return instance;
    }

    /**
     * Finds a user by username (case insensitive).
     */
    public User getUser(String username) {
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) return user;
        }
        return null;
    }

    /**
     * Finds a user by their ID.
     */
    public User getUserById(int userId) {
        for (User user : users) {
            if (user.getUserId() == userId) return user;
        }
        return null;
    }

    /**
     * Validates credentials and returns the user if correct, null otherwise.
     * Login logic lives here, not in the Facade.
     */
    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty()) return null;
        if (password == null || password.trim().isEmpty()) return null;
        User user = getUser(username);
        if (user != null && user.validatePassword(password)) return user;
        return null;
    }

    /**
     * Creates and adds a new user. Returns the new user or null if invalid.
     * Registration logic lives here, not in the Facade.
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

    public void addUser(User user) {
        if (user != null && !hasUser(user.getUsername())) users.add(user);
    }

    public void removeUser(User user) {
        users.remove(user);
    }

    public ArrayList<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public boolean hasUser(String username) {
        return getUser(username) != null;
    }

    public int getNextUserId() {
        return users.stream().mapToInt(User::getUserId).max().orElse(0) + 1;
    }

    public void save() {
        DataWriter.saveUsers();
    }
}