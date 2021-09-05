package bgu.spl.mics;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {

    private MessageBus mb;
    private MicroServiceExample m1;
    private MicroServiceExample m2;
    private EventExample e1;
    private EventExample e2;
    private EventExample e3;
    private BroadcastExample b;

    @BeforeEach
    void setUp() { //tests register - if the method does not work correctly - no test will pass
        mb = MessageBusImpl.getInstance();
        m1 = new MicroServiceExample("ex1");
        m2 = new MicroServiceExample("ex2");
        mb.register(m1); //registerTEST
        mb.register(m2);
        e1 = new EventExample();
        e2 = new EventExample();
        e3 = new EventExample();
        b = new BroadcastExample();
    }

    @AfterEach
    void tearDown(){
        mb.unregister(m1);
        mb.unregister(m2);
    }

    @Test
    void complete() {
        //check result and isDone of future, after a certain event is completed
        mb.subscribeEvent(e1.getClass(), m1);
        Future<String> f = mb.sendEvent(e1);
        mb.complete(e1, "Success");
        assertTrue(f.isDone());
        assertEquals("Success", f.get());
    }

    @Test
    void sendBroadcast() throws InterruptedException {
        //try to send broadcast to two microServices, and check if they got the broadcast
        //ALSO checks subscribeBroadcast - the microServices will not get the messages if they are not subscribed correctly
        //ALSO checks awaitMessage - we use this function to retrieve the last message the microService got
        mb.subscribeBroadcast(b.getClass(), m1); //subscribeBroadcast TEST
        mb.subscribeBroadcast(b.getClass(), m2);
        mb.sendBroadcast(b);
        Message b1 = new BroadcastExample();
        Message b2 = new BroadcastExample();
        try {
            b1 = mb.awaitMessage(m1); //awaitMessage TEST
            b2 = mb.awaitMessage(m2);
        }
        catch (InterruptedException e){ fail();}
        assertEquals(b1, b);
        assertEquals(b2, b);
    }

    @Test
    void sendEvent() {
        //try to send events to m1 and m2, we will check if they receive these events in the wanted round-robin manner
        //ALSO checks subscribeEvent - the microServices will not get the messages if they are not subscribed correctly
        mb.subscribeEvent(e1.getClass(), m1); //subscribeEvent TEST
        mb.subscribeEvent(e1.getClass(), m2);
        mb.sendEvent(e1);
        mb.sendEvent(e2);
        mb.sendEvent(e3);
        Message r1 = new EventExample();
        Message r2 = new EventExample();
        Message r3 = new EventExample();
        try{
            r1 = mb.awaitMessage(m1);
            r2 = mb.awaitMessage(m2);
            r3 = mb.awaitMessage(m1);
        } catch (InterruptedException e) { fail(); }
        assertEquals(e1, r1);
        assertEquals(e2, r2);
        assertEquals(e3, r3);
    }

    @Test
    void awaitMessage(){
        //checking if awaitMessage throws an exception
        mb.unregister(m1);
        try{
            mb.awaitMessage(m1);
            fail();
        }
        catch (Exception e) { }
        mb.register(m1);
    }
}