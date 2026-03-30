package com.model;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for QuestionList.
 * @author Thomas Dunn
 * 
 * +--------------------------------------------------+-----------------------------------------------------------------------+
 * | Test                                             | Reasoning                                                             |
 * +--------------------------------------------------+-----------------------------------------------------------------------+
 * | testAddQuestionAsEditor                          | Editors are authorized to add questions, should return a Question     |
 * | testAddQuestionAsAdmin                           | Admins are authorized to add questions, should return a Question      |
 * | testAddQuestionAsStudentFails                    | Students are not authorized to add questions, should return null      |
 * | testAddQuestionNullAuthorFails                   | A null author has no permissions, should return null                  |
 * | testRemoveQuestionAsAdmin                        | Only Admins can remove questions, should return true                  |
 * | testRemoveQuestionAsEditorFails                  | Editors cannot remove questions, should return false                  |
 * | testRemoveQuestionAsStudentFails                 | Students cannot remove questions, should return false                 |
 * | testGetQuestionsNullFilterReturnsAll             | A null filter means no filtering, should return full list             |
 * | testGetQuestionsBlankFilterReturnsAll            | A blank filter means no filtering, should return full list            |
 * | testGetQuestionsByDifficulty                     | Filter should match questions by difficulty enum name                 |
 * | testGetQuestionsByLanguage                       | Filter should match questions by language enum name                   |
 * | testGetQuestionsNoMatchReturnsEmpty              | A nonsense filter should match nothing, return empty list             |
 * | testGetQuestionByKeywordFound                    | A matching keyword should return the correct question                 |
 * | testGetQuestionByKeywordNotFound                 | A non-matching keyword should return null                             |
 * | testGetQuestionNullKeyword                       | A null keyword has nothing to search, should return null              |
 * | testGetDailyChallengeReturnsQuestion             | Should return a Question or null without crashing                     |
 * | testGetDailyChallengeMatchesDifficulty           | Junior classification maps to HARD, result should reflect that        |
 * | testSubmitSolutionValid                          | A valid question and author should produce a non-null Solution        |
 * | testSubmitSolutionNullQuestionFails              | Cannot submit to a null question, should return null                  |
 * | testAddCommentValid                              | A valid question and author should produce a non-null Reply           |
 * | testAddCommentNullQuestionFails                  | Cannot comment on a null question, should return null                 |
 * | testRemoveCommentValid                           | The author should be able to remove their own comment                 |
 * | testRemoveCommentNullQuestionFails               | Cannot remove a comment from a null question, should return false     |
 * +--------------------------------------------------+-----------------------------------------------------------------------+
 */

public class UserListTest {
    private QuestionList questionList;
    private Admin testAdmin;
    private Editor testEditor;
    private Student testStudent;

    @Before
    public void setup() {
        questionList = QuestionList.getInstance();
        testAdmin = new Admin(9999, "testAdmin", "adminPass", "admin@test.com");
        testEditor = new Editor(9998, "testEditor", "editorPass", "editor@test.com");
        testStudent = new Student(9997, "testStudent", "studentPass", "student@test.com");
    }

    // ===================== addQuestion =====================

    @Test
    public void testAddQuestionAsEditor() {
        Question q = questionList.addQuestion("Test Title", "Test Prompt",
                Difficulty.EASY, Language.JAVA, Course.CSCE145, testEditor);
        assertNotNull(q);
    }

    @Test
    public void testAddQuestionAsAdmin() {
        Question q = questionList.addQuestion("Admin Question", "Some Prompt",
                Difficulty.MEDIUM, Language.PYTHON, Course.CSCE146, testAdmin);
        assertNotNull(q);
    }

    @Test
    public void testAddQuestionAsStudentFails() {
        Question q = questionList.addQuestion("Student Question", "Some Prompt",
                Difficulty.EASY, Language.JAVA, Course.CSCE145, testStudent);
        assertNull(q);
    }

    @Test
    public void testAddQuestionNullAuthorFails() {
        Question q = questionList.addQuestion("No Author", "Some Prompt",
                Difficulty.EASY, Language.JAVA, Course.CSCE145, null);
        assertNull(q);
    }

    // ===================== removeQuestion =====================

    @Test
    public void testRemoveQuestionAsAdmin() {
        Question q = questionList.addQuestion("To Be Removed", "Prompt",
                Difficulty.EASY, Language.JAVA, Course.CSCE145, testAdmin);
        boolean removed = questionList.removeQuestion(q, testAdmin);
        assertTrue(removed);
    }

    @Test
    public void testRemoveQuestionAsEditorFails() {
        Question q = questionList.addQuestion("Editor Can't Remove", "Prompt",
                Difficulty.EASY, Language.JAVA, Course.CSCE145, testAdmin);
        boolean removed = questionList.removeQuestion(q, testEditor);
        assertFalse(removed);
    }

