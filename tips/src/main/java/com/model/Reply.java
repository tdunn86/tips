package com.model;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Represents a reply to a question in the TIPS system.
 * Replies can have nested replies (recursive comments).
 * @author Thomas Dunn, James Gessler
 */
public class Reply {
    protected User author;
    protected Question question;
    private UUID replyId;
    private String title;
    private String content;
    private int upvotes;
    private boolean isAccepted;
    private ArrayList<Reply> replies;  // recursive comments

    public Reply(User author, Question question, String content) {
        this.author = author;
        this.question = question;
        this.content = content;
        this.replyId = UUID.randomUUID();
        this.title = "";
        this.upvotes = 0;
        this.isAccepted = false;
        this.replies = new ArrayList<>();
    }

    // Getters
    public UUID getReplyId() { return replyId; }
    public User getAuthor() { return author; }
    public Question getQuestion() { return question; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public int getUpvotes() { return upvotes; }
    public boolean isAccepted() { return isAccepted; }
    public ArrayList<Reply> getReplies() { return replies; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setUpvotes(int upvotes) { this.upvotes = upvotes; }
    public void setAccepted(boolean accepted) { this.isAccepted = accepted; }

    public void upvote() { this.upvotes++; }

    // Add a nested reply (recursive comment)
    public void addReply(Reply reply) {
        if (reply != null) replies.add(reply);
    }

    public void report(String reason) {
        System.out.println("Reply by '" + author.getUsername() + "' reported. Reason: " + reason);
    }
}