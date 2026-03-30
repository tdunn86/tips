package com.model;

import static org.junit.Assert.assertFalse;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;


/**
 * Test class for User 
 * @author Thomas Dunn
 *
 * +------------------------------------------+-------------------------------------------------------------------------+
 * | Test                                     | Reasoning                                                               |
 * +------------------------------------------+-------------------------------------------------------------------------+
 * | testValidatePasswordCorrect              | Correct password should return true                                     |
 * | testValidatePasswordIncorrect            | Wrong password should return false                                      |
 * | testValidatePasswordNullPasswordNullInput| If password is null, only null input should match                       |
 * | testValidatePasswordNullInputWrongMatch  | If password is set, null input should not match                         |
 * | testValidatePasswordEmptyString          | Empty string is not the same as a real password, should return false    |
 * | testToString                             | toString should contain username, email, and accountType                |
 * +------------------------------------------+-------------------------------------------------------------------------+
 */

public class UserTest {
    private Student testUser;

    @BeforeEach
    public void setup() {
        testUser = new Student(1, "testUser", "securePass", "test@email.com");
    }

    // ===================== validatePassword =====================

    @Test
    public void testValidatePasswordCorrect() {
        assertTrue(testUser.validatePassword("securePass"));
    }

    @Test
    public void testValidatePasswordIncorrect() {
        assertFalse(testUser.validatePassword("wrongPass"));
    }

    @Test
    public void testValidatePasswordNullPasswordNullInput() {
        Student nullPassUser = new Student(2, "nullPass", null, "null@email.com");
        assertTrue(nullPassUser.validatePassword(null));
    }

    @Test
    public void testValidatePasswordNullInputWrongMatch() {
        assertFalse(testUser.validatePassword(null));
    }

    @Test
    public void testValidatePasswordEmptyString() {
        assertFalse(testUser.validatePassword(""));
    }

    // ===================== toString =====================

    @Test
    public void testToString() {
        String result = testUser.toString();
        assertTrue(result.contains("testUser"));
        assertTrue(result.contains("test@email.com"));
        assertTrue(result.contains("STUDENT"));
    }

}
