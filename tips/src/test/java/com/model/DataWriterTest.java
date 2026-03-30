package com.model;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DataWriterTest {

    private UserList userList         = UserList.getInstance();
    private QuestionList questionList = QuestionList.getInstance();

    // Reusable users
    private User teacher  = new User(1, "jdoe",   "pass1", "jdoe@uni.edu",   AccountType.TEACHER);
    private User student1 = new User(2, "asmith", "pass2", "asmith@uni.edu", AccountType.STUDENT);
    private User student2 = new User(3, "bsmith", "pass3", "bsmith@uni.edu", AccountType.STUDENT);

    @BeforeEach
    public void setup() {
        userList.getAllUsers().clear();
        questionList.getAllQuestions().clear();
        DataWriter.saveUsers();
        DataWriter.saveQuestions();
    }

    @AfterEach
    public void tearDown() {
        userList.getAllUsers().clear();
        questionList.getAllQuestions().clear();
        DataWriter.saveUsers();
        DataWriter.saveQuestions();
    }

    // -----------------------------------------------------------------------
    // User tests
    // -----------------------------------------------------------------------

    @Test
    void testWritingZeroUsers() {
        DataWriter.saveUsers();
        assertEquals(0, DataLoader.getUsers().size());
    }

    @Test
    void testWritingOneUser() {
        userList.getAllUsers().add(teacher);
        DataWriter.saveUsers();
        assertEquals("jdoe", DataLoader.getUsers().get(0).getUsername());
    }

    @Test
    void testWritingFiveUsers() {
        userList.getAllUsers().add(new User(1, "asmith", "pw", "a@x.com", AccountType.STUDENT));
        userList.getAllUsers().add(new User(2, "bsmith", "pw", "b@x.com", AccountType.STUDENT));
        userList.getAllUsers().add(new User(3, "csmith", "pw", "c@x.com", AccountType.STUDENT));
        userList.getAllUsers().add(new User(4, "dsmith", "pw", "d@x.com", AccountType.STUDENT));
        userList.getAllUsers().add(new User(5, "esmith", "pw", "e@x.com", AccountType.STUDENT));
        DataWriter.saveUsers();
        assertEquals("esmith", DataLoader.getUsers().get(4).getUsername());
    }

    @Test
    void testWritingUserPreservesEmail() {
        userList.getAllUsers().add(teacher);
        DataWriter.saveUsers();
        assertEquals("jdoe@uni.edu", DataLoader.getUsers().get(0).getEmail());
    }

    @Test
    void testWritingUserPreservesAccountType() {
        userList.getAllUsers().add(teacher);
        DataWriter.saveUsers();
        assertEquals(AccountType.TEACHER, DataLoader.getUsers().get(0).getAccountType());
    }

    @Test
    void testWritingUserWithEmptyUsername() {
        userList.getAllUsers().add(new User(10, "", "pw", "x@x.com", AccountType.STUDENT));
        DataWriter.saveUsers();
        assertEquals("", DataLoader.getUsers().get(0).getUsername());
    }

    @Test
    void testWritingUserWithEmptyPassword() {
        userList.getAllUsers().add(new User(10, "user", "", "x@x.com", AccountType.STUDENT));
        DataWriter.saveUsers();
        assertEquals("", DataLoader.getUsers().get(0).getPassword());
    }

    @Test
    void testWritingUserWithEmptyEmail() {
        userList.getAllUsers().add(new User(10, "user", "pw", "", AccountType.STUDENT));
        DataWriter.saveUsers();
        assertEquals("", DataLoader.getUsers().get(0).getEmail());
    }

    // -----------------------------------------------------------------------
    // Student tests
    // -----------------------------------------------------------------------

    @Test
    void testWritingOneStudent() {
        userList.getAllUsers().add(new Student(20, "stu1", "pw", "stu1@uni.edu"));
        DataWriter.saveUsers();
        assertEquals("stu1", DataLoader.getUsers().get(0).getUsername());
    }

    @Test
    void testWritingStudentPreservesStreak() {
        Student s = new Student(20, "stu1", "pw", "stu1@uni.edu");
        s.setStreak(5);
        userList.getAllUsers().add(s);
        DataWriter.saveUsers();
        Student loaded = (Student) DataLoader.getUsers().get(0);
        assertEquals(5, loaded.getStreak());
    }

    @Test
    void testWritingStudentPreservesClassification() {
        Student s = new Student(20, "stu1", "pw", "stu1@uni.edu");
        s.setClassification("Junior");
        userList.getAllUsers().add(s);
        DataWriter.saveUsers();
        Student loaded = (Student) DataLoader.getUsers().get(0);
        assertEquals("Junior", loaded.getClassification());
    }

    @Test
    void testWritingStudentWithZeroStreak() {
        Student s = new Student(21, "stu2", "pw", "stu2@uni.edu");
        s.setStreak(0);
        userList.getAllUsers().add(s);
        DataWriter.saveUsers();
        Student loaded = (Student) DataLoader.getUsers().get(0);
        assertEquals(0, loaded.getStreak());
    }

    // -----------------------------------------------------------------------
    // Question tests
    // -----------------------------------------------------------------------

    @Test
    void testWritingZeroQuestions() {
        DataWriter.saveQuestions();
        assertEquals(0, DataLoader.getQuestions().size());
    }

    @Test
    void testWritingOneQuestion() {
        questionList.getAllQuestions().add(
            new Question("Reverse a String", "Write a method to reverse a string.",
                         Difficulty.EASY, Language.JAVA, Course.CSCE145));
        DataWriter.saveQuestions();
        assertEquals("Reverse a String", DataLoader.getQuestions().get(0).getTitle());
    }

    @Test
    void testWritingFiveQuestions() {
        for (int i = 1; i <= 5; i++) {
            questionList.getAllQuestions().add(
                new Question("Question " + i, "Prompt " + i,
                             Difficulty.EASY, Language.JAVA, Course.CSCE145));
        }
        DataWriter.saveQuestions();
        assertEquals(5, DataLoader.getQuestions().size());
    }

    @Test
    void testWritingQuestionPreservesPrompt() {
        questionList.getAllQuestions().add(
            new Question("Title", "Detailed prompt here.",
                         Difficulty.MEDIUM, Language.PYTHON, Course.CSCE145));
        DataWriter.saveQuestions();
        assertEquals("Detailed prompt here.", DataLoader.getQuestions().get(0).getPrompt());
    }

    @Test
    void testWritingQuestionPreservesDifficulty() {
        questionList.getAllQuestions().add(
            new Question("Title", "Prompt", Difficulty.HARD, Language.JAVA, Course.CSCE145));
        DataWriter.saveQuestions();
        assertEquals(Difficulty.HARD, DataLoader.getQuestions().get(0).getDifficulty());
    }

    @Test
    void testWritingQuestionPreservesLanguage() {
        questionList.getAllQuestions().add(
            new Question("Title", "Prompt", Difficulty.EASY, Language.PYTHON, Course.CSCE145));
        DataWriter.saveQuestions();
        assertEquals(Language.PYTHON, DataLoader.getQuestions().get(0).getLanguage());
    }

    @Test
    void testWritingQuestionWithEmptyTitle() {
        questionList.getAllQuestions().add(
            new Question("", "Prompt", Difficulty.EASY, Language.JAVA, Course.CSCE145));
        DataWriter.saveQuestions();
        assertEquals("", DataLoader.getQuestions().get(0).getTitle());
    }

    // -----------------------------------------------------------------------
    // Solution tests
    // -----------------------------------------------------------------------

    @Test
    void testWritingZeroSolutions() {
        Question q = new Question("Title", "Prompt", Difficulty.EASY, Language.JAVA, Course.CSCE145);
        questionList.getAllQuestions().add(q);
        DataWriter.saveQuestions();
        assertEquals(0, DataLoader.getQuestions().get(0).getSolutions().size());
    }

    @Test
    void testWritingOneSolution() {
        Question q = new Question("Title", "Prompt", Difficulty.EASY, Language.JAVA, Course.CSCE145);
        q.getSolutions().add(new Solution(student1, q, "return x + 1;"));
        questionList.getAllQuestions().add(q);
        DataWriter.saveQuestions();
        assertEquals("return x + 1;",
            DataLoader.getQuestions().get(0).getSolutions().get(0).getContent());
    }

    @Test
    void testWritingMultipleSolutions() {
        Question q = new Question("Title", "Prompt", Difficulty.EASY, Language.JAVA, Course.CSCE145);
        q.getSolutions().add(new Solution(student1, q, "solution one"));
        q.getSolutions().add(new Solution(student2, q, "solution two"));
        questionList.getAllQuestions().add(q);
        DataWriter.saveQuestions();
        assertEquals(2, DataLoader.getQuestions().get(0).getSolutions().size());
    }

    @Test
    void testWritingSolutionPreservesAuthor() {
        userList.getAllUsers().add(student1);
        Question q = new Question("Title", "Prompt", Difficulty.EASY, Language.JAVA, Course.CSCE145);
        q.getSolutions().add(new Solution(student1, q, "code"));
        questionList.getAllQuestions().add(q);
        DataWriter.saveUsers();
        DataWriter.saveQuestions();
        assertEquals(student1.getUserId(),
            DataLoader.getQuestions().get(0).getSolutions().get(0).getAuthor().getUserId());
    }

    @Test
    void testWritingSolutionWithEmptyContent() {
        Question q = new Question("Title", "Prompt", Difficulty.EASY, Language.JAVA, Course.CSCE145);
        q.getSolutions().add(new Solution(student1, q, ""));
        questionList.getAllQuestions().add(q);
        DataWriter.saveQuestions();
        assertEquals("",
            DataLoader.getQuestions().get(0).getSolutions().get(0).getContent());
    }

    // -----------------------------------------------------------------------
    // Reply tests
    // -----------------------------------------------------------------------

    @Test
    void testWritingZeroReplies() {
        Question q = new Question("Title", "Prompt", Difficulty.EASY, Language.JAVA, Course.CSCE145);
        questionList.getAllQuestions().add(q);
        DataWriter.saveQuestions();
        assertEquals(0, DataLoader.getQuestions().get(0).getReplies().size());
    }

    @Test
    void testWritingOneReply() {
        Question q = new Question("Title", "Prompt", Difficulty.EASY, Language.JAVA, Course.CSCE145);
        q.getReplies().add(new Reply(student1, q, "Great question!"));
        questionList.getAllQuestions().add(q);
        DataWriter.saveQuestions();
        assertEquals("Great question!",
            DataLoader.getQuestions().get(0).getReplies().get(0).getContent());
    }

    @Test
    void testWritingMultipleReplies() {
        Question q = new Question("Title", "Prompt", Difficulty.EASY, Language.JAVA, Course.CSCE145);
        q.getReplies().add(new Reply(student1, q, "Reply one"));
        q.getReplies().add(new Reply(student2, q, "Reply two"));
        questionList.getAllQuestions().add(q);
        DataWriter.saveQuestions();
        assertEquals(2, DataLoader.getQuestions().get(0).getReplies().size());
    }

    @Test
    void testWritingReplyPreservesAuthor() {
        userList.getAllUsers().add(student1);
        Question q = new Question("Title", "Prompt", Difficulty.EASY, Language.JAVA, Course.CSCE145);
        q.getReplies().add(new Reply(student1, q, "My reply"));
        questionList.getAllQuestions().add(q);
        DataWriter.saveUsers();
        DataWriter.saveQuestions();
        assertEquals(student1.getUserId(),
            DataLoader.getQuestions().get(0).getReplies().get(0).getAuthor().getUserId());
    }

    @Test
    void testWritingReplyWithEmptyContent() {
        Question q = new Question("Title", "Prompt", Difficulty.EASY, Language.JAVA, Course.CSCE145);
        q.getReplies().add(new Reply(student1, q, ""));
        questionList.getAllQuestions().add(q);
        DataWriter.saveQuestions();
        assertEquals("",
            DataLoader.getQuestions().get(0).getReplies().get(0).getContent());
    }

    @Test
    void testWritingNestedReply() {
        Question q = new Question("Title", "Prompt", Difficulty.EASY, Language.JAVA, Course.CSCE145);
        Reply parent = new Reply(student1, q, "Parent reply");
        parent.getReplies().add(new Reply(student2, q, "Nested reply"));
        q.getReplies().add(parent);
        questionList.getAllQuestions().add(q);
        DataWriter.saveQuestions();
        assertEquals("Nested reply",
            DataLoader.getQuestions().get(0).getReplies().get(0).getReplies().get(0).getContent());
    }
}