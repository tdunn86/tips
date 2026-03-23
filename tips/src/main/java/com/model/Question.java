package com.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a technical interview question.
 * Owns all operations on itself: solutions, replies, revealing.
 * @author Samuel Britton, Thomas Dunn
 */
public class Question {
    private String title;
    private String prompt;
    private String hint;
    private UUID questionID;
    private Difficulty difficulty;
    private Language language;
    private Course course;
    private ArrayList<Course> courses;
    private User author;
    private boolean isSolutionRevealed;
    private ArrayList<Solution> solutions;
    private String sampleSolution;
    private String sampleExplanation;
    private List<String> images;
    private List<String> attachments;
    private List<Reply> replies;

    public Question(String title, String prompt,
            Difficulty difficulty, Language language, Course course) {
        this.title = title;
        this.prompt = prompt;
        this.difficulty = difficulty;
        this.language = language;
        this.course = course;
        this.questionID = UUID.randomUUID();
        this.isSolutionRevealed = false;
        this.solutions = new ArrayList<>();
        this.courses = new ArrayList<>();
        this.images = new ArrayList<>();
        this.attachments = new ArrayList<>();
        this.replies = new ArrayList<>();
    }

    // ===================== GETTERS =====================

    public String getTitle()               { return title; }
    public String getPrompt()              { return prompt; }
    public String getHint()                { return hint; }
    public UUID getQuestionID()            { return questionID; }
    public Difficulty getDifficulty()      { return difficulty; }
    public Language getLanguage()          { return language; }
    public Course getCourse()              { return course; }
    public ArrayList<Course> getCourses()  { return courses; }
    public User getAuthor()                { return author; }
    public boolean isSolutionRevealed()    { return isSolutionRevealed; }
    public ArrayList<Solution> getSolutions() { return solutions; }
    public String getSampleSolution()      { return sampleSolution; }
    public String getSampleExplanation()   { return sampleExplanation; }
    public List<String> getImages()        { return images; }
    public List<String> getAttachments()   { return attachments; }
    public List<Reply> getReplies()        { return replies; }

    // ===================== SETTERS =====================

    public void setTitle(String title)                       { this.title = title; }
    public void setPrompt(String prompt)                     { this.prompt = prompt; }
    public void setHint(String hint)                         { this.hint = hint; }
    public void setDifficulty(Difficulty difficulty)         { this.difficulty = difficulty; }
    public void setLanguage(Language language)               { this.language = language; }
    public void setCourse(Course course)                     { this.course = course; }
    public void setAuthor(User author)                       { this.author = author; }
    public void setSampleSolution(String s)                  { this.sampleSolution = s; }
    public void setSampleExplanation(String s)               { this.sampleExplanation = s; }
    public void setQuestionID(UUID questionID)               { this.questionID = questionID; }

    // ===================== SOLUTION OPERATIONS =====================

    /**
     * Submits a user's code solution for this question.
     * Increments the author's streak if they are a Student.
     * @return the created Solution
     */
    public Solution submitSolution(User author, String content) {
        if (author == null || content == null || content.trim().isEmpty()) return null;
        Solution solution = new Solution(author, this, content);
        solutions.add(solution);
        if (author instanceof Student) ((Student) author).incrementStreak();
        return solution;
    }

    public void addSolution(Solution solution) {
        if (solution != null) solutions.add(solution);
    }

    /**
     * Reveals the sample solution for this question.
     */
    public void revealSolution() {
        this.isSolutionRevealed = true;
    }

    public void showSolution() {
        this.isSolutionRevealed = true;
    }

    public boolean isComplete() {
        return isSolutionRevealed;
    }

    public void reset() {
        this.isSolutionRevealed = false;
        this.solutions.clear();
    }

    // ===================== REPLY OPERATIONS =====================

    /**
     * Adds a top-level comment to this question.
     * @return the created Reply
     */
    public Reply addComment(User author, String title, String content) {
        if (author == null || content == null || content.trim().isEmpty()) return null;
        Reply reply = new Reply(author, this, content);
        reply.setTitle(title);
        replies.add(reply);
        return reply;
    }

    /**
     * Adds a nested reply to an existing reply on this question.
     * @return the created Reply, or null if parentReply not found
     */
    public Reply addNestedReply(Reply parentReply, User author, String title, String content) {
        if (parentReply == null || author == null || content == null) return null;
        Reply reply = new Reply(author, this, content);
        reply.setTitle(title);
        parentReply.addReply(reply);
        return reply;
    }

    /**
     * Removes a top-level comment. Only the comment's author or an Admin may remove it.
     * @return true if removed, false if not found or not authorized
     */
    public boolean removeComment(Reply comment, User requestingUser) {
        if (comment == null || requestingUser == null) return false;
        if (!(requestingUser instanceof Admin) && !comment.getAuthor().equals(requestingUser))
            return false;
        return replies.remove(comment);
    }

    public void addReply(Reply reply) {
        if (reply != null) replies.add(reply);
    }

    // ===================== MISC =====================

    public void submit() { }

    public String toString() {
        return "Question{id=" + questionID + ", title='" + title +
               "', difficulty=" + difficulty + ", course=" + course + "}";
    }