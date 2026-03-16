package com.model;

import java.util.Scanner;

/**
 * Console UI for the TIPS system.
 * @author Thomas Dunn, James Gessler
 */
public class TIPSUI {

    private static TIPSFacade facade = TIPSFacade.getInstance();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("============================================");
        System.out.println("       Welcome to the TIPS System");
        System.out.println("============================================");

        boolean running = true;
        while (running) {
            System.out.println("\n1. Login");
            System.out.println("2. Create Account");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleLogin();
                    break;
                case "2":
                    handleCreateAccount();
                    break;
                case "3":
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }

        scanner.close();
    }

    private static void handleLogin() {
        System.out.println("\n--- Login ---");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        boolean success = facade.login(username, password);

        if (success) {
            System.out.println("Login successful! Welcome, "
                + facade.getCurrentUser().getUsername()
                + " (" + facade.getCurrentUser().getAccountType() + ")");
            handleLoggedInMenu();
        } else {
            System.out.println("Login failed. Invalid username or password.");
        }
    }

    private static void handleLoggedInMenu() {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n1. Logout");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    facade.logout();
                    System.out.println("Logged out successfully. Data saved.");
                    loggedIn = false;
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void handleCreateAccount() {
        System.out.println("\n--- Create Account ---");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println("Registration failed: username cannot be blank.");
            return;
        }

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        User newUser = facade.registerUser(username, password, email, AccountType.STUDENT);

        if (newUser != null) {
            System.out.println("Account created successfully! Welcome, " + newUser.getUsername());
            facade.registrationSuccess(newUser);
            // Log them in automatically after registering
            facade.login(username, password);
            handleLoggedInMenu();
        } else {
            System.out.println("Registration failed: username '" + username + "' already exists.");
        }
    }
}