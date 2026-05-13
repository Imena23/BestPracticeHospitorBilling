package com.example.protypebillingsystem;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the Protype Billing System logic.
 * These tests focus on session management and data calculations.
 */
public class ExampleUnitTest {

    @Test
    public void testPatientInitials() {
        PatientSession session = PatientSession.getInstance();
        
        // Test with full name
        session.name = "Kevine I";
        assertEquals("KD", session.getInitials());

        // Test with second name
        session.name = "Ketia K";
        assertEquals("KK", session.getInitials());

        // Test with another name
        session.name = "Brian A";
        assertEquals("BA", session.getInitials());

        session.clear();
    }

    @Test
    public void testIsFullyPaidLogic() {
        PatientSession session = PatientSession.getInstance();
        
        // Test unpaid
        session.unpaidAmount = 150.0;
        assertFalse(session.isFullyPaid());

        // Test fully paid
        session.unpaidAmount = 0.0;
        assertTrue(session.isFullyPaid());

        session.clear();
    }

    @Test
    public void testAddition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
}
