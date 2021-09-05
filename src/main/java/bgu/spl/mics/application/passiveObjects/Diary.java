package bgu.spl.mics.application.passiveObjects;


import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a Diary - in which the flow of the battle is recorded.
 * We are going to compare your recordings with the expected recordings, and make sure that your output makes sense.
 * <p>
 * Do not add to this class nothing but a single constructor, getters and setters.
 */

public class Diary {

    ///delete
    public void resetNumberAttacks() {
        totalAttacks.getAndSet(0);
    }

    private AtomicInteger totalAttacks;
    private long HanSoloFinish;
    private long C3POFinish;
    private long R2D2Deactivate;
    private long LeiaTerminate;
    private long HanSoloTerminate;
    private long C3POTerminate;
    private long R2D2Terminate;
    private long LandoTerminate;

    private Diary(){
        totalAttacks = new AtomicInteger(0);
    }

    private static class DiaryHolder {
        private static volatile Diary instance = new Diary();
    }

    public synchronized static Diary getInstance() {
        return DiaryHolder.instance;
    } // correct singleton

    public int getTotalAttacks() {
        return totalAttacks.get();
    }

    public long getC3POFinish() {
        return C3POFinish;
    }

    public void increaseAttacks() {
        totalAttacks.incrementAndGet(); //this is an atomic operation, and so this field is thread-safe
    }

    public long getHanSoloFinish() {
        return HanSoloFinish;
    }

    public void setHanSoloFinish(long hanSoloFinish) {
        HanSoloFinish = hanSoloFinish;
    }

    public void setC3POFinish(long c3POFinish) {
        C3POFinish = c3POFinish;
    }

    public long getR2D2Deactivate() {
        return R2D2Deactivate;
    }

    public void setR2D2Deactivate(long r2D2Deactivate) {
        R2D2Deactivate = r2D2Deactivate;
    }

    public long getLeiaTerminate() {
        return LeiaTerminate;
    }

    public void setLeiaTerminate(long leiaTerminate) {
        LeiaTerminate = leiaTerminate;
    }

    public long getHanSoloTerminate() {
        return HanSoloTerminate;
    }

    public void setHanSoloTerminate(long hanSoloTerminate) {
        HanSoloTerminate = hanSoloTerminate;
    }

    public long getC3POTerminate() {
        return C3POTerminate;
    }

    public void setC3POTerminate(long c3POTerminate) {
        C3POTerminate = c3POTerminate;
    }

    public long getR2D2Terminate() {
        return R2D2Terminate;
    }

    public void setR2D2Terminate(long r2D2Terminate) {
        R2D2Terminate = r2D2Terminate;
    }

    public long getLandoTerminate() {
        return LandoTerminate;
    }

    public void setLandoTerminate(long landoTerminate) {
        LandoTerminate = landoTerminate;
    }

    public String toString(){
        String s = "";
        s = "totalAttacks: "+ totalAttacks+"\r\n"+
                "HanSoloFinish: "+ HanSoloFinish+"\r\n"+
                "C3POFinish: "+ C3POFinish+"\r\n"+
                "R2D2Deactivate: "+ R2D2Deactivate+"\r\n"+
                "LeiaTerminate: "+ LeiaTerminate+"\r\n"+
                "HanSoloTerminate: "+ HanSoloTerminate+"\r\n"+
                "C3POTerminate: "+ C3POTerminate+"\r\n"+
                "LandoTerminate: "+ LandoTerminate+"\r\n"+
                "R2D2Terminate: "+ R2D2Terminate+"\r\n";
        return s;
    }
}
