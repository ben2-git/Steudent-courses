package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;

public class AttackEvent implements Event<Boolean> {

	private final Attack attack;

	public AttackEvent(Attack a){
	    attack = a;
    }

    public Attack getAttack() {
        return attack;
    }

}
