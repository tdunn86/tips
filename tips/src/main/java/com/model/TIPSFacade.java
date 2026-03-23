package com.model;

import java.util.ArrayList;

/**
 * Facade class - thin layer that delegates to UserList, QuestionList, etc.
 * Contains no business logic of its own.
 * @author Thomas Dunn, James Gessler, Oliver Benjamin
 */
public class TIPSFacade {
    private static TIPSFacade instance;

    private UserList userList;
    private QuestionList questionList;
    private User currentUser;

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
     * Delegates login to UserList.
     */
    public boolean login(String username, String password) {
        User user = userList.login(username, password);
        if (user != null) {
            this.currentUser = user;
            if (user instanceof Student) ((Student) user).startStreak();
            return true;
        }
        return false;
    }

    /**
     * Saves all data and clears the session.
     */
    public void logout() {
        if (currentUser != null) {
            DataWriter.saveUsers();
            this.currentUser = null;
        }
    }

    /**
     * Delegates registration to UserList.
     */
    public User registerUser(String username, String password, String email, AccountType accountType) {
        return userList.registerUser(username, password, email, accountType);
    }

    /**
     * Handles post-registration success.
     */
    public void registrationSuccess(User user) {
        System.out.println("Registration successful for: " + user.getUsername());
    }

    /**
     * Delegates question filtering to QuestionList.
     */
    public ArrayList<Question> getQuestions(String filter) {
        return questionList.getQuestions(filter);
    }

    /**
     * Delegates question creation to QuestionList.
     */
    public void addQuestion(String title, String prompt, Difficulty diff, Language lang, Course course) {
        Question q = questionList.addQuestion(title, prompt, diff, lang, course, currentUser);
        if (q != null) DataWriter.saveQuestions();
    }

    /**
     * Delegates question editing to Question itself.
     */
    public void editQuestion(Question q, String newTitle, String newPrompt) {
        if (q == null) return;
        if (!(currentUser instanceof Editor) && !(currentUser instanceof Admin)) return;
        q.setTitle(newTitle);
        q.setPrompt(newPrompt);
        DataWriter.saveQuestions();
    }

    /**
     * Delegates question removal to QuestionList.
     */
    public void removeQuestion(Question q) {
        questionList.removeQuestion(q, currentUser);
        DataWriter.saveQuestions();
    }
    
    /**
     * Returns all users(Admin only)
     * @return list of all current users in the system
     */
    public ArrayList<User> getAllUsers() {
        if (currentUser instanceof Admin) {
            return userList.getAllUsers();
        }
        return new ArrayList<>();
    }

    /**
     * Remove a user from the system (Admin only)
     * @param user the user to be removed
     */
    public void removeUser(User user) {
        if (currentUser instanceof Admin) {
            userList.removeUser(user);
            DataWriter.saveUsers();
        }
    }
    
    /**
     * Delegates solution submission to Question.
     */
    public Solution submitSolution(Question question, String content) {
        if (currentUser == null || question == null || content == null) return null;
        Solution solution = new Solution(currentUser, question, content);
        question.addSolution(solution);
        if (currentUser instanceof Student) ((Student) currentUser).incrementStreak();
        DataWriter.saveQuestions();
        return solution;
    }

    /**
     * Adds a top-level reply to a question.
     */
    public Reply addReply(Question question, String title, String content) {
        if (currentUser == null || question == null) return null;
        Reply reply = new Reply(currentUser, question, content);
        reply.setTitle(title);
        question.addReply(reply);
        DataWriter.saveQuestions();
        return reply;
    }

    /**
     * Adds a nested reply to an existing reply.
     */
    public Reply addNestedReply(Reply parent, Question question, String title, String content) {
        if (currentUser == null || parent == null || question == null) return null;
        Reply nested = new Reply(currentUser, question, content);
        nested.setTitle(title);
        parent.addReply(nested);
        DataWriter.saveQuestions();
        return nested;
    }

    /**
     * Removes a reply from a question's top-level reply list.
     * Only admins or the reply's own author may remove it.
     */
    public void removeReply(Question question, Reply reply) {
        if (question == null || reply == null) return;
        if (!(currentUser instanceof Admin) && !reply.getAuthor().equals(currentUser)) return;
        question.getReplies().remove(reply);
        DataWriter.saveQuestions();
    }

    /**
     * Removes a nested reply from a parent reply.
     * Only admins or the reply's own author may remove it.
     */
    public void removeNestedReply(Reply parent, Reply nested) {
        if (parent == null || nested == null) return;
        if (!(currentUser instanceof Admin) && !nested.getAuthor().equals(currentUser)) return;
        parent.getReplies().remove(nested);
        DataWriter.saveQuestions();
    }

    /**
     * Saves all data.
     */
    public void saveAll() {
        DataWriter.saveUsers();
        DataWriter.saveQuestions();
    }

    /**
     * Reloads all data.
     */
    public void loadAll() {
        this.userList = UserList.getInstance();
        this.questionList = QuestionList.getInstance();
    }
}