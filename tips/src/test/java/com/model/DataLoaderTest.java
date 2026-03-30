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
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for DataLoader.
 * @author Samuel Britton
 *
 * +----------------------------------------------------------+----------------------------------------------------------------------+
 * | Test                                                     | Reasoning                                                            |
 * +----------------------------------------------------------+----------------------------------------------------------------------+
 * | testGetUsersSize                                         | Happy path — valid user JSON should load all users                   |
 * | testGetUsersSizeZero                                     | Empty users file should return an empty list                         |
 * | testGetUsersSizeSingle                                   | Single user JSON should load exactly one user                        |
 * | testInvalidUserTypeIgnored                               | Unknown user types should be skipped                                 |
 * | testUserTypes                                            | DataLoader should create the correct User subclasses                |
 * | testStudentFields                                        | Student-specific fields should be loaded correctly                  |
 * | testUserFields                                           | Common user fields should be loaded correctly                       |
 * | testGetQuestionsSize                                     | Happy path — valid question JSON should load all questions          |
 * | testGetQuestionsSizeZero                                 | Empty questions file should return an empty list                    |
 * | testQuestionBasicFields                                  | Core question fields should be parsed correctly                     |
 * | testQuestionExtras                                       | Optional question fields should be parsed correctly                 |
 * | testQuestionAuthor                                       | Question author should be resolved from userId                      |
 * | testSolutionData                                         | Solutions should load with author, upvotes, and acceptance status   |
 * | testReplyData                                            | Replies should load with author and metadata                        |
 * | testNoSolutions                                          | Questions with no solutions should still load safely                |
 * | testNoReplies                                            | Questions with no replies should still load safely                  |
 * | testReplyWithInvalidAuthorIgnored                        | Replies with missing authors should be ignored                      |
 * | testNestedReplies                                        | Nested replies should load recursively                              |
 * | testNoSolutions / testNoReplies                          | Empty nested arrays should be handled without crashing              |
 * | testInvalidUserTypeIgnored                               | Invalid enum-like user data should not break loading                |
 * +----------------------------------------------------------+----------------------------------------------------------------------+
 */


class DataLoaderTest {

    private static final String USERS_FILE = "../json/users.json";
    private static final String QUESTIONS_FILE = "../json/questions.json";

    private static final String USERS_JSON =
        "[\n" +
        "  {\"userId\":1,\"username\":\"jdoe\",\"password\":\"pass123\",\"email\":\"jdoe@email.com\",\"userType\":\"STUDENT\",\"streak\":0,\"classification\":\"Freshman\"},\n" +
        "  {\"userId\":2,\"username\":\"eeditor\",\"password\":\"edpass\",\"email\":\"editor@email.com\",\"userType\":\"EDITOR\"},\n" +
        "  {\"userId\":3,\"username\":\"aadmin\",\"password\":\"adminpass\",\"email\":\"admin@email.com\",\"userType\":\"ADMIN\"},\n" +
        "  {\"userId\":4,\"username\":\"sclark\",\"password\":\"spass\",\"email\":\"sclark@email.com\",\"userType\":\"STUDENT\",\"streak\":5,\"classification\":\"SOPHOMORE\"}\n" +
        "]";

    private static final String QUESTIONS_JSON =
        "[\n" +
        "  {\n" +
        "    \"title\":\"Reverse a String\",\n" +
        "    \"prompt\":\"Write a method that reverses a string.\",\n" +
        "    \"difficulty\":\"EASY\",\n" +
        "    \"language\":\"JAVA\",\n" +
        "    \"course\":\"CSCE146\",\n" +
        "    \"hint\":\"Think about using a loop or StringBuilder.\",\n" +
        "    \"sampleSolution\":\"return new StringBuilder(s).reverse().toString();\",\n" +
        "    \"sampleExplanation\":\"StringBuilder has a built-in reverse method.\",\n" +
        "    \"authorId\":1,\n" +
        "    \"isSolutionRevealed\":false,\n" +
        "    \"solutions\":[{\"authorId\":2,\"content\":\"return new StringBuilder(s).reverse().toString();\",\"upvotes\":3,\"isAccepted\":true}],\n" +
        "    \"replies\":[{\"authorId\":1,\"content\":\"Great solution!\",\"title\":\"Feedback\",\"upvotes\":1,\"isAccepted\":false,\"datePosted\":\"\",\"replies\":[]}]\n" +
        "  }\n" +
        "]";

    private String originalUsersJson;
    private String originalQuestionsJson;
    private boolean usersFileExisted;
    private boolean questionsFileExisted;

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

    @BeforeEach
    void setup() throws Exception {
        usersFileExisted = Files.exists(Paths.get(USERS_FILE));
        questionsFileExisted = Files.exists(Paths.get(QUESTIONS_FILE));

        originalUsersJson = usersFileExisted ? readFile(USERS_FILE) : "";
        originalQuestionsJson = questionsFileExisted ? readFile(QUESTIONS_FILE) : "";

        writeFile(USERS_FILE, USERS_JSON);
        writeFile(QUESTIONS_FILE, QUESTIONS_JSON);
        resetSingletons();
    }

    @AfterEach
    void tearDown() throws Exception {
        restoreFile(USERS_FILE, usersFileExisted, originalUsersJson);
        restoreFile(QUESTIONS_FILE, questionsFileExisted, originalQuestionsJson);
        resetSingletons();
    }

    // ================= USERS =================

    @Test
    void testGetUsersSize() {
        assertEquals(4, DataLoader.getUsers().size());
    }

    @Test
    void testGetUsersSizeZero() throws Exception {
        writeFile(USERS_FILE, "[]");
        resetSingletons();
        assertEquals(0, DataLoader.getUsers().size());
    }

