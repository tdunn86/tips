package com.model;

import java.util.ArrayList;
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
                case "1": handleLogin();        break;
                case "2": handleCreateAccount(); break;
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

    // ----------------------------------------------------------------
    // Login
    // ----------------------------------------------------------------
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

    // ----------------------------------------------------------------
    // Logged-in menu
    // ----------------------------------------------------------------
    private static void handleLoggedInMenu() {
    boolean loggedIn = true;
    while (loggedIn) {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. View all questions");
        System.out.println("2. Search questions");

        User current = facade.getCurrentUser();
        if (current instanceof Editor || current instanceof Admin) {
            System.out.println("3. Add a question");
            System.out.println("4. Logout");
        } else {
            System.out.println("3. Logout");
        }

        System.out.print("Choose an option: ");
        String choice = scanner.nextLine().trim();

        boolean isEditorOrAdmin = (current instanceof Editor || current instanceof Admin);

        switch (choice) {
            case "1": handleViewAllQuestions(); break;
            case "2": handleSearchQuestions();  break;
            case "3":
                if (isEditorOrAdmin) {
                    handleAddQuestion();
                } else {
                    facade.logout();
                    System.out.println("Logged out. Data saved.");
                    loggedIn = false;
                }
                break;
            case "4":
                if (isEditorOrAdmin) {
                    facade.logout();
                    System.out.println("Logged out. Data saved.");
                    loggedIn = false;
                }
                break;
            default:
                System.out.println("Invalid option. Try again.");
        }
    }
}

    // ----------------------------------------------------------------
    // Add question (Editors and Admins only)
    // ----------------------------------------------------------------
private static void handleAddQuestion() {
    System.out.println("\n--- Add Question ---");

    System.out.print("Title: ");
    String title = scanner.nextLine().trim();

    System.out.print("Prompt: ");
    String prompt = scanner.nextLine().trim();

    System.out.println("Difficulty (EASY, MEDIUM, HARD): ");
    Difficulty difficulty = Difficulty.valueOf(scanner.nextLine().trim().toUpperCase());

    System.out.println("Language (JAVA, PYTHON, CPP, JAVASCRIPT, HTML, CSS): ");
    Language language = Language.valueOf(scanner.nextLine().trim().toUpperCase());

    System.out.println("Course (CSCE145, CSCE146, CSCE240, CSCE242, CSCE247): ");
    Course course = Course.valueOf(scanner.nextLine().trim().toUpperCase());

    facade.addQuestion(title, prompt, difficulty, language, course);
    DataWriter.saveQuestions();
    System.out.println("Question added successfully!");
}

    // ----------------------------------------------------------------
    // View all questions
    // ----------------------------------------------------------------
    private static void handleViewAllQuestions() {
        ArrayList<Question> questions = facade.getQuestions(null);
        if (questions.isEmpty()) {
            System.out.println("No questions found.");
            return;
        }

        System.out.println("\n--- All Questions ---");
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            System.out.println((i + 1) + ". [" + q.getDifficulty() + "] "
                + q.getTitle() + " (" + q.getCourse() + ")");
        }

        System.out.print("\nEnter question number to view (or 0 to go back): ");
        String input = scanner.nextLine().trim();
        try {
            int index = Integer.parseInt(input) - 1;
            if (index >= 0 && index < questions.size()) {
                handleViewQuestion(questions.get(index));
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    // ----------------------------------------------------------------
    // Search questions
    // ----------------------------------------------------------------
    private static void handleSearchQuestions() {
        System.out.print("\nEnter search keyword: ");
        String keyword = scanner.nextLine().trim();
        ArrayList<Question> results = facade.getQuestions(keyword);

        if (results.isEmpty()) {
            System.out.println("No questions found for '" + keyword + "'.");
            return;
        }

        System.out.println("\n--- Search Results ---");
        for (int i = 0; i < results.size(); i++) {
            Question q = results.get(i);
            System.out.println((i + 1) + ". [" + q.getDifficulty() + "] "
                + q.getTitle() + " (" + q.getCourse() + ")");
        }

        System.out.print("\nEnter question number to view (or 0 to go back): ");
        String input = scanner.nextLine().trim();
        try {
            int index = Integer.parseInt(input) - 1;
            if (index >= 0 && index < results.size()) {
                handleViewQuestion(results.get(index));
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    // ----------------------------------------------------------------
    // View a single question
    // ----------------------------------------------------------------
    private static void handleViewQuestion(Question question) {
        System.out.println("\n============================================");
        System.out.println("Title:      " + question.getTitle());
        System.out.println("Course:     " + question.getCourse());
        System.out.println("Difficulty: " + question.getDifficulty());
        System.out.println("Language:   " + question.getLanguage());
        System.out.println("--------------------------------------------");
        System.out.println("Prompt: " + question.getPrompt());

        if (question.getHint() != null) {
            System.out.println("--------------------------------------------");
            System.out.println("Hint: " + question.getHint());
        }

        // Show solutions if revealed
        if (question.isSolutionRevealed()) {
            System.out.println("--------------------------------------------");
            System.out.println("Sample Solution: " + question.getSampleSolution());
            System.out.println("Explanation:     " + question.getSampleExplanation());
        }

        // Show replies/comments
        if (!question.getReplies().isEmpty()) {
            System.out.println("--------------------------------------------");
            System.out.println("Comments:");
            for (Reply r : question.getReplies()) {
                printReply(r, 1);
            }
        }

        System.out.println("============================================");
        System.out.println("\n1. Reveal solution");
        System.out.println("2. Add a comment");
        System.out.println("3. Go back");
        System.out.print("Choose an option: ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                question.revealSolution();
                System.out.println("Solution: " + question.getSampleSolution());
                System.out.println("Explanation: " + question.getSampleExplanation());
                break;
            case "2":
                handleAddComment(question);
                break;
            case "3":
            default:
                break;
        }
    }

    // ----------------------------------------------------------------
    // Add a comment to a question
    // ----------------------------------------------------------------
    private static void handleAddComment(Question question) {
        System.out.print("Comment title: ");
        String title = scanner.nextLine().trim();
        System.out.print("Comment: ");
        String content = scanner.nextLine().trim();
        facade.addComment(question, title, content);
        System.out.println("Comment added!");
    }

    // ----------------------------------------------------------------
    // Print reply recursively (handles nested comments)
    // ----------------------------------------------------------------
    private static void printReply(Reply reply, int depth) {
        String indent = "  ".repeat(depth);
        System.out.println(indent + "[" + reply.getAuthor().getUsername() + "] "
            + reply.getTitle());
        System.out.println(indent + reply.getContent());
        for (Reply nested : reply.getReplies()) {
            printReply(nested, depth + 1);
        }
    }

    // ----------------------------------------------------------------
    // Create Account
    // ----------------------------------------------------------------
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
            System.out.println("Account created! Welcome, " + newUser.getUsername());
            facade.registrationSuccess(newUser);
            facade.login(username, password);
            handleLoggedInMenu();
        } else {
            System.out.println("Registration failed: username '" + username + "' already exists.");
        }
    }
}