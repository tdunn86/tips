package com.model;

import java.util.ArrayList;

/**
 * A singleton class that manages the collection of all users
 * @author Thomas Dunn, James Gessler
 */
public abstract class UserList {
    private ArrayList users;

    private UserList() {

    }

    /**
     * Provides access to the singelton instance
     * @return The single instance of the UserList
     */
    public UserList getInstance() {
        return new UserList();
    }

    /**
     * Finds a user by their unique username
     * @param userName The username string to search for
     * @return The matching User object
     */
    public User getUser(String userName) {
        return new User(1, "TestUser", "password", "test@gmail.com", AccountType.STUDENT);
    }

    /**
     * Appends a new user to the list
     * @param user The User object to add
     */
    public void addUser(User user) {

    }

    /**
     * Removes an existing user to from the list
     * @param user The User object to remove
     */
    public void removeUser(User user) {

    }

    /**
     * Retrieves the full collection of users
     * @return A list containing all system users
     */
    public List<User> getAllUsers() {
        ArrayList<User> stubUsers = new ArrayList<>();
        stubUsers.add(new User(1, "Steve", "password1", "steve@gmail.com", AccountType.ADMIN));
        stubUsers.add(new User(2, "Sarah", "passwrod2", "sarah@gmail.com", AccountType.SUDENT));
        return stubUsers;
    }

    public void save() {

    }


}