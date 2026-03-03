package com.model;

/**
 * A user role with permissions to manage educational content
 */
public class Editor {
    public List<Question> createdQuestions;
    public int totalQuestionsCreated;

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
