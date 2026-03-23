package com.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Represents a comment on a question.
 * Replies can contain nested replies, allowing threaded discussions.
 * Each reply records the date and time it was posted.
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
    private ArrayList<Reply> replies;
    private String datePosted;

    /**
     * Constructs a Reply with the given author, question, and content.
     * Records the current date and time as the post date.
     * @param author the user who wrote the reply
     * @param question the question this reply belongs to
     * @param content the text content of the reply
     */
    public Reply(User author, Question question, String content) {
        this.author = author;
        this.question = question;
        this.content = content;
        this.replyId = UUID.randomUUID();
        this.title = "";
        this.upvotes = 0;
        this.isAccepted = false;
        this.replies = new ArrayList<>();
        this.datePosted = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm"));
    }

    /**
     * Returns the unique ID of this reply.
     * @return the reply UUID
     */
    public UUID getReplyId()              { return replyId; }

    /**
     * Returns the author of this reply.
     * @return the reply author
     */
    public User getAuthor()               { return author; }

    /**
     * Returns the question this reply belongs to.
     * @return the associated question
     */
    public Question getQuestion()         { return question; }

    /**
     * Returns the title of this reply.
     * @return the reply title
     */
    public String getTitle()              { return title; }

    /**
     * Returns the text content of this reply.
     * @return the reply content
     */
    public String getContent()            { return content; }

    /**
     * Returns the number of upvotes this reply has received.
     * @return the upvote count
     */
    public int getUpvotes()               { return upvotes; }

    /**
     * Returns whether this reply has been accepted.
     * @return true if accepted, false otherwise
     */
    public boolean isAccepted()           { return isAccepted; }

    /**
     * Returns the list of nested replies on this reply.
     * @return the list of nested replies
     */
    public ArrayList<Reply> getReplies()  { return replies; }

    /**
     * Returns the date and time this reply was posted.
     * @return the post date as a formatted string
     */
    public String getDatePosted()         { return datePosted; }

    /**
     * Sets the title of this reply.
     * @param title the new title
     */
    public void setTitle(String title)             { this.title = title; }

    /**
     * Sets the text content of this reply.
     * @param content the new content
     */
    public void setContent(String content)         { this.content = content; }

    /**
     * Sets the upvote count for this reply.
     * @param upvotes the new upvote count
     */
    public void setUpvotes(int upvotes)            { this.upvotes = upvotes; }

    /**
     * Sets whether this reply is accepted.
     * @param accepted true to mark as accepted, false otherwise
     */
    public void setAccepted(boolean accepted)      { this.isAccepted = accepted; }

    /**
     * Sets the date this reply was posted.
     * Used by DataLoader when restoring replies from JSON.
     * @param datePosted the date string to set
     */
    public void setDatePosted(String datePosted)   { this.datePosted = datePosted; }

    /**
     * Increments the upvote count by one.
     */
    public void upvote() { this.upvotes++; }

    /**
     * Adds a nested reply to this reply.
     * @param reply the nested reply to add
     */
    public void addReply(Reply reply) {
        if (reply != null) replies.add(reply);
    }

    /**
     * Reports this reply for the given reason.
     * @param reason the reason for reporting
     */
    public void report(String reason) {
        System.out.println("Reply by '" + author.getUsername() + "' reported. Reason: " + reason);
    }
}