    @Test
    public void testRemoveQuestionAsStudentFails() {
        Question q = questionList.addQuestion("Student Can't Remove", "Prompt",
                Difficulty.EASY, Language.JAVA, Course.CSCE145, testAdmin);
        boolean removed = questionList.removeQuestion(q, testStudent);
        assertFalse(removed);
    }

    // ===================== getQuestions (filter) =====================

    @Test
    public void testGetQuestionsNullFilterReturnsAll() {
        ArrayList<Question> results = questionList.getQuestions(null);
        assertEquals(questionList.getAllQuestions().size(), results.size());
    }

    @Test
    public void testGetQuestionsBlankFilterReturnsAll() {
        ArrayList<Question> results = questionList.getQuestions("   ");
        assertEquals(questionList.getAllQuestions().size(), results.size());
    }

    @Test
    public void testGetQuestionsByDifficulty() {
        questionList.addQuestion("Easy Q", "Prompt",
                Difficulty.EASY, Language.JAVA, Course.CSCE145, testEditor);
        ArrayList<Question> results = questionList.getQuestions("EASY");
        assertFalse(results.isEmpty());
        for (Question q : results) {
            assertEquals(Difficulty.EASY, q.getDifficulty());
        }
    }

    @Test
    public void testGetQuestionsByLanguage() {
        questionList.addQuestion("Python Q", "Prompt",
                Difficulty.EASY, Language.PYTHON, Course.CSCE145, testEditor);
        ArrayList<Question> results = questionList.getQuestions("PYTHON");
        assertFalse(results.isEmpty());
    }

    @Test
    public void testGetQuestionsNoMatchReturnsEmpty() {
        ArrayList<Question> results = questionList.getQuestions("xyznonexistent");
        assertTrue(results.isEmpty());
    }

    // ===================== getQuestion =====================

    @Test
    public void testGetQuestionByKeywordFound() {
        questionList.addQuestion("Unique Keyword Title", "Prompt",
                Difficulty.EASY, Language.JAVA, Course.CSCE145, testEditor);
        Question q = questionList.getQuestion("Unique Keyword Title");
        assertNotNull(q);
    }

    @Test
    public void testGetQuestionByKeywordNotFound() {
        Question q = questionList.getQuestion("xyznonexistent");
        assertNull(q);
    }

    @Test
    public void testGetQuestionNullKeyword() {
        Question q = questionList.getQuestion(null);
        assertNull(q);
    }

    // ===================== getDailyChallenge =====================

    @Test
    public void testGetDailyChallengeReturnsQuestion() {
        testStudent.setClassification("freshman");
        Question q = questionList.getDailyChallenge(testStudent);
        // may be null if student has solved everything, but shouldn't crash
        // just verify no exception is thrown and result is Question or null
        assertTrue(q == null || q instanceof Question);
    }

    @Test
    public void testGetDailyChallengeMatchesDifficulty() {
        testStudent.setClassification("junior"); // should map to HARD
        Question q = questionList.getDailyChallenge(testStudent);
        if (q != null) {
            assertEquals(Difficulty.HARD, q.getDifficulty());
        }
    }

    // ===================== submitSolution =====================

    @Test
    public void testSubmitSolutionValid() {
        Question q = questionList.addQuestion("Solution Test", "Prompt",
                Difficulty.EASY, Language.JAVA, Course.CSCE145, testEditor);
        Solution s = questionList.submitSolution(q, testStudent, "System.out.println('hi');");
        assertNotNull(s);
    }

    @Test
    public void testSubmitSolutionNullQuestionFails() {
        Solution s = questionList.submitSolution(null, testStudent, "some code");
        assertNull(s);
    }

    // ===================== addComment / removeComment =====================

    @Test
    public void testAddCommentValid() {
        Question q = questionList.addQuestion("Comment Test", "Prompt",
                Difficulty.EASY, Language.JAVA, Course.CSCE145, testEditor);
        Reply r = questionList.addComment(q, testStudent, "My Title", "My comment");
        assertNotNull(r);
    }

    @Test
    public void testAddCommentNullQuestionFails() {
        Reply r = questionList.addComment(null, testStudent, "Title", "Content");
        assertNull(r);
    }

    @Test
    public void testRemoveCommentValid() {
        Question q = questionList.addQuestion("Remove Comment Test", "Prompt",
                Difficulty.EASY, Language.JAVA, Course.CSCE145, testEditor);
        Reply r = questionList.addComment(q, testStudent, "Title", "Content");
        boolean removed = questionList.removeComment(q, r, testStudent);
        assertTrue(removed);
    }

    @Test
    public void testRemoveCommentNullQuestionFails() {
        Reply r = new Reply(testStudent, null, "Content");
        boolean removed = questionList.removeComment(null, r, testStudent);
        assertFalse(removed);
    }
}