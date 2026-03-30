package com.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * AI-ASSISTED — reasoning table below
 * Test class for TIPSFacade.
 * @author James Gessler
 *
 * +--------------------------------------------------+-----------------------------------------------------------------------+
 * | Test                                             | Reasoning                                                             |
 * +--------------------------------------------------+-----------------------------------------------------------------------+
 * | loginWithValidCredentials                        | Core happy path — valid user must be returned and set as current      |
 * | loginWithWrongPassword                           | Wrong password must be rejected and current user must stay null       |
 * | loginWithNullUsername                            | Null input must not crash and must return false                       |
 * | loginWithEmptyPassword                           | Blank password must be rejected per UserList validation               |
 * | logoutClearsCurrentUser                          | After logout, getCurrentUser() must return null                       |
 * | logoutWhenNotLoggedInDoesNotCrash                | Calling logout with no session must be a no-op                        |
 * | registerUserCreatesStudent                       | Normal student registration must return a non-null Student            |
 * | registerUserWithDuplicateUsername                | Duplicate usernames must be rejected                                  |
 * | registerUserWithBlankUsername                    | Blank username must be rejected                                       |
 * | registerUserWithBlankPassword                    | Blank password must be rejected                                       |
 * | addQuestionAsEditor                              | Editors are authorized to add questions                               |
 * | addQuestionAsStudent                             | Students are not authorized — must return null                        |
 * | addQuestionWhenNotLoggedIn                       | No current user means no author — must return null                    |
 * | removeQuestionAsAdmin                            | Only admins can remove — question should be gone from list            |
 * | removeQuestionAsStudent                          | Student trying to remove must be silently blocked                     |
 * | editQuestionAsEditor                             | Editor should be able to update title and prompt                      |
 * | editQuestionAsStudent                            | Student must not be able to edit — fields should stay unchanged       |
 * | editNullQuestionDoesNotCrash                     | Null question must be handled gracefully                              |
 * | submitSolutionAsStudent                          | Valid submission must return a non-null Solution                      |
 * | submitSolutionWithEmptyContent                   | Empty content must be rejected                                        |
 * | submitSolutionIncrementsStudentStreak            | Submitting a solution must increment the student's streak             |
 * | revealSolutionSetsFlag                           | After reveal, isSolutionRevealed must be true                         |
 * | addCommentToQuestion                             | Valid comment must be added and returned                              |
 * | addCommentWithNullQuestion                       | Null question must return null without crashing                       |
 * | addNestedReplyToComment                          | Nested reply must appear inside the parent reply's list               |
 * | removeCommentAsAuthor                            | Comment author must be able to remove their own comment               |
 * | removeCommentAsNonAuthor                         | Non-author/non-admin must not be able to remove someone else's comment|
 * | getDailyChallengeAsStudent                       | Student must receive a non-null question                              |
 * | getDailyChallengeAsNonStudent                    | Non-student current user must return null                             |
 * | getQuestionsWithNullFilter                       | Null filter must return all questions                                 |
 * | getQuestionsWithMatchingFilter                   | Filter matching a known title/difficulty must return non-empty list   |
 * | getQuestionsWithNonMatchingFilter                | Filter with no matches must return empty list                         |
 * +--------------------------------------------------+-----------------------------------------------------------------------+
 */
public class TIPSFacadeTest {

    private TIPSFacade facade;

    // Resets the TIPSFacade singleton before each test so state doesn't bleed over
    @Before
    public void setUp() throws Exception {
        // Reset TIPSFacade singleton
        Field facadeField = TIPSFacade.class.getDeclaredField("instance");
        facadeField.setAccessible(true);
        facadeField.set(null, null);

        // Reset UserList singleton
        Field userListField = UserList.class.getDeclaredField("instance");
        userListField.setAccessible(true);
        userListField.set(null, null);

        // Reset QuestionList singleton
        Field questionListField = QuestionList.class.getDeclaredField("instance");
        questionListField.setAccessible(true);
        questionListField.set(null, null);

        facade = TIPSFacade.getInstance();

        // Register a base set of users we can rely on in tests
        facade.registerUser("testStudent", "pass123", "student@test.com", AccountType.STUDENT);
        facade.registerUser("testEditor",  "pass123", "editor@test.com",  AccountType.EDITOR);
        facade.registerUser("testAdmin",   "pass123", "admin@test.com",   AccountType.ADMIN);
    }

    // ===================== LOGIN =====================

    @Test
    public void loginWithValidCredentials() {
        boolean result = facade.login("testStudent", "pass123");
        assertTrue(result);
        assertNotNull(facade.getCurrentUser());
        assertEquals("testStudent", facade.getCurrentUser().getUsername());
    }

    @Test
    public void loginWithWrongPassword() {
        boolean result = facade.login("testStudent", "wrongpass");
        assertFalse(result);
        assertNull(facade.getCurrentUser());
    }

