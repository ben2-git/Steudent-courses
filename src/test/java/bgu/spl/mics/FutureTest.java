package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;


import static org.junit.jupiter.api.Assertions.*;


public class FutureTest {

    private Future<String> future;

    @BeforeEach
    public void setUp(){
        future = new Future<>();
    }

    @Test
    public void testAll(){
        //check if isDone starts as 'false'
        //check if get returns null when the future object is not resolved
        //check if resolving the object changes isDone to 'true' and get returns the wanted result
        assertFalse(future.isDone()); //checking isDone starts as false
        String str = future.get(100, TimeUnit.MILLISECONDS);
        assertEquals(str, null); //checking get returning null
        str = "Success";
        future.resolve(str);
        assertTrue(future.isDone()); //checking that resolve worked and isDone equals true
        assertEquals("Success", future.get()); //checking output of get is correct
        assertEquals("Success", future.get(1000, TimeUnit.MILLISECONDS)); //checking output of get(timeout, unit) is correct
    }
}
