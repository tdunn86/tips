package com.model;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for DataWriter.
 * @author Oliver Benjamin
 *
 * +----------------------------------------------------------+----------------------------------------------------------------------+
 * | Test                                                     | Reasoning                                                            |
 * +----------------------------------------------------------+----------------------------------------------------------------------+
 * | testWritingZeroUsers                                     | Empty user list should serialize to an empty JSON array             |
 * | testWritingOneStudent                                    | A single student should round-trip through JSON correctly           |
 * | testWritingMixedUsersPreservesTypes                       | Different user subclasses should preserve their types              |
 * | testWritingStudentPreservesStreak                         | Student streak should be written and loaded back correctly         |
 * | testWritingStudentPreservesClassification                  | Student classification should be written and loaded back correctly |
 * | testWritingZeroQuestions                                 | Empty question list should serialize to an empty JSON array       |
 * | testWritingOneQuestion                                    | A basic question should round-trip through JSON correctly          |
 * | testWritingQuestionPreservesDifficultyLanguageCourse       | Core enum fields should remain unchanged after saving/loading      |
 * | testWritingQuestionWithAuthor                             | Question author ID should be preserved through serialization       |
 * | testWritingQuestionWithSolution                           | Solutions should be serialized with author, content, votes, and acceptance |
 * | testWritingQuestionWithReply                              | Replies should be serialized with author and metadata              |
 * | testWritingNestedReply                                    | Nested replies should be preserved recursively                     |
 * | testWritingQuestionOptionalFieldsRoundTrip                 | Optional fields like hint/sample solution should persist correctly |
 * +----------------------------------------------------------+----------------------------------------------------------------------+
 */

class DataWriterTest {

    private static final String USERS_FILE = DataConstants.USER_FILE_NAME;
    private static final String QUESTIONS_FILE = DataConstants.QUESTION_FILE_NAME;

    private String originalUsersJson;
    private String originalQuestionsJson;
    private boolean usersFileExisted;
    private boolean questionsFileExisted;

    private UserList userList;
    private QuestionList questionList;

    private Student student1;
    private Student student2;
    private Editor editor;
    private Admin admin;

    private void writeFile(String path, String content) throws IOException {
        Path p = Paths.get(path);
        Path parent = p.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.write(p, content.getBytes(StandardCharsets.UTF_8));
    }

    private String readFile(String path) throws IOException {
        Path p = Paths.get(path);
        return Files.exists(p)
            ? new String(Files.readAllBytes(p), StandardCharsets.UTF_8)
            : "";
    }

    private void restoreFile(String path, boolean existedBefore, String originalContent) throws IOException {
        if (existedBefore) {
            writeFile(path, originalContent);
        } else {
            Files.deleteIfExists(Paths.get(path));
        }
    }

    private void resetSingletons() throws Exception {
        Field ul = UserList.class.getDeclaredField("instance");
        ul.setAccessible(true);
        ul.set(null, null);

        Field ql = QuestionList.class.getDeclaredField("instance");
        ql.setAccessible(true);
        ql.set(null, null);
    }

    @SuppressWarnings("unchecked")
    private ArrayList<User> getBackingUsers() throws Exception {
        Field usersField = UserList.class.getDeclaredField("users");
        usersField.setAccessible(true);
        return (ArrayList<User>) usersField.get(userList);
    }

    @SuppressWarnings("unchecked")
    private ArrayList<Question> getBackingQuestions() throws Exception {
        Field questionsField = QuestionList.class.getDeclaredField("questions");
        questionsField.setAccessible(true);
        return (ArrayList<Question>) questionsField.get(questionList);
    }

    private void clearBackingData() throws Exception {
        getBackingUsers().clear();
        getBackingQuestions().clear();
    }

