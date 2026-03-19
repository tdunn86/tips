package com.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A singleton manager for all questions.
 * Owns all question-related business logic.
 * @author Thomas Dunn, James Gessler
 */
public class QuestionList {
    private static QuestionList instance;
    private ArrayList<Question> questions;

    private QuestionList() {
        questions = DataLoader.getQuestions();
    }

    public static QuestionList getInstance() {
        if (instance == null) instance = new QuestionList();
        return instance;
    }

    /**
     * Returns all questions, optionally filtered by keyword, difficulty, language, or course.
     * Filter logic lives here, not in the Facade.
     */
    public ArrayList<Question> getQuestions(String filter) {
        if (filter == null || filter.trim().isEmpty()) return getAllQuestions();
        ArrayList<Question> results = new ArrayList<>();
        String lower = filter.toLowerCase();
        for (Question q : questions) {
            if (q.getTitle().toLowerCase().contains(lower)
                    || q.getPrompt().toLowerCase().contains(lower)
                    || q.getDifficulty().name().equalsIgnoreCase(filter)
                    || q.getLanguage().name().equalsIgnoreCase(filter)
                    || q.getCourse().name().equalsIgnoreCase(filter)) {
                results.add(q);
            }
        }
        return results;
    }

    /**
     * Finds the first question matching a keyword.
     */
    public Question getQuestion(String keyword) {
        if (keyword == null || keyword.isEmpty()) return null;
        String lower = keyword.toLowerCase();
        for (Question q : questions) {
            if (q.getTitle().toLowerCase().contains(lower)
                    || q.getPrompt().toLowerCase().contains(lower)) return q;
        }
        return null;
    }

    /**
     * Creates and adds a new question. Author must be an Editor or Admin.
     * Question creation logic lives here, not in the Facade.
     */
    public Question addQuestion(String title, String prompt, Difficulty difficulty,
            Language language, Course course, User author) {
        if (author == null) return null;
        if (!(author instanceof Editor) && !(author instanceof Admin)) {
            System.out.println("Only Editors and Admins can add questions.");
            return null;
        }
        Question q = new Question(title, prompt, difficulty, language, course);
        q.setAuthor(author);
        questions.add(q);
        return q;
    }

    /**
     * Adds an already-created question directly.
     */
    public void addQuestion(Question q) {
        if (q != null && !questions.contains(q)) questions.add(q);
    }

    /**
     * Removes a question. Only Admins can remove questions.
     */
    public boolean removeQuestion(Question q, User requestingUser) {
        if (!(requestingUser instanceof Admin)) {
            System.out.println("Only Admins can remove questions.");
            return false;
        }
        return questions.remove(q);
    }

    /**
     * Removes a question directly (no permission check).
     */
    public void removeQuestion(Question q) {
        questions.remove(q);
    }

    public ArrayList<Question> getAllQuestions() {
        return new ArrayList<>(questions);
    }

    public void save() {
        DataWriter.saveQuestions();
    }
}