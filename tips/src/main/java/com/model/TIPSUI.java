package com.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Provides the console interface for the TIPS system.
 * Reads user input, calls the facade, and prints results.
 * Contains no business logic of its own.
 * @author Thomas Dunn, James Gessler
 */
public class TIPSUI {

    private static final TIPSFacade facade  = TIPSFacade.getInstance();
    private static final Scanner    scanner = new Scanner(System.in);

    /**
     * Launches the TIPS system and displays the main menu.
     * @param args command line arguments (unused)
     */
    public static void main(String[] args) {
        System.out.println("============================================");
        System.out.println("       Welcome to the TIPS System");
        System.out.println("============================================");

        boolean running = true;
        while (running) {
            System.out.println("\n1. Login\n2. Create Account\n3. Exit");
            System.out.print("Choose an option: ");
            switch (scanner.nextLine().trim()) {
                case "1": handleLogin();         break;
                case "2": handleCreateAccount(); break;
                case "3": running = false; System.out.println("Goodbye!"); break;
                default:  System.out.println("Invalid option. Try again.");
            }
        }
        scanner.close();
    }

    /**
     * Prompts the user for their credentials and attempts to log in.
     * Navigates to the main menu on success.
     */
    private static void handleLogin() {
        System.out.println("\n--- Login ---");
        System.out.print("Username: "); String username = scanner.nextLine().trim();
        System.out.print("Password: "); String password = scanner.nextLine().trim();

        if (facade.login(username, password)) {
            User u = facade.getCurrentUser();
            System.out.println("Welcome, " + u.getUsername() + " (" + u.getAccountType() + ")");
            handleLoggedInMenu();
        } else {
            System.out.println("Login failed. Invalid username or password.");
        }
    }

    /**
     * Prompts the user to create a new account.
     * Automatically logs in and navigates to the main menu on success.
     */
    private static void handleCreateAccount() {
        System.out.println("\n--- Create Account ---");
        System.out.print("Username: "); String username = scanner.nextLine().trim();
        if (username.isEmpty()) { System.out.println("Username cannot be blank."); return; }
        System.out.print("Password: "); String password = scanner.nextLine().trim();
        System.out.print("Email: ");    String email    = scanner.nextLine().trim();

        User newUser = facade.registerUser(username, password, email, AccountType.STUDENT);
        if (newUser != null) {
            System.out.println("Account created! Welcome, " + newUser.getUsername());
            facade.login(username, password);
            handleLoggedInMenu();
        } else {
            System.out.println("Registration failed: username '" + username + "' already exists.");
        }
    }