    @Test
    void testGetUsersSizeSingle() throws Exception {
        writeFile(USERS_FILE,
            "[{\"userId\":1,\"username\":\"only\",\"password\":\"onlypass\",\"email\":\"only@email.com\",\"userType\":\"STUDENT\",\"streak\":0,\"classification\":\"Freshman\"}]");
        resetSingletons();
        assertEquals(1, DataLoader.getUsers().size());
    }

    @Test
    void testInvalidUserTypeIgnored() throws Exception {
        writeFile(USERS_FILE,
            "[{\"userId\":1,\"username\":\"bad\",\"password\":\"p\",\"email\":\"e\",\"userType\":\"UNKNOWN\"}]");
        resetSingletons();
        assertEquals(0, DataLoader.getUsers().size());
    }

    @Test
    void testUserTypes() {
        ArrayList<User> users = DataLoader.getUsers();
        assertInstanceOf(Student.class, users.get(0));
        assertInstanceOf(Editor.class, users.get(1));
        assertInstanceOf(Admin.class, users.get(2));
    }

    @Test
    void testStudentFields() {
        Student s = (Student) DataLoader.getUsers().get(3);
        assertEquals("SOPHOMORE", s.getClassification());
        assertEquals(5, s.getStreak());
    }

    @Test
    void testUserFields() {
        User u = DataLoader.getUsers().get(0);
        assertEquals("jdoe", u.getUsername());
        assertEquals("pass123", u.getPassword());
        assertEquals("jdoe@email.com", u.getEmail());
        assertEquals(1, u.getUserId());
    }

    // ================= QUESTIONS =================

    @Test
    void testGetQuestionsSize() {
        assertEquals(1, DataLoader.getQuestions().size());
    }

    @Test
    void testGetQuestionsSizeZero() throws Exception {
        writeFile(QUESTIONS_FILE, "[]");
        resetSingletons();
        assertEquals(0, DataLoader.getQuestions().size());
    }

    @Test
    void testQuestionBasicFields() {
        Question q = DataLoader.getQuestions().get(0);
        assertEquals("Reverse a String", q.getTitle());
        assertEquals("Write a method that reverses a string.", q.getPrompt());
        assertEquals(Difficulty.EASY, q.getDifficulty());
        assertEquals(Language.JAVA, q.getLanguage());
        assertEquals(Course.CSCE146, q.getCourse());
    }

    @Test
    void testQuestionExtras() {
        Question q = DataLoader.getQuestions().get(0);
        assertEquals("Think about using a loop or StringBuilder.", q.getHint());
        assertEquals("return new StringBuilder(s).reverse().toString();", q.getSampleSolution());
        assertEquals("StringBuilder has a built-in reverse method.", q.getSampleExplanation());
    }

    @Test
    void testQuestionAuthor() {
        assertEquals("jdoe", DataLoader.getQuestions().get(0).getAuthor().getUsername());
    }

    @Test
    void testSolutionData() {
        Solution s = DataLoader.getQuestions().get(0).getSolutions().get(0);
        assertEquals("return new StringBuilder(s).reverse().toString();", s.getContent());
        assertEquals(3, s.getUpvotes());
        assertTrue(s.isAccepted());
        assertEquals("eeditor", s.getAuthor().getUsername());
    }

    @Test
    void testReplyData() {
        Reply r = DataLoader.getQuestions().get(0).getReplies().get(0);
        assertEquals("Great solution!", r.getContent());
        assertEquals("Feedback", r.getTitle());
        assertEquals(1, r.getUpvotes());
        assertEquals("jdoe", r.getAuthor().getUsername());
    }

    @Test
    void testNoSolutions() throws Exception {
        writeFile(QUESTIONS_FILE,
            "[{\"title\":\"Q\",\"prompt\":\"P\",\"difficulty\":\"HARD\",\"language\":\"JAVA\",\"course\":\"CSCE145\",\"solutions\":[],\"replies\":[]}]");
        resetSingletons();
        assertEquals(0, DataLoader.getQuestions().get(0).getSolutions().size());
    }

    @Test
    void testNoReplies() throws Exception {
        writeFile(QUESTIONS_FILE,
            "[{\"title\":\"Q\",\"prompt\":\"P\",\"difficulty\":\"MEDIUM\",\"language\":\"JAVA\",\"course\":\"CSCE145\",\"solutions\":[],\"replies\":[]}]");
        resetSingletons();
        assertEquals(0, DataLoader.getQuestions().get(0).getReplies().size());
    }

    @Test
    void testReplyWithInvalidAuthorIgnored() throws Exception {
        writeFile(QUESTIONS_FILE,
            "[{\"title\":\"Q\",\"prompt\":\"P\",\"difficulty\":\"EASY\",\"language\":\"JAVA\",\"course\":\"CSCE146\",\"replies\":[{\"authorId\":999,\"content\":\"bad\"}]}]");
        resetSingletons();
        assertEquals(0, DataLoader.getQuestions().get(0).getReplies().size());
    }

    @Test
    void testNestedReplies() throws Exception {
        writeFile(QUESTIONS_FILE,
            "[{\"title\":\"Q\",\"prompt\":\"P\",\"difficulty\":\"EASY\",\"language\":\"JAVA\",\"course\":\"CSCE146\"," +
            "\"replies\":[{\"authorId\":1,\"content\":\"Parent\",\"replies\":[{\"authorId\":1,\"content\":\"Child\",\"replies\":[]}]}]}]");
        resetSingletons();

        Reply parent = DataLoader.getQuestions().get(0).getReplies().get(0);
        assertEquals(1, parent.getReplies().size());
        assertEquals("Child", parent.getReplies().get(0).getContent());
    }
}