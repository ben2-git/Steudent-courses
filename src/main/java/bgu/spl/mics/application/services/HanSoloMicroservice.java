package bgu.spl.mics.application.services;


import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;

/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class HanSoloMicroservice extends MicroService {

    private long lastAttackTime;

    public HanSoloMicroservice() {
        super("Han");
        lastAttackTime = -1;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TerminationBroadcast.class, (TerminationBroadcast b) ->  {
            Diary.getInstance().setHanSoloTerminate(System.currentTimeMillis());
            Diary.getInstance().setHanSoloFinish(lastAttackTime);
            terminate();
        }); //subscribing to terminate together
        subscribeEvent(AttackEvent.class, (AttackEvent e) -> {
            Attack a = e.getAttack();
            Ewoks.getInstance().acquire(a); //will wait for the ewoks to be available if necessary
            try{
                Thread.sleep(a.getDuration());
            } catch (Exception ex) {}
            Ewoks.getInstance().release(a);
            lastAttackTime = System.currentTimeMillis();
            Diary.getInstance().increaseAttacks();
            MessageBusImpl.getInstance().complete(e, true);
        });
    }
}
