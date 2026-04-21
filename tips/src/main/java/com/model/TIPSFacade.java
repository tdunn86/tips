package com.model;

import java.util.ArrayList;

/**
 * Provides a single access point for all system operations.
 * Acts as a pure delegation layer with no business logic of its own.
 * Every method calls into UserList, QuestionList, or the model.
 * Implemented as a singleton.
 * @author Thomas Dunn, James Gessler
 */
public class TIPSFacade {
    private static TIPSFacade instance;

    private final UserList userList;
    private final QuestionList questionList;
    private User currentUser;

    /**
     * Constructs the facade by obtaining singleton instances of UserList and QuestionList.
     */
    private TIPSFacade() {
        this.userList     = UserList.getInstance();
        this.questionList = QuestionList.getInstance();
    }

    /**
     * Returns the single instance of TIPSFacade, creating it if needed.
     * @return the TIPSFacade instance
     */
    public static TIPSFacade getInstance() {
        if (instance == null) instance = new TIPSFacade();
        return instance;
    }

    /**
     * Returns the currently logged-in user.
     * @return the current user, or null if no one is logged in
     */
    public User getCurrentUser() { return currentUser; }

    /**
     * Attempts to log in with the given credentials.
     * Sets the current user and starts their streak if successful.
     * @param username the username to log in with
     * @param password the password to log in with
     * @return true if login was successful, false otherwise
     */
    public boolean login(String username, String password) {
        User user = userList.login(username, password);
        if (user == null) return false;
        currentUser = user;
        if (user instanceof Student) ((Student) user).startStreak();
        return true;
    }

    /**
     * Logs out the current user and saves all user data.
     */
    public void logout() {
        if (currentUser == null) return;
        userList.save();
        currentUser = null;
    }

    /**
     * Registers a new user account.
     * @param username the new user's username
     * @param password the new user's password
     * @param email the new user's email address
     * @param type the account type to assign
     * @return the created User, or null if registration failed
     */
    public User registerUser(String username, String password, String email, AccountType type) {
        return userList.registerUser(username, password, email, type);
    }

    /**
     * Returns all questions, optionally filtered by a keyword.
     * @param filter the keyword to filter by, or null for all questions
     * @return a list of matching questions
     */
    public ArrayList<Question> getQuestions(String filter) {
        return questionList.getQuestions(filter);
    }

    /**
     * Returns a daily challenge question tailored to the current user's skill level.
     * @return a suitable question, or null if the current user is not a Student
     */
    public Question getDailyChallenge() {
        return questionList.getDailyChallengeForToday();
    }

    /**
     * Creates and adds a new question using the current user as the author.
     * @param title the question title
     * @param prompt the question prompt
     * @param diff the difficulty level
     * @param lang the programming language
     * @param course the associated course
     * @return the created Question, or null if not authorized
     */
    public Question addQuestion(String title, String prompt, Difficulty diff, Language lang, Course course) {
        return questionList.addQuestion(title, prompt, diff, lang, course, currentUser);
    }

    /**
     * Removes a question using the current user as the requesting user.
     * @param q the question to remove
     */
    public void removeQuestion(Question q) {
        questionList.removeQuestion(q, currentUser);
    }

    /**
     * Updates the title and prompt of a question.
     * Only Editors and Admins are allowed to edit questions.
     * @param q the question to edit
     * @param newTitle the new title
     * @param newPrompt the new prompt
     */
    public void editQuestion(Question q, String newTitle, String newPrompt) {
        if (q == null) return;
        if (!(currentUser instanceof Editor) && !(currentUser instanceof Admin)) return;
        q.setTitle(newTitle);
        q.setPrompt(newPrompt);
        questionList.save();
    }

    /**
     * Submits a code solution for the given question from the current user.
     * @param question the question being answered
     * @param content the submitted code
     * @return the created Solution, or null if submission failed
     */
    public Solution submitSolution(Question question, String content) {
        return questionList.submitSolution(question, currentUser, content);
    }

    /**
     * Reveals the sample solution for the given question.
     * @param question the question whose solution to reveal
     */
    public void revealSolution(Question question) {
        questionList.revealSolution(question);
    }

    /**
     * Adds a top-level comment to a question from the current user.
     * @param question the question being commented on
     * @param title the comment title
     * @param content the comment text
     * @return the created Reply, or null if it failed
     */
    public Reply addComment(Question question, String title, String content) {
        return questionList.addComment(question, currentUser, title, content);
    }

    /**
     * Adds a nested reply to an existing comment from the current user.
     * @param parent the reply being responded to
     * @param question the question the comment belongs to
     * @param title the reply title
     * @param content the reply text
     * @return the created Reply, or null if it failed
     */
    public Reply addNestedReply(Reply parent, Question question, String title, String content) {
        return questionList.addNestedReply(question, parent, currentUser, title, content);
    }

    /**
     * Removes a comment from a question.
     * Only the comment's author or an Admin may remove it.
     * @param question the question the comment belongs to
     * @param comment the reply to remove
     * @return true if removed, false if not authorized or not found
     */
    public boolean removeComment(Question question, Reply comment) {
        return questionList.removeComment(question, comment, currentUser);
    }

    /**
     * Prints the full details of a question to a text file.
     * @param question the question to print
     * @param filename the output file path
     */
    public void printQuestionToFile(Question question, String filename) {
        questionList.printQuestionToFile(question, filename);
    }

    /**
     * Saves all user and question data to the JSON files.
     */
    public void saveAll() {
        userList.save();
        questionList.save();
    }
}