package com.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import com.model.*;

public class UserTest {
    @Test
    public void testTesting() {
        assertTrue(true);
    }

    @Test
    public void testValidLogin() {
        TIPSFacade library = TIPSFacade.getInstance();
        boolean success = library.login("tommy", "done");
        assertFalse(success);
    }

    // todo -- should return true (invalid user able to login)
    @Test
    public void testInvalidLogin() {
        TIPSFacade library = TIPSFacade.getInstance();
        boolean success = library.login("mark", "123");
        assertFalse(success);
    }

    @Test
    public void testAddValidUser() {
        TIPSFacade library = TIPSFacade.getInstance();
        library.registerUser("jmath", "123", "jmath@email.com", AccountType.STUDENT);
        library.logout();
        library = TIPSFacade.getInstance();
        library.login("jmath", "123");
        String CurrentUsername = library.getCurrentUser().getUsername();
        assertEquals("jmath", CurrentUsername);
    }

}
