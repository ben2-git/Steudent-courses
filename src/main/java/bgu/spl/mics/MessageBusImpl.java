package bgu.spl.mics;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private ConcurrentHashMap<Class<? extends Event> , ConcurrentLinkedQueue <MicroService>> eventMap;
	private ConcurrentHashMap <Class<? extends Broadcast> , ConcurrentLinkedQueue <MicroService>> broadcastMap;
	private ConcurrentHashMap <MicroService , LinkedBlockingQueue <Message>> messageMap;
	private ConcurrentHashMap <Event, Future> futureMap;

	private MessageBusImpl(){
		eventMap = new ConcurrentHashMap<>();
		broadcastMap = new ConcurrentHashMap<>();
		messageMap = new ConcurrentHashMap<>();
		futureMap = new ConcurrentHashMap<>();
	}

	private static class MessageBusImpHolder {
		private static volatile MessageBusImpl instance = new MessageBusImpl();
	}

	public void eraseMap(){
		futureMap = new ConcurrentHashMap<>();
	}

	public synchronized static MessageBusImpl getInstance() {
		return MessageBusImpHolder.instance;
	} // correct singleton

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if(!messageMap.containsKey(m))
			throw new IllegalArgumentException("This service is not registered to the messageBus!");
		eventMap.putIfAbsent(type, new ConcurrentLinkedQueue<MicroService>());
		eventMap.get(type).add(m);
	}

	@Override
	public synchronized void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if(!messageMap.containsKey(m))
			throw new IllegalArgumentException("This service is not registered to the messageBus!");
		broadcastMap.putIfAbsent(type, new ConcurrentLinkedQueue<MicroService>());
		broadcastMap.get(type).add(m);
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		Future f = futureMap.remove(e);
		f.resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		if(!broadcastMap.containsKey(b.getClass()) || broadcastMap.get(b.getClass()).isEmpty())
			throw new IllegalStateException("No service registered to deal with such event!");
		for (MicroService m : broadcastMap.get(b.getClass())) {
			try { //if m unregistered during the context switch we will get nullpointerexcpetion
				messageMap.get(m).add(b); //blocking queue wakes up sleeping threads
			} catch (NullPointerException e) {}
		}


	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		if(futureMap.containsKey(e))
			throw new IllegalArgumentException("This event was already added to the messageBus!");
		ConcurrentLinkedQueue<MicroService> eventQueue = eventMap.get(e.getClass());
		Future<T> f = new Future<>();
		futureMap.put(e,f);
			if (!eventMap.containsKey(e.getClass()))
				throw new IllegalStateException("No service registered to deal with such event!"); //maybe return null
		synchronized (eventQueue) {
			if(eventQueue.isEmpty())
				throw new IllegalStateException("No service registered to deal with such event!"); //maybe return null
			boolean eventSent = false;
			while (!eventSent) {
				try {
					MicroService m = eventQueue.poll(); //pops the first microService in the queue
					messageMap.get(m).add(e); //waking up the thread if necessary
					eventQueue.add(m); //returns the microService to the end of the queue
					eventSent = true;
					}
				catch (NullPointerException ex) { }
			}
		}
		return f;
	}

	@Override
	public void register(MicroService m) {
		if(messageMap.containsKey(m))
			throw new IllegalArgumentException("This service is already registered to the messageBus!");
		messageMap.put(m, new LinkedBlockingQueue<Message>());
	}

	@Override
	public void unregister(MicroService m) {
		if (!messageMap.containsKey(m))
			throw new IllegalArgumentException("This service is not registered to the messageBus!");
		messageMap.remove(m);
		//removing 'm' from all events he was subscribed to
		Collection<ConcurrentLinkedQueue<MicroService>> events = eventMap.values();
		for (ConcurrentLinkedQueue<MicroService> list : events)
			list.remove(m);
		//removing 'm' from all broadcasts he was subscribed to
		Collection<ConcurrentLinkedQueue<MicroService>> broadcasts = broadcastMap.values();
		for (ConcurrentLinkedQueue<MicroService> list : broadcasts)
			list.remove(m);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if(!messageMap.containsKey(m))
			throw new IllegalArgumentException("This service is not registered to the messageBus!");
		return messageMap.get(m).take(); //the blocking queue will wait for a message to be available if necessary
	}
}
