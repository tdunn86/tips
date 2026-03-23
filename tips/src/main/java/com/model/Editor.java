package com.model;
 
import java.util.ArrayList;
import java.util.List;
 
/**
 * Represents a user with editor permissions.
 * Editors can create, edit, and delete questions.
 * @author Thomas Dunn, James Gessler, Samuel Britton
 */
public class Editor extends User {
    public List<Question> createdQuestions;
    public int totalQuestionsCreated;
 
    /**
     * Constructs an Editor with the given user details.
     * @param userId the unique user ID
     * @param username the editor's username
     * @param password the editor's password
     * @param email the editor's email address
     */
    public Editor(int userId, String username, String password, String email) {
        super(userId, username, password, email, AccountType.EDITOR);
        this.createdQuestions = new ArrayList<>();
        this.totalQuestionsCreated = 0;
    }
 
    /**
     * Adds a question to the editor's list of created questions.
     * @param q the question to add
     * @return the question that was added
     */
    public Question createQuestion(Question q) {
        if (q != null) { createdQuestions.add(q); totalQuestionsCreated++; }
        return q;
    }
 
    /**
     * Edits an existing question.
     * @param q the question to edit
     * @return the modified question
     */
    public Question editQuestion(Question q) {
        return q;
    }
 
    /**
     * Removes a question from the editor's list of created questions.
     * @param q the question to remove
     * @return the removed question
     */
    public Question deleteQuestion(Question q) {
        createdQuestions.remove(q);
        return q;
    }
 
}