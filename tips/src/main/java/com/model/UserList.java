package com.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A singleton class that manages the collection of all users
 * @author Thomas Dunn, James Gessler
 */
public abstract class UserList {
    private ArrayList<User> users;;
    private static UserList userList;

    /**
     * Private constructor to prevent direct instantiation
     */
    private UserList() {
        this.users = DataLoader.getUser();
    }

    /**
     * Provides access to the singelton instance
     * @return The single instance of the UserList
     */
    public static UserList getInstance() {
        if (userList == null) userList = new UserList();
        return userList;
    }

    /**
     * Finds a user by their unique username
     * @param userName The username string to search for
     * @return The matching User object
     */
    public User getUser(String userName) {
        for (User user : users) {
            if (user.getUsername().equals(userName)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Appends a new user to the list
     * @param user The User object to add
     */
    public void addUser(User user) {
        users.add(user);
    }

    /**
     * Removes an existing user to from the list
     * @param user The User object to remove
     */
    public void removeUser(User user) {
        users.remove(user);
    }

    /**
     * Retrieves the full collection of users
     * @return A list containing all system users
     */
    public List<User> getAllUsers() {
        return users;
    }

    /**
     * Saves the current list of users
     */
    public void save() {
        DataWriter.saveUser();
    }


}
