package com.model;

import java.util.ArrayList;

/**
 * Facade class that serves as the main entry point for the TIPS system.
 * Manages user authentication, questions, solutions, and comments.
 * @author Thomas Dunn, James Gessler
 */
public class TIPSFacade {
    private static TIPSFacade instance;

    private UserList userList;
    private QuestionList questionList;
    private User currentUser;
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
        if (username == null || username.trim().isEmpty()) return false;
        if (password == null || password.trim().isEmpty()) return false;

        User user = userList.getUser(username);
        if (user != null && user.validatePassword(password)) {
            this.currentUser = user;
            if (user instanceof Student) ((Student) user).startStreak();
            return true;
        }
        return false;
    }

    /**
     * Logs out the current user, saves data, and clears the session.
     * FIXED: now calls DataWriter.saveUsers() so new users persist
     */
    public void logout() {
        if (currentUser != null) {
            DataWriter.saveUsers();  // FIXED: persist any changes (e.g. newly registered user)
            this.currentUser = null;
        }
    }

    /**
     * Registers a new user in the system.
     * @param username the username of the new user
     * @param password the password of the new user
     * @param email the email of the new user
     * @param accountType the account type of the new user
     * @return the newly registered user, or null if registration fails
     */
    public User registerUser(String username, String password, String email, AccountType accountType) {
        // FIXED: validate blank username
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Registration failed: username cannot be blank.");
            return null;
        }
        if (password == null || password.trim().isEmpty()) {
            System.out.println("Registration failed: password cannot be blank.");
            return null;
        }
        if (userList.hasUser(username)) {
            System.out.println("Registration failed: username '" + username + "' already exists.");
            return null;
        }

        int newId = userList.getNextUserId();
        User newUser;
        switch (accountType) {
            case EDITOR: newUser = new Editor(newId, username, password, email); break;
            case ADMIN:  newUser = new Admin(newId, username, password, email);  break;
            default:     newUser = new Student(newId, username, password, email); break;
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
     * @param filter the filter criteria for questions (null or empty returns all)
     */
    public ArrayList<Question> getQuestions(String filter) {
        ArrayList<Question> allQuestions = questionList.getAllQuestions();
        if (filter == null || filter.isEmpty()) return allQuestions;

        ArrayList<Question> filtered = new ArrayList<>();
        for (Question q : allQuestions) {
            if (q.getTitle().toLowerCase().contains(filter.toLowerCase())
                    || q.getPrompt().toLowerCase().contains(filter.toLowerCase())) {
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
        DataWriter.saveQuestions();
        return newSolution;
    }

    /**
     * Saves the current solution state.
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
     * Adds a question to the system (Editor/Admin only).
     */
    public void addQuestion(String title, String prompt, Difficulty diff, Language lang, Course course) {
        if (currentUser instanceof Editor || currentUser instanceof Admin) {
            Question q = new Question(title, prompt, diff, lang, course);
            q.setAuthor(currentUser);
            questionList.addQuestion(q);
        }
    }

    /**
     * Edits an existing question (Editor/Admin only).
     */
    public void editQuestion(Question q, String newTitle, String newPrompt) {
        if (q != null && (currentUser instanceof Editor || currentUser instanceof Admin)) {
            q.setTitle(newTitle);
            q.setPrompt(newPrompt);
            DataWriter.saveQuestions();
        }
    }

    /**
     * Removes a question from the system (Admin only).
     */
    public void removeQuestion(Question q) {
        if (currentUser instanceof Admin) {
            questionList.removeQuestion(q);
        }
    }

    /**
     * Adds a comment/reply to a question.
     */
    public Reply addComment(Question question, String title, String content) {
        if (currentUser == null || question == null) return null;
        Reply reply = new Reply(currentUser, question, content);
        reply.setTitle(title);
        question.addReply(reply);
        return reply;
    }

    /**
     * Removes a comment from a solution (Admin or comment author only).
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
}