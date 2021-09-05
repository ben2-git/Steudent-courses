package bgu.spl.mics.application.passiveObjects;


import java.util.concurrent.Semaphore;

/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {

    private Ewok[] ewoks;

    public Ewoks (){
        ewoks = null;
    }

    private static class EwoksHolder {
        private static volatile Ewoks instance = new Ewoks();
    }

    public synchronized static Ewoks getInstance() {
        return EwoksHolder.instance;
    } // correct singleton

    public synchronized boolean setEwoks (int n)
    {
        if(ewoks != null)
            return false; //only allowed to change once
        ewoks = new Ewok[n+1];
        //ewoks[0] will be null for convenience
        for(int i = 1; i <= n; i++)
            ewoks[i] = new Ewok(i);
        return true;
    }

    public synchronized void acquire(Attack a){
        boolean allFree = false;
        while(!allFree)
        { //checking if all required ewoks are available for the attack
            allFree = true;
            for(Integer i : a.serials){
                if(!ewoks[i].isAvailable()){
                    allFree = false;
                    try {
                        wait();
                    }
                    catch (InterruptedException e) {break;}
                }
            }
        }
        //all required ewoks are free, we can begin acquiring them
        for (Integer i : a.serials)
            ewoks[i].acquire();
    }

    public void release(Attack a){
        for (Integer i : a.serials)
            ewoks[i].release();
        synchronized (this) {
            notifyAll();
        }
    }

    public String toString(){
        String s = "{"+"\n";
        for(int i=1; i < ewoks.length; i++)
            s+="ewok "+ewoks[i].serialNumber+" is "+ewoks[i].isAvailable()+"\n";
        s+="}";
        return s;
    }
}