    @BeforeEach
    void setup() throws Exception {
        usersFileExisted = Files.exists(Paths.get(USERS_FILE));
        questionsFileExisted = Files.exists(Paths.get(QUESTIONS_FILE));

        originalUsersJson = usersFileExisted ? readFile(USERS_FILE) : "";
        originalQuestionsJson = questionsFileExisted ? readFile(QUESTIONS_FILE) : "";

        resetSingletons();

        userList = UserList.getInstance();
        questionList = QuestionList.getInstance();

        clearBackingData();

        student1 = new Student(1, "jdoe", "pass1", "jdoe@uni.edu");
        student2 = new Student(2, "asmith", "pass2", "asmith@uni.edu");
        editor = new Editor(3, "eeditor", "edpass", "editor@uni.edu");
        admin = new Admin(4, "aadmin", "adminpass", "admin@uni.edu");
    }

    @AfterEach
    void tearDown() throws Exception {
        clearBackingData();

        restoreFile(USERS_FILE, usersFileExisted, originalUsersJson);
        restoreFile(QUESTIONS_FILE, questionsFileExisted, originalQuestionsJson);

        resetSingletons();
    }

    // ================= USERS =================

    @Test
    void testWritingZeroUsers() {
        DataWriter.saveUsers();
        assertEquals(0, DataLoader.getUsers().size());
    }

    @Test
    void testWritingOneStudent() {
        userList.addUser(student1);
        DataWriter.saveUsers();

        assertEquals(1, DataLoader.getUsers().size());
        assertInstanceOf(Student.class, DataLoader.getUsers().get(0));
        assertEquals("jdoe", DataLoader.getUsers().get(0).getUsername());
        assertEquals("jdoe@uni.edu", DataLoader.getUsers().get(0).getEmail());
    }

    @Test
    void testWritingMixedUsersPreservesTypes() {
        userList.addUser(student1);
        userList.addUser(editor);
        userList.addUser(admin);

        DataWriter.saveUsers();

        assertEquals(3, DataLoader.getUsers().size());
        assertInstanceOf(Student.class, DataLoader.getUsers().get(0));
        assertInstanceOf(Editor.class, DataLoader.getUsers().get(1));
        assertInstanceOf(Admin.class, DataLoader.getUsers().get(2));
    }

    @Test
    void testWritingStudentPreservesStreak() {
        student1.setStreak(5);
        userList.addUser(student1);

        DataWriter.saveUsers();

        Student loaded = (Student) DataLoader.getUsers().get(0);
        assertEquals(5, loaded.getStreak());
    }

    @Test
    void testWritingStudentPreservesClassification() {
        student1.setClassification("Junior");
        userList.addUser(student1);

        DataWriter.saveUsers();

        Student loaded = (Student) DataLoader.getUsers().get(0);
        assertEquals("Junior", loaded.getClassification());
    }

    // ================= QUESTIONS =================

    @Test
    void testWritingZeroQuestions() {
        DataWriter.saveQuestions();
        assertEquals(0, DataLoader.getQuestions().size());
    }

    @Test
    void testWritingOneQuestion() {
        Question q = new Question(
            "Reverse a String",
            "Write a method to reverse a string.",
            Difficulty.EASY,
            Language.JAVA,
            Course.CSCE145
        );
        questionList.addQuestion(q);

        DataWriter.saveQuestions();

        assertEquals(1, DataLoader.getQuestions().size());
        assertEquals("Reverse a String", DataLoader.getQuestions().get(0).getTitle());
        assertEquals("Write a method to reverse a string.", DataLoader.getQuestions().get(0).getPrompt());
    }

    @Test
    void testWritingQuestionPreservesDifficultyLanguageCourse() {
        Question q = new Question(
            "Title",
            "Prompt",
            Difficulty.HARD,
            Language.PYTHON,
            Course.CSCE146
        );
        questionList.addQuestion(q);

        DataWriter.saveQuestions();

        Question loaded = DataLoader.getQuestions().get(0);
        assertEquals(Difficulty.HARD, loaded.getDifficulty());
        assertEquals(Language.PYTHON, loaded.getLanguage());
        assertEquals(Course.CSCE146, loaded.getCourse());
    }

