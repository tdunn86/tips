package com.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * AI-ASSISTED — reasoning table below
 * 
 * Test class for Solution.
 * @author James Gessler
 * 
 *
 * +--------------------------------------------------+-----------------------------------------------------------------------+
 * | Test                                             | Reasoning                                                             |
 * +--------------------------------------------------+-----------------------------------------------------------------------+
 * | defaultUpvotesIsZero                             | New solution should start with 0 upvotes                              |
 * | defaultIsAcceptedIsFalse                         | New solution should not be accepted by default                        |
 * | upvoteIncreasesByOne                             | Each upvote call should increment count by exactly 1                  |
 * | upvoteMultipleTimes                              | Multiple upvotes should accumulate correctly                          |
 * | editUpdatesContent                               | edit() should replace the content with the new value                  |
 * | deleteReplacesContentWithPlaceholder             | delete() should set content to "[deleted]"                            |
 * | deleteUnsetsAccepted                             | delete() should also set isAccepted to false                          |
 * | setAcceptedToTrue                                | setAccepted(true) should mark solution as accepted                    |
 * | setAcceptedToFalse                               | setAccepted(false) should unmark an accepted solution                 |
 * | addCommentAddsToList                             | A valid reply should appear in comments list after adding             |
 * | addCommentDoesNotAddNull                         | Null reply must be silently ignored                                   |
 * | getAuthorReturnsCorrectAuthor                    | Author set at construction must be retrievable                        |
 * | getQuestionReturnsCorrectQuestion                | Question set at construction must be retrievable                      |
 * | getContentReturnsInitialContent                  | Content set at construction must be retrievable                       |
 * +--------------------------------------------------+-----------------------------------------------------------------------+
 */
public class SolutionTest {

    private Student author;
    private Question question;
    private Solution solution;

    @Before
    public void setUp() {
        author   = new Student(1, "testStudent", "pass123", "student@test.com");
        question = new Question("Test Q", "prompt", Difficulty.EASY, Language.JAVA, Course.CSCE145);
        solution = new Solution(author, question, "int x = 1;");
    }

    // ===================== DEFAULTS =====================

    @Test
    public void defaultUpvotesIsZero() {
        assertEquals(0, solution.getUpvotes());
    }

    @Test
    public void defaultIsAcceptedIsFalse() {
        assertFalse(solution.isAccepted());
    }

    @Test
    public void getContentReturnsInitialContent() {
        assertEquals("int x = 1;", solution.getContent());
    }

    @Test
    public void getAuthorReturnsCorrectAuthor() {
        assertEquals(author, solution.getAuthor());
    }

    @Test
    public void getQuestionReturnsCorrectQuestion() {
        assertEquals(question, solution.getQuestion());
    }

    // ===================== UPVOTE =====================

    @Test
    public void upvoteIncreasesByOne() {
        solution.upvote();
        assertEquals(1, solution.getUpvotes());
    }

    @Test
    public void upvoteMultipleTimes() {
        solution.upvote();
        solution.upvote();
        solution.upvote();
        assertEquals(3, solution.getUpvotes());
    }

    // ===================== EDIT =====================

    @Test
    public void editUpdatesContent() {
        solution.edit("int y = 2;");
        assertEquals("int y = 2;", solution.getContent());
    }

    // ===================== DELETE =====================

    @Test
    public void deleteReplacesContentWithPlaceholder() {
        solution.delete();
        assertEquals("[deleted]", solution.getContent());
    }

    @Test
    public void deleteUnsetsAccepted() {
        solution.setAccepted(true);
        solution.delete();
        assertFalse(solution.isAccepted());
    }

    // ===================== ACCEPTED =====================

    @Test
    public void setAcceptedToTrue() {
        solution.setAccepted(true);
        assertTrue(solution.isAccepted());
    }

    @Test
    public void setAcceptedToFalse() {
        solution.setAccepted(true);
        solution.setAccepted(false);
        assertFalse(solution.isAccepted());
    }

    // ===================== COMMENTS =====================

    @Test
    public void addCommentAddsToList() {
        Reply reply = new Reply(author, question, "Nice solution!");
        solution.addComment(reply);
        assertTrue(solution.getComments().contains(reply));
    }

    @Test
    public void addCommentDoesNotAddNull() {
        solution.addComment(null);
        assertTrue(solution.getComments().isEmpty());
    }
}