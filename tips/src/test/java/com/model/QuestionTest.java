package com.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Test class for Question.
 * @author Neel Patel
 *
 * +----------------------------------------------------------+----------------------------------------------------------------------+
 * | Test                                                     | Reasoning                                                            |
 * +----------------------------------------------------------+----------------------------------------------------------------------+
 * | testSubmitSolutionValid                                  | Core happy path — a valid submission must be stored on the question  |
 * | testSubmitSolutionNullAuthorFails                        | A solution with no author is invalid and must be rejected            |
 * | testSubmitSolutionEmptyContentFails                      | Whitespace-only content is not a real submission                     |
 * | testSubmitSolutionByStudentIncrementsStreak              | Submitting is the action that earns a streak point for students      |
 * | testAddCommentValid                                      | Core happy path for posting a comment to a question                  |
 * | testAddCommentNullAuthorFails                            | Anonymous comments are not allowed                                   |
 * | testAddCommentEmptyContentFails                          | Blank content is still no content and must be rejected               |
 * | testRemoveCommentByAuthorSucceeds                        | Authors always have the right to remove their own comments           |
 * | testRemoveCommentByAdminSucceeds                         | Admins have moderation authority over any comment                    |
 * | testRemoveCommentByOtherStudentFails                     | A student cannot delete another student's comment                    |
 * | testRemoveCommentNullCommentFails                        | Null comment input must be handled gracefully without a crash        |
 * | testAddNestedReplyStoredUnderParent                      | Nested reply must live under the parent, not at top level            |
 * | testAddNestedReplyNullParentFails                        | Cannot reply to a comment that does not exist                        |
 * | testIsCompleteBeforeRevealIsFalse                        | A question is not complete until the solution is revealed            |
 * | testRevealSolutionSetsComplete                           | Revealing is a one-way action; isComplete must reflect it            |
 * | testResetClearsSolutionsAndUnreveals                     | Reset must wipe submissions and hide the solution again              |
 * | testAddSolutionNullDoesNotAdd                            | Null guard must protect the list from bad DataLoader input           |
 * | testAddReplyNullDoesNotAdd                               | Null guard must protect the replies list                             |
 * +----------------------------------------------------------+----------------------------------------------------------------------+
 */
public class QuestionTest {

    // ===================== submitSolution =====================

    @Test
    public void testSubmitSolutionValid() {
        Question question = new Question("Two Sum", "Find two numbers.",
                Difficulty.EASY, Language.JAVA, Course.CSCE247);
        Student student = new Student(1, "alice", "pass", "alice@email.com");
        Solution s = question.submitSolution(student, "return new int[]{0,1};");
        assertNotNull(s);
        assertEquals(1, question.getSolutions().size());
    }

    @Test
    public void testSubmitSolutionNullAuthorFails() {
        Question question = new Question("Two Sum", "Find two numbers.",
                Difficulty.EASY, Language.JAVA, Course.CSCE247);
        Solution s = question.submitSolution(null, "some code");
        assertNull(s);
    }

    @Test
    public void testSubmitSolutionEmptyContentFails() {
        Question question = new Question("Two Sum", "Find two numbers.",
                Difficulty.EASY, Language.JAVA, Course.CSCE247);
        Student student = new Student(1, "alice", "pass", "alice@email.com");
        Solution s = question.submitSolution(student, "   ");
        assertNull(s);
        assertEquals(0, question.getSolutions().size());
    }

    @Test
    public void testSubmitSolutionByStudentIncrementsStreak() {
        Question question = new Question("Two Sum", "Find two numbers.",
                Difficulty.EASY, Language.JAVA, Course.CSCE247);
        Student student = new Student(1, "alice", "pass", "alice@email.com");
        int before = student.getStreak();
        question.submitSolution(student, "valid code");
        assertEquals(before + 1, student.getStreak());
    }

    // ===================== addComment =====================

    @Test
    public void testAddCommentValid() {
        Question question = new Question("Two Sum", "Find two numbers.",
                Difficulty.EASY, Language.JAVA, Course.CSCE247);
        Student student = new Student(1, "alice", "pass", "alice@email.com");
        Reply r = question.addComment(student, "My Question", "How does this work?");
        assertNotNull(r);
        assertEquals(1, question.getReplies().size());
    }

    @Test
    public void testAddCommentNullAuthorFails() {
        Question question = new Question("Two Sum", "Find two numbers.",
                Difficulty.EASY, Language.JAVA, Course.CSCE247);
        Reply r = question.addComment(null, "Title", "Content");
        assertNull(r);
    }

    @Test
    public void testAddCommentEmptyContentFails() {
        Question question = new Question("Two Sum", "Find two numbers.",
                Difficulty.EASY, Language.JAVA, Course.CSCE247);
        Student student = new Student(1, "alice", "pass", "alice@email.com");
        Reply r = question.addComment(student, "Title", "   ");
        assertNull(r);
    }

