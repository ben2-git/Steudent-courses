package bgu.spl.mics.application.services;
import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.messages.*;
import java.util.ArrayList;
import java.util.List;

import bgu.spl.mics.MicroService;

/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {

	private Attack[] attacks;
	private Future<Boolean>[] results;
	
    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
		this.attacks = attacks;
		results = new Future[attacks.length];
    }

    @Override
    protected void initialize() {
    	subscribeBroadcast(TerminationBroadcast.class, (TerminationBroadcast b) ->  {
            Diary.getInstance().setLeiaTerminate(System.currentTimeMillis());
    	    terminate();
    	}); //subscribing to terminate together
        boolean attacksSent = false;
        while(!attacksSent) {
            try { //trying to send attacks
                for (int i = 0; i < attacks.length; i++)
                    results[i] = sendEvent(new AttackEvent(attacks[i])); //sending all attackEvents
                attacksSent = true;
            } catch (IllegalStateException e) { //no one registered to handle attack events yet
                try { Thread.sleep(50); } catch (Exception ex) { } //wait a little and try again
            }
        }
        for(int i = 0; i < attacks.length; i++)
            results[i].get(); //waiting for all attacks to be handled
        Future deactivation = sendEvent(new DeactivationEvent()); //sending the deactivation event
        deactivation.get(); //waiting for it to be handled
        Future bombDestroy = sendEvent(new BombDestroyerEvent()); //sending bombdestroyer event to landao
        bombDestroy.get(); //waiting for it to be handled
        //now all threads need to terminate
        sendBroadcast(new TerminationBroadcast());
    }
}