    /**
     * Displays the main menu for a logged-in user.
     * Shows additional options for Editors and Admins.
     */
    private static void handleLoggedInMenu() {
        boolean loggedIn = true;
        while (loggedIn) {
            boolean isPrivileged = facade.getCurrentUser() instanceof Editor
                                || facade.getCurrentUser() instanceof Admin;

            System.out.println("\n--- Main Menu ---");
            System.out.println("1. View all questions");
            System.out.println("2. Search questions");
            if (isPrivileged) { System.out.println("3. Add a question"); System.out.println("4. Logout"); }
            else              { System.out.println("3. Logout"); }

            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1": handleViewAllQuestions(); break;
                case "2": handleSearchQuestions();  break;
                case "3":
                    if (isPrivileged) handleAddQuestion();
                    else { facade.logout(); System.out.println("Logged out."); loggedIn = false; }
                    break;
                case "4":
                    if (isPrivileged) { facade.logout(); System.out.println("Logged out."); loggedIn = false; }
                    else System.out.println("Invalid option.");
                    break;
                default: System.out.println("Invalid option.");
            }
        }
    }

    /**
     * Displays all questions and lets the user select one to view.
     */
    private static void handleViewAllQuestions() {
        ArrayList<Question> questions = facade.getQuestions(null);
        if (questions.isEmpty()) { System.out.println("No questions found."); return; }
        printQuestionList(questions);
        pickAndViewQuestion(questions);
    }

    /**
     * Prompts the user for a search keyword and displays matching questions.
     */
    private static void handleSearchQuestions() {
        System.out.print("\nSearch keyword: ");
        ArrayList<Question> results = facade.getQuestions(scanner.nextLine().trim());
        if (results.isEmpty()) { System.out.println("No results found."); return; }
        printQuestionList(results);
        pickAndViewQuestion(results);
    }

    /**
     * Prints a numbered list of questions with their difficulty and course.
     * @param questions the list of questions to print
     */
    private static void printQuestionList(ArrayList<Question> questions) {
        System.out.println("\n--- Questions ---");
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            System.out.printf("%d. [%s] %s (%s)%n", i + 1, q.getDifficulty(), q.getTitle(), q.getCourse());
        }
    }

    /**
     * Prompts the user to select a question from the list by number.
     * @param questions the list to select from
     */
    private static void pickAndViewQuestion(ArrayList<Question> questions) {
        System.out.print("\nEnter number to view (0 to go back): ");
        try {
            int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (index >= 0 && index < questions.size()) handleViewQuestion(questions.get(index));
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    /**
     * Displays a single question and presents the user with action options.
     * @param question the question to display
     */
    private static void handleViewQuestion(Question question) {
        printQuestionHeader(question);

        System.out.println("\n1. Write and submit code");
        System.out.println("2. View sample solution");
        System.out.println("3. Add a comment");
        System.out.println("4. Reply to a comment");
        System.out.println("5. Remove a comment");
        System.out.println("6. Go back");
        System.out.print("Choose an option: ");

        switch (scanner.nextLine().trim()) {
            case "1": handleWriteCode(question);          break;
            case "2": handleViewSampleSolution(question); break;
            case "3": handleAddComment(question);         break;
            case "4": handleReplyToComment(question);     break;
            case "5": handleRemoveComment(question);      break;
            default: break;
        }
    }

    /**
     * Prints the full header for a question including prompt, hint,
     * submission count, sample solution if revealed, and any comments.
     * @param question the question to print
     */
    private static void printQuestionHeader(Question question) {
        System.out.println("\n============================================");
        System.out.println("Title:      " + question.getTitle());
        System.out.println("Course:     " + question.getCourse());
        System.out.println("Difficulty: " + question.getDifficulty());
        System.out.println("Language:   " + question.getLanguage());
        if (question.getAuthor() != null)
            System.out.println("Author:     " + question.getAuthor().getUsername());
        System.out.println("--------------------------------------------");
        System.out.println(question.getPrompt());
        if (question.getHint() != null && !question.getHint().isEmpty())
            System.out.println("\nHint: " + question.getHint());
        if (!question.getSolutions().isEmpty()) {
            System.out.println("--------------------------------------------");
            System.out.println("Submissions: " + question.getSolutions().size());
        }
        if (question.isSolutionRevealed()) {
            System.out.println("--------------------------------------------");
            System.out.println("SAMPLE SOLUTION:");
            printNumberedCode(question.getSampleSolution());
            System.out.println("Explanation: " + question.getSampleExplanation());
        }
        if (!question.getReplies().isEmpty()) {
            System.out.println("--------------------------------------------");
            System.out.println("Comments:");
            question.getReplies().forEach(r -> printReply(r, 1));
        }
        System.out.println("============================================");
    }

    /**
     * Opens a multi-line code editor in the terminal.
     * The user types code line by line and uses commands to submit, clear, or cancel.
     * After submitting, offers to show the sample solution for comparison.
     * @param question the question being answered
     */
    private static void handleWriteCode(Question question) {
        System.out.println("\n--- Code Editor [" + question.getLanguage() + "] ---");
        System.out.println(":submit = submit  |  :clear = clear  |  :quit = cancel");
        System.out.println("--------------------------------------------");

        StringBuilder code = new StringBuilder();
        int line = 1;
        while (true) {
            System.out.printf("%3d | ", line);
            String input = scanner.nextLine();
            if (input.equals(":quit"))   { System.out.println("Cancelled."); return; }
            if (input.equals(":clear"))  { code = new StringBuilder(); line = 1; System.out.println("-- cleared --"); continue; }
            if (input.equals(":submit")) {
                if (code.length() == 0) { System.out.println("Nothing to submit."); continue; }
                break;
            }
            code.append(input).append("\n");
            line++;
        }

        facade.submitSolution(question, code.toString());
        System.out.println("\nSubmitted! Your code:");
        printNumberedCode(code.toString());

        System.out.print("\nCompare with sample solution? (yes/no): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("yes")) {
            facade.revealSolution(question);
            System.out.println("\n--- SAMPLE SOLUTION ---");
            printNumberedCode(question.getSampleSolution());
            System.out.println("\nExplanation: " + question.getSampleExplanation());
        }
    }

    /**
     * Reveals and prints the sample solution for a question.
     * @param question the question whose solution to display
     */
    private static void handleViewSampleSolution(Question question) {
        if (question.getSampleSolution() == null) { System.out.println("No sample solution available."); return; }
        facade.revealSolution(question);
        System.out.println("\n--- SAMPLE SOLUTION ---");
        printNumberedCode(question.getSampleSolution());
        System.out.println("\nExplanation: " + question.getSampleExplanation());
    }

    /**
     * Prompts the user to write and submit a comment on a question.
     * @param question the question to comment on
     */
    private static void handleAddComment(Question question) {
        System.out.print("Title: ");   String title   = scanner.nextLine().trim();
        System.out.print("Comment: "); String content = scanner.nextLine().trim();
        if (content.isEmpty()) { System.out.println("Comment cannot be empty."); return; }
        facade.addComment(question, title, content);
        System.out.println("Comment added!");
    }

    /**
     * Lets the user select an existing comment and post a nested reply to it.
     * @param question the question whose comments to reply to
     */
    private static void handleReplyToComment(Question question) {
        List<Reply> replies = question.getReplies();
        if (replies.isEmpty()) { System.out.println("No comments to reply to."); return; }
        printRepliesNumbered(replies);
        System.out.print("Select comment number (0 to cancel): ");
        try {
            int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (index < 0 || index >= replies.size()) return;
            System.out.print("Title: ");   String title   = scanner.nextLine().trim();
            System.out.print("Reply: ");   String content = scanner.nextLine().trim();
            if (content.isEmpty()) { System.out.println("Reply cannot be empty."); return; }
            facade.addNestedReply(replies.get(index), question, title, content);
            System.out.println("Reply added!");
        } catch (NumberFormatException e) { System.out.println("Invalid input."); }
    }

    /**
     * Lets the user select and remove a comment from a question.
     * Only the comment's author or an Admin may remove it.
     * @param question the question whose comments to remove from
     */
    private static void handleRemoveComment(Question question) {
        List<Reply> replies = question.getReplies();
        if (replies.isEmpty()) { System.out.println("No comments to remove."); return; }
        printRepliesNumbered(replies);
        System.out.print("Select comment number (0 to cancel): ");
        try {
            int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (index < 0 || index >= replies.size()) return;
            boolean removed = facade.removeComment(question, replies.get(index));
            System.out.println(removed ? "Comment removed." : "Not authorized to remove that comment.");
        } catch (NumberFormatException e) { System.out.println("Invalid input."); }
    }

    /**
     * Prompts the user to fill in the details for a new question.
     * Only available to Editors and Admins.
     */
    private static void handleAddQuestion() {
        System.out.println("\n--- Add Question ---");
        System.out.print("Title: ");   String title  = scanner.nextLine().trim();
        System.out.print("Prompt: ");  String prompt = scanner.nextLine().trim();

        Difficulty difficulty = promptEnum(Difficulty.class, "Difficulty (EASY, MEDIUM, HARD): ");
        if (difficulty == null) return;
        Language language = promptEnum(Language.class, "Language (JAVA, PYTHON, CPP, JAVASCRIPT, HTML, CSS): ");
        if (language == null) return;
        Course course = promptEnum(Course.class, "Course (CSCE145, CSCE146, CSCE240, CSCE242, CSCE247): ");
        if (course == null) return;

        facade.addQuestion(title, prompt, difficulty, language, course);
        System.out.println("Question added!");
    }

    /**
     * Prints a numbered list of replies showing the author and title of each.
     * @param replies the list of replies to print
     */
    private static void printRepliesNumbered(List<Reply> replies) {
        for (int i = 0; i < replies.size(); i++) {
            Reply r = replies.get(i);
            System.out.printf("%d. [%s] %s%n", i + 1, r.getAuthor().getUsername(), r.getTitle());
        }
    }

    /**
     * Prints a reply and any nested replies recursively.
     * Indents nested replies based on their depth.
     * @param reply the reply to print
     * @param depth the current indentation depth
     */
    private static void printReply(Reply reply, int depth) {
        String indent = "  ".repeat(depth);
        System.out.printf("%s[%s] %s (%d upvotes)%n", indent, reply.getAuthor().getUsername(),
                reply.getTitle(), reply.getUpvotes());
        System.out.println(indent + reply.getContent());
        reply.getReplies().forEach(nested -> printReply(nested, depth + 1));
    }

    /**
     * Prints a block of code with line numbers.
     * @param code the code string to print
     */
    private static void printNumberedCode(String code) {
        if (code == null || code.isEmpty()) { System.out.println("  (empty)"); return; }
        String[] lines = code.split("\n");
        for (int i = 0; i < lines.length; i++)
            System.out.printf("%3d | %s%n", i + 1, lines[i]);
    }

    /**
     * Prompts the user to enter a value for the given enum type.
     * Returns null and prints a message if the input is not a valid enum constant.
     * @param enumClass the enum class to parse
     * @param prompt the message to display to the user
     * @return the matching enum constant, or null if the input was invalid
     */
    private static <T extends Enum<T>> T promptEnum(Class<T> enumClass, String prompt) {
        System.out.print(prompt);
        try {
            return Enum.valueOf(enumClass, scanner.nextLine().trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid value. Operation cancelled.");
            return null;
        }
    }
}
