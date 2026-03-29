package com.model;

import org.junit.Before;
import org.junit.Test;
import java.lang.reflect.Field;
import java.util.ArrayList;
import static org.junit.Assert.*;

/**
 * Test class for QuestionList.
 * @author Neel Patel
 * 
 * +----------------------------------------------------------+----------------------------------------------------------------------+
 * | Test                                                     | Reasoning                                                            |
 * +----------------------------------------------------------+----------------------------------------------------------------------+
 * | addQuestion_editorSuccess_storesQuestion                 | Happy path — valid editor input should create and store a question   |
 * | addQuestion_emptyTitle_shouldBeRejected                  | Titles are required; empty title must be rejected                    |
 * | addQuestion_emptyPrompt_shouldBeRejected                 | Prompt is essential; empty prompt must not be accepted               |
 * | addQuestion_studentDenied                                | Students do not have permission to create questions                  |
 * | addQuestion_nullAuthorDenied                             | Author is required for accountability and ownership                  |
 * | removeQuestion_adminRemovesQuestion                      | Admins have authority to delete questions                            |
 * | removeQuestion_nonAdminDenied                            | Non-admin users should not be able to remove questions               |
 * | getQuestions_filtersByDifficultyAndLanguage              | Filtering must correctly return only matching questions              |
 * | getQuestions_blankFilter_returnsAllQuestions             | a blank filter should behave like no filter and return all questions |
 * | getQuestion_foundAndMissing                              | Should return existing question and handle missing/null safely       |
 * | getDailyChallenge_nullStudent_shouldNotCrash             | Null safety — method should not throw exceptions                     |
 * | getDailyChallenge_juniorShouldGetHardQuestion            | Business logic — juniors should receive harder challenges            |
 * | submitSolution_nullQuestion_returnsNull                  | Cannot submit a solution to a null question                          |
 * | addComment_nullQuestion_returnsNull                      | Cannot comment on a null question                                    |
 * | addNestedReply_blankContent_shouldBeRejected             | Replies must contain meaningful content                              |
 * | removeComment_authorCanRemove                            | Authors should be able to delete their own comments                  |
 * +----------------------------------------------------------+----------------------------------------------------------------------+
 */

public class QuestionListTest {

    private QuestionList questionList;
    private Admin admin;
    private Editor editor;
    private Student student;

    @Before
    public void setup() throws Exception {
        resetSingleton(QuestionList.class, "instance");
        resetSingleton(UserList.class, "instance");

        questionList = QuestionList.getInstance();
        admin = new Admin(1, "adminUser", "pass", "admin@test.com");
        editor = new Editor(2, "editorUser", "pass", "editor@test.com");
        student = new Student(3, "studentUser", "pass", "student@test.com");
    }