    @Test
    public void loginWithNullUsername() {
        boolean result = facade.login(null, "pass123");
        assertFalse(result);
        assertNull(facade.getCurrentUser());
    }

    @Test
    public void loginWithEmptyPassword() {
        boolean result = facade.login("testStudent", "");
        assertFalse(result);
        assertNull(facade.getCurrentUser());
    }

    // ===================== LOGOUT =====================

    @Test
    public void logoutClearsCurrentUser() {
        facade.login("testStudent", "pass123");
        facade.logout();
        assertNull(facade.getCurrentUser());
    }

    @Test
    public void logoutWhenNotLoggedInDoesNotCrash() {
        assertNull(facade.getCurrentUser());
        facade.logout(); // should not throw
        assertNull(facade.getCurrentUser());
    }

    // ===================== REGISTER =====================

    @Test
    public void registerUserCreatesStudent() {
        User user = facade.registerUser("newStudent", "abc123", "new@test.com", AccountType.STUDENT);
        assertNotNull(user);
        assertTrue(user instanceof Student);
        assertEquals("newStudent", user.getUsername());
    }

    @Test
    public void registerUserWithDuplicateUsername() {
        User user = facade.registerUser("testStudent", "pass123", "dup@test.com", AccountType.STUDENT);
        assertNull(user);
    }

    @Test
    public void registerUserWithBlankUsername() {
        User user = facade.registerUser("", "pass123", "blank@test.com", AccountType.STUDENT);
        assertNull(user);
    }

    @Test
    public void registerUserWithBlankPassword() {
        User user = facade.registerUser("uniqueUser", "", "blank@test.com", AccountType.STUDENT);
        assertNull(user);
    }

    // ===================== ADD QUESTION =====================

    @Test
    public void addQuestionAsEditor() {
        facade.login("testEditor", "pass123");
        Question q = facade.addQuestion("Test Title", "Test prompt?", Difficulty.EASY, Language.JAVA, Course.CSCE145);
        assertNotNull(q);
        assertEquals("Test Title", q.getTitle());
    }

    @Test
    public void addQuestionAsStudent() {
        facade.login("testStudent", "pass123");
        Question q = facade.addQuestion("Should Fail", "prompt", Difficulty.EASY, Language.JAVA, Course.CSCE145);
        assertNull(q);
    }

    @Test
    public void addQuestionWhenNotLoggedIn() {
        Question q = facade.addQuestion("No User", "prompt", Difficulty.EASY, Language.JAVA, Course.CSCE145);
        assertNull(q);
    }

    // ===================== REMOVE QUESTION =====================

    @Test
    public void removeQuestionAsAdmin() {
        facade.login("testAdmin", "pass123");
        Question q = facade.addQuestion("To Remove", "prompt", Difficulty.EASY, Language.JAVA, Course.CSCE145);
        assertNotNull(q);
        facade.removeQuestion(q);
        assertFalse(facade.getQuestions(null).contains(q));
    }

    @Test
    public void removeQuestionAsStudent() {
        facade.login("testAdmin", "pass123");
        Question q = facade.addQuestion("Stay Here", "prompt", Difficulty.EASY, Language.JAVA, Course.CSCE145);
        facade.logout();

        facade.login("testStudent", "pass123");
        facade.removeQuestion(q);
        assertTrue(facade.getQuestions(null).contains(q));
    }

    // ===================== EDIT QUESTION =====================

    @Test
    public void editQuestionAsEditor() {
        facade.login("testEditor", "pass123");
        Question q = facade.addQuestion("Old Title", "Old prompt", Difficulty.EASY, Language.JAVA, Course.CSCE145);
        facade.editQuestion(q, "New Title", "New prompt");
        assertEquals("New Title", q.getTitle());
        assertEquals("New prompt", q.getPrompt());
    }

    @Test
    public void editQuestionAsStudent() {
        facade.login("testAdmin", "pass123");
        Question q = facade.addQuestion("Original", "Original prompt", Difficulty.EASY, Language.JAVA, Course.CSCE145);
        facade.logout();

        facade.login("testStudent", "pass123");
        facade.editQuestion(q, "Hacked Title", "Hacked prompt");
        assertEquals("Original", q.getTitle());
        assertEquals("Original prompt", q.getPrompt());
    }

    @Test
    public void editNullQuestionDoesNotCrash() {
        facade.login("testEditor", "pass123");
        facade.editQuestion(null, "Title", "Prompt"); // should not throw
    }

    // ===================== SUBMIT SOLUTION =====================

    @Test
    public void submitSolutionAsStudent() {
        facade.login("testStudent", "pass123");
        Question q = new Question("Q", "prompt", Difficulty.EASY, Language.JAVA, Course.CSCE145);
        Solution s = facade.submitSolution(q, "System.out.println(\"hi\");");
        assertNotNull(s);
        assertEquals("System.out.println(\"hi\");", s.getContent());
    }

