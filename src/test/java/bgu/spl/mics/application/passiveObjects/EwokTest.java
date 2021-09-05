package bgu.spl.mics.application.passiveObjects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EwokTest {

    private Ewok ew;

    @BeforeEach
    void setUp() {
        ew = new Ewok();
    }

    @Test
    void acquire() {
        //checks if after acquire isAvailable is false
        //check if when trying to acquire a non-available ewok, an exception is thrown
        ew.acquire();
        assertFalse(ew.isAvailable());
        try {
            ew.acquire();
            fail();
        }
        catch (Exception e){ }
    }

    @Test
    void release() {
        //checks if the ewok is available after release method
        ew.release();
        assertTrue(ew.isAvailable());
    }
}