    private void resetSingleton(Class<?> clazz, String fieldName) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, null);
    }

    private Question createQuestion(String title, Difficulty difficulty) {
        return questionList.addQuestion(title, "Prompt",
                difficulty, Language.JAVA, Course.CSCE145, editor);
    }

    // ===================== addQuestion =====================

    @Test
    public void addQuestion_editorSuccess_storesQuestion() {
        int before = questionList.getAllQuestions().size();

        Question q = createQuestion("Valid Question", Difficulty.EASY);

        assertNotNull(q);
        assertEquals(before + 1, questionList.getAllQuestions().size());
        assertEquals(editor, q.getAuthor());
    }

    @Test
    public void addQuestion_emptyTitle_shouldBeRejected() {
        int before = questionList.getAllQuestions().size();

        Question q = questionList.addQuestion("", "Prompt",
                Difficulty.EASY, Language.JAVA, Course.CSCE145, editor);

        assertNull("Empty titles should not be accepted", q);
        assertEquals("Empty titles should not add a question", before,
                questionList.getAllQuestions().size());
    }

    @Test
    public void addQuestion_emptyPrompt_shouldBeRejected() {
        int before = questionList.getAllQuestions().size();

        Question q = questionList.addQuestion("Title", "",
                Difficulty.EASY, Language.JAVA, Course.CSCE145, editor);

        assertNull("Empty prompts should not be accepted", q);
        assertEquals("Empty prompts should not add a question", before,
                questionList.getAllQuestions().size());
    }

    @Test
    public void addQuestion_studentDenied() {
        Question q = questionList.addQuestion("Bad", "Prompt",
                Difficulty.EASY, Language.JAVA, Course.CSCE145, student);

        assertNull(q);
    }

    @Test
    public void addQuestion_nullAuthorDenied() {
        Question q = questionList.addQuestion("Bad", "Prompt",
                Difficulty.EASY, Language.JAVA, Course.CSCE145, null);

        assertNull(q);
    }

    // ===================== removeQuestion =====================

    @Test
    public void removeQuestion_adminRemovesQuestion() {
        Question q = createQuestion("Remove Me", Difficulty.EASY);

        assertTrue(questionList.removeQuestion(q, admin));
        assertFalse(questionList.getAllQuestions().contains(q));
    }

    @Test
    public void removeQuestion_nonAdminDenied() {
        Question q = createQuestion("Stay Put", Difficulty.EASY);

        assertFalse(questionList.removeQuestion(q, editor));
        assertTrue(questionList.getAllQuestions().contains(q));
    }

    // ===================== getQuestions =====================

    @Test
    public void getQuestions_filtersByDifficultyAndLanguage() {
        questionList.addQuestion("Easy Java", "Prompt",
                Difficulty.EASY, Language.JAVA, Course.CSCE145, editor);
        questionList.addQuestion("Python Hard", "Prompt",
                Difficulty.HARD, Language.PYTHON, Course.CSCE145, editor);

        ArrayList<Question> easyResults = questionList.getQuestions("EASY");
        ArrayList<Question> pythonResults = questionList.getQuestions("PYTHON");

        assertFalse(easyResults.isEmpty());
        for (Question q : easyResults) {
            assertEquals(Difficulty.EASY, q.getDifficulty());
        }

        assertFalse(pythonResults.isEmpty());
        for (Question q : pythonResults) {
            assertEquals(Language.PYTHON, q.getLanguage());
        }
    }

    @Test
    public void getQuestions_blankFilter_returnsAllQuestions() {
        createQuestion("Q1", Difficulty.EASY);
        createQuestion("Q2", Difficulty.HARD);

        int total = questionList.getAllQuestions().size();

        ArrayList<Question> results = questionList.getQuestions("   ");

        assertEquals(total, results.size());
    }

    @Test
    public void getQuestion_foundAndMissing() {
        questionList.addQuestion("Unique Title", "Unique Prompt",
                Difficulty.EASY, Language.JAVA, Course.CSCE145, editor);

        assertNotNull(questionList.getQuestion("Unique Title"));
        assertNull(questionList.getQuestion("does-not-exist"));
        assertNull(questionList.getQuestion(null));
    }

    // ===================== getDailyChallenge =====================

    @Test
    public void getDailyChallenge_nullStudent_shouldNotCrash() {
        try {
            Question q = questionList.getDailyChallenge(null);
            assertNull("Null student should be handled safely", q);
        } catch (Exception e) {
            fail("getDailyChallenge(null) should not throw, but threw: " + e);
        }
    }

    @Test
    public void getDailyChallenge_juniorShouldGetHardQuestion() {
        student.setClassification("Junior");
        createQuestion("Hard One", Difficulty.HARD);

        Question q = questionList.getDailyChallenge(student);

        assertNotNull(q);
        assertEquals(Difficulty.HARD, q.getDifficulty());
    }

    // ===================== submitSolution =====================

    @Test
    public void submitSolution_nullQuestion_returnsNull() {
        assertNull(questionList.submitSolution(null, student, "code"));
    }

    // ===================== addComment =====================

    @Test
    public void addComment_nullQuestion_returnsNull() {
        assertNull(questionList.addComment(null, student, "Title", "Content"));
    }

    // ===================== addNestedReply =====================

    @Test
    public void addNestedReply_blankContent_shouldBeRejected() {
        Question q = createQuestion("Comment Test", Difficulty.EASY);
        Reply parent = questionList.addComment(q, student, "Parent", "Parent content");
        int before = parent.getReplies().size();

        Reply nested = questionList.addNestedReply(q, parent, admin, "Re", "   ");

        assertNull("Whitespace-only nested replies should be rejected", nested);
        assertEquals("Whitespace-only nested replies should not be added",
                before, parent.getReplies().size());
    }

    // ===================== removeComment =====================

    @Test
    public void removeComment_authorCanRemove() {
        Question q = createQuestion("Remove Comment", Difficulty.EASY);
        Reply r = questionList.addComment(q, student, "Title", "Content");

        assertTrue(questionList.removeComment(q, r, student));
    }
}