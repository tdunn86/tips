package com.model;

import java.util.ArrayList;

/**
 * A singleton manager for all questions
 * @author Thomas Dunn, James Gessler
 */
public abstract class QuestionList {
    private QuestionList instance;
    private ArrayList<Question> questions;

    private QuestionList() {

    }

    /**
     * Provides access to single instance of the question list
     * @return The static question list instance
     */
    public QuestionList getInstance() {
        return instance;
    }

    /**
     * Finds a question using a search keyword
     * @param keyword The text used to filter questions
     * @return The matching question object
     */
    public Question getQuestion(String keyword) {
        return new Question(101, "Stub Question", "What is a stub?", Difficult.EASY, Language.PYTHON, Course.CSCE146);
    }

    /**
     * Adds a question to the list
     * @param q The question to add
     */
    public void addQuestion(Question q) {

    }

    /**
     * Removes a question from the list
     * @param q The question to remove
     */
    public void removeQuestion(Question q) {

    }

    /**
     * Retrieves all available questions
     * @return A list of all questions
     */
    public List<Question> getAllQuestions() {
        ArrayList<Question> questions = new ArrayList<>();
        questions.add(new Question(1, "Sample Title", "Sample Prompt", Difficulty.EASY, Language.JAVA, Course.CSCE247));
        return questions;
    }

    public void save() {

    }
}