    // ===================== removeComment =====================

    @Test
    public void testRemoveCommentByAuthorSucceeds() {
        Question question = new Question("Two Sum", "Find two numbers.",
                Difficulty.EASY, Language.JAVA, Course.CSCE247);
        Student student = new Student(1, "alice", "pass", "alice@email.com");
        Reply comment = question.addComment(student, "Q", "Content");
        boolean removed = question.removeComment(comment, student);
        assertTrue(removed);
        assertEquals(0, question.getReplies().size());
    }

    @Test
    public void testRemoveCommentByAdminSucceeds() {
        Question question = new Question("Two Sum", "Find two numbers.",
                Difficulty.EASY, Language.JAVA, Course.CSCE247);
        Student student = new Student(1, "alice", "pass", "alice@email.com");
        Admin admin = new Admin(2, "tommy", "done", "tommy@email.com");
        Reply comment = question.addComment(student, "Q", "Content");
        boolean removed = question.removeComment(comment, admin);
        assertTrue(removed);
    }

    @Test
    public void testRemoveCommentByOtherStudentFails() {
        Question question = new Question("Two Sum", "Find two numbers.",
                Difficulty.EASY, Language.JAVA, Course.CSCE247);
        Student student = new Student(1, "alice", "pass", "alice@email.com");
        Student other   = new Student(3, "bob",   "pass", "bob@email.com");
        Reply comment = question.addComment(student, "Q", "Content");
        boolean removed = question.removeComment(comment, other);
        assertFalse(removed);
        assertEquals(1, question.getReplies().size());
    }

    @Test
    public void testRemoveCommentNullCommentFails() {
        Question question = new Question("Two Sum", "Find two numbers.",
                Difficulty.EASY, Language.JAVA, Course.CSCE247);
        Student student = new Student(1, "alice", "pass", "alice@email.com");
        boolean removed = question.removeComment(null, student);
        assertFalse(removed);
    }

    // ===================== addNestedReply =====================

    @Test
    public void testAddNestedReplyStoredUnderParent() {
        Question question = new Question("Two Sum", "Find two numbers.",
                Difficulty.EASY, Language.JAVA, Course.CSCE247);
        Student student = new Student(1, "alice", "pass", "alice@email.com");
        Student other   = new Student(3, "bob",   "pass", "bob@email.com");
        Reply parent = question.addComment(student, "Parent", "Content");
        Reply nested = question.addNestedReply(parent, other, "Re", "I agree!");
        assertNotNull(nested);
        assertEquals(1, parent.getReplies().size());
        assertEquals(1, question.getReplies().size());
    }

    @Test
    public void testAddNestedReplyNullParentFails() {
        Question question = new Question("Two Sum", "Find two numbers.",
                Difficulty.EASY, Language.JAVA, Course.CSCE247);
        Student student = new Student(1, "alice", "pass", "alice@email.com");
        Reply nested = question.addNestedReply(null, student, "Title", "Content");
        assertNull(nested);
    }

    // ===================== revealSolution / isComplete / reset =====================

    @Test
    public void testIsCompleteBeforeRevealIsFalse() {
        Question question = new Question("Two Sum", "Find two numbers.",
                Difficulty.EASY, Language.JAVA, Course.CSCE247);
        assertFalse(question.isComplete());
    }

    @Test
    public void testRevealSolutionSetsComplete() {
        Question question = new Question("Two Sum", "Find two numbers.",
                Difficulty.EASY, Language.JAVA, Course.CSCE247);
        question.revealSolution();
        assertTrue(question.isSolutionRevealed());
        assertTrue(question.isComplete());
    }

    @Test
    public void testResetClearsSolutionsAndUnreveals() {
        Question question = new Question("Two Sum", "Find two numbers.",
                Difficulty.EASY, Language.JAVA, Course.CSCE247);
        Student student = new Student(1, "alice", "pass", "alice@email.com");
        question.submitSolution(student, "some code");
        question.revealSolution();
        question.reset();
        assertEquals(0, question.getSolutions().size());
        assertFalse(question.isSolutionRevealed());
    }

    // ===================== null guards =====================

    @Test
    public void testAddSolutionNullDoesNotAdd() {
        Question question = new Question("Two Sum", "Find two numbers.",
                Difficulty.EASY, Language.JAVA, Course.CSCE247);
        question.addSolution(null);
        assertEquals(0, question.getSolutions().size());
    }

    @Test
    public void testAddReplyNullDoesNotAdd() {
        Question question = new Question("Two Sum", "Find two numbers.",
                Difficulty.EASY, Language.JAVA, Course.CSCE247);
        question.addReply(null);
        assertEquals(0, question.getReplies().size());
    }
}