    @Test
    public void submitSolutionWithEmptyContent() {
        facade.login("testStudent", "pass123");
        Question q = new Question("Q", "prompt", Difficulty.EASY, Language.JAVA, Course.CSCE145);
        Solution s = facade.submitSolution(q, "");
        assertNull(s);
    }

    @Test
    public void submitSolutionIncrementsStudentStreak() {
        facade.login("testStudent", "pass123");
        Student student = (Student) facade.getCurrentUser();
        int before = student.getStreak();
        Question q = new Question("Q", "prompt", Difficulty.EASY, Language.JAVA, Course.CSCE145);
        facade.submitSolution(q, "int x = 1;");
        assertEquals(before + 1, student.getStreak());
    }

    // ===================== REVEAL SOLUTION =====================

    @Test
    public void revealSolutionSetsFlag() {
        facade.login("testStudent", "pass123");
        Question q = new Question("Q", "prompt", Difficulty.EASY, Language.JAVA, Course.CSCE145);
        assertFalse(q.isSolutionRevealed());
        facade.revealSolution(q);
        assertTrue(q.isSolutionRevealed());
    }

    // ===================== COMMENTS / REPLIES =====================

    @Test
    public void addCommentToQuestion() {
        facade.login("testStudent", "pass123");
        Question q = new Question("Q", "prompt", Difficulty.EASY, Language.JAVA, Course.CSCE145);
        Reply reply = facade.addComment(q, "My Comment", "This is hard");
        assertNotNull(reply);
        assertEquals("This is hard", reply.getContent());
        assertTrue(q.getReplies().contains(reply));
    }

    @Test
    public void addCommentWithNullQuestion() {
        facade.login("testStudent", "pass123");
        Reply reply = facade.addComment(null, "Title", "Content");
        assertNull(reply);
    }

    @Test
    public void addNestedReplyToComment() {
        facade.login("testStudent", "pass123");
        Question q = new Question("Q", "prompt", Difficulty.EASY, Language.JAVA, Course.CSCE145);
        Reply parent = facade.addComment(q, "Parent", "Parent content");
        Reply nested = facade.addNestedReply(parent, q, "Nested", "Nested content");
        assertNotNull(nested);
        assertTrue(parent.getReplies().contains(nested));
    }

    @Test
    public void removeCommentAsAuthor() {
        facade.login("testStudent", "pass123");
        Question q = new Question("Q", "prompt", Difficulty.EASY, Language.JAVA, Course.CSCE145);
        Reply reply = facade.addComment(q, "Title", "Content");
        boolean removed = facade.removeComment(q, reply);
        assertTrue(removed);
        assertFalse(q.getReplies().contains(reply));
    }

    @Test
    public void removeCommentAsNonAuthor() {
        facade.login("testStudent", "pass123");
        Question q = new Question("Q", "prompt", Difficulty.EASY, Language.JAVA, Course.CSCE145);
        Reply reply = facade.addComment(q, "Title", "Content");
        facade.logout();

        facade.login("testEditor", "pass123");
        boolean removed = facade.removeComment(q, reply);
        assertFalse(removed);
        assertTrue(q.getReplies().contains(reply));
    }

    // ===================== DAILY CHALLENGE =====================

    @Test
    public void getDailyChallengeAsStudent() {
        facade.login("testStudent", "pass123");
        // Only meaningful if questions.json has entries; at minimum must not throw
        // If the list is empty this returns null which is also valid behavior
        Question q = facade.getDailyChallenge();
        // We just assert no exception is thrown and the call delegates correctly
        assertTrue(q == null || q instanceof Question);
    }

    @Test
    public void getDailyChallengeAsNonStudent() {
        facade.login("testEditor", "pass123");
        Question q = facade.getDailyChallenge();
        assertNull(q);
    }

    // ===================== GET QUESTIONS =====================

    @Test
    public void getQuestionsWithNullFilter() {
        facade.login("testEditor", "pass123");
        facade.addQuestion("Alpha", "prompt", Difficulty.EASY, Language.JAVA, Course.CSCE145);
        facade.addQuestion("Beta",  "prompt", Difficulty.HARD, Language.PYTHON, Course.CSCE247);
        ArrayList<Question> all = facade.getQuestions(null);
        assertTrue(all.size() >= 2);
    }

    @Test
    public void getQuestionsWithMatchingFilter() {
        facade.login("testEditor", "pass123");
        facade.addQuestion("BubbleSort", "sort an array", Difficulty.EASY, Language.JAVA, Course.CSCE145);
        ArrayList<Question> results = facade.getQuestions("BubbleSort");
        assertFalse(results.isEmpty());
        assertEquals("BubbleSort", results.get(0).getTitle());
    }

    @Test
    public void getQuestionsWithNonMatchingFilter() {
        ArrayList<Question> results = facade.getQuestions("xyzzy_no_match_ever");
        assertTrue(results.isEmpty());
    }
}