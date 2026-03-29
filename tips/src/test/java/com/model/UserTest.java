package com.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Test;

public class UserTest {
    @Test
    public void testValidLogin() {
        TIPSFacade library = TIPSFacade.getInstance();
        boolean success = library.login("tommy", "done");
        assertFalse(success);
    }

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
