package package1;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
/*import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;*/

public class Scheduler {
	//int numberOfNotif = 0;
	int mapperDesiredNumber;
	int reducerDesiredNumber;
	
	long time1= System.nanoTime();
	long initTime= System.nanoTime();
	private BlockingQueue<Message> queue;
	
	int mapperActualNumber = 0;
	ArrayList<Mapper> mapperList = new ArrayList<Mapper>();
	ArrayList<Reducer2> reducerList = new ArrayList<Reducer2>();
	
	boolean mapperJoined = false;
	BoolHolder reducerJoined = new BoolHolder(false);

	AtomicInteger nbOfReducersReadPhase1 = new AtomicInteger(0);
	
	boolean notified = false;
	int processedPhase1 = 0;
	
	TreeMap<String,Integer> finalMap = new TreeMap<String,Integer>();
	boolean over = false;
	
	/*private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private final Lock writeLock = readWriteLock.writeLock();*/
	
	public Scheduler(BlockingQueue<Message> messageQueue, int numberOfReducers, int numberOfMappers) {
		this.queue = messageQueue;
		this.reducerDesiredNumber = numberOfReducers;
		this.mapperDesiredNumber = numberOfMappers;
		for (int i = 0; i<=reducerDesiredNumber-1;i++) {
			Reducer2 reducer = new Reducer2(this.queue, i, this.nbOfReducersReadPhase1, this.reducerDesiredNumber, this.mapperDesiredNumber);
			reducerList.add(reducer);
		}
	}
	
	public void processQueue() throws InterruptedException {
				
		while (true) {
			
			if (over) {
				long time3= System.nanoTime();
				System.out.println("Total time for reducing : " + (time3-time1) + " nanoseconds");
				System.out.println(this.finalMap);
				break;
			}
			
			if ((mapperJoined == false) && (mapperActualNumber == mapperDesiredNumber)) {
				for(Mapper mapper :mapperList) {
					mapper.thread.join();
				}
				long time2= System.nanoTime();
				System.out.println("Mapping time : " + (time2-time1) + " nanoseconds");
				time1 = time2;
				System.out.println("I joined mappers");
				mapperJoined = true;
			}

			if ((processedPhase1 == mapperDesiredNumber) && (reducerJoined.value == false)) {
				for (Reducer2 reducer:reducerList) {
					reducer.thread.join();
				}
				long time3= System.nanoTime();
				System.out.println("Reducing time : " + (time3-time1) + " nanoseconds");
				System.out.println("I joined reducers");
				//System.out.println(queue.poll().getLabel());
				reducerJoined.value = true;
			}
			
			Message currentMessage = queue.peek();
			if (currentMessage != null) {
				this.processMessage(currentMessage);
			}
			
		}
	}
	
	public void processMessage(Message message) throws InterruptedException {

		String messagePhase = message.getLabel().substring(0, 6);

		if (messagePhase.equals("Phase0")) {
			Mapper mapper = new Mapper(this.queue);
			mapperActualNumber += 1;
			mapperList.add(mapper);
		}

		if ((messagePhase.equals("Phase1")) && (mapperJoined == true)) {

			if ((nbOfReducersReadPhase1.get() == reducerDesiredNumber) && (notified == true)) {
				//writeLock.lock();
				//try {
				nbOfReducersReadPhase1.getAndSet(0);
				queue.poll();
				//}
				//finally {
				//	writeLock.unlock();
				//}
				
				notified = false;
			}
			else if ((notified == false)) {
				boolean stillRuning = false;
				for (Reducer2 red : reducerList) {
					if (red.thread.getState() != Thread.State.WAITING) {
						stillRuning = true;
						break;
					}
				}
				if (stillRuning == false) {
					synchronized (queue) {
						processedPhase1 += 1;
						queue.notifyAll();
						//numberOfNotif += 1;
						notified = true;
					}
				}
				
			}
		}

		if ((messagePhase.equals("Phase2")) && (reducerJoined.value == true)) {
			finalProcess();
		}
	}
	
	public void finalProcess() {
		//System.out.println(queue.size() + " " + reducerList.size());
		Message message = queue.poll();
		if (queue.isEmpty()) {
			over = true;
		}
		if ((message.getContent() != null)) {
			TreeMap<String,Integer> myMap = new TreeMap<String,Integer>();
			//System.out.println(message.getContent());
			String[] pairs = message.getContent().replace("{", "").replace("}", "").split(" ,");
			for (int i=0;i<pairs.length;i++) {
				String pair = pairs[i];
				String[] keyValue = pair.split(":");
				myMap.put(keyValue[0], Integer.parseInt(keyValue[1].replace("\"", "").replace(" ", "")));
			} 

			myMap.forEach((key, value) -> this.finalMap.merge(key, value, (v1, v2) -> v1+v2));
		}
		
		
	}

}
