package com.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;

/**
 * AI-ASSISTED — reasoning table below
 * 
 * Test class for Student.
 * @author James Gessler
 * 
 * 
 *
 * +--------------------------------------------------+-----------------------------------------------------------------------+
 * | Test                                             | Reasoning                                                             |
 * +--------------------------------------------------+-----------------------------------------------------------------------+
 * | startStreakSetsStreakToOne                        | If streak is 0, startStreak should set it to 1                        |
 * | startStreakDoesNotOverwriteExistingStreak         | If streak is already > 0, startStreak should leave it alone           |
 * | incrementStreakIncreasesByOne                     | Each submission should bump streak by exactly 1                       |
 * | incrementStreakMultipleTimes                      | Multiple increments should accumulate correctly                       |
 * | addFavQuestionAddsToList                         | A valid question should appear in favQuestions after adding           |
 * | addFavQuestionDoesNotAddNull                     | Null question must be silently ignored                                |
 * | addFavQuestionDoesNotAddDuplicate                | Same question added twice should only appear once                     |
 * | removeFavQuestionRemovesFromList                 | A question that was added should be removable                         |
 * | removeFavQuestionNotInListDoesNotCrash           | Removing a question that was never added should be a no-op            |
 * | defaultClassificationIsFreshman                 | Newly created student should default to "Freshman"                    |
 * | setClassificationUpdatesValue                    | setClassification should change the stored value                      |
 * | defaultStreakIsZero                              | Newly created student should start with streak of 0                   |
 * | accountTypeIsStudent                             | Student must always have STUDENT account type                         |
 * +--------------------------------------------------+-----------------------------------------------------------------------+
 */
public class StudentTest {

    private Student student;
    private Question sampleQuestion;

    @Before
    public void setUp() {
        student = new Student(1, "testStudent", "pass123", "student@test.com");
        sampleQuestion = new Question("Test Q", "prompt", Difficulty.EASY, Language.JAVA, Course.CSCE145);
    }

    // ===================== STREAK =====================

    @Test
    public void defaultStreakIsZero() {
        assertEquals(0, student.getStreak());
    }

    @Test
    public void startStreakSetsStreakToOne() {
        student.startStreak();
        assertEquals(1, student.getStreak());
    }

    @Test
    public void startStreakDoesNotOverwriteExistingStreak() {
        student.setStreak(5);
        student.startStreak();
        assertEquals(5, student.getStreak());
    }

    @Test
    public void incrementStreakIncreasesByOne() {
        student.setStreak(3);
        student.incrementStreak();
        assertEquals(4, student.getStreak());
    }

    @Test
    public void incrementStreakMultipleTimes() {
        student.incrementStreak();
        student.incrementStreak();
        student.incrementStreak();
        assertEquals(3, student.getStreak());
    }

    // ===================== FAVORITE QUESTIONS =====================

    @Test
    public void addFavQuestionAddsToList() {
        student.addFavQuestion(sampleQuestion);
        assertTrue(student.getFavQuestions().contains(sampleQuestion));
    }

    @Test
    public void addFavQuestionDoesNotAddNull() {
        student.addFavQuestion(null);
        assertTrue(student.getFavQuestions().isEmpty());
    }

    @Test
    public void addFavQuestionDoesNotAddDuplicate() {
        student.addFavQuestion(sampleQuestion);
        student.addFavQuestion(sampleQuestion);
        assertEquals(1, student.getFavQuestions().size());
    }

    @Test
    public void removeFavQuestionRemovesFromList() {
        student.addFavQuestion(sampleQuestion);
        student.removeFavQuestion(sampleQuestion);
        assertFalse(student.getFavQuestions().contains(sampleQuestion));
    }

    @Test
    public void removeFavQuestionNotInListDoesNotCrash() {
        student.removeFavQuestion(sampleQuestion); // never added — should not throw
        assertTrue(student.getFavQuestions().isEmpty());
    }

    // ===================== CLASSIFICATION =====================

    @Test
    public void defaultClassificationIsFreshman() {
        assertEquals("Freshman", student.getClassification());
    }

    @Test
    public void setClassificationUpdatesValue() {
        student.setClassification("Junior");
        assertEquals("Junior", student.getClassification());
    }

    // ===================== ACCOUNT TYPE =====================

    @Test
    public void accountTypeIsStudent() {
        assertEquals(AccountType.STUDENT, student.getAccountType());
    }
}