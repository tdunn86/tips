package com.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Facade class that serves as the main entry point for the TIPS system.
 * Manages user authentication, questions, solutions, and comments.
 * @author Thomas Dunn, James Gessler
 */
public class TIPSFacade {
    /** The list of all users in the system. */
    private UserList userList;
    /** The currently logged in user. */
    private User currentUser;
    /** The password of the current user. */
    private String password;
    /** The email of the current user. */
    private String email;
    /** The account type of the current user. */
    private AccountType accountType;

    public TIPSFacade() {
        this.userList = UserList.getInstance();
        this.questionList = QuestionList.getInstance();
    }

    /**
     * Authenticates a user with their username and password.
     * @param username the username of the user
     * @param password the password of the user
     */
    public boolean login(String username, String password) {
        User user = userList.getUser(username);
        if (user != null && user.getPassword().equals(password)) {
            this.currentUser = user;
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
        User newUser = new User(userId, username, password, email, accountType);
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
        ArrayList<Question> allQuestions = (ArrayListQuestion>) questionList.getAllQuestions();
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
     * Submits a solution for the current question.
     */
    public void submitSolution() {
        if (currentUser != null && question != null) {
            Solution newSolution = new Solution(question.getQuestionID(), content);
            question.addSolution(newSolution);
            saveSolution();
        }
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
        DataWriter.saveUser();
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
     * Adds a question to the system
     * @param id The question ID identifier
     * @param title The title of the question
     * @param prompt The content of the question
     * @param diff The difficulty of the question
     * @param lang The programming language of the question
     * @param course The course the question is from
     */
    public void addQuestion(int id, String title, String prompt, Difficulty diff, Language lang, Course course) {
        if (currentuser instanceof Editor || currentUser instanceof Admin) {
            Question q = new Question(id, title, prompt, diff, lang, course);
            questionList.addQuestion(q);
        }
    }

    /**
     * Edits an existing question in the system.
     * @param q The question to edit
     * @param newTitle The new title of the question
     * @param newPrompt The new content of the quesiton
     */
    public void editQuestion(Question q, String newTitle, String newPrompt) {
        if (q != null && (currentUser instanceof Editor || currentUser instanceof Admin)) {
            if (question != null) {
                question.setTitle(newTitle);
                question.setPrompt(newPrompt);
                DataWriter.saveQuestions();
            }
        }
    }

    /**
     * Removes a question from the system.
     * @param q The question to be removed
     */
    public void removeQuestion(Question q) {
        if (currentUser instaceof Admin) {
            questionList.removeQuestion(q);
        }
    }

    /**
     * Adds a comment to a question or solution.
     */
    public void addComment() {
        if (currentUser != null) {
            Comment comment = new Comment(currentUser, s, content);
            s.addComment(currentUser.getUsernmae(), content);
        }
    }

    /**
     * Removes a comment from a solution.
     * @param solution The solution to remove a comment from
     * @param commentToRemove The comment to remove
     */
    public void removeComment(Solution solution, Reply commentToRemove) {
        if (currentUser instanceof Admin || (commentToRemove != null && commentToRemove.getAuthor().equals(currentUser))) {
            if (solution != null && commentToRemove != null) {
                solution.getComments().remove(commentToRemove);
                DataWriter.saveQuestion();
            }
        }
    }
}