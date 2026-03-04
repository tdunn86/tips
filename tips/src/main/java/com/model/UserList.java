package com.model;

import java.util.ArrayList;

/**
 * A singleton class that manages the collection of all users
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

    public User getUser(String userName) {
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(userName)) return user;
        }
        return null;
    }

    public User getUserById(int userId) {
        for (User user : users) {
            if (user.getUserId() == userId) return user;
        }
        return null;
    }

    public void addUser(User user) {
        if (user != null && getUser(user.getUsername()) == null) users.add(user);
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
