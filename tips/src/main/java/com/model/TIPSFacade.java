package com.model;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Facade class that serves as the main entry point for the TIPS system.
 * Manages user authentication, questions, solutions, and comments.
 * @author Thomas Dunn, James Gessler
 */
public class TIPSFacade {
    private static TIPSFacade instance;

    /** The list of all users in the system. */
    private UserList userList;
    /** The list of all questions in the system. */
    private QuestionList questionList;
    /** The currently logged in user. */
    private User currentUser;
    /** The account type of the current user. */
    private AccountType accountType;

    private TIPSFacade() {
        this.userList = UserList.getInstance();
        this.questionList = QuestionList.getInstance();
    }

    public static TIPSFacade getInstance() {
        if (instance == null) instance = new TIPSFacade();
        return instance;
    }

    public User getCurrentUser() { return currentUser; }

    /**
     * Authenticates a user with their username and password.
     * @param username the username of the user
     * @param password the password of the user
     * @return true if login successful, false otherwise
     */
    public boolean login(String username, String password) {
        User user = userList.getUser(username);
        if (user != null && user.validatePassword(password)) {
            this.currentUser = user;
            if (user instanceof Student) ((Student) user).startStreak();
            return true;
        }
        return false;
    }

    /**
     * Logs out the current user and clears the session.
     */
    public void logout() {
        this.currentUser = null;
    }

    /**
     * Registers a new user in the system.
     * @param userId the ID of the new user
     * @param username the username of the new user
     * @param password the password of the new user
     * @param email the email of the new user
     * @param accountType the account type of the new user
     * @return the newly registered user
     */
    public User registerUser(int userId, String username, String password, String email, AccountType accountType) {
        if (userList.hasUser(username)) return null;
        User newUser;
        switch (accountType) {
            case EDITOR: newUser = new Editor(userId, username, password, email); break;
            case ADMIN:  newUser = new Admin(userId, username, password, email);  break;
            default:     newUser = new Student(userId, username, password, email); break;
        }
        userList.addUser(newUser);
        return newUser;
    }

    /**
     * Handles post-registration success logic for a user.
     * @param user the newly registered user
     */
    public void registrationSuccess(User user) {
        System.out.println("Registration successful for: " + user.getUsername());
    }

    /**
     * Retrieves questions based on a given filter.
     * @param filter the filter criteria for questions
     */
    public ArrayList<Question> getQuestions(String filter) {
        ArrayList<Question> allQuestions = questionList.getAllQuestions();
        if (filter == null || filter.isEmpty()) return allQuestions;

        ArrayList<Question> filtered = new ArrayList<>();
        for (Question q : allQuestions) {
            if (q.getTitle().contains(filter) || q.getPrompt().contains(filter)) {
                filtered.add(q);
            }
        }
        return filtered;
    }

    /**
     * Submits a solution for the given question.
     * @param question The question being answered
     * @param content The content of the solution
     */
    public Solution submitSolution(Question question, String content) {
        if (currentUser == null || question == null || content == null) return null;
        Solution newSolution = new Solution(currentUser, question, content);
        question.addSolution(newSolution);
        if (currentUser instanceof Student) ((Student) currentUser).incrementStreak();
        saveSolution();
        return newSolution;
    }

    /**
     * Saves the current solution.
     */
    public void saveSolution() {
        DataWriter.saveQuestions();
    }

    /**
     * Saves all data in the system.
     */
    public void saveAll() {
        DataWriter.saveUsers();
        DataWriter.saveQuestions();
    }

    /**
     * Loads all data into the system.
     */
    public void loadAll() {
        this.userList = UserList.getInstance();
        this.questionList = QuestionList.getInstance();
    }

    /**
     * Adds a question to the system.
     * @param title The title of the question
     * @param prompt The content of the question
     * @param diff The difficulty of the question
     * @param lang The programming language of the question
     * @param course The course the question is from
     */
    public void addQuestion(String title, String prompt, Difficulty diff, Language lang, Course course) {
        if (currentUser instanceof Editor || currentUser instanceof Admin) {
            Question q = new Question(title, prompt, diff, lang, course);
            q.setAuthor(currentUser);
            questionList.addQuestion(q);
        }
    }

    /**
     * Edits an existing question in the system.
     * @param q The question to edit
     * @param newTitle The new title of the question
     * @param newPrompt The new content of the question
     */
    public void editQuestion(Question q, String newTitle, String newPrompt) {
        if (q != null && (currentUser instanceof Editor || currentUser instanceof Admin)) {
            q.setTitle(newTitle);
            q.setPrompt(newPrompt);
            DataWriter.saveQuestions();
        }
    }

    /**
     * Removes a question from the system.
     * @param q The question to be removed
     */
    public void removeQuestion(Question q) {
        if (currentUser instanceof Admin) {
            questionList.removeQuestion(q);
        }
    }

    /**
     * Adds a comment to a question.
     * @param question The question to comment on
     * @param title The title of the comment
     * @param content The content of the comment
     */
    public Reply addComment(Question question, String title, String content) {
        if (currentUser == null || question == null) return null;
        Reply reply = new Reply(currentUser, question, content);
        reply.setTitle(title);
        question.addReply(reply);
        return reply;
    }

    /**
     * Removes a comment from a solution.
     * @param solution The solution to remove a comment from
     * @param commentToRemove The comment to remove
     */
    public void removeComment(Solution solution, Reply commentToRemove) {
        if (currentUser instanceof Admin ||
            (commentToRemove != null && commentToRemove.getAuthor().equals(currentUser))) {
            if (solution != null && commentToRemove != null) {
                solution.getComments().remove(commentToRemove);
                DataWriter.saveQuestions();
            }
        }
    }

    // -----------------------------------------------------------------------
    // Main - console login test
    // -----------------------------------------------------------------------

    public static void main(String[] args) {
        TIPSFacade facade = TIPSFacade.getInstance();

        // Debug: test if files can be found
        java.io.File usersFile = new java.io.File("json/users.json");
        System.out.println("Looking for users.json at: " + usersFile.getAbsolutePath());
        System.out.println("File exists: " + usersFile.exists());
        System.out.println("Users loaded: " + UserList.getInstance().getAllUsers().size());

        Scanner scanner = new Scanner(System.in);
        System.out.println("\n=== TIPS Login ===");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        boolean success = facade.login(username, password);

        if (success) {
            System.out.println("Login successful! Welcome, " + facade.getCurrentUser().getUsername()
                + " (" + facade.getCurrentUser().getAccountType() + ")");
        } else {
            System.out.println("Login failed. Invalid username or password.");
        }

        scanner.close();
    }
}