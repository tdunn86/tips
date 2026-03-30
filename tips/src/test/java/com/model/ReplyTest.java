package com.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * AI-ASSISTED — reasoning table below
 * 
 * Test class for Reply.
 * @author James Gessler
 * 
 *
 * +--------------------------------------------------+-----------------------------------------------------------------------+
 * | Test                                             | Reasoning                                                             |
 * +--------------------------------------------------+-----------------------------------------------------------------------+
 * | defaultUpvotesIsZero                             | New reply should start with 0 upvotes                                 |
 * | defaultIsAcceptedIsFalse                         | New reply should not be accepted by default                           |
 * | defaultTitleIsEmpty                              | New reply should have an empty string title by default                |
 * | defaultRepliesListIsEmpty                        | New reply should have no nested replies by default                    |
 * | datePostedIsNotNull                              | Date posted should be set automatically on construction               |
 * | upvoteIncreasesByOne                             | Each upvote call should increment count by exactly 1                  |
 * | upvoteMultipleTimes                              | Multiple upvotes should accumulate correctly                          |
 * | setTitleUpdatesTitle                             | setTitle() should update the stored title                             |
 * | setContentUpdatesContent                         | setContent() should update the stored content                         |
 * | setAcceptedToTrue                                | setAccepted(true) should mark reply as accepted                       |
 * | setAcceptedToFalse                               | setAccepted(false) should unmark an accepted reply                    |
 * | setDatePostedUpdatesDate                         | setDatePosted() should update the stored date string                  |
 * | addNestedReplyAddsToList                         | A valid nested reply should appear in replies list                    |
 * | addNestedReplyDoesNotAddNull                     | Null nested reply must be silently ignored                            |
 * | getAuthorReturnsCorrectAuthor                    | Author set at construction must be retrievable                        |
 * | getContentReturnsInitialContent                  | Content set at construction must be retrievable                       |
 * | getQuestionReturnsCorrectQuestion                | Question set at construction must be retrievable                      |
 * +--------------------------------------------------+-----------------------------------------------------------------------+
 */
public class ReplyTest {

    private Student author;
    private Question question;
    private Reply reply;

    @Before
    public void setUp() {
        author   = new Student(1, "testStudent", "pass123", "student@test.com");
        question = new Question("Test Q", "prompt", Difficulty.EASY, Language.JAVA, Course.CSCE145);
        reply    = new Reply(author, question, "This is my reply.");
    }

    // ===================== DEFAULTS =====================

    @Test
    public void defaultUpvotesIsZero() {
        assertEquals(0, reply.getUpvotes());
    }

    @Test
    public void defaultIsAcceptedIsFalse() {
        assertFalse(reply.isAccepted());
    }

    @Test
    public void defaultTitleIsEmpty() {
        assertEquals("", reply.getTitle());
    }

    @Test
    public void defaultRepliesListIsEmpty() {
        assertTrue(reply.getReplies().isEmpty());
    }

    @Test
    public void datePostedIsNotNull() {
        assertNotNull(reply.getDatePosted());
    }

    // ===================== GETTERS =====================

    @Test
    public void getAuthorReturnsCorrectAuthor() {
        assertEquals(author, reply.getAuthor());
    }

    @Test
    public void getContentReturnsInitialContent() {
        assertEquals("This is my reply.", reply.getContent());
    }

    @Test
    public void getQuestionReturnsCorrectQuestion() {
        assertEquals(question, reply.getQuestion());
    }

    // ===================== UPVOTE =====================

    @Test
    public void upvoteIncreasesByOne() {
        reply.upvote();
        assertEquals(1, reply.getUpvotes());
    }

    @Test
    public void upvoteMultipleTimes() {
        reply.upvote();
        reply.upvote();
        reply.upvote();
        assertEquals(3, reply.getUpvotes());
    }

    // ===================== SETTERS =====================

    @Test
    public void setTitleUpdatesTitle() {
        reply.setTitle("New Title");
        assertEquals("New Title", reply.getTitle());
    }

    @Test
    public void setContentUpdatesContent() {
        reply.setContent("Updated content.");
        assertEquals("Updated content.", reply.getContent());
    }

    @Test
    public void setAcceptedToTrue() {
        reply.setAccepted(true);
        assertTrue(reply.isAccepted());
    }

    @Test
    public void setAcceptedToFalse() {
        reply.setAccepted(true);
        reply.setAccepted(false);
        assertFalse(reply.isAccepted());
    }

    @Test
    public void setDatePostedUpdatesDate() {
        reply.setDatePosted("01/01/2025 12:00");
        assertEquals("01/01/2025 12:00", reply.getDatePosted());
    }

    // ===================== NESTED REPLIES =====================

    @Test
    public void addNestedReplyAddsToList() {
        Reply nested = new Reply(author, question, "Nested reply content.");
        reply.addReply(nested);
        assertTrue(reply.getReplies().contains(nested));
    }

    @Test
    public void addNestedReplyDoesNotAddNull() {
        reply.addReply(null);
        assertTrue(reply.getReplies().isEmpty());
    }
}