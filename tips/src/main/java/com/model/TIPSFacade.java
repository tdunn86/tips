package com.model;

import java.util.ArrayList;

/**
 * Facade class - thin layer that delegates to UserList, QuestionList, etc.
 * Contains no business logic of its own.
 * @author Thomas Dunn, James Gessler
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

    public boolean login(String username, String password) {
        User user = userList.login(username, password);
        if (user != null) {
            this.currentUser = user;
            if (user instanceof Student) ((Student) user).startStreak();
            return true;
        }
        return false;
    }

    public void logout() {
        if (currentUser != null) {
            DataWriter.saveUsers();
            this.currentUser = null;
        }
    }

    public User registerUser(String username, String password, String email, AccountType accountType) {
        return userList.registerUser(username, password, email, accountType);
    }

    public void registrationSuccess(User user) {
        System.out.println("Registration successful for: " + user.getUsername());
    }

    public boolean deleteUser(String username) {
        return userList.deleteUser(username, currentUser);
    }

    public ArrayList<Question> getQuestions(String filter) {
        return questionList.getQuestions(filter);
    }

    public void addQuestion(String title, String prompt, Difficulty diff, Language lang, Course course) {
        Question q = questionList.addQuestion(title, prompt, diff, lang, course, currentUser);
        if (q != null) DataWriter.saveQuestions();
    }

    public void editQuestion(Question q, String newTitle, String newPrompt) {
        if (q == null) return;
        if (!(currentUser instanceof Editor) && !(currentUser instanceof Admin)) return;
        q.setTitle(newTitle);
        q.setPrompt(newPrompt);
        DataWriter.saveQuestions();
    }

    public void removeQuestion(Question q) {
        questionList.removeQuestion(q, currentUser);
        DataWriter.saveQuestions();
    }

    public Solution submitSolution(Question question, String content) {
        if (currentUser == null || question == null || content == null) return null;
        Solution solution = new Solution(currentUser, question, content);
        question.addSolution(solution);
        if (currentUser instanceof Student) ((Student) currentUser).incrementStreak();
        DataWriter.saveQuestions();
        return solution;
    }

    public Reply addComment(Question question, String title, String content) {
        if (currentUser == null || question == null) return null;
        Reply reply = new Reply(currentUser, question, content);
        reply.setTitle(title);
        question.addReply(reply);
        DataWriter.saveQuestions();
        return reply;
    }

    public void removeComment(Solution solution, Reply comment) {
        if (solution == null || comment == null) return;
        if (!(currentUser instanceof Admin) && !comment.getAuthor().equals(currentUser)) return;
        solution.getComments().remove(comment);
        DataWriter.saveQuestions();
    }

    public void saveAll() {
        DataWriter.saveUsers();
        DataWriter.saveQuestions();
    }

    public void loadAll() {
        this.userList = UserList.getInstance();
        this.questionList = QuestionList.getInstance();
    }
}