    @Test
    void testWritingQuestionWithAuthor() {
        userList.addUser(student1);

        Question q = new Question(
            "Title",
            "Prompt",
            Difficulty.EASY,
            Language.JAVA,
            Course.CSCE146
        );
        q.setAuthor(student1);
        questionList.addQuestion(q);

        DataWriter.saveUsers();
        DataWriter.saveQuestions();

        Question loaded = DataLoader.getQuestions().get(0);
        assertNotNull(loaded.getAuthor());
        assertEquals("jdoe", loaded.getAuthor().getUsername());
    }

    @Test
    void testWritingQuestionWithSolution() {
        userList.addUser(student1);

        Question q = new Question(
            "Title",
            "Prompt",
            Difficulty.EASY,
            Language.JAVA,
            Course.CSCE146
        );

        Solution solution = new Solution(student1, q, "return s;");
        solution.setUpvotes(3);
        solution.setAccepted(true);
        q.addSolution(solution);
        questionList.addQuestion(q);

        DataWriter.saveUsers();
        DataWriter.saveQuestions();

        Question loaded = DataLoader.getQuestions().get(0);
        assertEquals(1, loaded.getSolutions().size());
        assertEquals("return s;", loaded.getSolutions().get(0).getContent());
        assertEquals(3, loaded.getSolutions().get(0).getUpvotes());
        assertTrue(loaded.getSolutions().get(0).isAccepted());
        assertEquals("jdoe", loaded.getSolutions().get(0).getAuthor().getUsername());
    }

    @Test
    void testWritingQuestionWithReply() {
        userList.addUser(student1);

        Question q = new Question(
            "Title",
            "Prompt",
            Difficulty.EASY,
            Language.JAVA,
            Course.CSCE146
        );

        Reply reply = new Reply(student1, q, "Great question!");
        reply.setTitle("Feedback");
        reply.setUpvotes(2);
        q.addReply(reply);
        questionList.addQuestion(q);

        DataWriter.saveUsers();
        DataWriter.saveQuestions();

        Question loaded = DataLoader.getQuestions().get(0);
        assertEquals(1, loaded.getReplies().size());
        assertEquals("Great question!", loaded.getReplies().get(0).getContent());
        assertEquals("Feedback", loaded.getReplies().get(0).getTitle());
        assertEquals(2, loaded.getReplies().get(0).getUpvotes());
        assertEquals("jdoe", loaded.getReplies().get(0).getAuthor().getUsername());
    }

    @Test
    void testWritingNestedReply() {
        userList.addUser(student1);
        userList.addUser(student2);

        Question q = new Question(
            "Title",
            "Prompt",
            Difficulty.EASY,
            Language.JAVA,
            Course.CSCE146
        );

        Reply parent = new Reply(student1, q, "Parent reply");
        Reply child = new Reply(student2, q, "Nested reply");
        parent.addReply(child);
        q.addReply(parent);
        questionList.addQuestion(q);

        DataWriter.saveUsers();
        DataWriter.saveQuestions();

        Question loaded = DataLoader.getQuestions().get(0);
        assertEquals(1, loaded.getReplies().size());
        assertEquals("Parent reply", loaded.getReplies().get(0).getContent());
        assertEquals(1, loaded.getReplies().get(0).getReplies().size());
        assertEquals("Nested reply", loaded.getReplies().get(0).getReplies().get(0).getContent());
    }

    @Test
    void testWritingQuestionOptionalFieldsRoundTrip() {
        Question q = new Question(
            "Title",
            "Prompt",
            Difficulty.MEDIUM,
            Language.PYTHON,
            Course.CSCE145
        );
        q.setHint("Think step by step.");
        q.setSampleSolution("print('hello')");
        q.setSampleExplanation("Simple example.");
        q.revealSolution();
        questionList.addQuestion(q);

        DataWriter.saveQuestions();

        Question loaded = DataLoader.getQuestions().get(0);
        assertEquals("Think step by step.", loaded.getHint());
        assertEquals("print('hello')", loaded.getSampleSolution());
        assertEquals("Simple example.", loaded.getSampleExplanation());
        assertTrue(loaded.isSolutionRevealed());
    }
}