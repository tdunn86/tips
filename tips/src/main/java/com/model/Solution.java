package com.model;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Represents an answer or explanation for a question
 * @author Thomas Dunn, James Gessler
 */
public class Solution {
    protected User author;
    protected Question question;
    private UUID solutionId;    // FIXED: was missing, DataWriter called getSolutionId()
    private String content;
    private int upvotes;
    private boolean isAccepted;
    private ArrayList<Reply> comments;

    public Solution(User author, Question question, String content) {
        this.author = author;
        this.question = question;
        this.content = content;
        this.solutionId = UUID.randomUUID();
        this.upvotes = 0;
        this.isAccepted = false;
        this.comments = new ArrayList<>();
    }

    // Getters
    public UUID getSolutionId() { return solutionId; }  // FIXED: was throwing UnsupportedOperationException
    public User getAuthor() { return author; }
    public Question getQuestion() { return question; }
    public String getContent() { return content; }
    public int getUpvotes() { return upvotes; }
    public boolean isAccepted() { return isAccepted; }
    public ArrayList<Reply> getComments() { return comments; }

    // Setters
    public void setContent(String content) { this.content = content; }
    public void setUpvotes(int upvotes) { this.upvotes = upvotes; }
    public void setAccepted(boolean accepted) { this.isAccepted = accepted; }

    public void edit(String content) { this.content = content; }
    public void upvote() { this.upvotes++; }
    public void delete() { this.content = "[deleted]"; this.isAccepted = false; }
    public void addComment(Reply reply) { if (reply != null) comments.add(reply); }
}