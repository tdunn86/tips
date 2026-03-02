package com.model;

import java.util.List;

/**
 * A user role with administrative oversight
 * @author Thomas Dunn, James Gessler
 */
public class Admin {
    public List<Questions> createdQuestions;
    public int totalQuestionsCreated;

    /**
     * Greants editor status to a user
     * @param ID The ID of the user to promote
     * @return True if approved, false otherwise
     */
    public boolean approveEditor(int userid) {
        return true;
    }

    /**
     * Adds a new question
     * @param q The Question object to be created 
     * @return The created question
     */
    public Question createQuestion(Question q) {
        return q;
    }

    /**
     * Updates an existing question
     * @param q The Question object to be edited 
     * @return The modified question object
     */
    public Question editQuestion(Question q) {
        return q;
    }

    /**
     * Removes a question
     * @param q The Question object to remove
     * @return The removed question object
     */
    public Question deleteQuestion (Question q) {
        return q;
    }

    public void createSection() {

    }



}