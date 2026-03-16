package com.model;

import java.util.UUID;

/**
 * Represents a reply to a question in the TIPS system
 * @author Thomas Dunn, James Gessler
 */
public class Reply {
    protected User author;
    protected Question question;
    private UUID replyId;       // FIXED: was missing, DataWriter called getReplyId()
    private String title;
    private String content;
    private int upvotes;
    private boolean isAccepted;

    public Reply(User author, Question question, String content) {
        this.author = author;
        this.question = question;
        this.content = content;
        this.replyId = UUID.randomUUID();
        this.title = "";
        this.upvotes = 0;
        this.isAccepted = false;
    }

    // Getters
    public UUID getReplyId() { return replyId; }   // FIXED: was throwing UnsupportedOperationException
    public User getAuthor() { return author; }
    public Question getQuestion() { return question; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public int getUpvotes() { return upvotes; }
    public boolean isAccepted() { return isAccepted; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setUpvotes(int upvotes) { this.upvotes = upvotes; }
    public void setAccepted(boolean accepted) { this.isAccepted = accepted; }

    public void upvote() { this.upvotes++; }

    public void report(String reason) {
        System.out.println("Reply by '" + author.getUsername() + "' reported. Reason: " + reason);
    }
}