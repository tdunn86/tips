package com.model;

import java.util.ArrayList;

/**
 * Singleton manager for all questions.
 * Owns question-level business logic: filtering, adding, removing, submitting solutions.
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

    // ===================== QUERY =====================

    /**
     * Returns all questions filtered by keyword, difficulty, language, or course.
     * Passing null or blank returns all questions.
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

    public Question getQuestion(String keyword) {
        if (keyword == null || keyword.isEmpty()) return null;
        String lower = keyword.toLowerCase();
        for (Question q : questions) {
            if (q.getTitle().toLowerCase().contains(lower)
                    || q.getPrompt().toLowerCase().contains(lower)) return q;
        }
        return null;
    }

    public ArrayList<Question> getAllQuestions() {
        return new ArrayList<>(questions);
    }

    // ===================== ADD / REMOVE =====================

    /**
     * Creates and adds a new question. Only Editors and Admins may add questions.
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

    public void addQuestion(Question q) {
        if (q != null && !questions.contains(q)) questions.add(q);
    }

    /**
     * Removes a question. Only Admins may remove questions.
     */
    public boolean removeQuestion(Question q, User requestingUser) {
        if (!(requestingUser instanceof Admin)) {
            System.out.println("Only Admins can remove questions.");
            return false;
        }
        return questions.remove(q);
    }

    public void removeQuestion(Question q) {
        questions.remove(q);
    }

    // ===================== SOLUTION / REPLY =====================

    /**
     * Submits a code solution to a question. Delegates to Question.
     * Saves questions after submission.
     */
    public Solution submitSolution(Question question, User author, String content) {
        if (question == null) return null;
        Solution solution = question.submitSolution(author, content);
        if (solution != null) DataWriter.saveQuestions();
        return solution;
    }

    /**
     * Adds a top-level comment to a question. Delegates to Question.
     * Saves after adding.
     */
    public Reply addComment(Question question, User author, String title, String content) {
        if (question == null) return null;
        Reply reply = question.addComment(author, title, content);
        if (reply != null) DataWriter.saveQuestions();
        return reply;
    }

    /**
     * Adds a nested reply to an existing comment. Delegates to Question.
     * Saves after adding.
     */
    public Reply addNestedReply(Question question, Reply parentReply, User author,
            String title, String content) {
        if (question == null) return null;
        Reply reply = question.addNestedReply(parentReply, author, title, content);
        if (reply != null) DataWriter.saveQuestions();
        return reply;
    }

    /**
     * Removes a comment from a question. Delegates to Question.
     * Saves after removing.
     */
    public boolean removeComment(Question question, Reply comment, User requestingUser) {
        if (question == null) return false;
        boolean removed = question.removeComment(comment, requestingUser);
        if (removed) DataWriter.saveQuestions();
        return removed;
    }

    /**
     * Reveals the sample solution for a question. Saves after revealing.
     */
    public void revealSolution(Question question) {
        if (question == null) return;
        question.revealSolution();
        DataWriter.saveQuestions();
    }

    // ===================== PERSIST =====================

    public void save() {
        DataWriter.saveQuestions();
    }
}
