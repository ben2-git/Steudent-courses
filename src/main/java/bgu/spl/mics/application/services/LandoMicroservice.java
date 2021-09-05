package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService {

    private long duration;

    public LandoMicroservice(long duration) {
        super("Lando");
        this.duration = duration;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TerminationBroadcast.class, (TerminationBroadcast b) ->  {
            Diary.getInstance().setLandoTerminate(System.currentTimeMillis());
            terminate();
        }); //subscribing to terminate together
        subscribeEvent(BombDestroyerEvent.class, (BombDestroyerEvent e) -> {
            try {Thread.sleep(duration);}
            catch (Exception ex){}
            complete(e, true);
        });
    }
}
