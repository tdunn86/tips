package com.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A singleton manager for all questions
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

    public Question getQuestion(String keyword) {
        if (keyword == null || keyword.isEmpty()) return null;
        String lower = keyword.toLowerCase();
        for (Question q : questions) {
            if (q.getTitle().toLowerCase().contains(lower)
                    || q.getPrompt().toLowerCase().contains(lower)) return q;
        }
        return null;
    }

    public void addQuestion(Question q) {
        if (q != null && !questions.contains(q)) questions.add(q);
    }

    public void removeQuestion(Question q) {
        questions.remove(q);
    }

    public ArrayList<Question> getAllQuestions() {
        return new ArrayList<>(questions);
    }

    public List<Question> getFilteredQuestions(String filter) {
        if (filter == null || filter.isEmpty()) return getAllQuestions();
        List<Question> results = new ArrayList<>();
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

    public void save() {
        DataWriter.saveQuestions();
    }
}
